package com.example.jitsitelemedicina.firebase

import android.content.Intent
import android.util.Log
import androidx.annotation.NonNull
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.jitsitelemedicina.activity.IncomingInvitationActivity
import com.example.jitsitelemedicina.util.*
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MessagingService(): FirebaseMessagingService() {

    val preference: PreferenceManager by lazy {
        PreferenceManager(this)
    }

    override fun onNewToken(@NonNull token: String) {
        super.onNewToken(token)
        preference.putString(KEY_FCM_TOKEN, token)
        Log.d("FCM","meu token: " + token.toString())
    }

    override fun onMessageReceived(@NonNull remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

       var type =  remoteMessage.data.get(REMOTE_MSG_TYPE)
        Log.i("tipoooo", type.toString())

        if(type!= null){
            Log.i("valorrr", REMOTE_MSG_INVITATION)
            if (type.equals(REMOTE_MSG_INVITATION)){
                Log.i("toker de quem ligou", remoteMessage.data.get(REMOTE_MSG_INVITER_TOKEN).toString())
                startActivity(Intent(this, IncomingInvitationActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra(REMOTE_MSG_INVITER_TOKEN, remoteMessage.data.get(REMOTE_MSG_INVITER_TOKEN))
                        .putExtra(REMOTE_MSG_MEETING_ROOM, remoteMessage.data.get(REMOTE_MSG_MEETING_ROOM)))
            }else if (type.equals(REMOTE_MSG_INVITATION_RESPONSE)){
                val intent =  Intent(REMOTE_MSG_INVITATION_RESPONSE)
                    intent.putExtra(REMOTE_MSG_INVITATION_RESPONSE, remoteMessage.data.get(REMOTE_MSG_INVITATION_RESPONSE))
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
            }
        }
    }

}