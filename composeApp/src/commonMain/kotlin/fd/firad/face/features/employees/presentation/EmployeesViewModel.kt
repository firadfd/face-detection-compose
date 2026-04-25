package fd.firad.face.features.employees.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fd.firad.face.core.network.ResponseData
import fd.firad.face.features.employees.domain.entity.Employee
import fd.firad.face.features.employees.domain.usecase.GetEmployeesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class EmployeesUiState {
    data object Idle : EmployeesUiState()
    data object Loading : EmployeesUiState()
    data class Success(val employees: List<Employee>) : EmployeesUiState()
    data class Error(val message: String) : EmployeesUiState()
}

class EmployeesViewModel(
    private val getEmployeesUseCase: GetEmployeesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<EmployeesUiState>(EmployeesUiState.Idle)
    val uiState = _uiState.asStateFlow()

    init {
        loadEmployees()
    }

    fun loadEmployees() {
        viewModelScope.launch {
            _uiState.value = EmployeesUiState.Loading
            when (val result = getEmployeesUseCase()) {
                is ResponseData.Success -> {
                    _uiState.value = EmployeesUiState.Success(result.data)
                }
                is ResponseData.Error -> {
                    _uiState.value = EmployeesUiState.Error(result.error.message)
                }
                is ResponseData.Loading -> {
                    _uiState.value = EmployeesUiState.Loading
                }
            }
        }
    }
}
