package com.example.notesapp.extensions

import android.content.Context
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity

fun AppCompatActivity.hideKeyboard() {
    val view = this.currentFocus
    if (view != null) {
        val im = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        im.hideSoftInputFromWindow(view.windowToken, 0)
    }

    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
}