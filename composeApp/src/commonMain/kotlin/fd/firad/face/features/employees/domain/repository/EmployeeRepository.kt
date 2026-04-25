package fd.firad.face.features.employees.domain.repository

import fd.firad.face.core.network.ResponseData
import fd.firad.face.features.employees.domain.entity.Employee

interface EmployeeRepository {
    suspend fun getEmployees(): ResponseData<List<Employee>>
}
