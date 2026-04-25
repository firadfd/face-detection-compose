package fd.firad.face.feature.demo.domain.usecase

import fd.firad.face.core.network.ResponseData
import fd.firad.face.feature.demo.domain.entity.DemoPost
import fd.firad.face.feature.demo.domain.repository.DemoRepository

class GetDemoPostUseCase(
    private val repository: DemoRepository
) {
    suspend operator fun invoke(id: Int): ResponseData<DemoPost> {
        return repository.getPost(id)
    }
}
