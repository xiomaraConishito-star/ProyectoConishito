package com.cibertec.conishitoapp.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

private const val DATABASE_NAME = "conishito.db"
private const val DATABASE_VERSION = 2

class AppDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE usuario (
                id_usuario INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                nombre TEXT NOT NULL,
                correo TEXT UNIQUE NOT NULL,
                clave TEXT NOT NULL,
                telefono TEXT,
                ciudad TEXT
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE mascota (
                id_mascota INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                nombre TEXT NOT NULL,
                tipo TEXT NOT NULL,
                edad TEXT,
                ciudad TEXT,
                foto TEXT,
                descripcion TEXT,
                contacto TEXT
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE favorito (
                id_favorito INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                id_usuario INTEGER NOT NULL,
                id_mascota INTEGER NOT NULL,
                UNIQUE(id_usuario, id_mascota),
                FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario) ON DELETE CASCADE,
                FOREIGN KEY (id_mascota) REFERENCES mascota (id_mascota) ON DELETE CASCADE
            )
            """.trimIndent()
        )

        seedPets(db)
    }
//comando para borrar tablas mas adelante en la app
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS favorito")
        db.execSQL("DROP TABLE IF EXISTS mascota")
        db.execSQL("DROP TABLE IF EXISTS usuario")
        onCreate(db)
    }

    private fun seedPets(db: SQLiteDatabase) {
        val pets = listOf(
            arrayOf("Luna", "Perro", "4 meses", "Lima", null, "Cachorra cariñosa, le encantan los paseos en parque.", "987654321"),
            arrayOf("Milo", "Gato", "1 año", "Arequipa", null, "Gatito tranquilo que disfruta dormir al sol.", "912345678"),
            arrayOf("Kira", "Perro", "8 meses", "Cusco", null, "Muy juguetona y se lleva bien con niños.", "955112233"),
            arrayOf("Nala", "Gato", "3 años", "Lima", null, "Gata sociable, está esterilizada y vacunada.", "998887766"),
            arrayOf("Shira", "Perro", "4 años", "Lima", null, "La mejor de todas", "999965865")
        )

        pets.forEach { pet ->
            db.execSQL(
                "INSERT INTO mascota (nombre, tipo, edad, ciudad, foto, descripcion, contacto) VALUES (?, ?, ?, ?, ?, ?, ?)",
                pet
            )
        }
    }
}
