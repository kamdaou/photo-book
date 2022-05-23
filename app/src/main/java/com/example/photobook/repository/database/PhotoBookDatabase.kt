package com.example.photobook.repository.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.photobook.data.Image
import com.example.photobook.data.Media
import com.example.photobook.data.Post
import com.example.photobook.data.User
import com.example.photobook.utils.Converter

@Database(
    entities = [
        User::class,
        Post::class,
        Media::class,
        Image::class
    ],
    version = 2,
    exportSchema = false)
@TypeConverters(Converter::class)
abstract class PhotoBookDatabase: RoomDatabase()
{
    abstract val photoBookDao:PhotoBookDao

    companion object {
        @Volatile
        private var INSTANCE: PhotoBookDatabase? = null

        /**
         * getInstance - gets an instance of the local db
         *
         * @context: Application context
         *
         * Return: an instance of photoBookDatabase class
         */
        fun getInstance(context: Context): PhotoBookDatabase
        {
            synchronized(this){
                var instance = INSTANCE
                if (instance == null){
                    instance = Room.databaseBuilder(context,
                        PhotoBookDatabase::class.java,
                        "asteroid_database")
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
