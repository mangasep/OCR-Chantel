package com.example.barnaclebot.chantel.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.barnaclebot.chantel.R;
import com.example.barnaclebot.chantel.util.SharedPrefManager;
import com.example.barnaclebot.chantel.util.api.BaseApiService;
import com.example.barnaclebot.chantel.model.ServerResponse;
import com.example.barnaclebot.chantel.util.api.UtilsApi;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.jess.ui.TwoWayAdapterView;
import com.jess.ui.TwoWayGridView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CameraActivity extends AppCompatActivity {
    @BindView(R.id.preview)
    ImageView imageView;
    @BindView(R.id.pickImage)
    FloatingActionButton pickImage;
    @BindView(R.id.tvContent)
    TextView tvContent;
    /*@BindView(R.id.tvView_Content)
    TextView tvView;*/
    @BindView(R.id.tvUsername)
    TextView tvUsername;
    @BindView(R.id.tekscontent)
    RelativeLayout textcontent;
    @BindView(R.id.button_copy)
    ImageButton textcopy;
    @BindView(R.id.action)
    RelativeLayout lay_action;
    @BindView(R.id.image_background)
    ImageView image_background;
    @BindView(R.id.lay_scroll)
    NestedScrollView lay_scroll;
    @BindView(R.id.button_share)
    ImageView button_share;
    @BindView(R.id.bottomsheet)
    BottomSheetLayout bottomSheetLayout;

    private ArrayList<String> images;
    private Uri imageUri;
    private File photoFile = null;
    private ContentResolver contentResolver;
    private static final int TAKE_PICTURE = 0;
    private static final int SELECT_PHOTO = 1;


    private static final int REQUEST_CHOOSE_IMAGE = 3;
    private ShareActionProvider mShareActionProvider;

    ProgressDialog pDialog;
    private String postPath;

    SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        ButterKnife.bind(this);
        bottomSheetLayout.setPeekOnDismiss(true);

        sharedPrefManager = new SharedPrefManager(this);
        tvUsername.setText(sharedPrefManager.getUsername());


        if ((getIntent().getStringExtra("postPath")) == null) {
            imageView.setVisibility(View.INVISIBLE);
            lay_action.setVisibility(View.INVISIBLE);
            textcontent.setVisibility(View.INVISIBLE);
        } else {
            image_background.setVisibility(View.INVISIBLE);
            Glide.with(this).load(getIntent().getStringExtra("postPath")).into(imageView);
            tvContent.setText(getIntent().getStringExtra("content"));
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            pickImage.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        } else {
            pickImage.setEnabled(true);
        }

        // Get clipboard manager object.
        Object clipboardService = getSystemService(CLIPBOARD_SERVICE);
        final ClipboardManager clipboardManager = (ClipboardManager) clipboardService;

        pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBottomSheet();
            }

        });

        textcopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String srcText = tvContent.getText().toString();

                // Create a new ClipData.
                ClipData clipData = ClipData.newPlainText("Text", srcText);
                // Set it as primary clip data to copy text to system clipboard.
                clipboardManager.setPrimaryClip(clipData);
                // Popup a snackbar.
                Snackbar snackbar = Snackbar.make(v, "Text has been copied to system clipboard.", Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        });

        button_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(android.content.Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(android.content.Intent.EXTRA_TEXT, tvContent.getText().toString());
                startActivity(Intent.createChooser(i, "Share Via"));
            }
        });

        lay_scroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {
                    pickImage.hide();
                } else {
                    pickImage.show();
                }
            }
        });

    }


    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button logout action
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.mybutton) {
            // do something here
            sharedPrefManager.saveSPBoolean(SharedPrefManager.SP_SUDAH_LOGIN, false);
            startActivity(new Intent(CameraActivity.this, LoginActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    // premission handling
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImage.setEnabled(true);
            }
        }
    }

    private Drawable getRotateDrawable(final Bitmap b, final float angle) {
        final BitmapDrawable drawable = new BitmapDrawable(getResources(), b) {
            @Override
            public void draw(final Canvas canvas) {
                canvas.save();
                canvas.rotate(angle, b.getWidth() / 2, b.getHeight() / 2);
                super.draw(canvas);
                canvas.restore();
            }
        };
        return drawable;
    }

    private void takeImage() {
        Intent capturePhotoIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        capturePhotoIntent.putExtra("return-data", true);

        try {
            photoFile = getOutputMediaFile();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(CameraActivity.this, "com.example.barnaclebot.chantel", photoFile);
            //Uri photoURI = getOutputMediaFileUri();
            capturePhotoIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(capturePhotoIntent, TAKE_PICTURE);
        }
    }


    private static File getOutputMediaFile() throws IOException {

        // External sdcard location
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "OCR");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.e("Monitoring", "Oops! Failed create Monitoring directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_OCR_" + timeStamp + ".jpg");

        return mediaFile;
    }

    private int getOrientation(ContentResolver cr, int id) {

        String photoID = String.valueOf(id);

        Cursor cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media.ORIENTATION}, MediaStore.Images.Media._ID + "=?",
                new String[]{"" + photoID}, null);
        int orientation = -1;

        if (cursor.getCount() != 1) {
            return -1;
        }

        if (cursor.moveToFirst()) {
            orientation = cursor.getInt(0);
        }
        cursor.close();
        return orientation;
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SELECT_PHOTO:
                    imageUri = data.getData();
                    if (imageUri != null) {
                        CropImage.activity(imageUri)
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setCropMenuCropButtonTitle("DONE")
                                .setMultiTouchEnabled(true)
                                .setInitialCropWindowPaddingRatio(0)
                                .start(CameraActivity.this);
                    }
                    break;
                case TAKE_PICTURE:
                    Uri imageUri = null;
                    if (data == null || data.getData() == null
                            || data.getData().equals(Uri.fromFile(photoFile))) {
                        imageUri = Uri.fromFile(photoFile);
                    } else if (null != data.getData()) {
                        imageUri = data.getData();
                    }
                    if (null != imageUri) {
                        CropImage.activity(imageUri)
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setCropMenuCropButtonTitle("DONE")
                                .setInitialCropWindowPaddingRatio(0)
                                .setMultiTouchEnabled(true)
                                .start(CameraActivity.this);
                    }
                    break;

            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                Uri resultUri = result.getUri();
                postPath = resultUri.getPath();
                uploadFile();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();

                Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    //dialog interface
    protected void showpDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
        if (pDialog == null) {
            pDialog = new ProgressDialog(CameraActivity.this);
            pDialog.setTitle("OCR Processing");
            pDialog.setMessage("Please Wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }
    }

    // Upload gambar
    private void uploadFile() {
        showpDialog();
        // Map multipart
        Map<String, RequestBody> map = new HashMap<>();
        File file = new File(postPath);
        // Parsing any Media type file
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), file);
        map.put("file\"; filename=\"" + file.getName() + "\"", requestBody);
        BaseApiService getResponse = UtilsApi.getAPIService();
        Call<ServerResponse> call = getResponse.upload(sharedPrefManager.getToken(), map);
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        ServerResponse serverResponse = response.body();
                        if (serverResponse.getStatus() == 200) {
                            //Toast.makeText(getApplicationContext(), "berhasil", Toast.LENGTH_LONG).show();
                            Intent myIntent = new Intent(getApplicationContext(), CameraActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            myIntent.putExtra("content", serverResponse.getData().getFilesContent());
                            myIntent.putExtra("postPath", postPath);

                            startActivity(myIntent);
                            //String content = serverResponse.getData().getFilesContent();
//                                Intent i = new Intent();
//                                i.putExtra("content", serverResponse.getData().getFilesContent());

                        } else {
                            Toast.makeText(getApplicationContext(), "Gagal", Toast.LENGTH_LONG).show();
                            Toast.makeText(getApplicationContext(), serverResponse.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    pDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "problem uploading image", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                //hidepDialog();
                //Log.v("Response gotten is", t.getMessage());
                pDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Internet Connection Problem", Toast.LENGTH_SHORT).show();

            }
        });
    }

    //bottomsheet
    public void openBottomSheet() {
        View view = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet, null);

        final TwoWayGridView gallery = (TwoWayGridView) view.findViewById(R.id.gridview);
        LinearLayout camera_btn = (LinearLayout) view.findViewById(R.id.camera);
        LinearLayout gallery_btn = (LinearLayout) view.findViewById(R.id.gallery);
        LinearLayout close_btn = (LinearLayout) view.findViewById(R.id.close);

        final Dialog mBottomSheetDialog = new Dialog(CameraActivity.this, R.style.MaterialDialogSheet);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        mBottomSheetDialog.show();

        gallery.setAdapter(new ImageAdapter(this));


        gallery.setOnItemClickListener(new TwoWayAdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(TwoWayAdapterView parent, View v, int position, long id) {
                if (null != images && !images.isEmpty()) {
                    postPath = images.get(position);
                    Uri imageUri = Uri.fromFile(new File(postPath));

                    CropImage.activity(imageUri)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setCropMenuCropButtonTitle("DONE")
                            .setMultiTouchEnabled(true)
                            .setInitialCropWindowPaddingRatio(0)
                            .start(CameraActivity.this);


                    mBottomSheetDialog.dismiss();

//                    Toast.makeText(
//                            getApplicationContext(),
//                            "position " + position + " " + images.get(position),
//                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        camera_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeImage();
                mBottomSheetDialog.dismiss();
            }
        });

        gallery_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
                mBottomSheetDialog.dismiss();
            }
        });

        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();
            }
        });
    }

    //adapter recent-image
    private class ImageAdapter extends BaseAdapter {

        //new image adapter
        private Activity context;

        public ImageAdapter(Activity localContext) {
            context = localContext;
            images = getAllShownImagesPath(context);
        }

        public int getCount() {
            return images.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            ImageView picturesView;
            if (convertView == null) {
                picturesView = new ImageView(context);
                picturesView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                picturesView.setLayoutParams(new TwoWayGridView.LayoutParams(170, 170));
            } else {
                picturesView = (ImageView) convertView;
            }

            Glide.with(context).load(images.get(position))
                    .apply(new RequestOptions().placeholder(R.drawable.ic_launcher_background_black).centerCrop())
                    .into(picturesView);

            return picturesView;
        }

        //get all image path from storage
        private ArrayList<String> getAllShownImagesPath(Activity activity) {
            Uri uri;
            Cursor cursor;
            int column_index_data, column_index_folder_name;
            ArrayList<String> listOfAllImages = new ArrayList<String>();
            String absolutePathOfImage = null;
            uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

            String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.DATE_TAKEN};
            String orderBy = MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC";
            cursor = activity.getContentResolver().query(uri, projection, null, null, orderBy);

            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN);
            while (cursor.moveToNext()) {
                absolutePathOfImage = cursor.getString(column_index_data);
                listOfAllImages.add(absolutePathOfImage);
            }
            return listOfAllImages;
        }
    }

}
