package com.example.whahu_library_manager

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException


class SplashActivity : AppCompatActivity() {

    // 태그
    private val TAG = "SplashActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Log.d(TAG, "================== APP START ==================")

        // firebase 접근 권한 갖기
        FirebaseApp.initializeApp(this@SplashActivity)

        connectToDatabase()

        permissionCheck()
    }

    private fun connectToDatabase(): Connection? {
        val url = "jdbc:mysql://localhost:3306/kotlindb"
        val address = "address"
        val state = "state"

        return try {
            DriverManager.getConnection(url, address, state)
        } catch (e: SQLException) {
            null
        }
    }

    private fun permissionCheck() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
        ) {
            Toast.makeText(this, "권한이 필요합니다", Toast.LENGTH_SHORT).show()
        } else {
            multiPermissionResultLauncher.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_PHONE_NUMBERS,
                    Manifest.permission.SEND_SMS
                )
            )
        }
    }

    private val multiPermissionResultLauncher : ActivityResultLauncher<Array<String>> =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                // map으로 받아온 권한을 key와 value로 구분하여 요청 결과값을 받아옴
                val permissionName = it.key  // 권한 이름
                val isGranted = it.value     // 권한 요청 결과값
                if (isGranted) {
                    if (permissionName == Manifest.permission.CAMERA) {
                        // 카메라 권한 승인
                    } else if (permissionName == Manifest.permission.READ_PHONE_NUMBERS) {
                        // 휴대폰 번호 권한 승인
                    } else {
                        // 메시지 권한 승인

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    if (permissionName == Manifest.permission.CAMERA) {
                        Toast.makeText(
                            this,
                            "카메라 권한 거절",
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (permissionName == Manifest.permission.READ_PHONE_NUMBERS) {
                        Toast.makeText(
                            this,
                            "휴대폰 번호 권한 거절",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            this,
                            "메시지 권한 거절",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
}