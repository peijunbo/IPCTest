package com.pjb.receiver3

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class MessageProvider : ContentProvider() {
    companion object {
        const val TAG = "MessageProvider"
        private var hasNewMessage = false
        private var newMessageId: Long = 0
        val receiveFlow: Flow<Long> = flow {
            while (true) {
                if (hasNewMessage) {
                    emit(newMessageId)
                    hasNewMessage = false
                }
                delay(1000)
            }
        }.flowOn(Dispatchers.IO)
    }
    private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)
    private val messageAll = 0
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
                val supportSQLiteDatabase = messageDatabase.openHelper.writableDatabase
                val id: Long
                try {
                    supportSQLiteDatabase.beginTransaction()
                    id = supportSQLiteDatabase.insert("Message", SQLiteDatabase.CONFLICT_NONE, values)
                    supportSQLiteDatabase.setTransactionSuccessful()
                } catch (e: Exception) {
                    Log.e(TAG, "insert: error!")
                    return null
                } finally {
                    supportSQLiteDatabase.endTransaction()
                    Log.d(TAG, "insert: $values")
                }
                hasNewMessage = true
                newMessageId = id
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