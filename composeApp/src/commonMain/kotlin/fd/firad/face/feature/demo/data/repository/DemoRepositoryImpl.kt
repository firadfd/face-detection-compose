package fd.firad.face.feature.demo.data.repository

import fd.firad.face.core.network.NetworkCaller
import fd.firad.face.core.network.ResponseData
import fd.firad.face.feature.demo.data.remote.DemoPostDto
import fd.firad.face.feature.demo.domain.entity.DemoPost
import fd.firad.face.feature.demo.domain.repository.DemoRepository

class DemoRepositoryImpl(
    private val networkCaller: NetworkCaller
) : DemoRepository {
    override suspend fun getPost(id: Int): ResponseData<DemoPost> {
        val response = networkCaller.get<DemoPostDto>("https://jsonplaceholder.typicode.com/posts/$id")
        
        return when (response) {
            is ResponseData.Success -> {
                val dto = response.data
                ResponseData.Success(
                    DemoPost(
                        userId = dto.userId,
                        id = dto.id,
                        title = dto.title,
                        body = dto.body
                    ),
                    response.statusCode
                )
            }
            is ResponseData.Error -> ResponseData.Error(response.error, response.statusCode)
            is ResponseData.Loading -> ResponseData.Loading
        }
    }
}
