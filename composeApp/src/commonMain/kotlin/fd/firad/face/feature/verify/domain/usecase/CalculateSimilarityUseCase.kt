package fd.firad.face.feature.verify.domain.usecase

import fd.firad.face.feature.verify.domain.repository.VerifyRepository

class CalculateSimilarityUseCase(
    private val repository: VerifyRepository
) {
    suspend operator fun invoke(image1: ByteArray, image2: ByteArray): Float {
        return repository.calculateSimilarity(image1, image2)
    }
}
