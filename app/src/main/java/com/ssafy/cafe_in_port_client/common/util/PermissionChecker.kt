package com.ssafy.cafe_in_port_client.common.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import android.Manifest

// 권한이 모두 허용했을때 이동할 method주입을 위해서 만든 interface
fun interface OnGrantedListener {
    fun onGranted()
}

private const val TAG = "CheckPermission_싸피"
class PermissionChecker(
    private val context: Context,
    activityOrFragment: Any
) {
    private lateinit var permitted: OnGrantedListener

    fun setOnGrantedListener(listener: OnGrantedListener) {
        permitted = listener
    }

    // 권한 체크
    fun checkPermission(permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    // 권한 요청 결과를 처리할 Launcher
    val requestPermissionLauncher: ActivityResultLauncher<Array<String>> = when (activityOrFragment) {
        is AppCompatActivity -> {
            activityOrFragment.registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { result ->
                resultChecking(result)
            }
        }
        is Fragment -> {
            activityOrFragment.registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { result ->
                resultChecking(result)
            }
        }
        else -> {
            throw RuntimeException("Activity 혹은 Fragment에서 권한 설정이 가능합니다.")
        }
    }

    private fun resultChecking(result: Map<String, Boolean>) {
        Log.d(TAG, "requestPermissionLauncher: 건수: ${result.size}")

        if (result.values.contains(false)) { // 하나라도 false인 경우
            Toast.makeText(context, "권한이 부족합니다.", Toast.LENGTH_SHORT).show()
            moveToSettings()
        } else {
            Toast.makeText(context, "모든 권한이 허가되었습니다.", Toast.LENGTH_SHORT).show()
            permitted.onGranted()
        }
    }

    // 사용자가 권한을 허용하지 않았을 때, 설정 창으로 이동
    private fun moveToSettings() {
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle("권한이 필요합니다.")
        alertDialog.setMessage("설정으로 이동합니다.")
        alertDialog.setPositiveButton("확인") { dialogInterface, _ ->
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:${context.packageName}")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            dialogInterface.cancel()
        }
        alertDialog.setNegativeButton("취소") { dialogInterface, _ ->
            dialogInterface.cancel()
        }
        alertDialog.show()
    }

    companion object {
        val runtimePermissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }
}



