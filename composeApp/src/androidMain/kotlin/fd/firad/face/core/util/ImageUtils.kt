package fd.firad.face.core.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

actual typealias AppImage = Bitmap

actual fun ByteArray.toAppImage(): AppImage? {
    return BitmapFactory.decodeByteArray(this, 0, this.size)
}

actual fun AppImage.resize(width: Int, height: Int): AppImage {
    return Bitmap.createScaledBitmap(this, width, height, true)
}

actual fun AppImage.toNormalizedFloatArray(): FloatArray {
    val pixels = IntArray(width * height)
    getPixels(pixels, 0, width, 0, 0, width, height)
    val floatArray = FloatArray(width * height * 3)
    for (i in pixels.indices) {
        val pixel = pixels[i]
        floatArray[i * 3] = ((pixel shr 16 and 0xFF) - 127.5f) / 128f
        floatArray[i * 3 + 1] = ((pixel shr 8 and 0xFF) - 127.5f) / 128f
        floatArray[i * 3 + 2] = ((pixel and 0xFF) - 127.5f) / 128f
    }
    return floatArray
}
