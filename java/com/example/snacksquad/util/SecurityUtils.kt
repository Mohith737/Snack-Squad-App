package com.example.snacksquad.util

import java.security.MessageDigest

object SecurityUtils {
    private const val HASH_ALGORITHM = "SHA-256"

    fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance(HASH_ALGORITHM)
        val hashedBytes = digest.digest(password.toByteArray(Charsets.UTF_8))
        return hashedBytes.joinToString(separator = "") { byte ->
            "%02x".format(byte)
        }
    }

    fun hashPassword(username: String, password: String): String {
        return hashPassword("$username:$password")
    }
}
