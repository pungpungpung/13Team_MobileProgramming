package com.example.mvplayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mvplayer.databinding.FragmentHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    private val USER_AGENT = "PC용 Agent"
    private val GOOGLE_URL = "http://www.google.com"
    val data:ArrayList<MyData> = ArrayList()
    var limitCount = 5 //리밋 카운트
    var mvena = ArrayList<Boolean>()
    var counter1 : Boolean = true
    var counter2 : Int = 0
    lateinit var adapter : MyAdapter
    lateinit var myDBHelper: MyDBHelper
    var MelonNewUrl1:String="https://www.melon.com/chart/index.htm"
    var MelonNewUrl2:String="https://www.melon.com/new/index.htm"
    var MelonNewUrl3:String="https://www.melon.com/genre/song_list.htm?gnrCode=GN0100"
    var MelonNewUrl4:String="https://www.melon.com/genre/song_list.htm?gnrCode=GN0300"
    val scope = CoroutineScope(Dispatchers.IO)

    fun getNewlist1() {
        var count = 0
        scope.launch {
            val doc1 = Jsoup.connect(MelonNewUrl1).get()
            val songname = doc1.select("div.wrap_song_info")
            val musicvideo = doc1.select("div.wrap.t_center").select("button.button_icons.video")
            for (mv in musicvideo) {
                if (mv.attr("class") == "button_icons video ") {
                    mvena.add(true)
                } else if (mv.attr("class") == "button_icons video disabled") {
                    mvena.add(false)
                }
                /* adapter.items.add(MyData(mv.attr("class"),"mv.text",false))*/
            }
            for (song in songname) {
                if (counter1) {
                    if (mvena[counter2] == true) {
                        val songname = song.select("div.ellipsis.rank01 span a").text()
                        val singer = song.select("div.ellipsis.rank02 span a").text()
                        var strParse = ""
                        var str = ""
                        val url = "https://www.youtube.com/results?search_query=" +
                                "$songname" +
                                " $singer" + " mv" + "&page=1"
                        val doc = Jsoup.connect(url)
                                .userAgent(USER_AGENT)
                                .referrer(GOOGLE_URL)
                                .get()
                        val scanner: Scanner = Scanner(doc.toString())
                        while (scanner.hasNextLine()) {
                            val line = scanner.nextLine()
                            if (line.contains("ytInitialData")) {
                                strParse = line
                                val indexof1 = strParse.indexOf("videoId")
                                val indexof2 = strParse.indexOf("thumbnail")
                                str = strParse.substring(indexof1 + 10, indexof2 - 3)
                                break
                            }

                        }
                        scanner.close()
                        myDBHelper.insertProduct(MyData(songname, singer, str.toString()))
                        count++
                    }
                    counter2++
                    counter1 = false
                } else {
                    counter1 = true
                }
                //삭제해도되는 구문
                if (count == limitCount)
                    break
            }
            withContext(Dispatchers.Main) {
                makeView()
                adapter.notifyDataSetChanged() //UI가 변경됨
            }
            binding!!.progressBar.visibility = View.GONE
        }

    }
    //멜론 최신차트
    fun getNewlist2(){
        var count = 0
        scope.launch {
            val doc1 = Jsoup.connect(MelonNewUrl2).get()
            val songname = doc1.select("div.wrap_song_info")
            val musicvideo = doc1.select("div.wrap.t_center").select("button.button_icons.video")
            for (mv in musicvideo) {
                if(mv.attr("class") == "button_icons video "){
                    mvena.add(true)
                }else if(mv.attr("class")== "button_icons video disabled"){
                    mvena.add(false)
                }
                /* adapter.items.add(MyData(mv.attr("class"),"mv.text",false))*/
            }
            for (song in songname) {
                if (counter1) {
                    if(counter2==10){
                        break
                    }
                    if(mvena[counter2]==true) {
                        val songname = song.select("div.ellipsis.rank01 span a").text()
                        val singer = song.select("div.ellipsis.rank02 span a").text()
                        var strParse = ""
                        var str = ""
                        val url = "https://www.youtube.com/results?search_query="+
                                "$songname"+
                                " $singer"+" mv"+"&page=1"
                        val doc = Jsoup.connect(url)
                                .userAgent(USER_AGENT)
                                .referrer(GOOGLE_URL)
                                .get()
                        val scanner: Scanner = Scanner(doc.toString())
                        while (scanner.hasNextLine()) {
                            val line = scanner.nextLine()
                            if (line.contains("ytInitialData")) {
                                strParse = line
                                val indexof1 = strParse.indexOf("videoId")
                                val indexof2 = strParse.indexOf("thumbnail")
                                str = strParse.substring(indexof1+10, indexof2-3)
                                break
                            }

                        }
                        scanner.close()
                        myDBHelper.insertProduct(MyData(songname,singer,str.toString()))
                        count++
                    }
                    counter2++
                    counter1 = false
                } else {
                    counter1 = true
                }
                //삭제해도되는 구문
                if(count == limitCount)
                    break
            }
            withContext(Dispatchers.Main) {
                makeView()
                adapter.notifyDataSetChanged() //UI가 변경됨
            }
        }
    }
    //멜론 발라드
    fun getNewlist3(){
        scope.launch {
            var count = 0
            val doc1 = Jsoup.connect(MelonNewUrl3).get()
            val songname = doc1.select("div.wrap_song_info")
            val musicvideo = doc1.select("div.wrap.t_center").select("button.button_icons.video")
            for (mv in musicvideo) {
                if(mv.attr("class") == "button_icons video "){
                    mvena.add(true)
                }else if(mv.attr("class")== "button_icons video disabled"){
                    mvena.add(false)
                }
                /* adapter.items.add(MyData(mv.attr("class"),"mv.text",false))*/
            }
            for (song in songname) {
                if (counter1) {
                    if(mvena[counter2]==true) {
                        val songname = song.select("div.ellipsis.rank01 span a").text()
                        val singer = song.select("div.ellipsis.rank02 span a").text()
                        var strParse = ""
                        var str = ""
                        val url = "https://www.youtube.com/results?search_query="+
                                "$songname"+
                                " $singer"+" mv"+"&page=1"
                        val doc = Jsoup.connect(url)
                                .userAgent(USER_AGENT)
                                .referrer(GOOGLE_URL)
                                .get()
                        val scanner: Scanner = Scanner(doc.toString())
                        while (scanner.hasNextLine()) {
                            val line = scanner.nextLine()
                            if (line.contains("ytInitialData")) {
                                strParse = line
                                val indexof1 = strParse.indexOf("videoId")
                                val indexof2 = strParse.indexOf("thumbnail")
                                str = strParse.substring(indexof1+10, indexof2-3)
                                break
                            }

                        }
                        scanner.close()
                        myDBHelper.insertProduct(MyData(songname,singer,str.toString()))
                        count++
                    }
                    counter2++
                    counter1 = false
                } else {
                    counter1 = true
                }
                //삭제해도되는 구문
                if(count == limitCount)
                    break
            }
            withContext(Dispatchers.Main) {
                makeView()
                adapter.notifyDataSetChanged() //UI가 변경됨
            }
        }
    }
    //멜론 발라드 랩 힙합
    fun getNewlist4(){
        var count = 0
        scope.launch {
            val doc1 = Jsoup.connect(MelonNewUrl4).get()
            val songname = doc1.select("div.wrap_song_info")
            val musicvideo = doc1.select("div.wrap.t_center").select("button.button_icons.video")
            for (mv in musicvideo) {
                if(mv.attr("class") == "button_icons video "){
                    mvena.add(true)
                }else if(mv.attr("class")== "button_icons video disabled"){
                    mvena.add(false)
                }
                /* adapter.items.add(MyData(mv.attr("class"),"mv.text",false))*/
            }
            for (song in songname) {
                if (counter1) {
                    if(mvena[counter2]==true) {
                        val songname = song.select("div.ellipsis.rank01 span a").text()
                        val singer = song.select("div.ellipsis.rank02 span a").text()
                        var strParse = ""
                        var str = ""
                        val url = "https://www.youtube.com/results?search_query="+
                                "$songname"+
                                " $singer"+" mv"+"&page=1"
                        val doc = Jsoup.connect(url)
                                .userAgent(USER_AGENT)
                                .referrer(GOOGLE_URL)
                                .get()
                        val scanner: Scanner = Scanner(doc.toString())
                        while (scanner.hasNextLine()) {
                            val line = scanner.nextLine()
                            if (line.contains("ytInitialData")) {
                                strParse = line
                                val indexof1 = strParse.indexOf("videoId")
                                val indexof2 = strParse.indexOf("thumbnail")
                                str = strParse.substring(indexof1+10, indexof2-3)
                                break
                            }

                        }
                        scanner.close()
                        myDBHelper.insertProduct(MyData(songname,singer,str.toString()))
                        count++
                    }
                    counter2++
                    counter1 = false
                } else {
                    counter1 = true
                }

                //삭제해도되는 구문
                if(count == limitCount)
                    break
            }
            withContext(Dispatchers.Main) {
                makeView()
                adapter.notifyDataSetChanged() //UI가 변경됨
            }
        }
    }
    // 데이터 출력부 adapter.items.add(MyData(data1.get(0),data1.get(1),data1.get(2)))에서 MyData안에 들어가는 걸 보시면됩니다. 0이 노래제목 1이 가수 2가 비디오 ID입니다
    private fun makeView (){
        val count:Int = myDBHelper.getattrCount()
        for(i in 2 until count+2){
            val data1 = myDBHelper.getinfo(i)
            adapter.items.add(MyData(data1.get(0),data1.get(1),data1.get(2)))
        }
    }

    private fun AllClear(){
        counter1 = true
        counter2 = 0
        mvena.clear()
        adapter.clear()
        myDBHelper.clear()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myDBHelper = MyDBHelper(context)
        myDBHelper.init1()
        //getlist호출전에 꼭 myDBHelper를 Init시켜주셔야 합니다.
        binding.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.addItemDecoration(
                DividerItemDecoration(context,
                        LinearLayoutManager.VERTICAL)
        )

        adapter = MyAdapter(ArrayList<MyData>())
        adapter.thiscontext = requireContext()
        adapter.thisactivity = requireActivity()
        binding!!.recyclerView.adapter = adapter


        binding!!.apply{
            hotBtn.setOnClickListener{
                AllClear()
                getNewlist1()
                makeView()
            }
            latestBtn.setOnClickListener{
                AllClear()
                getNewlist2()
                makeView()
            }
            valadBtn.setOnClickListener{
                AllClear()
                getNewlist3()
                makeView()
            }
            rapBtn.setOnClickListener{
                AllClear()
                getNewlist4()
                makeView()
            }
        }

    }
}