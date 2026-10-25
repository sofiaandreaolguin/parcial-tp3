package com.example.parcial_tp3.data.repository

import com.example.parcial_tp3.data.remote.ProfileRemote
import com.example.parcial_tp3.domain.model.CoachingStyle
import com.example.parcial_tp3.domain.model.Diet
import com.example.parcial_tp3.domain.model.Experience
import com.example.parcial_tp3.domain.model.Food
import com.example.parcial_tp3.domain.model.Gender
import com.example.parcial_tp3.domain.model.Goal
import com.example.parcial_tp3.domain.model.NutritionPriority
import com.example.parcial_tp3.domain.model.Profile
import com.example.parcial_tp3.domain.model.ProfileEdit
import com.example.parcial_tp3.domain.model.ResponseStyle
import com.example.parcial_tp3.domain.model.TrainingDays
import com.example.parcial_tp3.domain.model.WorkoutType
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Perfil de la cuenta en Firestore (`users/{uid}`). Publica el perfil que muestra la app, que sale
 * de la cache local del SDK. `open` y con constructor vacio para que los tests la reemplacen con un falso.
 */
@Singleton
open class ProfileRepository private constructor(
    private val remote: ProfileRemote,
    initial: Profile,
    private val scope: CoroutineScope,
) {
    @Inject
    constructor(remote: ProfileRemote) : this(remote, EMPTY, CoroutineScope(SupervisorJob() + Dispatchers.Default))

    /** Sin Firebase y con el perfil de demostracion: los tests de Home y los repositorios falsos. */
    constructor() : this(ProfileRemote.Unavailable, DEMO, CoroutineScope(SupervisorJob() + Dispatchers.Default))

    /** Para probar el repositorio real con un [ProfileRemote] falso y un scope de prueba. */
    internal constructor(remote: ProfileRemote, scope: CoroutineScope) : this(remote, EMPTY, scope)

    private val _sessionExpired = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    /** El servidor dejo de aceptar la sesion (permiso denegado) despues de haber cargado el perfil (C35). */
    open val sessionExpired: Flow<Unit> = _sessionExpired.asSharedFlow()

    private var observing: Job? = null
    private var uid: String? = null
    private var email: String = ""

    private val _profile = MutableStateFlow(initial)
    open val profile: StateFlow<Profile> = _profile.asStateFlow()

    private val _loadError = MutableStateFlow<ProfileError?>(null)

    /** Error de la ultima lectura sin cache local; null si no hay (C24). */
    open val loadError: StateFlow<ProfileError?> = _loadError.asStateFlow()

    /** Lee `users/{uid}`, lo crea con Profile.default si no existe y lo publica en `profile` (C18, C23). */
    open suspend fun load(uid: String, email: String) {
        this.uid = uid
        this.email = email
        try {
            val dto = remote.get(uid)
            if (dto == null) remote.create(uid, profileOrDefault(null, email).toDto())
            _profile.value = profileOrDefault(dto, email)
            _loadError.value = null
        } catch (e: ProfileDataException) {
            if (e.error != ProfileError.NO_PERMISSION) _loadError.value = e.error
            throw e
        }
        // el observador anterior se cambia solo despues de una lectura exitosa: si falla, sigue vivo
        startObserving(uid)
    }

    /**
     * Un error del listener lo termina: un permiso denegado cierra la sesion (C35); otro se deja en
     * `loadError` y el proximo `reload` vuelve a observar. Del snapshot se toman solo los campos que
     * escribe Edit Profile: Goals, Nutrition y Voice cambian en memoria y no se persisten (spec, fuera de alcance).
     */
    private fun startObserving(uid: String) {
        observing?.cancel()
        observing = scope.launch {
            remote.observe(uid)
                .catch {
                    if (it.isPermissionDenied()) _sessionExpired.tryEmit(Unit)
                    else _loadError.value = (it as? ProfileDataException)?.error ?: ProfileError.UNKNOWN
                }
                .collect { dto ->
                    val remoto = dto.toDomain()
                    _profile.update {
                        it.copy(
                            firstName = remoto.firstName,
                            lastName = remoto.lastName,
                            email = remoto.email,
                            birthDate = remoto.birthDate,
                            heightCm = remoto.heightCm,
                            weightKg = remoto.weightKg,
                            gender = remoto.gender,
                        )
                    }
                }
        }
    }

    /** Crea `users/{uid}` (C2). */
    open suspend fun create(uid: String, profile: Profile) {
        remote.create(uid, profile.toDto())
        this.uid = uid
        email = profile.email
        _profile.value = profile
        startObserving(uid)
    }

    /** Actualizacion parcial de los 6 campos; no espera al servidor (C19, C36). */
    open fun saveEdit(edit: ProfileEdit) {
        val uid = uid ?: return
        _profile.value = _profile.value.copy(
            firstName = edit.firstName,
            lastName = edit.lastName,
            birthDate = edit.birthDate,
            heightCm = edit.heightCm,
            weightKg = edit.weightKg,
            gender = edit.gender,
        )
        remote.update(uid, edit.toFields()) { if (it == ProfileError.NO_PERMISSION) _sessionExpired.tryEmit(Unit) }
    }

    /** Reintento tras un error de lectura (C24). */
    open suspend fun reload() {
        val uid = uid ?: return
        try {
            load(uid, email)
        } catch (e: ProfileDataException) {
            // load ya dejo el error en loadError; un permiso denegado es una sesion que ya no vale
            if (e.error == ProfileError.NO_PERMISSION) _sessionExpired.tryEmit(Unit)
        }
    }

    open fun update(transform: (Profile) -> Profile) = _profile.update(transform)

    /** Cierre de sesion: no queda ningun dato de la cuenta anterior (C15, C17). */
    open fun clear() {
        observing?.cancel()
        observing = null
        uid = null
        email = ""
        _loadError.value = null
        _profile.value = EMPTY
    }

    private fun Throwable.isPermissionDenied() =
        this is ProfileDataException && error == ProfileError.NO_PERMISSION

    private companion object {
        val EMPTY = Profile.default("", "", "")

        val DEMO = Profile(
            firstName = "Alex",
            lastName = "Morgan",
            email = "alex@morgan.com",
            birthDate = LocalDate.of(1995, 3, 14),
            heightCm = 178,
            weightKg = 76,
            gender = Gender.PREFER_NOT_TO_SAY,
            experience = Experience.INTERMEDIATE,
            goals = setOf(Goal.BUILD_STRENGTH, Goal.IMPROVE_FITNESS),
            trainingDays = TrainingDays.FOUR,
            workoutType = WorkoutType.STRENGTH,
            coachingStyle = CoachingStyle.BALANCED,
            diet = Diet.EVERYTHING,
            foodsEnjoyed = Food.entries.toSet(),
            foodsToAvoid = listOf("Peanuts", "Shellfish", "Mushrooms"),
            priorities = setOf(NutritionPriority.HIT_PROTEIN, NutritionPriority.EAT_CONSISTENTLY),
            responseStyle = ResponseStyle.BALANCED,
            speakAloud = true,
            autoPlay = true,
            allowInterruptions = true,
            voiceFeedback = true,
            hapticFeedback = true,
        )
    }
}
