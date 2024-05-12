package com.example.movies.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.movies.model.Genre
import com.example.movies.model.Movie
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Database(entities = [Movie::class], version = 4, exportSchema = false)
@TypeConverters(GenreTypeConverter::class)
abstract class MovieDatabase : RoomDatabase() {

    abstract fun movieDao(): MovieDao

    companion object {
        @Volatile
        private var Instance: MovieDatabase? = null

        fun getDatabase(context: Context): MovieDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, MovieDatabase::class.java, "movie_database")
                    /**
                     * Setting this option in your app's database builder means that Room
                     * permanently deletes all data from the tables in your database when it
                     * attempts to perform a migration with no defined migration path.
                     */
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}

class GenreTypeConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromGenreList(genres: List<Genre>): String {
        return gson.toJson(genres)
    }

    @TypeConverter
    fun toGenreList(genresString: String): List<Genre> {
        val type = object : TypeToken<List<Genre>>() {}.type
        return gson.fromJson(genresString, type)
    }
}
