package com.example.valdir.inventoryproject.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * Created by VALDIR on 19/03/2018.
 */

public class InventoryProvider extends ContentProvider {

    private InventoryHelperDb mDbHelper;

    private static final int PRODUCT = 0;

    private static final int PRODUCT_ID = 1;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);


    static {
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY,InventoryContract.PATH_INVENTORY, PRODUCT);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY,InventoryContract.PATH_INVENTORY+"/#", PRODUCT_ID);
    }
    @Override
    public boolean onCreate() {
        mDbHelper = new InventoryHelperDb(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor = null;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCT:
                cursor=database.query(InventoryContract.InventoryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,null,null,sortOrder);
                break;
            case  PRODUCT_ID:

                selection = InventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };


                cursor = database.query(InventoryContract.InventoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Não é possível consultar um URI desconhecido" + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCT:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Inserção não é suportada por" + uri);
        }
    }
    public static final String LOG_TAG = InventoryProvider.class.getSimpleName();

    private Uri insertProduct(Uri uri, ContentValues values) {
        // Check that the name is not null
        String name = values.getAsString(InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME);
        if (name == null) {
            Log.e(LOG_TAG, "Produto requer um nome");
            throw new IllegalArgumentException("Produto requer um nome");
        }

        Integer price = values.getAsInteger(InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE);
        if (price != null && price <= 0) {
            Log.e(LOG_TAG, "O produto deve ter um preço maior que 0 ");
            throw new IllegalArgumentException("Deve ter um preço maior que 0 ");
        }

        Integer quantity = values.getAsInteger(InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY);
        if(quantity!=null && quantity<0){
            Log.e(LOG_TAG, "Você não pode adicionar um produto que está fora de estoque");
            throw new IllegalArgumentException("Você não pode adicionar um produto que está fora de estoque");

        }
        String images = values.getAsString(InventoryContract.InventoryEntry.COLUMN_INVENTORY_IMAGE);
        if(images==null ){
            Log.e(LOG_TAG, "Você deve ter uma imagem");
            throw new IllegalArgumentException("você deve ter uma imagem");

        }
        String phoneNumber = values.getAsString(InventoryContract.InventoryEntry.COLUMN_INVENTORY_PHONENUMBER);
        if (phoneNumber == null) {
            Log.e(LOG_TAG, "Produto requer um número de telefone");
            throw new IllegalArgumentException("Produto requer um número de telefone");
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(InventoryContract.InventoryEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Falha ao inserir linha para " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri,null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCT:
                return updateInvetory(uri, contentValues, selection, selectionArgs);
            case  PRODUCT_ID:
                selection = InventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateInvetory(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("A atualização não é suportada por " + uri);
        }
    }


    private int updateInvetory(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME)) {
            String name = values.getAsString(InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME);
            if (name == null) {
                Log.e(LOG_TAG, "Produto requer um nome");
                throw new IllegalArgumentException("Produto requer um nome");
            }
        }

        if (values.containsKey(InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY)) {

            Integer quantity = values.getAsInteger(InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY);
            if (quantity != null && quantity < 0) {
                Log.e(LOG_TAG, "O produto não pode ser menor que zero");
                throw new IllegalArgumentException("O produto não pode ser menor que zero");
            }
        }

        if(values.containsKey(InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE)){
            Integer price = values.getAsInteger(InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE);
            if(price!=null && price <=0){
                Log.e(LOG_TAG, "O produto deve custar mais do que zero ");
                throw new IllegalArgumentException("O produto deve custar mais do que zero");
            }
        }
        if (values.containsKey(InventoryContract.InventoryEntry.COLUMN_INVENTORY_PHONENUMBER)) {
            String phoneNumber = values.getAsString(InventoryContract.InventoryEntry.COLUMN_INVENTORY_PHONENUMBER);
            if (phoneNumber == null) {
                Log.e(LOG_TAG, "Produto requer um número de telefone");
                throw new IllegalArgumentException("Produto requer um número de telefone");
            }
        }
        if (values.containsKey(InventoryContract.InventoryEntry.COLUMN_INVENTORY_IMAGE)) {
            String images = values.getAsString(InventoryContract.InventoryEntry.COLUMN_INVENTORY_IMAGE);
            if (images == null) {
                Log.e(LOG_TAG, "Você deve ter uma imagem");
                throw new IllegalArgumentException("você deve ter uma imagem");

            }
        }
        if (values.size() == 0) {
            return 0;
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Retorna o número de linhas do banco de dados afetadas pela instrução de atualização
        int rowsUpdated = database.update(InventoryContract.InventoryEntry.TABLE_NAME,values,selection,selectionArgs);
        if(rowsUpdated!=0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowsUpdated;
    }

    /**
     * Exclua os dados nos argumentos de seleção e seleção fornecidos.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCT:
                rowsDeleted =database.delete(InventoryContract.InventoryEntry.TABLE_NAME,selection,selectionArgs);
                break;
            case  PRODUCT_ID:
                selection = InventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(InventoryContract.InventoryEntry.TABLE_NAME,selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("A exclusão não é suportada para " + uri);
        }
        if(rowsDeleted !=0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowsDeleted;
    }


    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCT:
                return InventoryContract.InventoryEntry.CONTENT_LIST_TYPE;
            case  PRODUCT_ID:
                return InventoryContract.InventoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("URI desconhecida " + uri + " com correspondência " + match);
        }
    }
}
