package fd.firad.face.feature.liveverify.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import fd.firad.face.feature.verify.domain.repository.VerifyRepository
import fd.firad.face.feature.verify.domain.usecase.GetEmbeddingUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

data class LiveVerifyUiState(
    val isScanning: Boolean = false,
    val savedEmbedding: FloatArray? = null,
    val currentEmbedding: FloatArray? = null,
    val similarity: Float? = null,
    val message: String? = null,
    val isSuccess: Boolean? = null,
    val step: LiveVerifyStep = LiveVerifyStep.INITIAL
)

enum class LiveVerifyStep {
    INITIAL, SCANNING_TO_SAVE, SCANNING_TO_VERIFY, COMPLETED
}

class LiveVerifyViewModel(
    private val getEmbeddingUseCase: GetEmbeddingUseCase,
    private val verifyRepository: VerifyRepository,
    private val settings: Settings
) : ViewModel() {

    private val _uiState = MutableStateFlow(LiveVerifyUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadSavedEmbedding()
    }

    private fun loadSavedEmbedding() {
        val saved = settings.getStringOrNull("saved_face_embedding")
        if (saved != null) {
            try {
                val embedding = Json.decodeFromString<FloatArray>(saved)
                _uiState.update { it.copy(savedEmbedding = embedding) }
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    fun startScanning() {
        val step = if (_uiState.value.savedEmbedding == null) {
            LiveVerifyStep.SCANNING_TO_SAVE
        } else {
            LiveVerifyStep.SCANNING_TO_VERIFY
        }
        _uiState.update { it.copy(step = step, message = null, similarity = null, isSuccess = null) }
    }

    fun onImageCaptured(bytes: ByteArray) {
        viewModelScope.launch {
            _uiState.update { it.copy(isScanning = true) }
            val embedding = getEmbeddingUseCase(bytes)
            if (embedding != null) {
                if (_uiState.value.step == LiveVerifyStep.SCANNING_TO_SAVE) {
                    saveEmbedding(embedding)
                } else if (_uiState.value.step == LiveVerifyStep.SCANNING_TO_VERIFY) {
                    verifyEmbedding(embedding)
                }
            } else {
                _uiState.update { it.copy(isScanning = false, message = "No face detected. Please try again.", step = LiveVerifyStep.INITIAL) }
            }
        }
    }

    private fun saveEmbedding(embedding: FloatArray) {
        val json = Json.encodeToString(embedding)
        settings["saved_face_embedding"] = json
        _uiState.update { 
            it.copy(
                savedEmbedding = embedding, 
                isScanning = false, 
                message = "Face data saved successfully!", 
                step = LiveVerifyStep.INITIAL,
                isSuccess = true
            ) 
        }
    }

    private fun verifyEmbedding(embedding: FloatArray) {
        val saved = _uiState.value.savedEmbedding ?: return
        val similarity = verifyRepository.compareEmbeddings(saved, embedding)
        val isMatch = similarity > 0.8f
        _uiState.update { 
            it.copy(
                isScanning = false, 
                similarity = similarity, 
                isSuccess = isMatch,
                message = if (isMatch) "Identity Verified!" else "Verification Failed",
                step = LiveVerifyStep.COMPLETED
            ) 
        }
    }

    fun reset() {
        settings.remove("saved_face_embedding")
        _uiState.update { LiveVerifyUiState() }
    }
    
    fun clearResult() {
        _uiState.update { it.copy(step = LiveVerifyStep.INITIAL, message = null, similarity = null, isSuccess = null) }
    }
}
