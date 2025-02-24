package ru.yandex.praktikumchatapp.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.retryWhen
import java.util.concurrent.atomic.AtomicLong

class ChatRepository(
    private val api: ChatApi = ChatApi()
) {
    private var currentDelay = AtomicLong(200L)

    fun getReplyMessage(): Flow<String> {
        val maxRetries = 3
        return api.getReply().retryWhen { cause, attempt ->
            if (attempt >= maxRetries) {
                return@retryWhen false
            }
            if (cause is Exception) {
                if (attempt % 3 == 0L) {
                    currentDelay.set(200L)
                }
                delay(currentDelay.get())
                currentDelay.set(currentDelay.get() * DELAY_FACTOR)
            }
            true
        }
    }

    private companion object {
        private const val DELAY_FACTOR = 2L
    }
}
