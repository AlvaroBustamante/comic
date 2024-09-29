package pe.bustamante

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities=arrayOf(ComicEntity::class),version=1)

abstract class ComicDatabase: RoomDatabase() {
    //se define método abstracto
    abstract fun comicDao(): ComicDAO
}