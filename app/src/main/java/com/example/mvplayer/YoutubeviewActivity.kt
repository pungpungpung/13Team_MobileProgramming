package com.example.mvplayer

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mvplayer.databinding.ActivityYoutubeviewBinding
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import kotlinx.coroutines.*
import org.json.JSONObject
import org.jsoup.Jsoup

class YoutubeviewActivity : YouTubeBaseActivity() {
    lateinit var binding: ActivityYoutubeviewBinding
    lateinit var statArr:ArrayList<String>
    lateinit var infoArr:ArrayList<String>
    lateinit var adapter: MyAdapter

    val scope = CoroutineScope(Dispatchers.IO)
    val MAX_RESULTS = 10
    val API_KEY = "AIzaSyCsujsogdfUVhnm0RJxKGNFzJ6t1tE8spI"
    var videoId = "YykjpeuMNEk"
    lateinit var job: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        if(intent.hasExtra("ytViewIntent")){
            val ytViewIntent = intent.getStringExtra("ytViewIntent")
            if (ytViewIntent != null) {
                videoId = ytViewIntent
            }
        }
        super.onCreate(savedInstanceState)
        binding = ActivityYoutubeviewBinding.inflate(layoutInflater)
        initVideo()
        initRecyclerView()
        setContentView(binding!!.root)
    }

    private fun getInfoFromVideoId(videoId:String):ArrayList<String>{
        var infoArr:ArrayList<String> = ArrayList()

        val infoUrl:String = "https://www.googleapis.com/youtube/v3/videos?part=snippet&id="+videoId+"&key="+API_KEY
        runBlocking {
            job = scope.launch {
                val doc = Jsoup.connect(infoUrl).ignoreContentType(true).execute().body()
                val jObject = JSONObject(doc.toString())
                val itemobject = jObject.getJSONArray("items").getJSONObject(0)
                val snippetobject = JSONObject(itemobject.getString("snippet"))
                //0: ?????? ??????, 1: ?????????, 2: ????????? ??????
                infoArr.add(snippetobject.getString("title"))
                infoArr.add(snippetobject.getString("channelTitle"))
                infoArr.add(snippetobject.getString("publishedAt"))
            }
            job.join()
        }
        return infoArr
    }
    private fun getStatsFromVideoId(videoId:String):ArrayList<String>{
        var countArr:ArrayList<String> = ArrayList()

        val statsUrl:String = "https://www.googleapis.com/youtube/v3/videos?part=statistics&id="+videoId+"&key="+API_KEY
        runBlocking {
            job = scope.launch {
                val doc = Jsoup.connect(statsUrl).ignoreContentType(true).execute().body()
                val jObject = JSONObject(doc.toString())
                val itemobject = jObject.getJSONArray("items").getJSONObject(0)
                val statisticsobject = JSONObject(itemobject.getString("statistics"))
                //0: ?????? ???, 1: ????????? ???, 2: ????????? ???
                countArr.add(statisticsobject.getString("viewCount"))
                countArr.add(statisticsobject.getString("likeCount"))
                countArr.add(statisticsobject.getString("dislikeCount"))
            }
            job.join()
        }
        return countArr
    }

    /*PIP ?????? ??????
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when(keyCode){
            KeyEvent.KEYCODE_BACK->{
                enterPictureInPictureMode()
                return true
            }
        }
        return false
    }
    */

    private fun initVideo(){

        binding.apply {
            youtubeView.initialize("develop", object: YouTubePlayer.OnInitializedListener{
                override fun onInitializationSuccess(
                    provider: YouTubePlayer.Provider?,
                    player: YouTubePlayer?,
                    wasRestored: Boolean
                ) {
                    if(!wasRestored) {
                        player!!.cueVideo(videoId)
                    }

                    player!!.setPlayerStateChangeListener(object: YouTubePlayer.PlayerStateChangeListener{
                        override fun onAdStarted() {}
                        override fun onLoading() {}
                        override fun onVideoStarted() {}
                        override fun onLoaded(p0: String?) {
                            player.play()
                        }
                        override fun onVideoEnded() {}
                        override fun onError(p0: YouTubePlayer.ErrorReason?) {}
                    })
                }
                override fun onInitializationFailure(
                    provider: YouTubePlayer.Provider?,
                    result: YouTubeInitializationResult?
                ) {
                    Toast.makeText(this@YoutubeviewActivity, "Video Initialization Failure!", Toast.LENGTH_SHORT).show()
                }
            })
            infoArr = getInfoFromVideoId(videoId)
            videoTitle.text = infoArr[0]
            videoSubtitle.text = "?????????: " + infoArr[1]
            videoSubtitle2.text = "????????? ??????: " + infoArr[2]

            statArr = getStatsFromVideoId(videoId)
            otherInfo.text = "????????? : " + statArr[0]
            likeTextView.text = statArr[1]
            dislikeTextView.text = statArr[2]

            shareImg.setOnClickListener {
                //??????????????? ???????????? ?????? ?????? ??????
                val myClipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val myClip: ClipData = ClipData.newPlainText("label", "https://www.youtube.com/watch?v="+videoId)
                myClipboard.setPrimaryClip(myClip)

                //?????? ??????
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "This is text to send.")
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, "??????????????? ?????? ????????????")
                startActivity(shareIntent)
            }

            playlistImg.setOnClickListener{
                val dlgBuilder = AlertDialog.Builder(this@YoutubeviewActivity)
                    .setTitle("???????????? ??????\n")
                    .setPositiveButton("??????"){ _, _ ->
                        Toast.makeText(this@YoutubeviewActivity,"??????",Toast.LENGTH_SHORT).show()
                    }
                    .setNeutralButton("??????",null)
                    .show()
            }
        }
    }



    private fun initRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        )

        //?????? ????????? ?????? ??????
        val relatedUrl = "https://www.googleapis.com/youtube/v3/search?part=snippet&relatedToVideoId=" + videoId + "&type=video&key=" + API_KEY + "&maxResults=" + MAX_RESULTS.toString()
        job = scope.launch {
            val doc = Jsoup.connect(relatedUrl).ignoreContentType(true).execute().body()
            val jObject = JSONObject(doc.toString())

            for(i in 0..MAX_RESULTS-1) {
                val itemobject = jObject.getJSONArray("items").getJSONObject(i)
                val idobject = JSONObject(itemobject.getString("id"))
                //snippet ????????? ?????? video?????? ??????
                val isSnippetNull = itemobject.optString("snippet")
                if(isSnippetNull.isNotEmpty()) {
                    val snippetobject = JSONObject(isSnippetNull)
                    //0: ????????????, 1: ?????????, 2: videoId
                    val videoTitle = snippetobject.getString("title")
                    val channelTitle = snippetobject.getString("channelTitle")
                    val vId = idobject.getString("videoId")

                    withContext(Dispatchers.Main){
                        adapter.items.add(MyData(videoTitle, channelTitle, vId))
                        adapter.notifyDataSetChanged()
                    }
                }
            }
            job.join()
        }

        adapter = MyAdapter(ArrayList<MyData>())
        adapter.thisactivity = this@YoutubeviewActivity
        adapter.thiscontext = this
        binding!!.recyclerView.adapter = adapter
    }
}