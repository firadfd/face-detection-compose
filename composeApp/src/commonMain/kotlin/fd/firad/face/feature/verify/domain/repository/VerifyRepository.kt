package fd.firad.face.feature.verify.domain.repository

interface VerifyRepository {
    suspend fun calculateSimilarity(image1: ByteArray, image2: ByteArray): Float
    suspend fun getEmbedding(image: ByteArray): FloatArray?
    fun compareEmbeddings(v1: FloatArray, v2: FloatArray): Float
}
