package fd.firad.face.feature.demo.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class DemoPostDto(
    val userId: Int,
    val id: Int,
    val title: String,
    val body: String
)
