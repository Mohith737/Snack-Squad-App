package com.example.snacksquad

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.snacksquad.util.SecurityUtils

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(database: SQLiteDatabase?) {
        database?.execSQL(
            "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_USERNAME TEXT PRIMARY KEY, " +
                "$COLUMN_PASSWORD TEXT NOT NULL, " +
                "$COLUMN_PASSWORD_RESET_REQUIRED INTEGER NOT NULL DEFAULT 0)"
        )
    }

    override fun onUpgrade(database: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (database == null || oldVersion >= newVersion) {
            return
        }

        if (oldVersion < 2) {
            migrateToVersion2(database)
        }
    }

    override fun onOpen(database: SQLiteDatabase?) {
        super.onOpen(database)
        if (database != null && !database.isReadOnly) {
            database.enableWriteAheadLogging()
        }
    }

    fun insertdata(username: String, password: String): Boolean {
        val writableDb = writableDatabase
        val cv = ContentValues()
        cv.put(COLUMN_USERNAME, username)
        cv.put(COLUMN_PASSWORD, SecurityUtils.hashPassword(username, password))
        cv.put(COLUMN_PASSWORD_RESET_REQUIRED, 0)
        val result = writableDb.insert(TABLE_NAME, null, cv)
        if (result == -1L) {
            return false
        }
        return true
    }

    fun checkuserpass(username: String, password: String): Boolean {
        val readableDb = readableDatabase
        val hashedPassword = SecurityUtils.hashPassword(username, password)
        val query = "SELECT 1 FROM $TABLE_NAME WHERE $COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ? " +
            "AND $COLUMN_PASSWORD_RESET_REQUIRED = 0"
        readableDb.rawQuery(query, arrayOf(username, hashedPassword)).use { cursor ->
            if (cursor.count <= 0) {
                return false
            }
        }
        return true
    }

    fun needsPasswordReset(username: String): Boolean {
        val readableDb = readableDatabase
        val query = "SELECT $COLUMN_PASSWORD_RESET_REQUIRED FROM $TABLE_NAME WHERE $COLUMN_USERNAME = ?"
        readableDb.rawQuery(query, arrayOf(username)).use { cursor ->
            if (!cursor.moveToFirst()) {
                return false
            }
            return cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD_RESET_REQUIRED)) == 1
        }
    }

    fun closeDatabase() {
        close()
    }

    private fun migrateToVersion2(database: SQLiteDatabase) {
        database.beginTransaction()
        try {
            if (tableExists(database, TABLE_NAME)) {
                database.execSQL("ALTER TABLE $TABLE_NAME RENAME TO ${TABLE_NAME}_legacy")
                onCreate(database)
                database.execSQL(
                    "INSERT INTO $TABLE_NAME ($COLUMN_USERNAME, $COLUMN_PASSWORD, $COLUMN_PASSWORD_RESET_REQUIRED) " +
                        "SELECT $COLUMN_USERNAME, '', 1 FROM ${TABLE_NAME}_legacy"
                )
                database.execSQL("DROP TABLE IF EXISTS ${TABLE_NAME}_legacy")
            } else {
                onCreate(database)
            }
            database.setTransactionSuccessful()
        } finally {
            database.endTransaction()
        }
    }

    private fun tableExists(database: SQLiteDatabase, tableName: String): Boolean {
        val query = "SELECT name FROM sqlite_master WHERE type = ? AND name = ?"
        database.rawQuery(query, arrayOf("table", tableName)).use { cursor ->
            return cursor.moveToFirst()
        }
    }

    companion object {
        private const val DATABASE_NAME = "Userdata"
        private const val DATABASE_VERSION = 2
        private const val TABLE_NAME = "Userdata"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_PASSWORD = "password"
        private const val COLUMN_PASSWORD_RESET_REQUIRED = "password_reset_required"
    }
}
