package ru.yandex.praktikumchatapp.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.retryWhen

class ChatRepository(
    private val api: ChatApi = ChatApi()
) {
    private var currentDelay = 200L

    fun getReplyMessage(): Flow<String> {
        return api.getReply().retryWhen { cause, attempt ->
            if (cause is Exception) {
                if (attempt % 3 == 0L) {
                    currentDelay = 200L
                }
                delay(currentDelay)
                currentDelay *= DELAY_FACTOR
            }
            true
        }
    }

    private companion object {
        private const val DELAY_FACTOR = 2L
    }
}
