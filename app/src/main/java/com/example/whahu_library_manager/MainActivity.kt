package com.example.whahu_library_manager

import android.animation.ObjectAnimator
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MainActivity : AppCompatActivity() {

    var handler : Handler = Handler()
    var runnable : Runnable = Runnable {  }

    // 좌표값 읽고 위치 표시 update
    private var coo_x: Float? = null
    private var coo_y: Float? = null

    // 객체 생성
    private lateinit var image: ImageView

    // TAG
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        image = findViewById(R.id.robot)
        val text_coo = findViewById<TextView>(R.id.text_coo)

        val serviceIntent = Intent(applicationContext, MyBackgroundService::class.java)
        applicationContext.startService(serviceIntent)

        Log.w(ContentValues.TAG, "~ Main ~")

        // firebase 접근 권한 갖기
        FirebaseApp.initializeApp(this)

        // 배달 시작 버튼
        var btn_DeliveryStart: Button? = null
        btn_DeliveryStart = findViewById<Button>(R.id.btn_DeliveryStart)
        btn_DeliveryStart!!.setOnClickListener {

        }

        ///////////////////////////////////////////////////////// Realtime Firebase
        val db = FirebaseDatabase.getInstance()

        //////////////////////

        val addressRef = db.reference.child("address")
        val addressEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val address = dataSnapshot.getValue(String::class.java)
                text_coo.text = address
                Log.w(TAG, "address:" + address)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        }

        //////////////////////

        val stateRef = db.reference.child("delivery_state")
        val stateEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val state = dataSnapshot.getValue(Int::class.java)

                Log.w(TAG, "state:" + state)

                if (state == 1) // 배달 요청 정보 수신
                {
                    btn_DeliveryStart.isEnabled = true

                    val notificationHelper = NotificationHelper(applicationContext)
                    notificationHelper.showNotification("조선대학교 중앙도서관", "책을 요청하는 사람이 있어요!");
                }
                else{
                    btn_DeliveryStart.isEnabled = false
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        }

        //////////////////////

        val xRef = db.reference.child("x_new")
        val yRef = db.reference.child("y_new")

        val valueEventListener1 = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                coo_x = dataSnapshot.getValue(Float::class.java)
                val x_temp = coo_x
                val y_temp = coo_y

                if (x_temp != null && y_temp != null) {
                    val x = x_temp
                    val y = y_temp

                    // 객체 이동
                    imagemove(image, y, x, 200L)

                    Log.w(ContentValues.TAG, "============ 맵 업데이트 성공 ============")
                    //Toast.makeText(this@MainActivity, "Map Update...", Toast.LENGTH_SHORT).show()
                } else {
                    Log.w(ContentValues.TAG, "============ 좌표 값 로딩 실패 ============")
                    //Toast.makeText(this@MainActivity, "Map Update Fail", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // 에러 처리
                Log.w(ContentValues.TAG, "============ x값 로드 실패 ============")
            }
        }

        val valueEventListener2 = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                coo_y = dataSnapshot.getValue(Float::class.java)
                val x_temp = coo_x
                val y_temp = coo_y

                if (x_temp != null && y_temp != null) {
                    val x = x_temp
                    val y = y_temp

                    // 객체 이동
                    imagemove(image, y, x, 200L)

                    Log.w(ContentValues.TAG, "============ 맵 업데이트 성공 ============")
                    //.makeText(this@MainActivity, "Map Update...", Toast.LENGTH_SHORT).show()
                } else {
                    Log.w(ContentValues.TAG, "============ 좌표 값 로딩 실패 ============")
                    //Toast.makeText(this@MainActivity, "Map Update Fail", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // 에러 처리
                Log.w(ContentValues.TAG, "============ y값 로드 실패 ============")
            }
        }

        addressRef.addValueEventListener(addressEventListener)
        stateRef.addValueEventListener(stateEventListener)
        xRef.addValueEventListener(valueEventListener1)
        yRef.addValueEventListener(valueEventListener2)
    } // onCreate END

    /////////////////////////////////////////////////////////

    // 함수 - 이미지 객체 이동
    fun imagemove(image1: ImageView, posix:Float, posiy:Float, duration1: Long)
    {
        runnable = object : Runnable
        {
            override fun run()
            {
                ObjectAnimator.ofFloat(image1, "translationX", posix).apply {
                    duration = duration1
                    start()
                }
                ObjectAnimator.ofFloat(image1, "translationY", posiy).apply {
                    duration = duration1
                    start()
                }
                handler.postDelayed(runnable, duration1)
            }
        }
        handler.post(runnable)
    }

    // 함수
    private var backPressedTime: Long = 0
    override fun onBackPressed() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - backPressedTime < BACK_PRESS_INTERVAL) {
            super.onBackPressed() // 앱 종료
        } else {
            backPressedTime = currentTime
            Toast.makeText(this, "한 번 더 누를 시 앱이 종료됩니다", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        ///////////////////////////////// 뒤로 가기 버튼
        private const val BACK_PRESS_INTERVAL = 2000 // 뒤로가기 버튼을 두 번 누르는 간격 (밀리초)
    }
}
