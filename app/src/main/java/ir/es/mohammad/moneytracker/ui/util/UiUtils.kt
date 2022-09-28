package ir.es.mohammad.moneytracker.ui

import android.content.Context
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import ir.es.mohammad.moneytracker.R
import ir.es.mohammad.moneytracker.util.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

inline fun Fragment.launchAndRepeatWithViewLifecycle(
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline block: suspend CoroutineScope.() -> Unit,
) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.lifecycle.repeatOnLifecycle(minActiveState) {
            block()
        }
    }
}

suspend inline fun <T> Flow<Result<T>>.collectResult(
    crossinline onLoading: suspend ((data: T?) -> Unit),
    crossinline onSuccess: suspend ((data: T) -> Unit),
    crossinline onError: suspend (message: String?) -> Unit,
) {
    collect { result ->
        when (result) {
            is Result.Loading -> onLoading(result.data)
            is Result.Success -> onSuccess(result.data!!)
            is Result.Error -> onError(result.message)
        }
    }
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}


fun showError(view: View, errorMessage: String?, action: View.OnClickListener) {
    val context = view.context
    val message = errorMessage ?: context.getString(R.string.unknown_error)
    val actionMessage = context.getString(R.string.try_again)
    Snackbar.make(view, message, Snackbar.LENGTH_LONG).setAction(actionMessage, action).show()
}

private val sdf = SimpleDateFormat("yyyy/MM/dd")
fun Long.toFormattedDate(): String = sdf.format(this)

fun View.setBackgroundTint(appContext: Context, @ColorRes color: Int) {
    val backgroundTintColor = ContextCompat.getColorStateList(appContext, color)
    this.backgroundTintList = backgroundTintColor
}