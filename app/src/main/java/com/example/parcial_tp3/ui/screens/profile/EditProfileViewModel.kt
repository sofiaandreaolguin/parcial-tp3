package com.example.parcial_tp3.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parcial_tp3.data.repository.ProfileRepository
import com.example.parcial_tp3.domain.model.Gender
import com.example.parcial_tp3.domain.model.Profile
import java.time.LocalDate
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/** Solo muestra el perfil: editar y guardar llega cuando Profile tenga persistencia. */
@HiltViewModel
class EditProfileViewModel @Inject constructor(
    repository: ProfileRepository,
) : ViewModel() {

    val uiState: StateFlow<EditProfileUiState> = repository.profile
        .map { it.toUiState() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = repository.profile.value.toUiState(),
        )

    fun onFirstNameChange(value: String) {}

    fun onLastNameChange(value: String) {}

    fun onBirthDateChange(value: LocalDate) {}

    fun onHeightChange(value: Int) {}

    fun onWeightChange(value: Int) {}

    fun onGenderChange(value: Gender) {}

    /** "Save changes": valida y escribe sin esperar al servidor (C19, C21, C37). */
    fun save() {}

    private fun Profile.toUiState() = EditProfileUiState(
        firstName = firstName,
        lastName = lastName,
        birthDate = birthDate,
        heightCm = heightCm,
        weightKg = weightKg,
        gender = gender,
    )
}
