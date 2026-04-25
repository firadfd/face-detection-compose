package fd.firad.face.feature.demo.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class DemoPost(
    val userId: Int,
    val id: Int,
    val title: String,
    val body: String
)
