package com.samuelav.app.utils.debug

import com.samuelav.domain.model.utils.debug.Logger
import co.touchlab.kermit.Logger as Kermit

class KermitLogger(private val kermit: Kermit = Kermit) : Logger {
    override fun v(message: String) = kermit.v(message)

    override fun v(t: Throwable, message: String) = kermit.v(message, t)

    override fun v(t: Throwable) = kermit.v(t.message.orEmpty(), t)

    override fun d(message: String) = kermit.d(message)

    override fun d(t: Throwable, message: String) = kermit.d(message, t)

    override fun d(t: Throwable) = kermit.d(t.message.orEmpty(), t)

    override fun i(message: String) = kermit.i(message)

    override fun i(t: Throwable, message: String) = kermit.i(message, t)

    override fun i(t: Throwable) = kermit.i(t.message.orEmpty(), t)

    override fun w(message: String) = kermit.w(message)

    override fun w(t: Throwable?, message: String) = kermit.w(message, t)

    override fun w(t: Throwable?) = kermit.w(t?.message.orEmpty(), t)

    override fun e(message: String) = kermit.e(message)

    override fun e(t: Throwable?, message: String) = kermit.e(message, t)

    override fun e(t: Throwable?) = kermit.e(t?.message.orEmpty(), t)
}
