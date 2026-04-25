package fd.firad.face.feature.home.domain.repository

import fd.firad.face.core.network.ResponseData
import fd.firad.face.feature.home.domain.entity.Employee

interface EmployeeRepository {
    suspend fun getEmployees(): ResponseData<List<Employee>>
}
