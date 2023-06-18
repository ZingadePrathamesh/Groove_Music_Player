package com.example.groove

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.groove.databinding.ActivityMainBinding
import java.io.File
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var musicAdapter: MusicAdapter

    companion object{
        lateinit var musicListMA: ArrayList<Music>
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {

        setTheme(R.style.nav_theme)
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

       //for asking request of writing external storage
        requestPermission()

        //navigation drawer
        navigationDrawer()


        initialiseMusicList()



        //shuffle button functions
        binding.shuffleButton.setOnClickListener(){
            Toast.makeText(this, "Shuffled!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra("index", 0)
            intent.putExtra("class", "MainActivity")
            startActivity(intent)
        }

        //Favourite button function
        binding.favouriteButton.setOnClickListener(){
            startActivity(Intent(this, FavouriteActivity::class.java))
        }


        //playlist button function
        binding.playlistButton.setOnClickListener {
            startActivity(Intent(this, PlaylistActivity::class.java))
        }



        //binding the navView
        binding.navView.setNavigationItemSelectedListener(){
            when(it.itemId){
                R.id.nav_feedback -> Toast.makeText(this, "Feedback", Toast.LENGTH_SHORT).show()
                R.id.nav_settings -> Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
                R.id.nav_about -> Toast.makeText(this, "About", Toast.LENGTH_SHORT).show()
                R.id.nav_exit -> exitProcess(1)
            }
            true
        }

    }

    private fun initialiseMusicList() {
        //recyclerView
        musicListMA = getAllAudio()
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.setItemViewCacheSize(16)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        musicAdapter = MusicAdapter(this, musicListMA)
        binding.recyclerView.adapter = musicAdapter
        binding.totalsongsTextview.text = "Total Songs: ${musicAdapter.itemCount}"
    }

    //accessing request
    private fun requestPermission(): Boolean{
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 13)
            return false
        }
        return true
    }
    //accessing request
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 13){
            if( grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                initialiseMusicList()
            }
            else ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 13)
        }
    }

    //navigation drawer
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    //navigation drawer
    fun navigationDrawer(){
        //navigation_drawer
//      also override fun onOptionsItemSelected
        toggle = ActionBarDrawerToggle(this, binding.root, R.string.open, R.string.close)
        binding.root.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    //accessing request
    //getting the songs from the storage
    @SuppressLint("Range")
    fun getAllAudio(): ArrayList<Music>{
        val tempList = ArrayList<Music>()

        val selection = MediaStore.Audio.Media.IS_MUSIC + " !=0"

        val projection = arrayOf(MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.DATE_ADDED, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM_ID)

        val cursor = this.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null,
        MediaStore.Audio.Media.DATE_ADDED + " DESC", null )

        if(cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val idC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                    val titleC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    val albumC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                    val artistC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    val pathC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val durationC =
                        cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val imageIdC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                    val uri = Uri.parse("content://media/external/audio/albumart")
                    val artUriC = Uri.withAppendedPath(uri, imageIdC).toString()
                    val music = Music(
                        id = idC,
                        title = titleC,
                        artist = artistC,
                        album = albumC,
                        duration = durationC,
                        path = pathC,
                        artUri = artUriC
                    )
                    val file = File(music.path)
                    if (file.exists()) tempList.add(music)
                } while (cursor.moveToNext())
                cursor.close()
            }
        }
        return tempList
    }
}