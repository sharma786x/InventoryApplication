package example.com.android.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import example.com.android.inventoryapp.data.InventoryContract.ProductEntry;

public class DetailLayout extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PRODUCT_LOADER = 0;

    private Uri currentProductUri;

    private TextView mName;
    private TextView mQuantity;
    private TextView mSupplier;

    private Button btnIncrease;
    private Button btnDecrease;
    private Button btnOrder;
    private Button btnDelete;
    private ImageView imageView;

    private boolean mPetHasChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_layout);
        Intent intent = getIntent();

        final long id = intent.getLongExtra("id", -1);
        mName=(TextView)findViewById(R.id.detailName);
        mQuantity=(TextView)findViewById(R.id.detailQuantity);
        mSupplier=(TextView)findViewById(R.id.detailSupplier);

        imageView=(ImageView)findViewById(R.id.proImage);
        currentProductUri= ContentUris.withAppendedId(ProductEntry.CONTENT_URI,id);
        btnIncrease=(Button)findViewById(R.id.btnIncrease);
        btnDecrease=(Button)findViewById(R.id.btnDecrease);
        btnOrder=(Button)findViewById(R.id.btnOrder);
        btnDelete=(Button)findViewById(R.id.btnDelete);

        btnIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    ContentValues values = new ContentValues();
                    values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, Integer.parseInt(mQuantity.getText().toString().trim()) + 1);
                    getContentResolver().update(currentProductUri, values, null, null);

            }
        });
        btnDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Integer.parseInt(mQuantity.getText().toString().trim())>0) {
                    ContentValues values = new ContentValues();
                    values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, Integer.parseInt(mQuantity.getText().toString().trim()) - 1);

                    getContentResolver().update(currentProductUri, values, null, null);
                }
            }
        });
        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL,new String[]{ mSupplier.getText().toString()});
                startActivity(emailIntent);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContentResolver().delete(currentProductUri,null, null);
                Intent i=new Intent(getBaseContext(),MainActivity.class);
                startActivity(i);
            }
        });

        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER,
                ProductEntry.COLUMN_PRODUCT_IMAGE
        };

        return new CursorLoader(this,currentProductUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {

            String name = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_NAME));
            int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_QUANTITY));
            String supplier=cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_SUPPLIER));

            mName.setText(name);
            mQuantity.setText(Integer.toString(quantity));
            mSupplier.setText(supplier);
//            byte[] image = cursor.getBlob(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_IMAGE));
//            Bitmap bitmap= BitmapFactory.decodeByteArray(image,0,image.length);
//            imageView.setImageBitmap(bitmap);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mName.setText("");
        mSupplier.setText("");
        mQuantity.setText("");
    }
    
}
