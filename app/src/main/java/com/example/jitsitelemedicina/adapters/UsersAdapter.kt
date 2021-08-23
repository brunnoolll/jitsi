package com.example.jitsitelemedicina.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.jitsitelemedicina.R
import com.example.jitsitelemedicina.model.Users
import com.example.jitsitelemedicina.listener.UserListener

class UsersAdapter(var clickListener: OnUserClickListener): RecyclerView.Adapter<UsersAdapter.UsersViewHolder>() {

    private var itens : ArrayList<Users> = ArrayList()


    class UsersViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val nome1 = itemView.findViewById<TextView>(R.id.textFirstChar)
        val textUserName = itemView.findViewById<TextView>(R.id.textUserName)
        val textEmail = itemView.findViewById<TextView>(R.id.textEmail)
        val imageAudio = itemView.findViewById<ImageView>(R.id.imageAudioMeeting)
        val imageVideo = itemView.findViewById<ImageView>(R.id.imageVideoMeeting)

        fun initialize(item: Users, action: OnUserClickListener){
            nome1.text = item.FirstName.substring(0,1)
            textUserName.text = item.FirstName + " " + item.LastName
            textEmail.text = item.email

            itemView.setOnClickListener {
               action.onItemClick(item, adapterPosition, itemView)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_container_user, parent, false)
        return UsersViewHolder(v)
    }

    override fun getItemCount(): Int {
        return itens.size
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        holder.initialize(itens.get(position), clickListener)
    }

    interface OnUserClickListener{
        fun onItemClick(item: Users, position: Int, itemView: View)
    }

    fun listPopular(usuario: ArrayList<Users>){
        itens.clear()
        itens.addAll(usuario)
        notifyDataSetChanged()
    }


}