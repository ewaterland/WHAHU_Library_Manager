package com.example.whahu_library_manager

import android.app.Service
import android.content.ContentValues.TAG
import android.content.Intent

import android.os.IBinder
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MyBackgroundService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 여기에서 Firebase 이벤트 리스너를 등록하고 알림을 처리하는 로직을 추가합니다.
        val db = FirebaseDatabase.getInstance()

        val stateRef = db.reference.child("delivery_state")
        val stateEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val state = dataSnapshot.getValue(Int::class.java)

                Log.w(TAG, "state:" + state)

                if (state == 1) // 배달 요청 정보 수신
                {
                    val notificationHelper = NotificationHelper(applicationContext)
                    notificationHelper.showNotification("조선대학교 중앙도서관", "책을 요청하는 사람이 있어요!");
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        }
        stateRef.addValueEventListener(stateEventListener)

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
