package com.example.click;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.example.click.Fragment_View_Item_User.EXTRA_AD_DETAIL;
import static com.example.click.Fragment_View_Item_User.EXTRA_ID;
import static com.example.click.Fragment_View_Item_User.EXTRA_IMG_ITEM;
import static com.example.click.Fragment_View_Item_User.EXTRA_ITEM_LOCATION;
import static com.example.click.Fragment_View_Item_User.EXTRA_MAIN;
import static com.example.click.Fragment_View_Item_User.EXTRA_PRICE;
import static com.example.click.Fragment_View_Item_User.EXTRA_SUB;

public class Activity_Edit_Item extends AppCompatActivity {

    private static String URL_UPLOAD = "https://annkalina53.000webhostapp.com/android_register_login/edituser.php";
    private static String URL_IMG = "https://annkalina53.000webhostapp.com/android_register_login/uploadimg02.php";

    ArrayAdapter<CharSequence> adapter_item_location, adapter_category, adapter_car,
            adapter_properties, adapter_electronic, adapter_home,
            adapter_leisure, adapter_business, adapter_jobs,
            adapter_travel, adapter_other;
    Uri filePath;
    String id;
    private Bitmap bitmap;
    private TextView Main_Category_TextView, Sub_Category_TextView, Ad_Detail_TextView, Category_TextView;
    private EditText EditText_Price, EditText_Ad_Detail;
    private Button Button_AcceptItem, Button_AcceptCategory, Button_BackCategory,
            Button_AcceptAdDetail, Button_BackAdDetail, Button_BackEdit, Button_SavedEdit;
    private Spinner spinner_main_category, spinner_sub_category, spinner_item_location;
    private RelativeLayout category_page_layout, ad_detail_page_layout;
    private LinearLayout item_page_layout;
    private ImageView Upload_Photo;
    private ProgressBar loading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        Declare();

        Intent intent = getIntent();
        id = intent.getStringExtra(EXTRA_ID);
//        final String userid = intent.getStringExtra(EXTRA_USERID);
        final String main_category = intent.getStringExtra(EXTRA_MAIN);
        final String sub_category = intent.getStringExtra(EXTRA_SUB);
        final String ad_detail = intent.getStringExtra(EXTRA_AD_DETAIL);
        final String price = intent.getStringExtra(EXTRA_PRICE);
        final String item_location = intent.getStringExtra(EXTRA_ITEM_LOCATION);
        final String photo = intent.getStringExtra(EXTRA_IMG_ITEM);
        String Category_Text = main_category + ", " + sub_category;

        Category_TextView.setText(Category_Text);
        Main_Category_TextView.setText(main_category);
        Sub_Category_TextView.setText(sub_category);
        Ad_Detail_TextView.setText(ad_detail);
        EditText_Ad_Detail.setText(ad_detail);
        EditText_Price.setText(price);
        Picasso.get().load(photo).into(Upload_Photo);

        int selectposition = adapter_item_location.getPosition(item_location);
        spinner_item_location.setSelection(selectposition);

