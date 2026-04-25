package fd.firad.face.feature.verify.domain.repository

interface VerifyRepository {
    suspend fun calculateSimilarity(image1: ByteArray, image2: ByteArray): Float
}
