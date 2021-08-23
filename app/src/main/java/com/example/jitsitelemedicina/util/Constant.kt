package com.example.jitsitelemedicina.util

import kotlin.collections.HashMap


const val KEY_COLLECTION_USERS = "users"
const val KEY_FIRST_NAME = "first_name"
const val KEY_LAST_NAME = "last_name"
const val KEY_EMAIL = "email"
const val KEY_PASSWORD = "password"
const val KEY_USER_ID = "user_id"

const val KEY_PREFERENCE_NAME = "videoMeetingPreference"
const val KEY_IS_SIGNED_IN = "isSignedIn"
const val KEY_FCM_TOKEN = "fcm_token"

const val REMOTE_MSG_AUTHORIZATION = "Authorization"
const val REMOTE_MSG_CONTENT_TYPE = "Content-Type"

const val REMOTE_MSG_TYPE = "type"
const val REMOTE_MSG_INVITATION = "invitation"
const val REMOTE_MSG_MEETING_TYPE = "meetingType"
const val REMOTE_MSG_INVITER_TOKEN = "inviterToken"
const val REMOTE_MSG_DATA = "data"
const val REMOTE_MSG_REGISTRATION_IDS = "registration_ids"

const val REMOTE_MSG_INVITATION_RESPONSE = "invitationResponse"

const val REMOTE_MSG_INVITATION_ACCEPTED = "accepted"
const val REMOTE_MSG_INVITATION_REJECTED = "rejected"
const val REMOTE_MSG_INVITATION_CANCELLED = "cancelled"

const val REMOTE_MSG_MEETING_ROOM = "meetingRoom"


fun getRemoteMessageHeaders(): HashMap<String, String> {
    val headers =
        HashMap<String, String>()
    headers[REMOTE_MSG_AUTHORIZATION] = "key=AAAARsMPeFI:APA91bEQzd84fU_2EnuiNTPOyHcdIjQ4-OVzSiGki18Jvj-0mDFx3FoKN8mCwMs6EYutqzqq3VNGdsfTaxRBasWzSfgIUyNCYl7XdalQxBZxGpqoOVu9Tz_jDhlAaRCkLKpmth76T1BV"
    headers[REMOTE_MSG_CONTENT_TYPE] = "application/json"
    return headers
}


