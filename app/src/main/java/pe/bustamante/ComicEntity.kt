package pe.bustamante

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName="ComicEntity")
data class ComicEntity(@PrimaryKey(autoGenerate=true) var id:Long=0,
                       var name:String,
                       var descripcion:String="",
                       var categoria: String,
                       var website:String="",
                       var photoUrl: String,
                       var isFavorite:Boolean=false) {
    //equals and hashCode -> evitar duplicidad en la propiedad ID
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ComicEntity

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}