package com.android.myapplication.data.auth

import com.android.myapplication.domain.model.AuthSession
import com.android.myapplication.domain.model.InvalidCredentialsException
import com.android.myapplication.domain.repository.AuthRepository
import kotlinx.coroutines.delay

/** Fake login: accepts one hard-coded account after a short delay. */
class MockAuthRepository : AuthRepository {

    override suspend fun login(email: String, password: String): Result<AuthSession> {
        delay(1_200)
        return if (email.equals(VALID_EMAIL, ignoreCase = true) && password == VALID_PASSWORD) {
            Result.success(AuthSession(token = "mock-access-token"))
        } else {
            Result.failure(InvalidCredentialsException())
        }
    }

    companion object {
        const val VALID_EMAIL = "test@example.com"
        const val VALID_PASSWORD = "password123"
    }
}
