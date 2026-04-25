package fd.firad.face.core.util

import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.Foundation.NSData
import platform.Foundation.create
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.CoreGraphics.*
import platform.UIKit.*
import platform.posix.*

actual typealias AppImage = UIImage

actual fun ByteArray.toAppImage(): AppImage? {
    val data = this.usePinned { pinned ->
        NSData.create(bytes = pinned.addressOf(0), length = this.size.toULong())
    }
    return UIImage.imageWithData(data)
}

actual fun AppImage.resize(width: Int, height: Int): AppImage {
    val size = CGSizeMake(width.toDouble(), height.toDouble())
    UIGraphicsBeginImageContextWithOptions(size, false, 1.0)
    this.drawInRect(CGRectMake(0.0, 0.0, width.toDouble(), height.toDouble()))
    val resizedImage = UIGraphicsGetImageFromCurrentImageContext()
    UIGraphicsEndImageContext()
    return resizedImage!!
}

actual fun AppImage.toNormalizedFloatArray(): FloatArray {
    // Basic implementation for iOS (can be optimized)
    // MobileFaceNet expects 112x112 RGB
    val width = 112
    val height = 112
    val floatArray = FloatArray(width * height * 3)
    
    // In a real app, you'd use Cinterop to read CGImage data directly
    // For now, this is a placeholder for the logic
    return floatArray
}
