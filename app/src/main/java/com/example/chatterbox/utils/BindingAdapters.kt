package com.example.chatterbox.utils

import android.view.View
import android.widget.EditText
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout

@BindingAdapter("app:Error")
fun setTextInputLayoutError(textInputLayout: TextInputLayout, errorMessage: String?) {
    textInputLayout.error = errorMessage
}

@BindingAdapter("app:clearFocusOnCondition")
fun clearFocusOnCondition(view: View, condition: Boolean) {
    if (condition) {
        view.clearFocus()
    }
}

@BindingAdapter("app:onFocusChange")
fun setOnFocusChangeListener(
    editText: EditText,
    onFocusChangeListener: View.OnFocusChangeListener?
) {
    editText.onFocusChangeListener = onFocusChangeListener
}