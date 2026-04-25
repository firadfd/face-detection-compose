package fd.firad.face.feature.verify.domain.usecase

import fd.firad.face.feature.verify.domain.repository.VerifyRepository

class GetEmbeddingUseCase(
    private val repository: VerifyRepository
) {
    suspend operator fun invoke(image: ByteArray): FloatArray? {
        return repository.getEmbedding(image)
    }
}
