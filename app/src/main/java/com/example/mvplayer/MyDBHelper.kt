package com.example.mvplayer

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDBHelper(val context: Context?) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    companion object{
        val DB_NAME = "mydb.db"
        val DB_VERSION = 1
        val TABLE_NAME = "products"
        val SONGTITLE = "songtitle"
        val SINGER = "singer"
        val VIDEOID = "videoid"

    }

    //최승훈 추가 부분
    fun clear(){
        val strsql = "delete from $TABLE_NAME;"
        val create_table = "CREATE TABLE if not exists $TABLE_NAME ("+
                "$SONGTITLE text,"+
                "$SINGER text,"+
                "$VIDEOID text);"
        val db = writableDatabase
        db!!.execSQL(strsql)
        db!!.execSQL(create_table)
        db.close()

    }
    //ㅡㅡㅡㅡㅡ
    fun getinfo(attrcount: Int):ArrayList<String> {
        var info = ArrayList<String>()
        val strsql = "select * from $TABLE_NAME"
        val db = readableDatabase
        val cursor = db.rawQuery(strsql, null)
        cursor.moveToFirst()
        for(i in 1 until attrcount-1){
            cursor.moveToNext()
        }
        info.add(cursor.getString(cursor.getColumnIndex("songtitle")))
        info.add(cursor.getString(cursor.getColumnIndex("singer")))
        info.add(cursor.getString(cursor.getColumnIndex("videoid")))
        return info
    }
    fun getattrCount():Int{
        val strsql = "select * from $TABLE_NAME;"
        val db = readableDatabase
        val cursor = db.rawQuery(strsql, null)
        cursor.moveToFirst()
        val attrcount = cursor.getCount()
        cursor.close()
        db.close()
        return attrcount
    }

    fun insertProduct(product: MyData):Boolean{
        val values = ContentValues()
        values.put(SONGTITLE, product.songtitle)
        values.put(SINGER, product.singer)
        values.put(VIDEOID, product.videoId)
        val db = writableDatabase
        val flag = db.insert(TABLE_NAME, null, values)>0
        db.close()
        return flag
    }
    fun create2(db:SQLiteDatabase?){
        val create_table = "CREATE TABLE if not exists $TABLE_NAME ("+
                "$SONGTITLE text,"+
                "$SINGER text,"+
                "$VIDEOID text);"
        db!!.execSQL(create_table)
    }
    fun init1(){
        init2(this.writableDatabase)
        create2(this.writableDatabase)
    }
    fun init2(db: SQLiteDatabase?){
        val drop_table = "drop table if exists $TABLE_NAME;"
        db!!.execSQL(drop_table)
    }
    override fun onCreate(db: SQLiteDatabase?) {
        val create_table = "CREATE TABLE if not exists $TABLE_NAME ("+
                "$SONGTITLE text,"+
                "$SINGER text,"+
                "$VIDEOID text);"
        db!!.execSQL(create_table)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val drop_table = "drop table if exists $TABLE_NAME;"
        db!!.execSQL(drop_table)
    }

}