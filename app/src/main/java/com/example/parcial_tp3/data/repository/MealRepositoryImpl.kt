package com.example.parcial_tp3.data.repository

import android.database.sqlite.SQLiteException
import com.example.parcial_tp3.data.local.dao.MealDao
import com.example.parcial_tp3.data.local.entity.MealEntity
import com.example.parcial_tp3.domain.model.Meal
import java.time.LocalDate
import java.time.format.DateTimeParseException
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/** Traduce lo que tira Room, o una Entity que no se puede leer, a [MealDataException] (CONTEXTO §2). */
class MealRepositoryImpl @Inject constructor(
    private val dao: MealDao,
) : MealRepository {

    override fun observeMealsByDate(date: LocalDate): Flow<List<Meal>> =
        dao.observeByDate(date.toString()).map { entities -> entities.map { it.toMeal() } }.storageErrors()

    override suspend fun addMeal(meal: Meal): Long = write { dao.insert(meal.toEntity()) }

    override fun observeMeal(id: Long): Flow<Meal?> = dao.observeById(id).map { it?.toMeal() }.storageErrors()

    override suspend fun updateMeal(meal: Meal) {
        val rows = write { dao.update(meal.toEntity()) }
        if (rows == 0) throw MealDataException(MealError.NOT_FOUND)
    }

    override suspend fun deleteMeal(meal: Meal) {
        write { dao.deleteById(meal.id) }
    }

    // valueOf tira IllegalArgumentException con un tipo desconocido; parse, DateTimeParseException.
    private fun MealEntity.toMeal(): Meal =
        try {
            toDomain()
        } catch (e: IllegalArgumentException) {
            throw MealDataException(MealError.INVALID_DATA, e)
        } catch (e: DateTimeParseException) {
            throw MealDataException(MealError.INVALID_DATA, e)
        }

    // Al leer, Room tira SQLiteException si la consulta falla (por ejemplo, una base dañada).
    private fun <T> Flow<T>.storageErrors(): Flow<T> = catch { e ->
        throw if (e is SQLiteException) MealDataException(MealError.STORAGE, e) else e
    }

    private suspend fun <T> write(block: suspend () -> T): T =
        try {
            block()
        } catch (e: SQLiteException) {
            throw MealDataException(MealError.STORAGE, e)
        }
}
