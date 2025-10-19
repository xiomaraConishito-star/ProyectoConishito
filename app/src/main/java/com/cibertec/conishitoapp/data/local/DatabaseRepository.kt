package com.cibertec.conishitoapp.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.cibertec.conishitoapp.data.Pet
import com.cibertec.conishitoapp.data.User

class DatabaseRepository(context: Context) {
    private val dbHelper = AppDatabaseHelper(context.applicationContext)

    fun registerUser(user: User): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("nombre", user.fullName)
            put("correo", user.email)
            put("clave", user.password)
            put("telefono", user.phone)
            put("ciudad", user.city)
        }

        return try {
            val id = db.insertOrThrow("usuario", null, values)
            id != -1L
        } catch (ex: Exception) {
            false
        }
    }

    fun loginUser(email: String, password: String): User? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "usuario",
            arrayOf("id_usuario", "nombre", "correo", "telefono", "ciudad"),
            "correo = ? AND clave = ?",
            arrayOf(email, password),
            null,
            null,
            null
        )

        cursor.use {
            if (it.moveToFirst()) {
                return User(
                    id = it.getLong(0),
                    fullName = it.getString(1),
                    email = it.getString(2),
                    password = password,
                    phone = it.getString(3),
                    city = it.getString(4)
                )
            }
        }
        return null
    }

    fun getPets(userId: Long?): List<Pet> {
        val db = dbHelper.readableDatabase
        val sql =
            """
            SELECT p.id_mascota, p.nombre, p.tipo, p.edad, p.ciudad, p.foto, p.descripcion, p.contacto,
            CASE WHEN f.id_favorito IS NULL THEN 0 ELSE 1 END AS is_favorite
            FROM mascota p
            LEFT JOIN favorito f ON p.id_mascota = f.id_mascota AND f.id_usuario = ?
            ORDER BY p.nombre COLLATE NOCASE
            """.trimIndent()

        val pets = mutableListOf<Pet>()
        val cursor = db.rawQuery(sql, arrayOf(userId?.toString() ?: "-1"))
        cursor.use {
            while (it.moveToNext()) {
                pets.add(
                    Pet(
                        id = it.getLong(0),
                        name = it.getString(1),
                        type = it.getString(2),
                        age = it.getString(3) ?: "",
                        city = it.getString(4) ?: "",
                        photoUrl = it.getString(5),
                        description = it.getString(6) ?: "",
                        contactPhone = it.getString(7),
                        isFavorite = it.getInt(8) == 1
                    )
                )
            }
        }
        return pets
    }

    fun getFavorites(userId: Long): List<Pet> {
        val db = dbHelper.readableDatabase
        val sql =
            """
            SELECT p.id_mascota, p.nombre, p.tipo, p.edad, p.ciudad, p.foto, p.descripcion, p.contacto
            FROM mascota p
            INNER JOIN favorito f ON p.id_mascota = f.id_mascota
            WHERE f.id_usuario = ?
            ORDER BY p.nombre COLLATE NOCASE
            """.trimIndent()

        val favorites = mutableListOf<Pet>()
        val cursor = db.rawQuery(sql, arrayOf(userId.toString()))
        cursor.use {
            while (it.moveToNext()) {
                favorites.add(
                    Pet(
                        id = it.getLong(0),
                        name = it.getString(1),
                        type = it.getString(2),
                        age = it.getString(3) ?: "",
                        city = it.getString(4) ?: "",
                        photoUrl = it.getString(5),
                        description = it.getString(6) ?: "",
                        contactPhone = it.getString(7),
                        isFavorite = true
                    )
                )
            }
        }
        return favorites
    }

    fun getPetById(petId: Long): Pet? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "mascota",
            arrayOf("id_mascota", "nombre", "tipo", "edad", "ciudad", "foto", "descripcion", "contacto"),
            "id_mascota = ?",
            arrayOf(petId.toString()),
            null,
            null,
            null
        )

        cursor.use {
            if (it.moveToFirst()) {
                return Pet(
                    id = it.getLong(0),
                    name = it.getString(1),
                    type = it.getString(2),
                    age = it.getString(3) ?: "",
                    city = it.getString(4) ?: "",
                    photoUrl = it.getString(5),
                    description = it.getString(6) ?: "",
                    contactPhone = it.getString(7),
                    isFavorite = false
                )
            }
        }
        return null
    }

    fun addFavorite(userId: Long, petId: Long) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("id_usuario", userId)
            put("id_mascota", petId)
        }
        db.insertWithOnConflict("favorito", null, values, SQLiteDatabase.CONFLICT_IGNORE)
    }

    fun removeFavorite(userId: Long, petId: Long) {
        val db = dbHelper.writableDatabase
        db.delete(
            "favorito",
            "id_usuario = ? AND id_mascota = ?",
            arrayOf(userId.toString(), petId.toString())
        )
    }

    fun isFavorite(userId: Long, petId: Long): Boolean {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "favorito",
            arrayOf("id_favorito"),
            "id_usuario = ? AND id_mascota = ?",
            arrayOf(userId.toString(), petId.toString()),
            null,
            null,
            null
        )
        cursor.use {
            return it.moveToFirst()
        }
    }
}
