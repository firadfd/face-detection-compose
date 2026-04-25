package fd.firad.face.feature.home.domain.usecase

import fd.firad.face.core.network.ResponseData
import fd.firad.face.feature.home.domain.entity.Employee
import fd.firad.face.feature.home.domain.repository.EmployeeRepository

class GetEmployeesUseCase(private val repository: EmployeeRepository) {
    suspend operator fun invoke(): ResponseData<List<Employee>> {
        return repository.getEmployees()
    }
}
