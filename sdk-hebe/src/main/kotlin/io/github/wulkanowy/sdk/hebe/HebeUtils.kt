package io.github.wulkanowy.sdk.hebe

internal fun <T> ApiResponse<T>.getEnvelopeOrThrowError(): T? {
    if (envelope == null) {
        when (status.code) {
            // 100 -> "Użytkownik nie jest uprawniony do przeglądania żądanych danych"
            // 101 -> "Żądanie nie zostało prawidłowo związane z oczekiwanym typem"
            // 102 -> "Brakuje nagłówka vOS"
            // todo: add more codes
            100 -> error("100: User is not privileged to browse any data.")
            101 -> error("101: The request was not properly tied to the expected type.")
            102 -> error("102: The vOS header is missing.")
            else -> error("Unknown error: ${status.message}")
        }
    }
    return envelope
}
