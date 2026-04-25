package fd.firad.face.features.employees.domain.usecase

import fd.firad.face.core.network.ResponseData
import fd.firad.face.features.employees.domain.entity.Employee
import fd.firad.face.features.employees.domain.repository.EmployeeRepository

class GetEmployeesUseCase(private val repository: EmployeeRepository) {
    suspend operator fun invoke(): ResponseData<List<Employee>> {
        return repository.getEmployees()
    }
}
