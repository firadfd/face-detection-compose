package fd.firad.face.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf

interface AppStrings {
    val welcome: String
    val loginContinue: String
    val username: String
    val password: String
    val login: String
    val profile: String
    val theme: String
    val language: String
    val light: String
    val dark: String
    val system: String
}

object EnStrings : AppStrings {
    override val welcome = "Welcome Back"
    override val loginContinue = "Login to continue"
    override val username = "Username"
    override val password = "Password"
    override val login = "Login"
    override val profile = "Profile"
    override val theme = "Theme"
    override val language = "Language"
    override val light = "Light"
    override val dark = "Dark"
    override val system = "System Default"
}

object BnStrings : AppStrings {
    override val welcome = "স্বাগতম"
    override val loginContinue = "চালিয়ে যেতে লগইন করুন"
    override val username = "ব্যবহারকারীর নাম"
    override val password = "পাসওয়ার্ড"
    override val login = "লগইন"
    override val profile = "প্রোফাইল"
    override val theme = "থিম"
    override val language = "ভাষা"
    override val light = "লাইট"
    override val dark = "ডার্ক"
    override val system = "সিস্টেম ডিফল্ট"
}

object ArStrings : AppStrings {
    override val welcome = "مرحباً بك"
    override val loginContinue = "سجل الدخول للمتابعة"
    override val username = "اسم المستخدم"
    override val password = "كلمة المرور"
    override val login = "تسجيل الدخول"
    override val profile = "الملف الشخصي"
    override val theme = "المظهر"
    override val language = "اللغة"
    override val light = "فاتح"
    override val dark = "داكن"
    override val system = "تلقائي"
}

val LocalAppStrings = staticCompositionLocalOf<AppStrings> { EnStrings }
