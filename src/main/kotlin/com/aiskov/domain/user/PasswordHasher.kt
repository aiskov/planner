package com.aiskov.domain.user

import jakarta.enterprise.context.ApplicationScoped
import org.mindrot.jbcrypt.BCrypt

@ApplicationScoped
class PasswordHasher {
    fun hash(plain: String): String {
        return BCrypt.hashpw(plain, BCrypt.gensalt())
    }

    fun verify(plain: String, hash: String): Boolean {
        return BCrypt.checkpw(plain, hash)
    }
}

