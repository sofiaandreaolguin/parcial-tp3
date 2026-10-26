package com.example.parcial_tp3.data.repository

/** Sin conexion y con un detalle que nunca se descargo (RF02): la pantalla avisa que no esta disponible. */
class WorkoutNotCachedException(val workoutId: String) :
    Exception("El entrenamiento $workoutId no esta guardado para verlo sin conexion")
