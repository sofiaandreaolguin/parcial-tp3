package com.example.parcial_tp3.data.repository

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

@Singleton
class FirebaseAuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
) : AuthRepository {

    override val currentUid: String? get() = auth.currentUser?.uid
    override val currentEmail: String? get() = auth.currentUser?.email

    override suspend fun signUp(email: String, password: String): String =
        translate { auth.createUserWithEmailAndPassword(email, password).await().user!!.uid }

    override suspend fun signIn(email: String, password: String): String =
        translate { auth.signInWithEmailAndPassword(email, password).await().user!!.uid }

    override fun signOut() = auth.signOut()

    private inline fun <T> translate(block: () -> T): T = try {
        block()
    } catch (e: CancellationException) {
        throw e
    } catch (e: FirebaseAuthException) {
        throw AuthDataException(authErrorFromCode(e.errorCode), e)
    } catch (e: FirebaseNetworkException) {
        throw AuthDataException(AuthError.NO_CONNECTION, e)
    } catch (e: FirebaseTooManyRequestsException) {
        throw AuthDataException(AuthError.TOO_MANY_REQUESTS, e)
    } catch (e: Exception) {
        throw AuthDataException(AuthError.UNKNOWN, e)
    }
}
