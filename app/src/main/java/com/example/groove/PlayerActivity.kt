package com.example.groove

import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.groove.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity() {

    companion object{
    lateinit var musicList: ArrayList<Music>
    var songNumber = 0
    var mediaPlayer: MediaPlayer? = null
    var isPlaying: Boolean = false
    }



    private lateinit var binding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.tango_activities)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initialiseLayout()


        binding.pauseButton.setOnClickListener(){
            //pausing the song
            playPauseFunction()
        }

        binding.nextButton.setOnClickListener(){
            var temp = songNumber+1
            songNumber = if (temp < musicList.size) temp else 0
            setMediaPlayer()
        }

        binding.previousButton.setOnClickListener(){
            var temp = songNumber-1
            songNumber = if (temp<0) musicList.size-1 else temp
            setMediaPlayer()
        }

    }

    private fun playPauseFunction() {
        if(isPlaying == true){
            isPlaying = false
            mediaPlayer!!.pause()
            binding.pauseButton.setIconResource(R.drawable.play_icon)
        }
        else{
            isPlaying = true
            mediaPlayer!!.start()
            binding.pauseButton.setIconResource(R.drawable.pause_icon)
        }
    }


    fun setLayout(){
        //using api for loading images of songs
        Glide.with(this)
            .load(musicList[songNumber].artUri)
            .apply(RequestOptions().placeholder(R.drawable.splash_screen1).centerCrop())
            .into(binding.songimageView)

        binding.songtitleTextview.text = musicList[songNumber].title
        binding.songdurationTextview.text = formatDuration(musicList[songNumber].duration)
    }


    fun setMediaPlayer(){
        try{
            if(mediaPlayer == null) mediaPlayer = MediaPlayer()
            mediaPlayer!!.reset()
            mediaPlayer!!.setDataSource(musicList[songNumber].path)
            mediaPlayer!!.prepare()
            mediaPlayer!!.start()
            setLayout()
            isPlaying = true
            binding.pauseButton.setIconResource(R.drawable.pause_icon)
        }catch (e: java.lang.Exception){return}
    }


    private fun initialiseLayout(){
        songNumber = intent.getIntExtra("index", 0)

        when(intent.getStringExtra("class")){
            "MusicAdapter" ->{
                musicList = ArrayList()
                musicList.addAll(MainActivity.musicListMA)
                setMediaPlayer()
            }
            "MainActivity"->{
                musicList = ArrayList()
                musicList.addAll(MainActivity.musicListMA.shuffled())
                setMediaPlayer()
            }
        }
    }
}