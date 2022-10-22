package com.pjb.receiver3

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.util.Log

class MessageProvider : ContentProvider() {
    companion object {
        const val TAG = "MessageProvider"
    }

    private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)
    private val messageAll = 0
    private val messageItem = 1
    private lateinit var messageDatabase: MessageDatabase
    private lateinit var messageDao: MessageDao

    init {
        uriMatcher.addURI("com.pjb.receiver3.provider", "Message", messageAll)
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        return 0
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        when (uriMatcher.match(uri)) {
            messageAll -> {
                Log.d(TAG, "insert: $values")
                var id: Long = 0
                id = messageDao.insertMessage(
                    Message(values?.get("content") as String)
                )
                return ContentUris.withAppendedId(uri, id)
            }
            else -> {
                return null
            }
        }
    }

    override fun onCreate(): Boolean {
        context?.let {
            messageDatabase = MessageDatabase.getDatabase(it)
            messageDao = messageDatabase.messageDao()
            Log.d(TAG, "onCreate: !!")
            return true
        }
        return false
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        return when (uriMatcher.match(uri)) {
            messageAll -> {
                messageDatabase.query(
                    "select * from Message",
                    arrayOf()
                )
            }
            else -> {
                null
            }
        }

    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        return 0
    }
}