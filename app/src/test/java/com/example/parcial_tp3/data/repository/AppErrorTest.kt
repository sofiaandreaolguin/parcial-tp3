package com.example.parcial_tp3.data.repository

import com.google.gson.JsonIOException
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import com.google.gson.stream.MalformedJsonException
import java.io.EOFException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * La clasificacion de las excepciones en errores propios, sin red (RF05, RF06).
 * Spec: specs/funcionales/estados-de-carga-y-error.md.
 */
class AppErrorTest {

    @Test
    fun `C1 estados toAppError clasifica cada excepcion`() {
        val casos: List<Pair<Throwable, AppError>> = listOf(
            MalformedJsonException("JSON roto") to AppError.INVALID_DATA,
            EOFException("cuerpo cortado") to AppError.INVALID_DATA,
            JsonSyntaxException("JSON de otro tipo") to AppError.INVALID_DATA,
            JsonIOException("no se pudo leer") to AppError.INVALID_DATA,
            JsonParseException("no se pudo leer") to AppError.INVALID_DATA,
            IllegalArgumentException("nivel desconocido") to AppError.INVALID_DATA,
            IOException("sin conexion") to AppError.NO_CONNECTION,
            ConnectException("servidor apagado") to AppError.NO_CONNECTION,
            SocketTimeoutException("timeout") to AppError.NO_CONNECTION,
            // Lo que tira OkHttp cuando el servidor corta: se mira el tipo, no la causa
            IOException("unexpected end of stream", EOFException()) to AppError.NO_CONNECTION,
            IllegalStateException("otra") to AppError.UNKNOWN,
            RuntimeException("otra") to AppError.UNKNOWN,
            AppException(AppError.NOT_FOUND) to AppError.NOT_FOUND,
            AppException(AppError.SERVER) to AppError.SERVER,
            AppException(AppError.NO_CONNECTION, IllegalArgumentException()) to AppError.NO_CONNECTION,
        )

        // Se comparan todos juntos, asi el fallo muestra cada caso mal clasificado
        val esperado = casos.map { (excepcion, error) -> excepcion.toString() to error }
        val obtenido = casos.map { (excepcion, _) -> excepcion.toString() to excepcion.toAppError() }
        assertEquals(esperado, obtenido)
    }
}
