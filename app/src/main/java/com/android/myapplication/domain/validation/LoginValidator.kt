package com.android.myapplication.domain.validation

enum class EmailError { Empty, Invalid }

enum class PasswordError { Empty, TooShort }

object LoginValidator {

    const val MIN_PASSWORD_LENGTH = 6

    private val emailPattern = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun validateEmail(email: String): EmailError? = when {
        email.isBlank() -> EmailError.Empty
        !emailPattern.matches(email.trim()) -> EmailError.Invalid
        else -> null
    }

    fun validatePassword(password: String): PasswordError? = when {
        password.isEmpty() -> PasswordError.Empty
        password.length < MIN_PASSWORD_LENGTH -> PasswordError.TooShort
        else -> null
    }
}
