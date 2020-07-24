package com.kaeonx.moneymanager.customclasses

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import androidx.appcompat.widget.AppCompatEditText

// Source: https://code.luasoftware.com/tutorials/android/edittext-clear-focus-on-keyboard-hidden/
class ClearFocusEditText: AppCompatEditText {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    // So that the cursor disappears when keyboard is dismissed
    override fun onKeyPreIme(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) clearFocus()
//        if (event!!.keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) clearFocus()
        return super.onKeyPreIme(keyCode, event)
    }
}
