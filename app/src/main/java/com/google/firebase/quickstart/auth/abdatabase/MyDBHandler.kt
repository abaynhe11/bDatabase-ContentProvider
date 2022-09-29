package com.google.firebase.quickstart.auth.abdatabase

import android.content.ContentResolver
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.Context
import android.content.ContentValues
import com.google.firebase.quickstart.auth.abdatabase.provider.MyContentProvider

class MyDBHandler(context: Context, name: String?,
                  factory: SQLiteDatabase.CursorFactory?, version: Int) :
    SQLiteOpenHelper(context, DATABASE_NAME,
        factory, DATABASE_VERSION) {

    private val myCR: ContentResolver

    init {
        myCR = context.contentResolver
    }

    override fun onCreate(db: SQLiteDatabase) {

        val CREATE_PRODUCTS_TABLE = ("CREATE TABLE " +
                TABLE_PRODUCTS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY," +
                COLUMN_PRODUCTNAME
                + " TEXT," + COLUMN_QUANTITY + " INTEGER" + ")")
        db.execSQL(CREATE_PRODUCTS_TABLE)

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int,
                           newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS)
        onCreate(db)

    }
    fun addProduct(product: Product) {

        val values = ContentValues()
        values.put(COLUMN_PRODUCTNAME, product.productName)
        values.put(COLUMN_QUANTITY, product.quantity)

        myCR.insert(MyContentProvider.CONTENT_URI, values)
    }

    fun findProduct(productname: String): Product? {
        val projection = arrayOf(COLUMN_ID, COLUMN_PRODUCTNAME, COLUMN_QUANTITY)

        val selection = "productname = \"" + productname + "\""

        val cursor = myCR.query(MyContentProvider.CONTENT_URI,
            projection, selection, null, null)

        var product: Product? = null

        if (cursor!!.moveToFirst()) {
            cursor.moveToFirst()
            val id = Integer.parseInt(cursor.getString(0))
            val productName = cursor.getString(1)
            val quantity = Integer.parseInt(cursor.getString(2))

            product = Product(id, productname, quantity)
            cursor.close()
        }
        return product
    }

    fun deleteProduct(productname: String): Boolean {

        var result = false

        val selection = "productname = \"" + productname + "\""

        val rowsDeleted = myCR.delete(MyContentProvider.CONTENT_URI,
            selection, null)

        if (rowsDeleted > 0)
            result = true

        return result
    }
    companion object {

        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "productDB.db"
        val TABLE_PRODUCTS = "products"

        val COLUMN_ID = "_id"
        val COLUMN_PRODUCTNAME = "productname"
        val COLUMN_QUANTITY = "quantity"
    }
}