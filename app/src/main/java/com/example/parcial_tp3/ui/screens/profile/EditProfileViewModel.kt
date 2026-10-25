package com.example.parcial_tp3.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parcial_tp3.R
import com.example.parcial_tp3.data.repository.ProfileRepository
import com.example.parcial_tp3.domain.model.Gender
import com.example.parcial_tp3.domain.model.Profile
import com.example.parcial_tp3.domain.model.ProfileEdit
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Edita los 6 campos de Edit Profile y los escribe en Firestore sin esperar al servidor (C19). */
@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val repository: ProfileRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(repository.profile.value.toUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    // mientras la persona no toca nada, el formulario sigue al perfil (por ejemplo si termina de cargar)
    private var edited = false

    init {
        viewModelScope.launch {
            repository.profile.collect { profile ->
                if (!edited) _uiState.value = profile.toUiState()
            }
        }
    }

    fun onFirstNameChange(value: String) = edit { it.copy(firstName = value, firstNameError = null) }

    fun onLastNameChange(value: String) = edit { it.copy(lastName = value) }

    fun onBirthDateChange(value: LocalDate) = edit { it.copy(birthDate = value, birthDateError = null) }

    fun onHeightChange(value: Int) = edit { it.copy(heightCm = value, heightError = null) }

    fun onWeightChange(value: Int) = edit { it.copy(weightKg = value, weightError = null) }

    fun onGenderChange(value: Gender) = edit { it.copy(gender = value) }

    /** "Save changes": valida y escribe sin esperar al servidor (C19, C21, C37). */
    fun save() {
        val form = _uiState.value
        if (form.saved || form.isSaving) return
        if (repository.loadError.value != null) {
            _uiState.value = form.copy(saveError = R.string.edit_error_not_loaded)
            return
        }
        val validated = form.copy(
            saveError = null,
            firstNameError = if (form.firstName.isBlank()) R.string.edit_error_name else null,
            birthDateError = if (form.birthDate.isAfter(LocalDate.now())) R.string.edit_error_birth_date else null,
            heightError = if (form.heightCm < 1) R.string.edit_error_height else null,
            weightError = if (form.weightKg < 1) R.string.edit_error_weight else null,
        )
        val valid = validated.firstNameError == null && validated.birthDateError == null &&
            validated.heightError == null && validated.weightError == null
        if (!valid) {
            _uiState.value = validated
            return
        }
        _uiState.value = validated.copy(saved = true)
        repository.saveEdit(
            ProfileEdit(
                firstName = form.firstName.trim(),
                lastName = form.lastName.trim(),
                birthDate = form.birthDate,
                heightCm = form.heightCm,
                weightKg = form.weightKg,
                gender = form.gender,
            ),
        )
    }

    private fun edit(transform: (EditProfileUiState) -> EditProfileUiState) {
        edited = true
        _uiState.update(transform)
    }

    private fun Profile.toUiState() = EditProfileUiState(
        firstName = firstName,
        lastName = lastName,
        birthDate = birthDate,
        heightCm = heightCm,
        weightKg = weightKg,
        gender = gender,
    )
}
