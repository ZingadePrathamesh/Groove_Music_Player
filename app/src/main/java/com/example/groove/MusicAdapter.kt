package com.example.groove

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.groove.databinding.SongListviewBinding

class MusicAdapter(private val context: Context, private val songList: ArrayList<Music>) : RecyclerView.Adapter<MusicAdapter.MyHolder>(){
    class MyHolder(binding: SongListviewBinding): RecyclerView.ViewHolder(binding.root) {
        val songName = binding.songNameTextview
        val albumName = binding.albumNameTextview
        val duration = binding.songDurationTextview
        val songImage = binding.songImageView
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicAdapter.MyHolder {
       return MyHolder(SongListviewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: MusicAdapter.MyHolder, position: Int) {
        holder.songName.text = songList[position].title
        holder.albumName.text = songList[position].album
        holder.duration.text = formatDuration(songList[position].duration)

        //using api for loading images of songs
        Glide.with(context)
            .load(songList[position].artUri)
            .apply(RequestOptions().placeholder(R.drawable.splash_screen1).centerCrop())
            .into(holder.songImage)

        //for going to the playerActivity
        holder.root.setOnClickListener(){
            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra("index", position)
            intent.putExtra("class", "MusicAdapter")
            ContextCompat.startActivity(context, intent, null)
        }
    }

    override fun getItemCount(): Int {
        return songList.size
    }

}