package com.example.jitsitelemedicina.listener

import com.example.jitsitelemedicina.model.Users

interface UserListener {

    fun initiateVideoMeeting(users: Users)

    fun initiateAudioMeeting(users: Users)

}