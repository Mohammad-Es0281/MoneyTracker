package ir.es.mohammad.moneytracker.util

sealed class Result<out R>(
    val data: R? = null,
    val message: String? = null
) {
    val isSuccess get() = this is Success && data != null

    class Success<out T>(data: T) : Result<T>(data)
    class Loading<out T>(data: T? = null) : Result<T>(data)
    class Error<T>(throwable: Throwable, data: T? = null) : Result<T>(data, throwable.message)

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data = $data]"
            is Error -> "Error[exception = $message]"
            is Loading -> "Loading"
        }
    }
}