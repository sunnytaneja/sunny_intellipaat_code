package com.android.myapplication.domain

import com.android.myapplication.domain.validation.EmailError
import com.android.myapplication.domain.validation.LoginValidator
import com.android.myapplication.domain.validation.PasswordError
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class LoginValidatorTest {

    @Test
    fun `blank email is rejected`() {
        assertEquals(EmailError.Empty, LoginValidator.validateEmail("  "))
    }

    @Test
    fun `malformed email is rejected`() {
        assertEquals(EmailError.Invalid, LoginValidator.validateEmail("not-an-email"))
        assertEquals(EmailError.Invalid, LoginValidator.validateEmail("user@domain"))
    }

    @Test
    fun `valid email passes`() {
        assertNull(LoginValidator.validateEmail("test@example.com"))
    }

    @Test
    fun `short password is rejected`() {
        assertEquals(PasswordError.Empty, LoginValidator.validatePassword(""))
        assertEquals(PasswordError.TooShort, LoginValidator.validatePassword("abc"))
        assertNull(LoginValidator.validatePassword("Password1"))
    }
}
