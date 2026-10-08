package com.android.myapplication.domain.repository

import com.android.myapplication.domain.model.AuthSession

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<AuthSession>
}
