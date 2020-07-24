package com.kaeonx.moneymanager.customclasses

import android.view.View
import com.google.android.material.snackbar.BaseTransientBottomBar

class NoSwipeBehaviour : BaseTransientBottomBar.Behavior() {
    override fun canSwipeDismissView(child: View): Boolean {
        return false
    }
}