package ch.abertschi.adfree.util

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment


fun Context.toast(message: CharSequence) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
@Suppress("unused")
fun Fragment.toast(message: CharSequence) = requireContext().toast(message)

fun Context.toast(resourceId: Int) = Toast.makeText(this, resourceId, Toast.LENGTH_SHORT).show()
@Suppress("unused")
fun Fragment.toast(resourceId: Int) = requireContext().toast(resourceId)

fun Context.longToast(message: CharSequence) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()
@Suppress("unused")
fun Context.longToast(resourceId: Int) = Toast.makeText(this, resourceId, Toast.LENGTH_LONG).show()