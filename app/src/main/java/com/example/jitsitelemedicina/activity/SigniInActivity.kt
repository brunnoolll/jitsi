package com.example.jitsitelemedicina.activity

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.jitsitelemedicina.R
import com.example.jitsitelemedicina.util.*
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_signi_in.*
import java.lang.Exception
import java.util.*
import kotlin.math.log
import com.google.firebase.firestore.QuerySnapshot as QuerySnapshot1


class SigniInActivity : AppCompatActivity() {

    val preference: PreferenceManager by lazy {
        PreferenceManager(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signi_in)

            var login = inputEmail.text
            var senha = inputPassword.text


        if (preference.getBoolean(KEY_IS_SIGNED_IN)){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        buttonSignIn.setOnClickListener {
            if (login.toString().trim().isEmpty()){
                Toast.makeText(this, "Campo Email vazio", Toast.LENGTH_SHORT).show()
            }else if (!Patterns.EMAIL_ADDRESS.matcher(login.toString()).matches()){
                Toast.makeText(this, "Formato de E-mail invalido", Toast.LENGTH_SHORT).show()
            }else if(senha.toString().trim().isEmpty()){
                Toast.makeText(this, "Campo Senha vazio", Toast.LENGTH_SHORT).show()
            }else{
                signIn(login.toString(), senha.toString())
            }
        }

        textSignUp.setOnClickListener {
            startActivity(Intent(this, SigniUpActivity::class.java))
        }
    }



    fun signIn(login: String, senha : String){
        buttonSignIn.visibility = View.INVISIBLE
        signInProgressBar.visibility = View.VISIBLE

        val db = FirebaseFirestore.getInstance()
        db.collection(KEY_COLLECTION_USERS)
            .whereEqualTo(KEY_EMAIL, login)
            .whereEqualTo(KEY_PASSWORD, senha)
            .get()
            .addOnCompleteListener({
                if (it.isSuccessful && it.result != null && it.result!!.documents.size > 0){
                    val documents: DocumentSnapshot = it.result!!.documents.get(0)
                    preference.putBoolean(KEY_IS_SIGNED_IN, true)
                    preference.putString(KEY_USER_ID, documents.id)
                    preference.putString(KEY_FIRST_NAME, documents.getString(KEY_FIRST_NAME).toString())
                    preference.putString(KEY_LAST_NAME, documents.getString(KEY_LAST_NAME).toString())
                    preference.putString(KEY_EMAIL, documents.getString(KEY_EMAIL).toString())
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }else{
                    Toast.makeText(this, "Erro ao realizar login", Toast.LENGTH_SHORT).show()
                    signInProgressBar.visibility = View.INVISIBLE
                    buttonSignIn.visibility = View.VISIBLE
                }
            })
            .addOnFailureListener {
                Toast.makeText(this, "falha ao realizar login", Toast.LENGTH_SHORT).show()
                inputEmail.text.clear()
                inputPassword.text.clear()
                signInProgressBar.visibility = View.INVISIBLE
                buttonSignIn.visibility = View.VISIBLE
            }
    }
}





