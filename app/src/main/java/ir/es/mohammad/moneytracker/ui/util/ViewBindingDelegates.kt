/*
 * Copyright (C) 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ir.es.mohammad.moneytracker.ui.util

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.viewbinding.ViewBinding


abstract class ViewBindingLazy<T : ViewBinding>(
    private val fragment: Fragment
) : Lazy<T> {
    private var cached: T? = null

    private val observer = LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_DESTROY) {
            cached = null
        }
    }

    override val value: T
        get() = if (isInitialized()) cached!!
        else bindView().also {
            fragment.viewLifecycleOwner.lifecycle.addObserver(observer)
            cached = it
        }

    override fun isInitialized() = cached != null

    abstract fun bindView(): T
}

inline fun <reified BindingT : ViewBinding> Fragment.viewBinding(
    crossinline bind: (View) -> BindingT
) = object : ViewBindingLazy<BindingT>(this) {

    override fun bindView(): BindingT {
        return bind(requireView())
    }
}
