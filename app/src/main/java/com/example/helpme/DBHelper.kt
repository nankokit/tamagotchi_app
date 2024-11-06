package com.example.helpme

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(private val context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, "app", factory, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        val queryUser =
            "CREATE TABLE IF NOT EXISTS Users (id INTEGER PRIMARY KEY, login TEXT UNIQUE, pass TEXT)"
        db!!.execSQL(queryUser)
        val queryEntry =
            "CREATE TABLE IF NOT EXISTS Entries (id INTEGER PRIMARY KEY, user_id INTEGER, title TEXT, date TEXT, text TEXT, images TEXT)"
        db.execSQL(queryEntry)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS Users")
        db.execSQL("DROP TABLE IF EXISTS Entries")
        onCreate(db)
    }
}