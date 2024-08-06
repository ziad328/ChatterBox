package com.example.chatterbox.utils

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

fun View.hideKeyboard(activity: AppCompatActivity?) {
    val inputMethodManager =
        activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
}


fun Fragment.showAlertDialog(
    message: String,
    posActionName: String? = null,
    posAction: DialogInterface.OnClickListener? = null,
    negActionName: String? = null,
    negAction: DialogInterface.OnClickListener? = null
): AlertDialog {

    val alertDialogBuilder = AlertDialog.Builder(requireContext())
    alertDialogBuilder.setMessage(message)
    alertDialogBuilder.setPositiveButton(posActionName, posAction)
    alertDialogBuilder.setNegativeButton(negActionName, negAction)
    return alertDialogBuilder.show()
}

fun Activity.showAlertDialog(
    message: String,
    posActionName: String? = null,
    posAction: OnDialogActionClick? = null,
    negActionName: String? = null,
    negAction: OnDialogActionClick? = null,
    isCancellable: Boolean = true
): AlertDialog {

    val alertDialogBuilder = AlertDialog.Builder(this)
    alertDialogBuilder.setMessage(message)
    alertDialogBuilder.setPositiveButton(posActionName) { dialog, i ->
        dialog.dismiss()
        posAction?.onActionClick()
    }
    alertDialogBuilder.setNegativeButton(negActionName) { dialog, i ->
        dialog.dismiss()
        negAction?.onActionClick()
    }
    alertDialogBuilder.setCancelable(isCancellable)
    return alertDialogBuilder.show()
}


