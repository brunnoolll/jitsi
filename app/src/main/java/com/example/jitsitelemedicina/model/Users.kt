package com.example.jitsitelemedicina.model

class Users(){

    var LastName: String = ""
    var FirstName: String = ""
    var email: String = ""
    var token: String = ""
    var ConfirmPassword : String = ""
    var Password : String = ""

    override fun toString(): String {
        return "Users(LastName='$LastName', FirstName='$FirstName', email='$email', token='$token', ConfirmPassword='$ConfirmPassword', Password='$Password')"
    }
}
