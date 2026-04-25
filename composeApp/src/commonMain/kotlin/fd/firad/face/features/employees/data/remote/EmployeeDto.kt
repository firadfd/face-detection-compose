package fd.firad.face.features.employees.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class EmployeeListResponse(
    val status: String,
    val message: String,
    val data: List<EmployeeDto>
)

@Serializable
data class EmployeeDto(
    val id: String,
    val uuid: String,
    val employeeId: String,
    val name: String,
    val email: String,
    val role: String,
    val bloodGroup: String,
    val embedding: List<Double>,
    val registeredAt: String
)
