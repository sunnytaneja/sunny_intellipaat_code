package com.android.myapplication.domain.usecase

import com.android.myapplication.domain.model.AuthSession
import com.android.myapplication.domain.repository.AuthRepository

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Result<AuthSession> =
        repository.login(email, password)
}
