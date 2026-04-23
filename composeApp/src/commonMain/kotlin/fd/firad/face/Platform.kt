package fd.firad.face

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform