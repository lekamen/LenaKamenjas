package com.example.lekamen.kolokvij2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by lekamen on 1/26/18.
 */

public class DBAdapter {

    static final String TAG = "DBAdapter";
    //stupci baze kolači
    static final String KEY_ID = "id";
    static final String KEY_NAZIV = "naziv";
    static final String KEY_VRSTA = "vrsta";
    static final String KEY_SASTOJAK = "glavni_sastojak";

    //stupci baze cijene
    static final String KEY_ID_SASTOJKA = "id_sastojka";
    static final String KEY_CIJENA = "cijena";
    static final String KEY_FOREIGN_ID = "id";

    //baza
    static final String DATABASE_NAME = "MyDB";
    static final String DATABASE_CAKES = "kolaci";
    static final String DATABASE_PRICES = "cijene";
    static final int DATABASE_VERSION = 1;

    static final String DATABASE_CREATE_CAKES =
            "create table " + DATABASE_CAKES + " (id integer primary key autoincrement, "
                    + "naziv text not null, vrsta text not null, glavni_sastojak text not null);";

    static final String DATABASE_CREATE_PRICES =
            "create table " + DATABASE_PRICES + " (id_sastojka integer primary key autoincrement, "
                    + "cijena real not null, id integer not null);";

    final Context context;
    DatabaseHelper DBHelper;
    SQLiteDatabase db;

    public DBAdapter(Context ctx) {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            try {
                sqLiteDatabase.execSQL(DATABASE_CREATE_CAKES);
                sqLiteDatabase.execSQL(DATABASE_CREATE_PRICES);
            } catch(SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            Log.w(TAG, "Upgrading db from " + i + " to " + i1);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DATABASE_CAKES);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DATABASE_PRICES);
            onCreate(sqLiteDatabase);
        }
    }

    public DBAdapter open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        DBHelper.close();
    }

    public long insertCake(String naziv, String vrsta, String glavniSastojak) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAZIV, naziv);
        values.put(KEY_VRSTA, vrsta);
        values.put(KEY_SASTOJAK, glavniSastojak);
        return db.insert(DATABASE_CAKES, null, values);
    }

    public long insertPrice(double cijena, long id) {
        ContentValues values = new ContentValues();
        values.put(KEY_CIJENA, cijena);
        values.put(KEY_FOREIGN_ID, id);
        return db.insert(DATABASE_PRICES, null, values);
    }

    public boolean deleteCakeAndPrice(long id) {
        return db.delete(DATABASE_CAKES, KEY_ID + "=" + id, null) > 0 &&
                db.delete(DATABASE_PRICES, KEY_FOREIGN_ID + "=" + id, null) > 0;
    }

    public Cursor getAllCakes() {
        return db.query(DATABASE_CAKES, null, null, null, null, null, null);
    }

    public Cursor getAllPrices() {
        return db.query(DATABASE_PRICES, null, null, null, null, null, null);
    }

    public Cursor[] getAllCakesAndPrices() {
        return new Cursor[] {getAllCakes(), getAllPrices()};
    }

    public Cursor getCakeWithIngredient(String sastojak) {
        Cursor c = db.query(DATABASE_CAKES, null, KEY_SASTOJAK + " LIKE '" + sastojak + "'", null,
                null, null, null, null);
        if(c != null)
            c.moveToFirst();
        return c;
    }

}
