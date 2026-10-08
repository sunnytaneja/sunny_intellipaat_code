package com.android.myapplication.domain.model

data class AuthSession(val token: String)

class InvalidCredentialsException : Exception("Invalid email or password")