package fd.firad.face.core.util

expect class AppImage

expect fun ByteArray.toAppImage(): AppImage?

expect fun AppImage.resize(width: Int, height: Int): AppImage

expect fun AppImage.toNormalizedFloatArray(): FloatArray
