package fd.firad.face.feature.verify.data.repository

import org.kmp.playground.kflite.Kflite
import org.kmp.playground.kflite.InterpreterOptions
import fd.firad.face.core.util.toAppImage
import fd.firad.face.core.util.resize
import fd.firad.face.core.util.toNormalizedFloatArray
import fd.firad.face.feature.verify.domain.repository.VerifyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.sqrt
import facedetection.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi

class VerifyRepositoryImpl : VerifyRepository {
    private var isInitialized = false

    @OptIn(ExperimentalResourceApi::class)
    private suspend fun initializeInterpreter() {
        if (!isInitialized) {
            val modelBytes = Res.readBytes("files/mobilefacenet.tflite")
            Kflite.init(
                model = modelBytes,
                options = InterpreterOptions()
            )
            isInitialized = true
        }
    }

    override suspend fun calculateSimilarity(image1: ByteArray, image2: ByteArray): Float {
        initializeInterpreter()
        
        val embedding1 = getEmbedding(image1) ?: return 0f
        val embedding2 = getEmbedding(image2) ?: return 0f
        
        return compareEmbeddings(embedding1, embedding2)
    }

    override suspend fun getEmbedding(image: ByteArray): FloatArray? {
        initializeInterpreter()
        return runInference(image)
    }

    private suspend fun runInference(imageBytes: ByteArray): FloatArray? {
        return withContext(Dispatchers.Default) {
            val appImage = imageBytes.toAppImage() ?: return@withContext null
            val resizedImage = appImage.resize(112, 112)
            val normalizedData = resizedImage.toNormalizedFloatArray()
            
            // MobileFaceNet expects [1, 112, 112, 3]
            val inputData = Array(1) {
                Array(112) { y ->
                    Array(112) { x ->
                        FloatArray(3) { c ->
                            normalizedData[(y * 112 + x) * 3 + c]
                        }
                    }
                }
            }
            
            val outputData = Array(1) { FloatArray(192) }
            
            Kflite.run(
                inputs = listOf(inputData),
                outputs = mapOf(0 to outputData)
            )
            
            outputData[0]
        }
    }

    override fun compareEmbeddings(v1: FloatArray, v2: FloatArray): Float {
        var dotProduct = 0.0f
        var norm1 = 0.0f
        var norm2 = 0.0f
        for (i in v1.indices) {
            dotProduct += v1[i] * v2[i]
            norm1 += v1[i] * v1[i]
            norm2 += v2[i] * v2[i]
        }
        val similarity = dotProduct / (sqrt(norm1) * sqrt(norm2))
        return similarity.coerceIn(0f, 1f)
    }
}