        Button_Func();
    }

    private void Declare() {
        Main_Category_TextView = findViewById(R.id.enter_main_category);
        Sub_Category_TextView = findViewById(R.id.enter_sub_category);
        Category_TextView = findViewById(R.id.enter_category);
        Ad_Detail_TextView = findViewById(R.id.enter_ad_detail);
        EditText_Price = findViewById(R.id.enter_price);
        spinner_item_location = findViewById(R.id.spinner_item_location);
        Button_AcceptItem = findViewById(R.id.accept_item);
        EditText_Ad_Detail = findViewById(R.id.edittext_ad_detail);
        Button_AcceptAdDetail = findViewById(R.id.accept_ad_detail);
        Button_BackAdDetail = findViewById(R.id.back_ad_detail);
        spinner_main_category = findViewById(R.id.spinner_main_category);
        spinner_sub_category = findViewById(R.id.spinner_sub_category);
        Button_AcceptCategory = findViewById(R.id.accept_category);
        Button_BackCategory = findViewById(R.id.back_category);
        Upload_Photo = findViewById(R.id.upload_photo);
        loading = findViewById(R.id.loading);
        category_page_layout = findViewById(R.id.category_page_layout);
        ad_detail_page_layout = findViewById(R.id.ad_detail_page_layout);
        item_page_layout = findViewById(R.id.item_page_layout);
        Button_BackEdit = findViewById(R.id.back_edit);
        Button_SavedEdit = findViewById(R.id.button_edit_item);

        adapter_item_location = ArrayAdapter.createFromResource(this, R.array.location, android.R.layout.simple_spinner_item);
        adapter_item_location.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_item_location.setAdapter(adapter_item_location);

        adapter_category = ArrayAdapter.createFromResource(this, R.array.main_category, android.R.layout.simple_spinner_item);
        adapter_category.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_main_category.setAdapter(adapter_category);

        spinner_main_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                showResult(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void Button_Func() {
        Upload_Photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFile();

            }
        });

        Category_TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoCategory();
            }
        });

        Button_BackCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category_page_layout.setVisibility(View.GONE);
                item_page_layout.setVisibility(View.VISIBLE);
            }
        });

        Ad_Detail_TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoAdDetail();
            }
        });

        Button_BackAdDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad_detail_page_layout.setVisibility(View.GONE);
                item_page_layout.setVisibility(View.VISIBLE);
            }
        });

        Button_BackEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_Edit_Item.this, Activity_All_View.class);
                startActivity(intent);
            }
        });

        Button_SavedEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEdit();
            }
        });

        Button_AcceptCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category_page_layout.setVisibility(View.GONE);
                item_page_layout.setVisibility(View.VISIBLE);

                final String mCategory = spinner_main_category.getSelectedItem().toString() + ", " + spinner_sub_category.getSelectedItem().toString();
                Main_Category_TextView.setText(spinner_main_category.getSelectedItem().toString());
                Sub_Category_TextView.setText(spinner_sub_category.getSelectedItem().toString());
                Category_TextView.setText(mCategory);
            }
        });

        Button_AcceptAdDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad_detail_page_layout.setVisibility(View.GONE);
                item_page_layout.setVisibility(View.VISIBLE);

                final String mAd_Detail = EditText_Ad_Detail.getText().toString();
                Ad_Detail_TextView.setText(mAd_Detail);
            }
        });
    }

    private void gotoAdDetail() {
        ad_detail_page_layout.setVisibility(View.VISIBLE);
        item_page_layout.setVisibility(View.GONE);
    }

    private void gotoCategory() {
        category_page_layout.setVisibility(View.VISIBLE);
        item_page_layout.setVisibility(View.GONE);
    }

    private void showResult(int position) {
        switch (position) {
            case 0:
                break;
            case 1:
                adapter_car = ArrayAdapter.createFromResource(this, R.array.vehicle_category, android.R.layout.simple_spinner_item);
                adapter_car.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_sub_category.setAdapter(adapter_car);
                break;
            case 2:
                adapter_properties = ArrayAdapter.createFromResource(this, R.array.properties_category, android.R.layout.simple_spinner_item);
                adapter_properties.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_sub_category.setAdapter(adapter_properties);
                break;
            case 3:
                adapter_electronic = ArrayAdapter.createFromResource(this, R.array.electronic_category, android.R.layout.simple_spinner_item);
                adapter_electronic.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_sub_category.setAdapter(adapter_electronic);
                break;
            case 4:
                adapter_home = ArrayAdapter.createFromResource(this, R.array.home_category, android.R.layout.simple_spinner_item);
                adapter_home.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_sub_category.setAdapter(adapter_home);
                break;
            case 5:
                adapter_leisure = ArrayAdapter.createFromResource(this, R.array.leisure_category, android.R.layout.simple_spinner_item);
                adapter_leisure.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_sub_category.setAdapter(adapter_leisure);
                break;
            case 6:
                adapter_business = ArrayAdapter.createFromResource(this, R.array.business_category, android.R.layout.simple_spinner_item);
                adapter_business.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_sub_category.setAdapter(adapter_business);
                break;
            case 7:
                adapter_jobs = ArrayAdapter.createFromResource(this, R.array.jobs_category, android.R.layout.simple_spinner_item);
                adapter_jobs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_sub_category.setAdapter(adapter_jobs);
                break;
            case 8:
                adapter_travel = ArrayAdapter.createFromResource(this, R.array.travel_category, android.R.layout.simple_spinner_item);
                adapter_travel.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_sub_category.setAdapter(adapter_travel);
                break;
            case 9:
                adapter_other = ArrayAdapter.createFromResource(this, R.array.other_category, android.R.layout.simple_spinner_item);
                adapter_other.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_sub_category.setAdapter(adapter_other);
                break;
        }
    }

    private void saveEdit() {
        Intent intent = getIntent();
        id = intent.getStringExtra(EXTRA_ID);
        final String strMain_category = this.Main_Category_TextView.getText().toString().trim();
        final String strSub_category = this.Sub_Category_TextView.getText().toString().trim();
        final String strAd_Detail = this.EditText_Ad_Detail.getText().toString();
        final String strPrice = this.EditText_Price.getText().toString().trim();
        final String strItem_location = this.spinner_item_location.getSelectedItem().toString().trim();

        loading.setVisibility(View.VISIBLE);
        Button_SavedEdit.setVisibility(View.GONE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_UPLOAD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            if (success.equals("1")) {
                                loading.setVisibility(View.GONE);
                                Button_SavedEdit.setVisibility(View.VISIBLE);

                                Toast.makeText(Activity_Edit_Item.this, "Item Updated", Toast.LENGTH_SHORT).show();

                                Intent intent1 = new Intent(Activity_Edit_Item.this, Activity_All_View.class);
                                startActivity(intent1);

                            } else {
                                Toast.makeText(Activity_Edit_Item.this, "Failed to Update", Toast.LENGTH_SHORT).show();
                                loading.setVisibility(View.GONE);
                                Button_SavedEdit.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            loading.setVisibility(View.GONE);
                            Button_SavedEdit.setVisibility(View.VISIBLE);
//                                Toast.makeText(Activity_Edit_Item.this, "JSON Parsing Error " + e.toString(), Toast.LENGTH_SHORT).show();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getMessage() == null) {
//                                Toast.makeText(Activity_Edit_Item.this, "Connection Error", Toast.LENGTH_SHORT).show();
                            loading.setVisibility(View.GONE);
                            Button_AcceptItem.setVisibility(View.VISIBLE);
                        } else {
//                                Toast.makeText(Activity_Edit_Item.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            loading.setVisibility(View.GONE);
                            Button_AcceptItem.setVisibility(View.VISIBLE);
                        }
                        loading.setVisibility(View.GONE);
                        Button_SavedEdit.setVisibility(View.VISIBLE);

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("main_category", strMain_category);
                params.put("sub_category", strSub_category);
                params.put("ad_detail", strAd_Detail);
                params.put("price", String.format("%.2f", strPrice));
                params.put("item_location", strItem_location);
                params.put("id", id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void saveImage(final String photo) {
        Intent intent = getIntent();
        id = intent.getStringExtra(EXTRA_ID);
//        final String strMain_category = this.Main_Category_TextView.toString();
//        final String strSub_category = this.spinner_sub_category.getSelectedItem().toString();
        final String strAd_Detail = this.EditText_Ad_Detail.getText().toString();
//        final String strPrice = this.EditText_Price.getText().toString().trim();
//        final String strItem_location = this.spinner_item_location.getSelectedItem().toString().trim();

//        loading.setVisibility(View.VISIBLE);
//        Button_SavedEdit.setVisibility(View.GONE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_IMG,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            if (success.equals("1")) {
//                                loading.setVisibility(View.GONE);
//                                Button_SavedEdit.setVisibility(View.VISIBLE);
                                Toast.makeText(Activity_Edit_Item.this, "Success!", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(Activity_Edit_Item.this, "Failed! ", Toast.LENGTH_SHORT).show();
//                                loading.setVisibility(View.GONE);
//                                Button_SavedEdit.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
//                            loading.setVisibility(View.GONE);
//                            Button_SavedEdit.setVisibility(View.VISIBLE);
                            Toast.makeText(Activity_Edit_Item.this, "Error " + e.toString(), Toast.LENGTH_SHORT).show();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        loading.setVisibility(View.GONE);
//                        Button_SavedEdit.setVisibility(View.VISIBLE);
                        Toast.makeText(Activity_Edit_Item.this, "Connection Error: " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("ad_detail", strAd_Detail);
                params.put("photo", photo);
                params.put("id", id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public String getStringImage(Bitmap bitmap) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        byte[] imageByteArray = byteArrayOutputStream.toByteArray();
        String encodedImage = Base64.encodeToString(imageByteArray, Base64.DEFAULT);

        return encodedImage;
    }

    private void chooseFile() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                Upload_Photo.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        saveImage(getStringImage(bitmap));
    }

}