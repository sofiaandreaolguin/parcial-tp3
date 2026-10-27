package com.example.parcial_tp3.ui.screens.nutrition

import com.example.parcial_tp3.data.repository.AppError
import com.example.parcial_tp3.data.repository.AppException
import com.example.parcial_tp3.domain.model.FoodItem
import com.example.parcial_tp3.domain.model.FoodPage
import com.example.parcial_tp3.domain.model.Nutrients
import com.example.parcial_tp3.util.MainDispatcherRule
import java.time.LocalDate
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * FoodSearchViewModel (`food_search`, EXT02) con un FoodRepository falso.
 * Spec: specs/funcionales/busqueda-de-alimentos.md, C19 a C32 y C35.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class FoodSearchViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val pollo = FoodItem("f05", "Pollo cocido", Nutrients(165, 31.0, 0.0, 3.6))
    private val aceite = FoodItem("f12", "Aceite de oliva", Nutrients(884, 0.0, 0.0, 100.0))
    private val hoy = LocalDate.of(2026, 10, 26)

    private fun pagina(vararg items: FoodItem, total: Int = items.size) =
        Result.success(FoodPage(items.toList(), 1, 50, total))

    private fun repo(resultado: Result<FoodPage> = pagina(pollo, aceite)) =
        FakeFoodRepository().apply { this.resultado = resultado }

    private fun vm(repo: FakeFoodRepository) = FoodSearchViewModel(repo)

    /** Escribe letra por letra, como quien tipea, sin dejar pasar el debounce entre teclas. */
    private fun FoodSearchViewModel.tipear(texto: String) {
        for (i in 1..texto.length) onQueryChange(texto.take(i))
    }

    @Test
    fun `C19 al abrir consulta con q vacio desde el init`() = runTest {
        val repo = repo()
        vm(repo)
        advanceUntilIdle()

        assertEquals(listOf(""), repo.consultas)
    }

    @Test
    fun `C20 escribir pollo de corrido dispara una sola consulta`() = runTest {
        val repo = repo()
        val vm = vm(repo)
        advanceUntilIdle()
        repo.consultas.clear()

        vm.tipear("pollo")
        advanceUntilIdle()

        assertEquals(listOf("pollo"), repo.consultas)
    }

    @Test
    fun `C20 antes de los 300 ms todavia no consulto`() = runTest {
        // Sin esta mitad, el test de arriba pasa aunque se saque el debounce: cancelar el job
        // anterior ya deja una sola consulta. Lo que hay que probar es que ESPERA.
        val repo = repo()
        val vm = vm(repo)
        advanceUntilIdle()
        repo.consultas.clear()

        vm.onQueryChange("pollo")
        advanceTimeBy(DEBOUNCE_MS - 1)
        runCurrent()
        assertEquals("todavia no tendria que haber consultado", emptyList<String>(), repo.consultas)

        advanceTimeBy(2)
        runCurrent()
        assertEquals(listOf("pollo"), repo.consultas)
    }

    @Test
    fun `C22 data vacia con total mayor que cero no es el estado vacio`() = runTest {
        // La lista puede venir vacia sin que el alimento no exista. Se distingue por meta.total,
        // no por data: con total 12 hay resultados, lo que paso fue otra cosa.
        val repo = repo(Result.success(FoodPage(emptyList(), 1, 50, 12)))
        val vm = vm(repo)
        advanceUntilIdle()

        assertFalse("hay 12 alimentos: no es 'no se encontro nada'", vm.estado.value.vacio)
        assertNull(vm.estado.value.error)
    }

    @Test
    fun `C21 mientras busca el estado es cargando`() = runTest {
        val repo = repo()
        val vm = vm(repo)
        advanceUntilIdle()
        repo.retenida = CompletableDeferred()

        vm.onQueryChange("pollo")
        advanceTimeBy(DEBOUNCE_MS + 1)
        runCurrent()

        assertTrue("esperaba cargando", vm.estado.value.cargando)
        assertNull(vm.estado.value.error)
    }

    @Test
    fun `C22 total cero deja el estado vacio y no el de error`() = runTest {
        val repo = repo(pagina(total = 0))
        val vm = vm(repo)
        advanceUntilIdle()

        assertTrue("esperaba el estado vacio", vm.estado.value.vacio)
        assertNull("vacio no es error", vm.estado.value.error)
        assertFalse(vm.estado.value.cargando)
    }

    @Test
    fun `C23 al fallar hay reintento con su mensaje y repite la busqueda`() = runTest {
        val repo = repo(Result.failure(AppException(AppError.SERVER)))
        val vm = vm(repo)
        advanceUntilIdle()
        vm.tipear("pollo")
        advanceUntilIdle()

        assertEquals(AppError.SERVER, vm.estado.value.error)
        assertFalse("error no es vacio", vm.estado.value.vacio)

        repo.consultas.clear()
        vm.onRetry()
        advanceUntilIdle()

        assertEquals("el reintento repite la misma busqueda", listOf("pollo"), repo.consultas)
    }

    @Test
    fun `C24 una busqueda cancelada no deja el estado en error`() = runTest {
        val repo = repo()
        val vm = vm(repo)
        advanceUntilIdle()

        // Cambia el texto antes de que termine la primera: la primera se cancela.
        vm.tipear("pol")
        advanceTimeBy(DEBOUNCE_MS + 1)
        vm.tipear("pollo")
        advanceUntilIdle()

        assertNull("cancelar no es fallar", vm.estado.value.error)
        assertEquals("pollo", vm.estado.value.query)
        assertEquals(listOf(pollo, aceite), vm.estado.value.items)
    }

    @Test
    fun `C24 una respuesta vieja que llega tarde no pisa a la nueva`() = runTest {
        // El caso que el test de arriba no ejercita: la primera consulta sigue EN VUELO cuando
        // se escribe otra cosa. Si el ViewModel no cancelara la anterior, la respuesta vieja
        // llegaria despues y pisaria los resultados de la nueva.
        val repo = repo()
        val vm = vm(repo)
        advanceUntilIdle()

        val vieja = CompletableDeferred<Result<FoodPage>>()
        repo.retenida = vieja
        vm.tipear("pol")
        advanceTimeBy(DEBOUNCE_MS + 1)
        runCurrent()

        repo.retenida = null
        repo.resultado = pagina(aceite)
        vm.tipear("aceite")
        advanceUntilIdle()
        assertEquals(listOf(aceite), vm.estado.value.items)

        // Recien ahora contesta la primera.
        vieja.complete(pagina(pollo))
        advanceUntilIdle()

        assertEquals("la vieja no tiene que pisar a la nueva", listOf(aceite), vm.estado.value.items)
        assertEquals("aceite", vm.estado.value.query)
        assertNull(vm.estado.value.error)
    }

    @Test
    fun `C25 borrar el texto vuelve a mostrar el catalogo`() = runTest {
        val repo = repo()
        val vm = vm(repo)
        advanceUntilIdle()
        vm.tipear("pollo")
        advanceUntilIdle()
        repo.consultas.clear()

        vm.onQueryChange("")
        advanceUntilIdle()

        assertEquals(listOf(""), repo.consultas)
        assertEquals(listOf(pollo, aceite), vm.estado.value.items)
    }

    @Test
    fun `C26 elegir un alimento deja la porcion en cien gramos`() = runTest {
        val vm = vm(repo())
        advanceUntilIdle()

        vm.onFoodSelected(pollo)

        assertEquals(pollo, vm.estado.value.seleccionado)
        assertEquals("100", vm.estado.value.gramos)
        assertEquals(Nutrients(165, 31.0, 0.0, 3.6), vm.estado.value.porcion)
    }

    @Test
    fun `C27 cambiar los gramos recalcula sin volver a consultar`() = runTest {
        val repo = repo()
        val vm = vm(repo)
        advanceUntilIdle()
        vm.onFoodSelected(pollo)
        repo.consultas.clear()

        vm.onGramsChange("150")
        advanceUntilIdle()

        assertEquals(Nutrients(248, 46.5, 0.0, 5.4), vm.estado.value.porcion)
        assertNull(vm.estado.value.gramosError)
        assertEquals("no tiene que volver al servidor", emptyList<String>(), repo.consultas)
    }

    @Test
    fun `C18 con los gramos invalidos no se calcula la porcion`() = runTest {
        // La otra mitad de C18: ademas de marcar el error, no tiene que calcular nada.
        val vm = vm(repo())
        advanceUntilIdle()
        vm.onFoodSelected(pollo)
        assertEquals(Nutrients(165, 31.0, 0.0, 3.6), vm.estado.value.porcion)

        for (malos in listOf("0", "-5", "abc", "", "99999")) {
            vm.onGramsChange(malos)
            assertNull("con \"$malos\" no se tiene que calcular nada", vm.estado.value.porcion)
            assertNotNull("y tiene que decir por que", vm.estado.value.gramosError)
            assertNull("sin porcion, onConfirm no devuelve formulario", vm.onConfirm(hoy))
        }
    }

    @Test
    fun `C28 volver desde la porcion conserva los resultados`() = runTest {
        val vm = vm(repo())
        advanceUntilIdle()
        vm.onFoodSelected(pollo)

        vm.onBackFromPortion()

        assertNull("sin alimento elegido", vm.estado.value.seleccionado)
        assertEquals("los resultados siguen", listOf(pollo, aceite), vm.estado.value.items)
    }

    @Test
    fun `C29 onConfirm arma el MealForm con nombre nutrientes y type null`() = runTest {
        val vm = vm(repo())
        advanceUntilIdle()
        vm.onFoodSelected(pollo)
        vm.onGramsChange("150")
        advanceUntilIdle()

        val form = vm.onConfirm(hoy)

        assertEquals("Pollo cocido", form?.name)
        assertEquals("248", form?.kcal)
        assertEquals("46.5", form?.protein)
        assertEquals("5.4", form?.fat)
        assertNull("el tipo lo elige la persona en 07", form?.type)
    }

    @Test
    fun `C30 los numeros entran como 248 y no como 248 punto cero`() = runTest {
        val vm = vm(repo())
        advanceUntilIdle()
        vm.onFoodSelected(pollo)
        vm.onGramsChange("100")
        advanceUntilIdle()

        val form = vm.onConfirm(hoy)

        // parseKcal valida con ^-?\d+$: "165.0" haria que 07 marque NOT_A_WHOLE_NUMBER.
        assertEquals("165", form?.kcal)
        assertEquals("sin el .0 de los Double", "0", form?.carbs)
        assertEquals("31", form?.protein)
    }

    @Test
    fun `C32 el formulario lleva la fecha seleccionada`() = runTest {
        val vm = vm(repo())
        advanceUntilIdle()
        vm.onFoodSelected(pollo)
        advanceUntilIdle()

        val otroDia = LocalDate.of(2026, 10, 20)
        assertEquals(otroDia, vm.onConfirm(otroDia)?.date)
    }

    @Test
    fun `C35 despues de confirmar no queda alimento elegido`() = runTest {
        val vm = vm(repo())
        advanceUntilIdle()
        vm.onFoodSelected(pollo)
        advanceUntilIdle()

        vm.onConfirm(hoy)

        assertNull("al volver de 07 se ven los resultados", vm.estado.value.seleccionado)
        assertEquals(listOf(pollo, aceite), vm.estado.value.items)
    }
}
