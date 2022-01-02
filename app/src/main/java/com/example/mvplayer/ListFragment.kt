package com.example.mvplayer

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mvplayer.databinding.FragmentListBinding
import com.google.firebase.database.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ListFragment : Fragment() {
    lateinit var binding: FragmentListBinding
    lateinit var rdb: DatabaseReference
    lateinit var layoutManager: LinearLayoutManager
    lateinit var adapter2: FDBAdapter

    var data:ArrayList<DBData> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListBinding.inflate(layoutInflater, container, false)

        var items = arrayOf("기본","많이 플레이 한 순", "최근 플레이 한 순")
        val spinnerAdapter = ArrayAdapter(requireContext(), R.layout.support_simple_spinner_dropdown_item, items)
        binding.spinner.adapter = spinnerAdapter

        initData()
        init()

        return binding!!.root
    }

    private fun init() {
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapter2 = FDBAdapter(data)
        adapter2.itemClickListener = object : FDBAdapter.OnItemClickListener {
            override fun OnItemClick(
                holder: FDBAdapter.ViewHolder,
                view: View,
                data: DBData,
                position: Int
            ) {
                var id = data.videoid
                var txt = data.title
                Click(id, txt)
            }
        }
        val simpleCallBack = object: ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.DOWN or ItemTouchHelper.UP,
            ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                rdb.child(adapter2.removeItem(viewHolder.adapterPosition)).removeValue()
            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleCallBack)
        binding.apply {
            recyclerview.layoutManager = layoutManager
            recyclerview.adapter = adapter2

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    when(position) {
                        0 -> {
                            data.sortBy{it.title}
                        }
                        1 -> {
                            data.sortByDescending{it.playtime}
                        }
                        2 -> {
                            data.sortByDescending{it.playdate}
                        }
                    }
                    adapter2.notifyDataSetChanged()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
            }

            itemTouchHelper.attachToRecyclerView(recyclerview)

            clearbtn.setOnClickListener {
                rdb.removeValue()
                adapter2.notifyDataSetChanged()
            }
        }
    }

    fun Click(id : String, txt : String) {
        val nowtime = Calendar.getInstance().time
        var dateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.KOREA).format(nowtime)

        if(played(id)) {
            var tmpplaytime = 0
            for(i in 0 until data.size) {
                if(txt == data[i].title) {
                    tmpplaytime = data[i].playtime + 1
                    data[i].playdate = dateFormat
                    data[i].playtime++
                }
            }
            val item = DBData(id, txt, tmpplaytime, dateFormat)
            rdb.child(txt).setValue(item)
        } else {
            val item = DBData(id, txt, 1, dateFormat)
            rdb.child(txt).setValue(item)
            data.add(0, DBData(id, txt,1, dateFormat))
        }
        adapter2.notifyDataSetChanged()
        val newIntent = Intent(context, YoutubeviewActivity::class.java)
        newIntent.putExtra("ytViewIntent", id)
        startActivity(newIntent)
    }

    fun initData() {
        rdb = FirebaseDatabase.getInstance().getReference("videos/id")
        rdb.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children) {
                    var title = i.key.toString()
                    var id = snapshot.child("$title/videoid").value.toString()
                    var playdate = snapshot.child("$title/playdate").value.toString()
                    var playtime = snapshot.child("$title/playtime").value.toString()
                    data.add(DBData(id,title,playtime.toInt(),playdate))
                    adapter2.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    fun played(id : String):Boolean {
        var boolean = true
        runBlocking {
            val job = launch {
                for(i in 0 until data.size) {
                    if(id == data[i].title) {
                        boolean = true
                    }
                }
            }
            job.join()
        }
        return boolean
    }

}