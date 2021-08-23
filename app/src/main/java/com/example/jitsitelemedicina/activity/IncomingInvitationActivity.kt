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
import kotlinx.android.synthetic.main.activity_incoming_invitation.*
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URL

class IncomingInvitationActivity : AppCompatActivity() {

    private val api: ApiClient by lazy {
        ApiClient(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incoming_invitation)

        Log.i("tokendaporra",intent.getStringExtra(REMOTE_MSG_INVITER_TOKEN)!! )

        imageAcceptInvitation.setOnClickListener {
            sendInvitationResponse(REMOTE_MSG_INVITATION_ACCEPTED, intent.getStringExtra(REMOTE_MSG_INVITER_TOKEN)!!)
        }

        imageRejectInvitation.setOnClickListener {
            sendInvitationResponse(REMOTE_MSG_INVITATION_REJECTED, intent.getStringExtra(REMOTE_MSG_INVITER_TOKEN)!!)
        }

    }

    fun sendInvitationResponse(type: String, receiverToken: String){
        try {

            val tokens = JSONArray()
            tokens.put(receiverToken)
            Log.i("vamosss", receiverToken)
            Log.i("vamosssType", type)

            val body = JSONObject()
            val data = JSONObject()

            data.put(REMOTE_MSG_TYPE, REMOTE_MSG_INVITATION_RESPONSE)
            data.put(REMOTE_MSG_INVITATION_RESPONSE, type)

            body.put(REMOTE_MSG_DATA, data)
            body.put(REMOTE_MSG_REGISTRATION_IDS, tokens)

            Log.i("bodiii", body.toString())
            Log.i("bodiii", type.toString())

            sendRemoteMessage(body.toString(), type)

        }catch (e: Exception){
            Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
        }

    }

    fun sendRemoteMessage(remoteMessageBody: String, type: String){
        val send = api.apiService.sendRemoteMessage(getRemoteMessageHeaders(), remoteMessageBody)

        send?.enqueue(object : Callback<String?> {
            override fun onFailure(call: Call<String?>, t: Throwable) {
                Toast.makeText(this@IncomingInvitationActivity,t.message, Toast.LENGTH_SHORT).show()
                Log.i("onFailure", t.message.toString())
                finish()
            }

            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if (response.isSuccessful){
                    if (type.equals(REMOTE_MSG_INVITATION_ACCEPTED)){
                        try {
                            val serverUrl = URL("https://meet.jit.si")
                            val conferenceOptions = JitsiMeetConferenceOptions.Builder()
                                .setServerURL(serverUrl)
                                .setWelcomePageEnabled(false)
                                .setAudioMuted(true)
                                .setRoom(intent.getStringExtra(REMOTE_MSG_MEETING_ROOM))
                                .build()

                            JitsiMeetActivity.launch(this@IncomingInvitationActivity, conferenceOptions)
                            finish()
                        }catch (e: Exception){
                            Toast.makeText(this@IncomingInvitationActivity,e.message, Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }else{
                        Toast.makeText(this@IncomingInvitationActivity,"Invitation Rejected", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }else{
                    Toast.makeText(this@IncomingInvitationActivity,response.message(), Toast.LENGTH_SHORT).show()
                    finish()
                }
            }


        })
    }

    private val invitationResponseReceiver : BroadcastReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            val type = intent?.getStringExtra(REMOTE_MSG_INVITATION_RESPONSE)
                if (type.equals(REMOTE_MSG_INVITATION_CANCELLED)){
                    Toast.makeText(this@IncomingInvitationActivity, "invitation Cancelled", Toast.LENGTH_SHORT).show()
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
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                invitationResponseReceiver
        )
    }


}