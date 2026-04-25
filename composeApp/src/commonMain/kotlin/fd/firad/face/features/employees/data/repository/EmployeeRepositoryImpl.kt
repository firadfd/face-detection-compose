package fd.firad.face.features.employees.data.repository

import fd.firad.face.core.network.ApiEndpoints
import fd.firad.face.core.network.NetworkCaller
import fd.firad.face.core.network.ResponseData
import fd.firad.face.features.employees.data.remote.EmployeeListResponse
import fd.firad.face.features.employees.domain.entity.Employee
import fd.firad.face.features.employees.domain.repository.EmployeeRepository

class EmployeeRepositoryImpl(
    private val networkCaller: NetworkCaller
) : EmployeeRepository {

    override suspend fun getEmployees(): ResponseData<List<Employee>> {
        val response = networkCaller.get<EmployeeListResponse>(ApiEndpoints.LIST_EMPLOYEES)
        
        return when (response) {
            is ResponseData.Success -> {
                val employees = response.data.data.map { dto ->
                    Employee(
                        employeeId = dto.employeeId,
                        name = dto.name,
                        email = dto.email,
                        bloodGroup = dto.bloodGroup,
                        role = dto.role
                    )
                }
                ResponseData.Success(employees, response.statusCode)
            }
            is ResponseData.Error -> ResponseData.Error(response.error, response.statusCode)
            is ResponseData.Loading -> ResponseData.Loading
        }
    }
}
