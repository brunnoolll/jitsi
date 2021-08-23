package com.example.jitsitelemedicina.activity

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.jitsitelemedicina.R
import com.example.jitsitelemedicina.model.Users
import com.example.jitsitelemedicina.util.*
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.String.*
import kotlinx.android.synthetic.main.activity_signi_up.*

class SigniUpActivity : AppCompatActivity() {

    val preference: PreferenceManager by lazy {
        PreferenceManager(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signi_up)

        imageBack.setOnClickListener { onBackPressed() }
        textSignIn.setOnClickListener { onBackPressed() }

        var nome = inputFirstName.text
        var nome2 = inputLastName.text
        var email = inputEmail.text
        var senha = inputPassword.text

        buttonSignUp.setOnClickListener {
            if (inputFirstName.text.toString().trim().isEmpty()){
                Toast.makeText(this, "Campo Nome vazio", Toast.LENGTH_SHORT).show()
            }else if (inputLastName.text.toString().trim().isEmpty()){
                Toast.makeText(this, "Campo Sobrenome  vazio", Toast.LENGTH_SHORT).show()
            }else if(!Patterns.EMAIL_ADDRESS.matcher(inputEmail.text.toString()).matches()){
                Toast.makeText(this, "campo E-mail vazio ou formato de E-mail invalido", Toast.LENGTH_SHORT).show()
            }else if (inputPassword.text.trim().isEmpty()){
                Toast.makeText(this, "Campo Senha vazio", Toast.LENGTH_SHORT).show()
            }else if (inputConfirmPassword.text.trim().isEmpty()){
                Toast.makeText(this, "Campo Confirmação de senha vazio ", Toast.LENGTH_SHORT).show()
            }else if(!inputPassword.text.toString().equals(inputConfirmPassword.text.toString())){
                Toast.makeText(this, "Senhas diferentes", Toast.LENGTH_SHORT).show()
            }else{
                signUp(nome.toString(), nome2.toString(), email.toString(), senha.toString())
            }
        }
    }

     private fun signUp(nome : String, nome2: String, email: String, senha: String){
        buttonSignUp.visibility = View.INVISIBLE
         signUpProgressBar.visibility = View.VISIBLE

         val db = FirebaseFirestore.getInstance()
         val user : MutableMap<String, Any> = HashMap()
         user [KEY_FIRST_NAME] = nome
         user [KEY_LAST_NAME] = nome2
         user [KEY_EMAIL] = email
         user [KEY_PASSWORD] = senha
         user [KEY_FCM_TOKEN] = preference.getString(KEY_FCM_TOKEN)
         db.collection(KEY_COLLECTION_USERS)
             .add(user)
             .addOnSuccessListener(fun(it: DocumentReference) {
                 Toast.makeText(this@SigniUpActivity, "Salvo com sucesso", Toast.LENGTH_SHORT)
                     .show()
                 preference.putBoolean(KEY_IS_SIGNED_IN, true)
                 preference.putString(KEY_FIRST_NAME, nome)
                 preference.putString(KEY_LAST_NAME, nome2)
                 preference.putString(KEY_EMAIL, email)
                 startActivity(Intent(this, MainActivity::class.java))
                 finish()
             })
             .addOnFailureListener {
                 Toast.makeText(this@SigniUpActivity,"Erro ao salvar", Toast.LENGTH_SHORT).show()
                 clearCampos()
                 buttonSignUp.visibility = View.VISIBLE
                 signUpProgressBar.visibility = View.INVISIBLE
             }

     }

    private fun clearCampos() {
        inputFirstName.text.clear()
        inputLastName.text.clear()
        inputEmail.text.clear()
        inputPassword.text.clear()
        inputConfirmPassword.text.clear()
    }
}