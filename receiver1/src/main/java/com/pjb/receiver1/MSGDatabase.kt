package com.pjb.receiver1

import android.content.Context
import androidx.room.*

@Entity
data class Message(
    var content: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}

@Dao
interface MessageDao {
    @Insert
    fun insertMessage(msg: Message): Long
    @Query("select * from Message")
    fun loadAllMessages(): List<Message>

    @Delete
    fun deleteMessage(message: Message)
}

@Database(version = 1, entities = [Message::class])
abstract class MessageDatabase: RoomDatabase() {
    abstract fun messageDao(): MessageDao
    companion object {
        private var instance: MessageDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): MessageDatabase {
            instance?.let {
                return it
            }
            return Room.databaseBuilder(context.applicationContext,
                MessageDatabase::class.java, "message_database").build().apply {
                    instance = this
            }
        }
    }
}