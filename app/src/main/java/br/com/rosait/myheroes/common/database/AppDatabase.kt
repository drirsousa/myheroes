package br.com.rosait.myheroes.common.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import br.com.rosait.myheroes.common.dao.ItemDao
import br.com.rosait.myheroes.common.entity.ItemEntity

@Database(entities = arrayOf(ItemEntity::class), version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun itemDao() : ItemDao

    companion object {
        var INSTANCE : AppDatabase? = null

        fun getDatabaseBuilder(context: Context) : AppDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context, AppDatabase::class.java, "database_app")
                    .allowMainThreadQueries()
                    .build()
            }
            return INSTANCE!!
        }

        fun destroyInstance(){
            INSTANCE = null
        }
    }
}