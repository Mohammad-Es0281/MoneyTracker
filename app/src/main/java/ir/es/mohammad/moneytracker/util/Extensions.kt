package ir.es.mohammad.moneytracker.util

import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import ir.es.mohammad.moneytracker.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

inline fun Fragment.launchAndRepeatWithViewLifecycle(
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline block: suspend CoroutineScope.() -> Unit
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
    crossinline onError: suspend (message: String?) -> Unit
) {
    collect { result ->
        when (result) {
            is Result.Loading -> onLoading(result.data)
            is Result.Success -> onSuccess(result.data!!)
            is Result.Error -> onError(result.message)
        }
    }
}

fun View.visible() { visibility = View.VISIBLE }
fun View.invisible() { visibility = View.INVISIBLE }
fun View.gone() { visibility = View.GONE }

fun View.setBackgroundTint(@ColorRes color: Int) {
    var buttonDrawable: Drawable = this.background
    buttonDrawable = DrawableCompat.wrap(buttonDrawable)
    DrawableCompat.setTint(buttonDrawable, color)
    this.background = buttonDrawable
}