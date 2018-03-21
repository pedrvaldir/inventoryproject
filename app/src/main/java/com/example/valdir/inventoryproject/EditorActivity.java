package com.example.valdir.inventoryproject;

/**
 * Created by VALDIR on 13/03/2018.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.example.valdir.inventoryproject.Data.InventoryContract;

import static com.example.valdir.inventoryproject.Data.InventoryProvider.LOG_TAG;


public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int QUANT_INCREMENT = 1;
    private static final int EXISTING_PRODUCT_LOADER = 0;

    private Uri mCurrentProductUri;
    private Button mOrder;
    private EditText mNameEditText;
    public int QuantityNumber = QUANT_INCREMENT;
    private EditText mPriceEditText;
    private ImageView mImageView;
    private EditText mQuantityEditText;
    private TextView imageTextView;
    private Uri mImageUri;
    private EditText mPhoneNumberEditText;
    private TextView mTextView;
    private Button mPhotoButton;
    private Button mIncreaseButton;
    private Button mDecreaseButton;
    private boolean mProductHasChanged = false;
    private String imageString;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };

    private static final int PICK_IMAGE_REQUEST = 0;

    private static final String STATE_URI = "STATE_URI";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mCurrentProductUri = getIntent().getData();

        if (mCurrentProductUri == null) {

            setTitle(getString(R.string.editor_activity_title_new_product));

            invalidateOptionsMenu();
        } else
        if(mCurrentProductUri!=null){

            setTitle(getString(R.string.editor_activity_title_edit_product));

            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }
        mPhoneNumberEditText = (EditText) findViewById(R.id.edit_product_phonenumber);
        mIncreaseButton = (Button) findViewById(R.id.increaseButton);
        mDecreaseButton = (Button) findViewById(R.id.decreaseButton);
        mNameEditText = (EditText) findViewById(R.id.edit_product_name);
        mPriceEditText = (EditText) findViewById(R.id.edit_product_price);
        mQuantityEditText = (EditText) findViewById(R.id.edit_product_quantity);
        mPhotoButton = (Button) findViewById(R.id.SelectPhoto);
        mImageView = (ImageView) findViewById(R.id.ivPreview);
        mOrder = (Button) findViewById(R.id.orderButton);
        mOrder.setOnTouchListener(mTouchListener);
        mNameEditText.setOnTouchListener(mTouchListener);
        mIncreaseButton.setOnTouchListener(mTouchListener);
        mDecreaseButton.setOnTouchListener(mTouchListener);
        mPhoneNumberEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        imageTextView = (TextView) findViewById(R.id.image_textview);

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageSelector();
            }
        });

        mIncreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String quantitynumber = mQuantityEditText.getText().toString();
                if (TextUtils.isEmpty(quantitynumber)) {

                        Toast.makeText(EditorActivity.this, getString(R.string.quantity_empty), Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    QuantityNumber = Integer.parseInt(quantitynumber);
                    mQuantityEditText.setText(String.valueOf(QuantityNumber + 1));
                }
            }
        });

        //Para diminuir a quantidade
        mDecreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantitynumber = mQuantityEditText.getText().toString();
                if (TextUtils.isEmpty(quantitynumber)) {
                    Toast.makeText(EditorActivity.this, getString(R.string.quantity_empty), Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    QuantityNumber = Integer.parseInt(quantitynumber);
                    //To validate Qunitity is greater than 0
                    if ((QuantityNumber - 1) >= 0) {
                        mQuantityEditText.setText(String.valueOf(QuantityNumber - 1));
                    } else {
                        Toast.makeText(EditorActivity.this, getString(R.string.quantity_less), Toast.LENGTH_SHORT).show();
                        return;

                    }
                }
            }
        });

        mOrder.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:"+ mPhoneNumberEditText.getText().toString()));
                startActivity(callIntent);
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mImageUri != null)
            outState.putString(STATE_URI, mImageUri.toString());
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState.containsKey(STATE_URI) &&
                !savedInstanceState.getString(STATE_URI).equals("")) {
            mImageUri = Uri.parse(savedInstanceState.getString(STATE_URI));
            mTextView.setText(mImageUri.toString());

            ViewTreeObserver viewTreeObserver = mImageView.getViewTreeObserver();
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    mImageView.setImageBitmap(getBitmapFromUri(mImageUri));

                }
            });
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {


        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {


            if (resultData != null) {
                mImageUri = resultData.getData();
                Log.i(LOG_TAG, "Uri: " + mImageUri.toString());
                imageString = mImageUri.toString();
                mImageView.setImageURI(mImageUri);
                imageTextView.setVisibility(View.INVISIBLE);

            }
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) {
        if (uri == null || uri.toString().isEmpty())
            return null;

        InputStream input = null;

        try {
            input = this.getContentResolver().openInputStream(uri);

            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();

            bmOptions.inJustDecodeBounds = false;

            bmOptions.inPurgeable = true;

            input = this.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();
            return bitmap;

        } catch (FileNotFoundException fne) {
            Log.e(LOG_TAG, "Falha ao carregar imagem.", fne);
            return null;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Falha ao carregar imagem.", e);
            return null;
        } finally {
            try {
                input.close();
            } catch (IOException ioe) {

            }
        }
    }

    public void ImageSelector() {
        Intent intent;

        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }

        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_image)), PICK_IMAGE_REQUEST);
    }

    private void saveProduct() {

        String nameString = mNameEditText.getText().toString().trim();
        if (TextUtils.isEmpty(nameString)) {
            mNameEditText.setError(getString(R.string.name_product_empty));
            return;
        }

        String quantityString = mQuantityEditText.getText().toString().trim();
        if (TextUtils.isEmpty(quantityString)) {
            mQuantityEditText.setError(getString(R.string.quant_product_stock));
            return;
        }

        String priceString = mPriceEditText.getText().toString().trim();
        if (TextUtils.isEmpty(priceString)) {
            mPriceEditText.setError(getString(R.string.price_product_empty));
            return;
        }
        String phoneNumberString = mPhoneNumberEditText.getText().toString();
        if(TextUtils.isEmpty((phoneNumberString))){
            mPhoneNumberEditText.setError(getString(R.string.tel_product_empty));
            return;
        }

        QuantityNumber = Integer.parseInt(quantityString);

        ContentValues values = new ContentValues();
        values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME, nameString);
        values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE, priceString);
        values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY,Integer.toString(QuantityNumber));
        values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_IMAGE,imageString);
        values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_PHONENUMBER, phoneNumberString);

        if(imageString == null){
            Toast.makeText(this, getString(R.string.photo_product), Toast.LENGTH_SHORT).show();
            return;
        }

        if (mCurrentProductUri == null) {

            Uri newUri = getContentResolver().insert(InventoryContract.InventoryEntry.CONTENT_URI, values);


            if (newUri == null) {

                Toast.makeText(this, getString(R.string.editor_insert_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(this, getString(R.string.editor_insert_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        if(mCurrentProductUri!=null)    {

            int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);


            if (rowsAffected == 0) {

                Toast.makeText(this, getString(R.string.editor_update_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(this, getString(R.string.editor_update_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (mCurrentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveProduct();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                InventoryContract.InventoryEntry._ID,
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME,
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE,
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY,
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_IMAGE,
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_PHONENUMBER};

        return new CursorLoader(this,
                mCurrentProductUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {

            int nameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY);
            int priceColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE);
            int imageColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_IMAGE);
            int phoneColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_PHONENUMBER);

            String phone = cursor.getString(phoneColumnIndex).toString();
            String name = cursor.getString(nameColumnIndex);

            QuantityNumber = cursor.getInt(quantityColumnIndex);

            int price = cursor.getInt(priceColumnIndex);

            String image = cursor.getString(imageColumnIndex);
            Uri imageUri = Uri.parse(image);

            imageString = imageUri.toString();
            mPhoneNumberEditText.setText(phone);
            mNameEditText.setText(name);
            mPriceEditText.setText(Integer.toString(price));
            mQuantityEditText.setText(Integer.toString(QuantityNumber));
            mImageView.setImageURI(imageUri);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mImageView.setVisibility(View.INVISIBLE);
        mPhoneNumberEditText.setText("");
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {

        if (mCurrentProductUri != null) {

            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_inventory_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_inventory_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }
}