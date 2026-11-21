package com.samuelav.domain.model.utils.debug

import com.samuelav.domain.model.extensions.isNotNull
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
object AppLogger : Logger {

    private val unsentMessages = mutableListOf<MessageInfo>()

    var logger: Logger? = null
        set(value) {
            field = value
            if (value.isNotNull()) {
                unsentMessages.forEach { info ->
                    val newMessage = "Stacked message: [${info.time}] ${info.message}"
                    if (info.throwable.isNotNull()) {
                        when (info.type) {
                            MessageInfo.Type.VERBOSE -> value.v(info.throwable, newMessage)
                            MessageInfo.Type.DEBUG -> value.d(info.throwable, newMessage)
                            MessageInfo.Type.INFO -> value.i(info.throwable, newMessage)
                            MessageInfo.Type.WARNING -> value.w(info.throwable, newMessage)
                            MessageInfo.Type.ERROR -> value.e(info.throwable, newMessage)
                        }
                    } else {
                        when (info.type) {
                            MessageInfo.Type.VERBOSE -> value.v(newMessage)
                            MessageInfo.Type.DEBUG -> value.d(newMessage)
                            MessageInfo.Type.INFO -> value.i(newMessage)
                            MessageInfo.Type.WARNING -> value.w(newMessage)
                            MessageInfo.Type.ERROR -> value.e(newMessage)
                        }
                    }
                }
                unsentMessages.clear()
            }
        }

    override fun v(message: String) {
        logger?.v(message)
            ?: run {
                unsentMessages.add(
                    MessageInfo(
                        type = MessageInfo.Type.VERBOSE,
                        time = Clock.System.now(),
                        message = message,
                    )
                )
            }
    }

    override fun v(t: Throwable, message: String) {
        logger?.v(t, message)
            ?: run {
                unsentMessages.add(
                    MessageInfo(
                        type = MessageInfo.Type.VERBOSE,
                        time = Clock.System.now(),
                        message = message,
                        throwable = t,
                    )
                )
            }
    }

    override fun v(t: Throwable) {
        logger?.v(t)
            ?: run {
                unsentMessages.add(
                    MessageInfo(
                        type = MessageInfo.Type.VERBOSE,
                        time = Clock.System.now(),
                        throwable = t,
                    )
                )
            }
    }

    override fun d(message: String) {
        logger?.d(message)
            ?: run {
                unsentMessages.add(
                    MessageInfo(
                        type = MessageInfo.Type.DEBUG,
                        time = Clock.System.now(),
                        message = message,
                    )
                )
            }
    }

    override fun d(t: Throwable, message: String) {
        logger?.d(t, message)
            ?: run {
                unsentMessages.add(
                    MessageInfo(
                        type = MessageInfo.Type.DEBUG,
                        time = Clock.System.now(),
                        message = message,
                        throwable = t,
                    )
                )
            }
    }

    override fun d(t: Throwable) {
        logger?.d(t)
            ?: run {
                unsentMessages.add(
                    MessageInfo(
                        type = MessageInfo.Type.DEBUG,
                        time = Clock.System.now(),
                        throwable = t,
                    )
                )
            }
    }

    override fun i(message: String) {
        logger?.i(message)
            ?: run {
                unsentMessages.add(
                    MessageInfo(
                        type = MessageInfo.Type.INFO,
                        time = Clock.System.now(),
                        message = message,
                    )
                )
            }
    }

    override fun i(t: Throwable, message: String) {
        logger?.i(t, message)
            ?: run {
                unsentMessages.add(
                    MessageInfo(
                        type = MessageInfo.Type.INFO,
                        time = Clock.System.now(),
                        message = message,
                        throwable = t,
                    )
                )
            }
    }

    override fun i(t: Throwable) {
        logger?.i(t)
            ?: run {
                unsentMessages.add(
                    MessageInfo(
                        type = MessageInfo.Type.INFO,
                        time = Clock.System.now(),
                        throwable = t,
                    )
                )
            }
    }

    override fun w(message: String) {
        logger?.w(message)
            ?: run {
                unsentMessages.add(
                    MessageInfo(
                        type = MessageInfo.Type.WARNING,
                        time = Clock.System.now(),
                        message = message,
                    )
                )
            }
    }

    override fun w(t: Throwable?, message: String) {
        logger?.w(t, message)
            ?: run {
                unsentMessages.add(
                    MessageInfo(
                        type = MessageInfo.Type.WARNING,
                        time = Clock.System.now(),
                        message = message,
                        throwable = t,
                    )
                )
            }
    }

    override fun w(t: Throwable?) {
        logger?.w(t)
            ?: run {
                unsentMessages.add(
                    MessageInfo(
                        type = MessageInfo.Type.WARNING,
                        time = Clock.System.now(),
                        throwable = t,
                    )
                )
            }
    }

    override fun e(message: String) {
        logger?.e(message)
            ?: run {
                unsentMessages.add(
                    MessageInfo(
                        type = MessageInfo.Type.ERROR,
                        time = Clock.System.now(),
                        message = message,
                    )
                )
            }
    }

    override fun e(t: Throwable?, message: String) {
        logger?.e(t, message)
            ?: run {
                unsentMessages.add(
                    MessageInfo(
                        type = MessageInfo.Type.ERROR,
                        time = Clock.System.now(),
                        message = message,
                        throwable = t,
                    )
                )
            }
    }

    override fun e(t: Throwable?) {
        logger?.e(t)
            ?: run {
                unsentMessages.add(
                    MessageInfo(
                        type = MessageInfo.Type.ERROR,
                        time = Clock.System.now(),
                        throwable = t,
                    )
                )
            }
    }
}

@OptIn(ExperimentalTime::class)
private data class MessageInfo(
    val type: Type,
    val time: Instant,
    val message: String? = null,
    val throwable: Throwable? = null,
) {
    enum class Type {
        VERBOSE,
        DEBUG,
        INFO,
        WARNING,
        ERROR,
    }
}
