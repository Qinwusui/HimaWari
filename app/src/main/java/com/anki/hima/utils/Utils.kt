package com.anki.hima.utils

import android.util.Log
import android.widget.Toast
import com.anki.hima.app.HimaApplication

fun String.toastShort() =
    Toast.makeText(HimaApplication.context, this, Toast.LENGTH_SHORT).show()

fun String.loge() = Log.e(HimaApplication.context.packageName, this)