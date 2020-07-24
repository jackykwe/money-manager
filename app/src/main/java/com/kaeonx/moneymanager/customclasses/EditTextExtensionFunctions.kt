package com.kaeonx.moneymanager.customclasses

import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText

fun EditText.fixCursorFocusProblems() {
    setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            clearFocus()
        }
        return@setOnEditorActionListener false
    }
    setOnKeyListener { _, keyCode, _ ->
        return@setOnKeyListener when (keyCode) {
            KeyEvent.KEYCODE_NAVIGATE_OUT -> {
                clearFocus()
                true
            }
            else -> true
        }
    }
}