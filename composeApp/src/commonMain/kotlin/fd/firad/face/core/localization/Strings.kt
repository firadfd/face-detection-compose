package fd.firad.face.core.localization

import androidx.compose.runtime.compositionLocalOf

interface AppStrings {
    val welcome: String
    val loginContinue: String
    val home: String
    val demo: String
    val profile: String
    val username: String
    val password: String
    val login: String
    val theme: String
    val language: String
    val light: String
    val dark: String
    val system: String
    val kmpStackDemo: String
    val showCurrentTime: String
    val timeLabel: String
    val fetchFromKtor: String
    val clickToFetch: String
    val fetching: String
    val requestPermissions: String
    val pickImage: String
    val sendLocalNotification: String
    val goToNextScreen: String
    val requestingCamera: String
    val cameraGranted: String
    val requestingGallery: String
    val galleryGranted: String
    val requestingNotifications: String
    val allPermissionsGranted: String
    val detailScreen: String
    val back: String
    val faceVerify: String
}

object EnStrings : AppStrings {
    override val welcome = "Welcome Back"
    override val loginContinue = "Login to continue"
    override val home = "Home"
    override val demo = "Demo"
    override val profile = "Profile"
    override val username = "Username"
    override val password = "Password"
    override val login = "Login"
    override val theme = "Theme"
    override val language = "Language"
    override val light = "Light"
    override val dark = "Dark"
    override val system = "System Default"
    override val kmpStackDemo = "KMP Stack Demo"
    override val showCurrentTime = "Show Current Time (DateTime)"
    override val timeLabel = "Time: "
    override val fetchFromKtor = "Fetch from Ktor (Serialization)"
    override val clickToFetch = "Click to fetch"
    override val fetching = "Fetching..."
    override val requestPermissions = "Request Permissions (Camera, Gallery, Notification)"
    override val pickImage = "Pick Image from Gallery"
    override val sendLocalNotification = "Send Local Notification"
    override val goToNextScreen = "Go to Next Screen (Navigation)"
    override val requestingCamera = "Requesting Camera..."
    override val cameraGranted = "Camera Granted. Requesting Gallery..."
    override val requestingGallery = "Requesting Gallery..."
    override val galleryGranted = "Gallery Granted."
    override val requestingNotifications = "Requesting Notifications..."
    override val allPermissionsGranted = "All Permissions Granted!"
    override val detailScreen = "Detail Screen"
    override val back = "Back"
    override val faceVerify = "Face Verify"
}

object BnStrings : AppStrings {
    override val welcome = "স্বাগতম"
    override val loginContinue = "চালিয়ে যেতে লগইন করুন"
    override val home = "হোম"
    override val demo = "ডেমো"
    override val profile = "প্রোফাইল"
    override val username = "ব্যবহারকারীর নাম"
    override val password = "পাসওয়ার্ড"
    override val login = "লগইন"
    override val theme = "থিম"
    override val language = "ভাষা"
    override val light = "লাইট"
    override val dark = "ডার্ক"
    override val system = "সিস্টেম ডিফল্ট"
    override val kmpStackDemo = "KMP স্ট্যাক ডেমো"
    override val showCurrentTime = "বর্তমান সময় দেখুন (DateTime)"
    override val timeLabel = "সময়: "
    override val fetchFromKtor = "Ktor থেকে ডেটা আনুন (Serialization)"
    override val clickToFetch = "ক্লিক করুন"
    override val fetching = "আনা হচ্ছে..."
    override val requestPermissions = "অনুমতি চাও (ক্যামেরা, গ্যালারি, বিজ্ঞপ্তি)"
    override val pickImage = "গ্যালারি থেকে ছবি বেছে নিন"
    override val sendLocalNotification = "স্থানীয় বিজ্ঞপ্তি পাঠান"
    override val goToNextScreen = "পরবর্তী স্ক্রিনে যান (Navigation)"
    override val requestingCamera = "ক্যামেরা অনুমতি চাওয়া হচ্ছে..."
    override val cameraGranted = "ক্যামেরা অনুমোদিত। গ্যালারি অনুমতি চাওয়া হচ্ছে..."
    override val requestingGallery = "গ্যালারি অনুমতি চাওয়া হচ্ছে..."
    override val galleryGranted = "গ্যালারি অনুমোদিত।"
    override val requestingNotifications = "বিজ্ঞপ্তি অনুমতি চাওয়া হচ্ছে..."
    override val allPermissionsGranted = "সব অনুমতি মঞ্জুর হয়েছে!"
    override val detailScreen = "বিস্তারিত স্ক্রিন"
    override val back = "ফিরে যান"
    override val faceVerify = "ফেস ভেরিফাই"
}

object ArStrings : AppStrings {
    override val welcome = "مرحباً بك"
    override val loginContinue = "سجل الدخول للمتابعة"
    override val home = "الرئيسية"
    override val demo = "عرض"
    override val profile = "الملف الشخصي"
    override val username = "اسم المستخدم"
    override val password = "كلمة المرور"
    override val login = "تسجيل الدخول"
    override val theme = "المظهر"
    override val language = "اللغة"
    override val light = "فاتح"
    override val dark = "داكن"
    override val system = "تلقائي"
    override val kmpStackDemo = "عرض KMP التجريبي"
    override val showCurrentTime = "إظهار الوقت الحالي (DateTime)"
    override val timeLabel = "الوقت: "
    override val fetchFromKtor = "جلب البيانات من Ktor (Serialization)"
    override val clickToFetch = "انقر للجلب"
    override val fetching = "جارٍ الجلب..."
    override val requestPermissions = "طلب الأذونات (الكاميرا، المعرض، الإشعارات)"
    override val pickImage = "اختر صورة من المعرض"
    override val sendLocalNotification = "إرسال إشعار محلي"
    override val goToNextScreen = "الانتقال إلى الشاشة التالية (Navigation)"
    override val requestingCamera = "جارٍ طلب إذن الكاميرا..."
    override val cameraGranted = "تمت الموافقة على الكاميرا. جارٍ طلب إذن المعرض..."
    override val requestingGallery = "جارٍ طلب إذن المعرض..."
    override val galleryGranted = "تمت الموافقة على المعرض."
    override val requestingNotifications = "جارٍ طلب إذن الإشعارات..."
    override val allPermissionsGranted = "تمت الموافقة على جميع الأذونات!"
    override val detailScreen = "شاشة التفاصيل"
    override val back = "رجوع"
    override val faceVerify = "التحقق من الوجه"
}

val LocalAppStrings = compositionLocalOf<AppStrings> { EnStrings }
