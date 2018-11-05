package com.bry.petfood.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.bry.petfood.Constants;
import com.bry.petfood.Models.FoodItem;
import com.bry.petfood.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    private ProgressDialog mAuthProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_item);
        ButterKnife.bind(this);
        mContext = this.getApplicationContext();
        setListeners();
        createProgressDialog();
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


    private void uploadItem() {
        String name = mNameEditText.getText().toString().trim();
        String desc = mDescriptionEditText.getText().toString().trim();
        Bitmap bm = imageBitmap;
        String quantityAvailable = mQuantityEditText.getText().toString().trim();
        String price = mPriceEditText.getText().toString().trim();
        String category = SpinnerFeedbackType.getSelectedItem().toString();

        if(isValidName(name) && isValidDescription(desc) && isImageValid(bm) && isValidQuantity(quantityAvailable) && isValidPrice(price)){
            showProg();
            String encodedImageToUpload = encodeBitmapForFirebaseStorage(bm);
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            DatabaseReference adRef = FirebaseDatabase.getInstance().getReference(Constants.LISTED_ITEMS);
            DatabaseReference pushRef = adRef.push();
            String pushId = pushRef.getKey();

            DatabaseReference adRef2 = FirebaseDatabase.getInstance().getReference(Constants.USERS)
                    .child(uid).child(Constants.MY_LISTED_ITEMS);
            DatabaseReference pushRef2 = adRef2.push();
            pushRef2.setValue(pushId);

            FoodItem item = new FoodItem(name,pushId,desc,Double.parseDouble(quantityAvailable),Double.parseDouble(price),category);
            pushRef.setValue(item);

            DatabaseReference adRef3 = FirebaseDatabase.getInstance().getReference(Constants.LISTED_ITEMS_IMAGES)
                    .child(pushId).child(Constants.IMG1);
            adRef3.setValue(encodedImageToUpload).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    hideProg();
                    Toast.makeText(mContext,"Uploaded Successfully",Toast.LENGTH_SHORT).show();
                }
            });


        }

    }

    private boolean isValidName(String name){
        if(name.equals("")){
            mNameEditText.setError("A name is required");
            return false;
        }

        return true;
    }

    private boolean isValidDescription(String desc){
        if(desc.equals("")){
            mDescriptionEditText.setError("Details are required");
            return false;
        }
        return true;
    }

    private boolean isImageValid(Bitmap image){
        if(image==null){
            Toast.makeText(mContext,"An image is required",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean isValidQuantity(String quant){
        try{
            Integer x = Integer.parseInt(quant);
            if(x<1){
                mQuantityEditText.setError("Thats not valid");
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
            mQuantityEditText.setError("Thats not a number");
            return false;
        }

        return true;
    }

    private boolean isValidPrice(String price){
        try{
            Integer x = Integer.parseInt(price);
            if(x<1){
                mQuantityEditText.setError("That's not valid");
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
            mQuantityEditText.setError("Thats not a number");
            return false;
        }

        return true;
    }


    private boolean isNetworkConnected(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected());
    }


    private void createProgressDialog(){
        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle(R.string.app_name);
        mAuthProgressDialog.setMessage("Uploading...");
        mAuthProgressDialog.setCancelable(false);
    }

    private void showProg(){
        mAuthProgressDialog.show();
    }

    private void hideProg(){
        mAuthProgressDialog.dismiss();
    }

}
