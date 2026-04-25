package fd.firad.face.feature.demo.presentation

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fd.firad.face.core.network.ResponseData
import fd.firad.face.feature.demo.domain.entity.DemoPost
import fd.firad.face.feature.demo.domain.usecase.GetDemoPostUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DemoUiState(
    val postResult: String = "",
    val isFetching: Boolean = false,
    val currentTime: String = "",
    val selectedImage: ImageBitmap? = null,
    val permissionStatus: String = ""
)

class DemoViewModel(
    private val getDemoPostUseCase: GetDemoPostUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DemoUiState())
    val uiState = _uiState.asStateFlow()

    fun fetchPost(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isFetching = true, postResult = "Fetching...") }
            when (val result = getDemoPostUseCase(id)) {
                is ResponseData.Success -> {
                    _uiState.update { 
                        it.copy(
                            isFetching = false, 
                            postResult = "Title: ${result.data.title}"
                        ) 
                    }
                }
                is ResponseData.Error -> {
                    _uiState.update { 
                        it.copy(
                            isFetching = false, 
                            postResult = "Error: ${result.error.message}"
                        ) 
                    }
                }
                else -> {}
            }
        }
    }

    fun updateTime(time: String) {
        _uiState.update { it.copy(currentTime = time) }
    }

    fun updateSelectedImage(image: ImageBitmap?) {
        _uiState.update { it.copy(selectedImage = image) }
    }

    fun updatePermissionStatus(status: String) {
        _uiState.update { it.copy(permissionStatus = status) }
    }
}
