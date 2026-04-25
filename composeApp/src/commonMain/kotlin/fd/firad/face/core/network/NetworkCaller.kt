package fd.firad.face.core.network

import fd.firad.face.core.storage.StorageService
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.header
import io.ktor.client.request.prepareRequest
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * A senior-level network service handling Ktor interactions.
 * Features: Generic safe requests, automatic auth header injection, error mapping, and multipart support.
 */
class NetworkCaller(
    val client: HttpClient,
    val storage: StorageService,
    val errorHandler: NetworkErrorHandler
) {
    /**
     * Executes a generic network request safely.
     * @param T The expected response type (must be @Serializable)
     */
    suspend inline fun <reified T> safeRequest(
        url: String,
        method: HttpMethod = HttpMethod.Get,
        body: Any? = null,
        isAuthCall: Boolean = false,
        headers: Map<String, String> = emptyMap()
    ): ResponseData<T> {
        return try {
            val response = client.prepareRequest(url) {
                this.method = method
                
                // Add default headers
                val token = storage.getAccessToken()
                if (!isAuthCall && token != null) {
                    header(HttpHeaders.Authorization, "Bearer $token")
                }
                
                // Add custom headers
                headers.forEach { (key, value) -> header(key, value) }

                if (body != null) {
                    setBody(body)
                }
            }.execute()

            handleResponse<T>(response)
        } catch (e: Exception) {
            errorHandler.handleException(e)
        }
    }

    /**
     * Handles the HTTP response and maps it to [ResponseData].
     */
    suspend inline fun <reified T> handleResponse(response: HttpResponse): ResponseData<T> {
        val statusCode = response.status.value
        
        return if (response.status.isSuccess()) {
            try {
                val data = response.body<T>()
                ResponseData.Success(data, statusCode)
            } catch (e: Exception) {
                ResponseData.Error(
                    AppError("Failed to parse response", ErrorType.SERVER_ERROR, e.message),
                    statusCode
                )
            }
        } else {
            errorHandler.handleResponseError(response)
        }
    }

    /**
     * Specifically for uploading images (Face Detection use case).
     */
    suspend inline fun <reified T> uploadImage(
        url: String,
        imageBytes: ByteArray,
        fileName: String,
        fields: Map<String, String> = emptyMap()
    ): ResponseData<T> {
        return try {
            val response = client.submitFormWithBinaryData(
                url = url,
                formData = formData {
                    fields.forEach { (key, value) -> append(key, value) }
                    append("image", imageBytes, Headers.build {
                        append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                        append(HttpHeaders.ContentType, "image/jpeg")
                    })
                }
            ) {
                val token = storage.getAccessToken()
                if (token != null) {
                    header(HttpHeaders.Authorization, "Bearer $token")
                }
            }
            handleResponse<T>(response)
        } catch (e: Exception) {
            errorHandler.handleException(e)
        }
    }

    // Convenience methods
    suspend inline fun <reified T> get(url: String) = safeRequest<T>(url, HttpMethod.Get)
    
    suspend inline fun <reified T> post(url: String, body: Any?) = safeRequest<T>(url, HttpMethod.Post, body)
    
    suspend inline fun <reified T> put(url: String, body: Any?) = safeRequest<T>(url, HttpMethod.Put, body)
    
    suspend inline fun <reified T> delete(url: String) = safeRequest<T>(url, HttpMethod.Delete)
}
