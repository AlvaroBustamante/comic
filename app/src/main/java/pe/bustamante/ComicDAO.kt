package pe.bustamante

import androidx.room.*

@Dao
interface ComicDAO {
    @Insert
    fun addComic(comicEntity: ComicEntity): Long

    @Update
    fun updateComic(comicEntity: ComicEntity)

    @Delete
    fun deleteComic(comicEntity: ComicEntity)

    @Query("select * from ComicEntity")
    fun getAllComic(): MutableList<ComicEntity>

    @Query("select * from ComicEntity where id=:id")
    fun getComicById(id:Long): ComicEntity
}