package com.example.jitsitelemedicina.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jitsitelemedicina.R
import com.example.jitsitelemedicina.adapters.UsersAdapter
import com.example.jitsitelemedicina.model.Users
import com.example.jitsitelemedicina.util.*
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_container_user.view.*
import java.util.*
import kotlin.collections.ArrayList


private const val REQUEST_CODE_BATERRY_OPTIMIZATIONS = 1
private const val REQUEST_CODE_POP_UP_BACKGROUND = 11
private const val OVERLAY_REQUEST_CODE = 110

class MainActivity : AppCompatActivity(), UsersAdapter.OnUserClickListener {

    val preference: PreferenceManager by lazy {
        PreferenceManager(this)
    }


    lateinit var adapter: UsersAdapter
    lateinit var usersLista: ArrayList<Users>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        adapter = UsersAdapter(this)
        usersLista = ArrayList()
        textTitle.text =  preference.getString(KEY_FIRST_NAME)+ " " + preference.getString(KEY_LAST_NAME)
        setupRecycler()

        swipeRefreshLayout.setOnRefreshListener(this::getUser)

        getUser()
        checkForBaterryOptimizations()
        checkXiaomi()
    }


    fun setupRecycler(){
        val reclycerView = findViewById<RecyclerView>(R.id.usersRecycler)
        reclycerView.layoutManager = LinearLayoutManager(this)
        reclycerView.addItemDecoration(DividerItemDecoration(this, 0))
        reclycerView.adapter = adapter
    }

    fun sendFCMTokenToDatabase(token: String){
        val db = FirebaseFirestore.getInstance()
        val documentReference: DocumentReference
        documentReference = db.collection(KEY_COLLECTION_USERS).document(preference.getString(KEY_FCM_TOKEN))
        documentReference.update(KEY_FCM_TOKEN, token)
            .addOnSuccessListener {
                Toast.makeText(this,"Token atualizado", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener(fun(it: Exception) {
                Toast.makeText(this, "Problema ao atualizar token: "+it.message, Toast.LENGTH_SHORT).show()
            })
    }

    fun getUser(){
        swipeRefreshLayout.isRefreshing = true
        val db =  FirebaseFirestore.getInstance()
            db.collection(KEY_COLLECTION_USERS)
                .get()
                .addOnSuccessListener(fun(it: QuerySnapshot) {
                    swipeRefreshLayout.isRefreshing = false
                    val MyuserId = preference.getString(KEY_USER_ID)
                        usersLista.clear()
                        it.forEach(fun(it: QueryDocumentSnapshot) {
                            if(MyuserId.equals(it.id)){
                               return@forEach
                            }
                            var user : Users = Users()
                            user.FirstName = it.getString(KEY_FIRST_NAME)!!
                            user.LastName = it.getString(KEY_LAST_NAME)!!
                            user.email = it.getString(KEY_EMAIL)!!
                            user.token = it.getString(KEY_FCM_TOKEN)!!
                            usersLista.add(user)
                        })
                    adapter.listPopular(usersLista)
                })
    }

    override fun onItemClick(item: Users, position: Int, itemView: View) {
        itemView.imageVideoMeeting.setOnClickListener {
            startActivity(Intent(this, OutgoingInvitationActivity::class.java).putExtra("users", item.token))

            Toast.makeText(this, "video Chamada", Toast.LENGTH_SHORT).show()
        }

        itemView.imageAudioMeeting.setOnClickListener {
            Toast.makeText(this, "Chamada", Toast.LENGTH_SHORT).show()
        }

    }

    private fun checkForBaterryOptimizations() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val powerManager =
                getSystemService(Context.POWER_SERVICE) as PowerManager
            if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
                val builder =
                    AlertDialog.Builder(this)
                builder.setTitle("Aviso")
                builder.setMessage("A otimização de Baterry está ativada. Pode interromper a execução de serviços em segundo plano")
                builder.setPositiveButton(
                    "Desabilitar"
                ) { dialog, which ->
                    val intent = Intent()
//                    intent.action = Settings.ACTION_IGNORE_BACKGROUND_DATA_RESTRICTIONS_SETTINGS
                    intent.action = Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
                    this.startActivity(intent)
                }
                builder.setNegativeButton(
                    "Cancelar"
                ) { dialog, which -> dialog.dismiss() }
                builder.create().show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i("permissaoooo", requestCode.toString())
        if (requestCode == REQUEST_CODE_BATERRY_OPTIMIZATIONS){
            checkForBaterryOptimizations()
        }
        if (requestCode == REQUEST_CODE_POP_UP_BACKGROUND){
            checkXiaomi()
        }else{
            Toast.makeText(this,"qualquer coisa", Toast.LENGTH_LONG).show()
        }
    }

    fun checkXiaomi(){
        if ("xiaomi" == Build.MANUFACTURER.toLowerCase(Locale.ROOT)) {
            if (!Settings.canDrawOverlays(this)){
                val builder =
                    AlertDialog.Builder(this)
                builder.setTitle("Aviso")
                builder.setMessage("Para o uso da Telemedicina, habilite a permissão \"Mostrar janelas pop-up enquanto estiver em segundo plano\"")
                builder.setPositiveButton(
                    "HAbilitar"
                ) { dialog, which ->
                    val intent = Intent("miui.intent.action.APP_PERM_EDITOR")
                    intent.setClassName("com.miui.securitycenter",
                        "com.miui.permcenter.permissions.PermissionsEditorActivity")
                    intent.putExtra("extra_pkgname", getPackageName())
//                    startActivity(intent)
                    startActivityForResult(intent, REQUEST_CODE_POP_UP_BACKGROUND)
                }
                builder.setNegativeButton(
                    "Cancelar"
                ) { dialog, which -> dialog.dismiss() }
                builder.create().show()
            }
        }
    }

}