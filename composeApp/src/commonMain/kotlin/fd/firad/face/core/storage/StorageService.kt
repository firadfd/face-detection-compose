package fd.firad.face.core.storage

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlin.time.Clock

class StorageService(private val settings: Settings) {

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
    }

    fun saveTokens(accessToken: String) {
        settings[KEY_ACCESS_TOKEN] = accessToken
    }

    fun getAccessToken(): String? = settings.getStringOrNull(KEY_ACCESS_TOKEN)


    fun clearAuth() {
        settings.remove(KEY_ACCESS_TOKEN)
    }

    private fun currentTimeMillis(): Long {
        val now = Clock.System.now()
        return now.toEpochMilliseconds()
    }
}
