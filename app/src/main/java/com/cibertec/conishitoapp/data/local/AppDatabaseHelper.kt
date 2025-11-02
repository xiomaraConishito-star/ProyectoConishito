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
            arrayOf("Rocky", "Perro", "11 meses", "Lima", null, "Cachorra jugueton, le encantan los paseos en parque.", "987654321"),
            arrayOf("Shira", "Perro", "4 años", "Lima", null, "Caracter fuerte y amorosa", "995365865"),
            arrayOf("Tora", "Gato", "4 años", "Arequipa", null, "Gatito tranquilo que disfruta dormir al sol y cazar ratones.", "912345678"),
            arrayOf("Chloe", "Perro", "10 meses", "Cusco", null, "Muy juguetona y se lleva bien con niños y ancianos.", "955112233"),
            arrayOf("Alaska", "Perro", "1 año", "Lima", null, "Sociable, está esterilizada y vacunada.", "998887766"),
            arrayOf("Popis", "Perro", "7 meses", "Lima", null, "Muy traviesa y risuena, le gusta jugar con gatos", "999650005"),
            arrayOf("Gato", "Gato", "7 meses", "Lima", null, "Solitario pero muy jugueton", "999569865")

        )

        pets.forEach { pet ->
            db.execSQL(
                "INSERT INTO mascota (nombre, tipo, edad, ciudad, foto, descripcion, contacto) VALUES (?, ?, ?, ?, ?, ?, ?)",
                pet
            )
        }
    }
}
