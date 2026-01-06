package com.aiskov.utils

import java.security.SecureRandom
import java.util.UUID

object UserData {
    private val random = SecureRandom()
    private val names = listOf("Alice", "Bob", "Carol", "Dave", "Eve", "Frank", "Grace", "Heidi")
    private val specials = "!@#\$%&*"

    fun email(): String = "${UUID.randomUUID().toString().replace("-", "").substring(0, 8)}@example.com"

    fun name(): String = names[random.nextInt(names.size)] + " " + names[random.nextInt(names.size)]

    fun password(length: Int = 12): String {
        // Ensure at least one uppercase, one lowercase, one digit and one special character
        require(length >= 4) { "password length must be at least 4 to include required character classes" }
        val upper = (('A'.code) + random.nextInt(26)).toChar()
        val lower = (('a'.code) + random.nextInt(26)).toChar()
        val digit = (('0'.code) + random.nextInt(10)).toChar()
        val special = specials[random.nextInt(specials.length)]

        val otherChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789" + specials
        val remaining = (1..(length - 4)).map { otherChars[random.nextInt(otherChars.length)] }
        val chars = (listOf(upper, lower, digit, special) + remaining).shuffled() // safe to use default kotlin Random here
        return chars.joinToString("")
    }

    fun createUserPayload(): Map<String, Any> = mapOf(
        "email" to email(),
        "name" to name(),
        "password" to password()
    )
}
