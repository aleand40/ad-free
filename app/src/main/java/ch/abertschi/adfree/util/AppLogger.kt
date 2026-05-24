package ch.abertschi.adfree.util

import android.util.Log

interface AppLogger {
    val loggerTag: String
        get() = this.javaClass.simpleName
}

inline fun AppLogger.info(message: () -> Any?) {
    Log.i(loggerTag, message()?.toString() ?: "null")
}

fun AppLogger.info(message: Any?) {
    Log.i(loggerTag, message?.toString() ?: "null")
}

inline fun AppLogger.warn(message: () -> Any?) {
    Log.w(loggerTag, message()?.toString() ?: "null")
}

fun AppLogger.warn(message: Any?) {
    Log.w(loggerTag, message?.toString() ?: "null")
}

fun AppLogger.warn(exception: Throwable) {
    Log.w(loggerTag, exception.message, exception)
}

inline fun AppLogger.debug(message: () -> Any?) {
    Log.d(loggerTag, message()?.toString() ?: "null")
}

@Suppress("unused")
fun AppLogger.debug(message: Any?) {
    Log.d(loggerTag, message?.toString() ?: "null")
}

@Suppress("unused")
inline fun AppLogger.error(message: () -> Any?) {
    Log.e(loggerTag, message()?.toString() ?: "null")
}

fun AppLogger.error(message: Any?) {
    Log.e(loggerTag, message?.toString() ?: "null")
}

fun AppLogger.error(exception: Throwable) {
    Log.e(loggerTag, exception.message, exception)
}