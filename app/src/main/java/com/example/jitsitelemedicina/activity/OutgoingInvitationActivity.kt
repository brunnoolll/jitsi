package com.example.jitsitelemedicina.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.jitsitelemedicina.R
import com.example.jitsitelemedicina.network.ApiClient
import com.example.jitsitelemedicina.util.*
import kotlinx.android.synthetic.main.activity_outgoing_invitation.*
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URL
import java.util.*

class OutgoingInvitationActivity : AppCompatActivity() {

    val preference: PreferenceManager by lazy {
        PreferenceManager(this)
    }

    private val api: ApiClient by lazy {
        ApiClient(this)
    }


    lateinit var inviterToken : String
     lateinit var meetingRoom : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_outgoing_invitation)

        inviterToken = preference.getString(KEY_FCM_TOKEN)


        imageStopInvitation.setOnClickListener {
            Log.i("cancelado", inviterToken)
                cancelInvitation(inviterToken)
        }



        val meetingType = intent.getStringExtra("users")
        initiateMetting("video", meetingType!! )

    }

    fun initiateMetting(meetingType: String, receiverToken: String){

        try {

            val tokens = JSONArray()
            tokens.put(receiverToken)

            val body = JSONObject()
            val data = JSONObject()

            data.put(REMOTE_MSG_TYPE, REMOTE_MSG_INVITATION)
            data.put(REMOTE_MSG_MEETING_TYPE, meetingType)
            data.put(KEY_FIRST_NAME, preference.getString(KEY_FIRST_NAME))
            data.put(KEY_LAST_NAME, preference.getString(KEY_LAST_NAME))
            data.put(KEY_EMAIL, preference.getString(KEY_EMAIL))
            data.put(REMOTE_MSG_INVITER_TOKEN, inviterToken)

            meetingRoom = preference.getString(KEY_USER_ID) + " " + UUID.randomUUID().toString().substring(0,5)

            data.put(REMOTE_MSG_MEETING_ROOM, meetingRoom)

            body.put(REMOTE_MSG_DATA, data)
            body.put(REMOTE_MSG_REGISTRATION_IDS, tokens)
            Log.i("bodydocrlho", body.toString())

            sendRemoteMessage(body.toString(), REMOTE_MSG_INVITATION)


        }catch (e: Exception){
            Toast.makeText(this@OutgoingInvitationActivity,e.message, Toast.LENGTH_SHORT).show()
            finish()
        }

    }


    fun sendRemoteMessage(remoteMessageBody: String, type: String){
        val send = api.apiService.sendRemoteMessage(getRemoteMessageHeaders(), remoteMessageBody)

        send?.enqueue(object : Callback<String?> {
            override fun onFailure(call: Call<String?>, t: Throwable) {
                Toast.makeText(this@OutgoingInvitationActivity,t.message, Toast.LENGTH_SHORT).show()
                Log.i("onFailure", t.message.toString())
                finish()
            }

            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if (response.isSuccessful){
                    if (type.equals(REMOTE_MSG_INVITATION)){
                        Toast.makeText(this@OutgoingInvitationActivity,"Invitation sent sucess", Toast.LENGTH_SHORT).show()
                    }else if (type.equals(REMOTE_MSG_INVITATION_RESPONSE)){
                        Toast.makeText(this@OutgoingInvitationActivity,"Invitation cancelled", Toast.LENGTH_SHORT).show()
                        Log.i("foiiii","foiiiiii")
                        finish()
                    }
                }else{
                    Toast.makeText(this@OutgoingInvitationActivity,response.message(), Toast.LENGTH_SHORT).show()
                    Log.i("falhaaa", response.message().toString())
                    finish()
                }
            }


        })
    }

    fun cancelInvitation(receiverToken: String){
        try {

            val tokens = JSONArray()
            tokens.put(receiverToken)
            Log.i("vamosss", receiverToken)


            val body = JSONObject()
            val data = JSONObject()

            data.put(REMOTE_MSG_TYPE, REMOTE_MSG_INVITATION_RESPONSE)
            data.put(REMOTE_MSG_INVITATION_RESPONSE, REMOTE_MSG_INVITATION_CANCELLED)

            body.put(REMOTE_MSG_DATA, data)
            body.put(REMOTE_MSG_REGISTRATION_IDS, tokens)

            Log.i("bodiii", body.toString())

            sendRemoteMessage(body.toString(), REMOTE_MSG_INVITATION_RESPONSE)

        }catch (e: Exception){
            Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
        }

    }

    private val invitationResponseReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val type = intent.getStringExtra(REMOTE_MSG_INVITATION_RESPONSE)
            if (type.equals(REMOTE_MSG_INVITATION_ACCEPTED)){

                try {

                    val serverUrl = URL("https://meet.jit.si")
                    val conferenceOptions = JitsiMeetConferenceOptions.Builder()
                        .setServerURL(serverUrl)
                        .setWelcomePageEnabled(false)
                        .setAudioMuted(true)
                        .setRoom(meetingRoom)
                        .build()

                    JitsiMeetActivity.launch(this@OutgoingInvitationActivity, conferenceOptions)
                    finish()

                }catch (e: Exception){
                    Toast.makeText(this@OutgoingInvitationActivity, e.message, Toast.LENGTH_SHORT).show()
                    finish()
                }
            }else if (type.equals(REMOTE_MSG_INVITATION_REJECTED)){
                Toast.makeText(this@OutgoingInvitationActivity, "Inivitation Rejected", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this).registerReceiver(
                invitationResponseReceiver, IntentFilter(REMOTE_MSG_INVITATION_RESPONSE)
        )
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(invitationResponseReceiver)
    }

}

