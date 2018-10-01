package com.bry.petfood.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bry.petfood.R;
import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ListItemActivity extends AppCompatActivity implements View.OnClickListener{
    private final String TAG = "ListItemActivity";
    @Bind(R.id.backBtnForList) ImageButton mBackBtnForList;
    @Bind(R.id.PickItemImage) ImageView mPickItemImage;
    @Bind(R.id.nameEditText) EditText mNameEditText;
    @Bind(R.id.quantityEditText) EditText mQuantityEditText;
    @Bind(R.id.DescriptionEditText)EditText mDescriptionEditText;
    @Bind(R.id.PriceEditText) EditText mPriceEditText;
    @Bind(R.id.SpinnerFeedbackType) Spinner SpinnerFeedbackType;
    @Bind(R.id.uploadBtn) CardView mUploadBtn;

    private static final int PICK_IMAGE_REQUEST = 234;
    private Uri mFilepath;
    private Context mContext;
    private Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_item);
        ButterKnife.bind(this);
        mContext = this.getApplicationContext();
        setListeners();
    }

    private void setListeners() {
        mBackBtnForList.setOnClickListener(this);
        mPickItemImage.setOnClickListener(this);
        mUploadBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if(v.equals(mBackBtnForList)){
            finish();
        }else if(v.equals(mPickItemImage)){
            openImagePicker();
        }else if(v.equals(mUploadBtn)){
            uploadItem();
        }
    }

    private void uploadItem() {

    }

    private void openImagePicker() {
        Log.d(TAG,"Starting intent for picking an image.");
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Log.d(TAG, "---Data gotten from activity is ok.");
            if (data.getData() != null) {
                mFilepath = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mFilepath);
                    imageBitmap = getResizedBitmap(bitmap,1300);
                    Glide.with(mContext).load(bitmapToByte(bitmap)).asBitmap().override(400, 400).into(mPickItemImage);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "---Unable to get and set image. " + e.getMessage());
                }
            } else {
                Log.e(TAG, "---Unable to work on the result code for some reason.");
                Toast.makeText(mContext,"the data.getData method returns null for some reason...", Toast.LENGTH_SHORT).show();
            }
        }

    }



    private byte[] bitmapToByte(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,80,baos);
        byte[] byteArray = baos.toByteArray();
        return byteArray;
    }

    private String encodeBitmapForFirebaseStorage(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,80,baos);
        String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        return imageEncoded;
    }

    private static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }

}
