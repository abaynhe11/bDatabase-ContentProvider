package com.google.firebase.quickstart.auth.abdatabase.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.text.TextUtils
import com.google.firebase.quickstart.auth.abdatabase.MyDBHandler

class MyContentProvider : ContentProvider() {


    private val PRODUCTS = 1
    private val PRODUCTS_ID = 2
    private var myDB: MyDBHandler? = null

    private val sURIMatcher = UriMatcher(UriMatcher.NO_MATCH)

    init {
        sURIMatcher.addURI(AUTHORITY, PRODUCTS_TABLE, PRODUCTS)
        sURIMatcher.addURI(AUTHORITY, PRODUCTS_TABLE + "/#",
            PRODUCTS_ID)
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
       // TODO("Implement this to handle requests to delete one or more rows")

        val uriType = sURIMatcher.match(uri)
        val sqlDB = myDB!!.writableDatabase
        val rowsDeleted: Int

        when (uriType) {
            PRODUCTS -> rowsDeleted = sqlDB.delete(MyDBHandler.TABLE_PRODUCTS,
                selection,
                selectionArgs)

            PRODUCTS_ID -> {
                val id = uri.lastPathSegment
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(MyDBHandler.TABLE_PRODUCTS,
                        MyDBHandler.COLUMN_ID + "=" + id,
                        null)
                } else {
                    rowsDeleted = sqlDB.delete(MyDBHandler.TABLE_PRODUCTS,
                        MyDBHandler.COLUMN_ID + "=" + id
                                + " and " + selection,
                        selectionArgs)
                }
            }
            else -> throw IllegalArgumentException("Unknown URI: " + uri)
        }
        context!!.contentResolver.notifyChange(uri, null)
        return rowsDeleted
    }

    override fun getType(uri: Uri): String? {
        TODO(
            "Implement this to handle requests for the MIME type of the data" +
                    "at the given URI"
        )
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        //TODO("Implement this to handle requests to insert a new row.")
        //throw UnsupportedOperationException("Not yet implemented")
        val uriType = sURIMatcher.match(uri)

        val sqlDB = myDB!!.writableDatabase

        val id: Long
        when (uriType) {
            PRODUCTS -> id = sqlDB.insert(MyDBHandler.TABLE_PRODUCTS, null, values)
            else -> throw IllegalArgumentException("Unknown URI: " + uri)
        }
        context!!.contentResolver.notifyChange(uri, null)
        return Uri.parse(PRODUCTS_TABLE + "/" + id)

    }

    override fun onCreate(): Boolean {
       // TODO("Implement this to initialize your content provider on startup.")
        myDB = MyDBHandler(context!!, null, null, 1)
        return false

    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        //TODO("Implement this to handle query requests from clients.")
        val queryBuilder = SQLiteQueryBuilder()
        queryBuilder.tables = MyDBHandler.TABLE_PRODUCTS

        val uriType = sURIMatcher.match(uri)

        when (uriType) {
            PRODUCTS_ID -> queryBuilder.appendWhere(MyDBHandler.COLUMN_ID + "="
                    + uri.lastPathSegment)
            PRODUCTS -> {
            }
            else -> throw IllegalArgumentException("Unknown URI")
        }

        val cursor = queryBuilder.query(myDB?.readableDatabase,
            projection, selection, selectionArgs, null, null,
            sortOrder)
        cursor.setNotificationUri(context!!.contentResolver,
            uri)
        return cursor

    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        //TODO("Implement this to handle requests to update one or more rows.")
        val uriType = sURIMatcher.match(uri)
        val sqlDB: SQLiteDatabase = myDB!!.writableDatabase
        val rowsUpdated: Int

        when (uriType) {
            PRODUCTS -> rowsUpdated = sqlDB.update(MyDBHandler.TABLE_PRODUCTS,
                values,
                selection,
                selectionArgs)
            PRODUCTS_ID -> {
                val id = uri.lastPathSegment
                if (TextUtils.isEmpty(selection)) {

                    rowsUpdated = sqlDB.update(MyDBHandler.TABLE_PRODUCTS,
                        values,
                        MyDBHandler.COLUMN_ID + "=" + id, null)

                } else {
                    rowsUpdated = sqlDB.update(MyDBHandler.TABLE_PRODUCTS,
                        values,
                        MyDBHandler.COLUMN_ID + "=" + id
                                + " and "
                                + selection,
                        selectionArgs)
                }
            }
            else -> throw IllegalArgumentException("Unknown URI: " + uri)
        }
        context!!.contentResolver.notifyChange(uri, null)
        return rowsUpdated
    }

    companion object {
        val AUTHORITY = "com.google.firebase.quickstart.auth.abdatabase.provider.MyContentProvider"
        private val PRODUCTS_TABLE = "products"
        val CONTENT_URI : Uri = Uri.parse("content://" + AUTHORITY + "/" +
                PRODUCTS_TABLE)
    }
}