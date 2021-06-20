package com.sudodevoss.artboard.utils

import android.R
import android.app.Activity
import com.google.android.material.snackbar.Snackbar


class SnackBarUtils {
    private constructor()

    private var mSnackBar: Snackbar? = null

    companion object {
        private var mInstance: SnackBarUtils? = null
        val instance: SnackBarUtils
            get() {
                if (mInstance == null) {
                    mInstance = SnackBarUtils()
                }
                return mInstance!!
            }
    }

    fun dismiss() {
        if (mSnackBar != null) {
            mSnackBar!!.dismiss()
        }
    }

    fun show(activity: Activity, message: String?) {
        mSnackBar = Snackbar.make(
            activity.findViewById(R.id.content),
            message!!, Snackbar.LENGTH_INDEFINITE
        )
        mSnackBar!!.show()
    }
}