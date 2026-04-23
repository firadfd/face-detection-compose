package fd.firad.face.core.util

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform