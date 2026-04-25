package fd.firad.face.core.network

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.utils.io.errors.IOException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Centralized error handler for all network operations.
 */
class NetworkErrorHandler {

    /**
     * Maps an exception to a [ResponseData.Error].
     */
    suspend fun handleException(e: Throwable): ResponseData.Error {
        return when (e) {
            is HttpRequestTimeoutException -> ResponseData.Error(AppError.timeout, 408)
            is IOException -> ResponseData.Error(AppError.noInternet)
            is ClientRequestException -> handleResponseError(e.response)
            is ServerResponseException -> handleResponseError(e.response)
            else -> ResponseData.Error(
                AppError(
                    message = e.message ?: "An unexpected error occurred",
                    type = ErrorType.UNKNOWN,
                    technicalDetails = e.stackTraceToString()
                )
            )
        }
    }

    /**
     * Maps an HTTP response error to a [ResponseData.Error].
     */
    suspend fun handleResponseError(response: HttpResponse): ResponseData.Error {
        val statusCode = response.status.value
        val errorBody = try {
            response.bodyAsText()
        } catch (e: Exception) {
            null
        }

        val serverMessage = try {
            errorBody?.let {
                Json.parseToJsonElement(it).jsonObject["message"]?.jsonPrimitive?.content
            }
        } catch (e: Exception) {
            null
        }

        val appError = when (statusCode) {
            401 -> AppError.unauthorized
            403 -> AppError("Access denied. You don't have permission to perform this action.", ErrorType.UNAUTHORIZED)
            404 -> AppError("The requested resource was not found.", ErrorType.SERVER_ERROR)
            422 -> AppError(serverMessage ?: "Validation failed.", ErrorType.VALIDATION_ERROR)
            in 500..599 -> AppError.serverError
            else -> AppError(serverMessage ?: "Request failed with status: $statusCode", ErrorType.SERVER_ERROR)
        }

        return ResponseData.Error(appError, statusCode)
    }
}
