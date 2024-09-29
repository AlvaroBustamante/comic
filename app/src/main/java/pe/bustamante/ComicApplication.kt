package pe.bustamante

import android.app.Application
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class ComicApplication: Application() {
    //nos permite acceder desde cualquier punto de la App
    companion object {
        lateinit var database: ComicDatabase
    }

    //al momento de crearse la Activity se llama al database
    override fun onCreate()
    {
        super.onCreate()

        //modificación de la tabla
        //val MIGRATION_1_2=object: Migration(1,2) {
        //    override fun migrate(database: SupportSQLiteDatabase) {
        //        database.execSQL("alter table ComicEntity add column photoUrl text not null default ''")
        //    }
        //}

        //actualizamos la BD con la migración a la versión 2
        database= Room.databaseBuilder(this,ComicDatabase::class.java,"ComicDatabase")
        //    .addMigrations(MIGRATION_1_2)
            .build()
    }
}