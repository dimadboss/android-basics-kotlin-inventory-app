package com.example.inventory.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.inventory.utils.SQLCipherUtils
import net.sqlcipher.database.SupportFactory

const val DBName = "item_database"

@Database(entities = [Item::class], version = 6, exportSchema = false)
abstract class ItemRoomDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao

    companion object {
        @Volatile
        private var INSTANCE: ItemRoomDatabase? = null
        fun getDatabase(context: Context, passphrase: ByteArray): ItemRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val dbFile = context.getDatabasePath(DBName)
                val state = SQLCipherUtils.getDatabaseState(context, DBName)

                if (state == SQLCipherUtils.State.UNENCRYPTED) {
                    SQLCipherUtils.encrypt(context, dbFile, passphrase)
                }

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ItemRoomDatabase::class.java,
                    DBName,
                ).fallbackToDestructiveMigration()
                    .openHelperFactory(SupportFactory(passphrase))
                    .build()
                INSTANCE = instance
                return instance
            }
        }

    }
}