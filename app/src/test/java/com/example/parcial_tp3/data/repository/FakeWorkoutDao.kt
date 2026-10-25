package com.example.parcial_tp3.data.repository

import com.example.parcial_tp3.data.local.dao.WorkoutDao
import com.example.parcial_tp3.data.local.entity.WorkoutEntity
import com.example.parcial_tp3.data.local.entity.WorkoutExerciseEntity
import com.example.parcial_tp3.data.local.entity.WorkoutSummaryRow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/**
 * WorkoutDao en memoria. Implementa solo las operaciones basicas, imitando las @Query reales, y
 * hereda saveSummaries y saveDetail: asi la logica real de esas dos corre tambien sin emulador.
 */
class FakeWorkoutDao : WorkoutDao {

    private val workouts = MutableStateFlow<Map<String, WorkoutEntity>>(emptyMap())
    private val exercises = mutableListOf<WorkoutExerciseEntity>()

    var lanzarAlGuardar = false
    var lanzarAlLeer = false

    /** Cuantas veces se leyo la tabla (getAll, getById o getExercises). */
    var lecturas = 0
        private set

    /** Carga datos sin pasar por el DAO: no cuenta como lectura ni lanza. */
    fun precargar(filas: List<WorkoutEntity>, ejercicios: List<WorkoutExerciseEntity> = emptyList()) {
        workouts.update { actual -> actual + filas.associateBy { it.id } }
        exercises += ejercicios
    }

    fun filasGuardadas(): List<WorkoutEntity> = workouts.value.values.sortedBy { it.id }

    fun ejerciciosGuardados(): List<WorkoutExerciseEntity> =
        exercises.sortedWith(compareBy({ it.workoutId }, { it.position }))

    override fun observeAll(): Flow<List<WorkoutEntity>> = workouts.map { it.values.sortedBy { fila -> fila.id } }

    override suspend fun upsertAll(workouts: List<WorkoutEntity>) {
        alGuardar()
        this.workouts.update { actual -> actual + workouts.associateBy { it.id } }
    }

    override suspend fun getAll(): List<WorkoutEntity> {
        alLeer()
        return filasGuardadas()
    }

    override suspend fun getById(id: String): WorkoutEntity? {
        alLeer()
        return workouts.value[id]
    }

    override suspend fun getExercises(workoutId: String): List<WorkoutExerciseEntity> {
        alLeer()
        return exercises.filter { it.workoutId == workoutId }.sortedBy { it.position }
    }

    // Como el @Upsert(entity = WorkoutEntity::class): description queda como estaba (nulo si es nueva).
    override suspend fun upsertSummaries(rows: List<WorkoutSummaryRow>) {
        alGuardar()
        workouts.update { actual ->
            actual + rows.associate { fila ->
                fila.id to WorkoutEntity(
                    id = fila.id,
                    name = fila.name,
                    level = fila.level,
                    durationMinutes = fila.durationMinutes,
                    exerciseCount = fila.exerciseCount,
                    description = actual[fila.id]?.description,
                )
            }
        }
    }

    override suspend fun upsertDetail(workout: WorkoutEntity) {
        alGuardar()
        workouts.update { actual -> actual + (workout.id to workout) }
    }

    override suspend fun deleteExercises(workoutId: String) {
        alGuardar()
        exercises.removeAll { it.workoutId == workoutId }
    }

    // Como @Insert sin onConflict: una clave (workoutId, position) repetida falla.
    override suspend fun insertExercises(exercises: List<WorkoutExerciseEntity>) {
        alGuardar()
        val claves = this.exercises.map { it.workoutId to it.position }.toSet()
        check(exercises.none { (it.workoutId to it.position) in claves }) { "Clave de ejercicio repetida" }
        this.exercises += exercises
    }

    private fun alGuardar() {
        if (lanzarAlGuardar) throw IllegalStateException("Room falla al guardar")
    }

    private fun alLeer() {
        lecturas++
        if (lanzarAlLeer) throw IllegalStateException("Room falla al leer")
    }
}
