package bd.com.albin.chatbot.data.service

interface LogService {
    fun logNonFatalCrash(throwable: Throwable)
}