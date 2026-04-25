package fd.firad.face.core.network

import kotlinx.serialization.Serializable

@Serializable
sealed class ResponseData<out T> {
    data class Success<out T>(val data: T, val statusCode: Int) : ResponseData<T>()
    data class Error(val error: AppError, val statusCode: Int? = null) : ResponseData<Nothing>()
    data object Loading : ResponseData<Nothing>()
}

@Serializable
data class AppError(
    val message: String,
    val type: ErrorType,
    val technicalDetails: String? = null
) {
    companion object {
        val noInternet = AppError("No internet connection. Please check your network.", ErrorType.NO_INTERNET)
        val timeout = AppError("The request took too long. Please try again.", ErrorType.TIMEOUT)
        val unauthorized = AppError("Your session has expired. Please login again.", ErrorType.UNAUTHORIZED)
        val serverError = AppError("The server encountered an error. Please try again later.", ErrorType.SERVER_ERROR)
        val unknown = AppError("An unexpected error occurred.", ErrorType.UNKNOWN)

        fun fromStatusCode(statusCode: Int, message: String? = null): AppError {
            return when (statusCode) {
                401 -> unauthorized
                403 -> AppError("Access denied.", ErrorType.UNAUTHORIZED)
                408 -> timeout
                in 500..599 -> serverError
                else -> AppError(message ?: "Request failed with code: $statusCode", ErrorType.SERVER_ERROR)
            }
        }
    }
}

enum class ErrorType {
    NO_INTERNET,
    TIMEOUT,
    UNAUTHORIZED,
    SERVER_ERROR,
    UNKNOWN,
    VALIDATION_ERROR
}
