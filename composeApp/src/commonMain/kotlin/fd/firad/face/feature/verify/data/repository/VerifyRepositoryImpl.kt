package fd.firad.face.feature.verify.data.repository

import dev.kursor.ktensorflow.Interpreter
import dev.kursor.ktensorflow.InterpreterOptions
import dev.kursor.ktensorflow.ModelDesc
import dev.kursor.ktensorflow.compose.ComposeUri
import dev.kursor.ktensorflow.tensor.Tensor
import dev.kursor.ktensorflow.tensor.run
import dev.kursor.ktensorflow.tensor.toArray
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
    private var interpreter: Interpreter? = null

    @OptIn(ExperimentalResourceApi::class)
    private suspend fun getInterpreter(): Interpreter {
        return withContext(Dispatchers.Default) {
            if (interpreter == null) {
                val modelUri = Res.getUri("files/mobilefacenet.tflite")
                val modelDesc = ModelDesc.ComposeUri(modelUri)
                interpreter = Interpreter(
                    modelDesc,
                    options = InterpreterOptions()
                )
            }
            interpreter!!
        }
    }

    override suspend fun calculateSimilarity(image1: ByteArray, image2: ByteArray): Float {
        val interpreter = getInterpreter()
        
        val embedding1 = runInference(interpreter, image1) ?: return 0f
        val embedding2 = runInference(interpreter, image2) ?: return 0f
        
        return cosineSimilarity(embedding1, embedding2)
    }

    private suspend fun runInference(interpreter: Interpreter, imageBytes: ByteArray): FloatArray? {
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
            
            val inputTensor = Tensor<Float>(inputData)
            val outputData = Array(1) { FloatArray(192) }
            val outputTensor = Tensor<Float>(outputData)
            
            interpreter.run(inputTensor, outputTensor)
            
            val result = outputTensor.toArray<Array<FloatArray>>()
            result[0]
        }
    }

    private fun cosineSimilarity(v1: FloatArray, v2: FloatArray): Float {
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

