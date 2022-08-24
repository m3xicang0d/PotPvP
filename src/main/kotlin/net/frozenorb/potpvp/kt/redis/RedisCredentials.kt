package net.frozenorb.potpvp.kt.redis

data class RedisCredentials(
    var host: String,
    var port: Int,
    var password: String,
    var dbId: Int
) {

    fun shouldAuthenticate(): Boolean {
        return password.isNotEmpty() && password.isNotBlank()
    }

}
