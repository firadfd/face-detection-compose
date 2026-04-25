package fd.firad.face.feature.verify.presentation

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fd.firad.face.feature.verify.domain.usecase.CalculateSimilarityUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FaceVerifyUiState(
    val image1: ByteArray? = null,
    val image2: ByteArray? = null,
    val imageBitmap1: ImageBitmap? = null,
    val imageBitmap2: ImageBitmap? = null,
    val similarity: Float? = null,
    val isVerifying: Boolean = false,
    val errorMessage: String? = null
)

class FaceVerifyViewModel(
    private val calculateSimilarityUseCase: CalculateSimilarityUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FaceVerifyUiState())
    val uiState = _uiState.asStateFlow()

    fun onImage1Selected(bytes: ByteArray, bitmap: ImageBitmap) {
        _uiState.update { it.copy(image1 = bytes, imageBitmap1 = bitmap, similarity = null) }
    }

    fun onImage2Selected(bytes: ByteArray, bitmap: ImageBitmap) {
        _uiState.update { it.copy(image2 = bytes, imageBitmap2 = bitmap, similarity = null) }
    }

    fun verify() {
        val state = _uiState.value
        if (state.image1 == null || state.image2 == null) return

        viewModelScope.launch {
            _uiState.update { it.copy(isVerifying = true, errorMessage = null) }
            try {
                val result = calculateSimilarityUseCase(state.image1, state.image2)
                _uiState.update { it.copy(isVerifying = false, similarity = result) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isVerifying = false, errorMessage = e.message) }
            }
        }
    }
    
    fun reset() {
        _uiState.value = FaceVerifyUiState()
    }
}
