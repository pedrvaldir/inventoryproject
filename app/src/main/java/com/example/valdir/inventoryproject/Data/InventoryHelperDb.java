package com.example.valdir.inventoryproject.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.valdir.inventoryproject.Data.InventoryContract.InventoryEntry;

/**
 * Created by VALDIR on 13/03/2018.
 */

public class InventoryHelperDb extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "inventory.db";
    public static final int DATABASE_VERSION = 1;

    public InventoryHelperDb(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    public InventoryHelperDb(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_INVENTORY_TABLE = "CREATE TABLE "+ InventoryContract.InventoryEntry.TABLE_NAME+"("
                +  InventoryEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                +  InventoryEntry.COLUMN_INVENTORY_NAME+" TEXT UNIQUE NOT NULL, "
                +  InventoryEntry.COLUMN_INVENTORY_PRICE+" INTEGER NOT NULL DEFAULT 0, "
                +  InventoryEntry.COLUMN_INVENTORY_QUANTITY+" INTEGER NOT NULL DEFAULT 0, "
                +  InventoryEntry.COLUMN_INVENTORY_IMAGE+" TEXT NOT NULL, "
                +  InventoryEntry.COLUMN_INVENTORY_PHONENUMBER+" TEXT NOT NULL);";

        db.execSQL(SQL_CREATE_INVENTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
