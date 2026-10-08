package com.android.myapplication.presentation.common

import com.android.myapplication.domain.model.InvalidCredentialsException
import java.io.IOException

fun Throwable.toUserMessage(): String = when (this) {
    is InvalidCredentialsException -> "Incorrect email or password."
    is IOException -> "Can't reach the server. Check your internet connection and try again."
    else -> "Something went wrong. Please try again."
}
