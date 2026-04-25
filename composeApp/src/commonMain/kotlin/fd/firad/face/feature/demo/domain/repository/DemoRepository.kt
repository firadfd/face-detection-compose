package fd.firad.face.feature.demo.domain.repository

import fd.firad.face.core.network.ResponseData
import fd.firad.face.feature.demo.domain.entity.DemoPost

interface DemoRepository {
    suspend fun getPost(id: Int): ResponseData<DemoPost>
}
