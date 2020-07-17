package com.example.click;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class Fragment_Sell_Items_Backup extends Fragment {

    private static String URL_READ = "http://192.168.1.15/android_register_login/itemsave.php";
    private static String URL_USERID = "http://192.168.1.15/android_register_login/save.php";
    private static String URL_UPLOAD = "http://192.168.1.15/android_register_login/uploadimg.php";
    ArrayAdapter<CharSequence> adapter_item_location, adapter_category, adapter_car, adapter_properties, adapter_elctronic, adapter_home, adapter_leisure, adapter_business, adapter_jobs, adapter_travel, adapter_other;
    SessionManager sessionManager;
    String getId;
    private Bitmap bitmap;
    private TextView enter_category, enter_ad_detail;
    private EditText enter_price, edittext_ad_detail;
    private Button accept_item, accept_category, back_category, accept_ad_detail, back_ad_detail;
    private Spinner spinner_main_category, spinner_sub_category, spinner_item_location;
    private RelativeLayout category_page_layout, ad_detail_page_layout;
    private LinearLayout item_page_layout;
    private ImageView upload_photo_img;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sell_item, container, false);
        Declare(view);

        sessionManager = new SessionManager(view.getContext());
        sessionManager.checkLogin();

        getUserId(view);

        HashMap<String, String> user = sessionManager.getUserDetail();
        getId = user.get(sessionManager.ID);

        upload_photo_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFile();
            }
        });
        
        enter_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoCategory();
            }
        });

        back_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category_page_layout.setVisibility(View.GONE);
                item_page_layout.setVisibility(View.VISIBLE);
            }
        });

        enter_ad_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoAdDetail();
            }
        });

        back_ad_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad_detail_page_layout.setVisibility(View.GONE);
                item_page_layout.setVisibility(View.VISIBLE);
            }
        });

        accept_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveItemDetail(v);
                UploadPicture(getId, getStringImage(bitmap));
            }
        });

        accept_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category_page_layout.setVisibility(View.GONE);
                item_page_layout.setVisibility(View.VISIBLE);

                final String mCategory = spinner_main_category.getSelectedItem().toString() + ", " + spinner_sub_category.getSelectedItem().toString();
                enter_category.setText(mCategory);
            }
        });

        accept_ad_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad_detail_page_layout.setVisibility(View.GONE);
                item_page_layout.setVisibility(View.VISIBLE);

                final String mAd_Detail = edittext_ad_detail.getText().toString();
                enter_ad_detail.setText(mAd_Detail);
            }
        });
        return view;
    }

    private void chooseFile() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }

    private void saveItemDetail(View view) {
        final String userid = getId;
        final String strMain_category = this.spinner_main_category.getSelectedItem().toString().trim();
        final String strSub_category = this.spinner_sub_category.getSelectedItem().toString();
        final String strAd_Detail = this.edittext_ad_detail.getText().toString();
        final String strPrice = this.enter_price.getText().toString().trim();
        final String strItem_location = this.spinner_item_location.getSelectedItem().toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_USERID,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")) {
//                                Toast.makeText(getContext(), "Success!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Error " + e.toString(), Toast.LENGTH_SHORT).show();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "Connection Error: " + error.toString(), Toast.LENGTH_SHORT).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("userid", userid);
                params.put("main_category", strMain_category);
                params.put("sub_category", strSub_category);
                params.put("ad_detail", strAd_Detail);
                params.put("price", strPrice);
                params.put("item_location", strItem_location);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(view.getContext());
        requestQueue.add(stringRequest);
    }

    private void getUserId(View view) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_READ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("read");


                            if (success.equals("1")) {
                                for (int i = 0; i < jsonArray.length(); i++) {
//                                    JSONObject object = jsonArray.getJSONObject(i);
//                                    Toast.makeText(getContext(), "Information", Toast.LENGTH_SHORT).show();

//                                    String strName = object.getString("name").trim();
//                                    String strEmail = object.getString("email").trim();
//                                    String strPhone_no = object.getString("phone_no").trim();
//                                    String strAddress = object.getString("address").trim();
//                                    String strBirthday = object.getString("birthday").trim();
//                                    String strGender = object.getString("gender");
//                                    String strPhoto = object.getString("photo");
//
//                                    gender.setVisibility(View.GONE);
//                                    gender_img_spinner.setVisibility(View.GONE);
//                                    Picasso.get().load(strPhoto).into(profile_image);
                                }
                            } else {
                                Toast.makeText(getContext(), "Incorrect Informations", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Error!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "Error!!", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", getId);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(view.getContext());
        requestQueue.add(stringRequest);
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
                adapter_car = ArrayAdapter.createFromResource(getContext(), R.array.vehicle_category, android.R.layout.simple_spinner_item);
                adapter_car.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_sub_category.setAdapter(adapter_car);
                Toast.makeText(getContext(), "Car", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                adapter_properties = ArrayAdapter.createFromResource(getContext(), R.array.properties_category, android.R.layout.simple_spinner_item);
                adapter_properties.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_sub_category.setAdapter(adapter_properties);
                Toast.makeText(getContext(), "Properties", Toast.LENGTH_SHORT).show();
                break;
            case 3:
                adapter_elctronic = ArrayAdapter.createFromResource(getContext(), R.array.electronic_category, android.R.layout.simple_spinner_item);
                adapter_elctronic.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_sub_category.setAdapter(adapter_elctronic);

                Toast.makeText(getContext(), "Electronics", Toast.LENGTH_SHORT).show();
                break;
            case 4:
                adapter_home = ArrayAdapter.createFromResource(getContext(), R.array.home_category, android.R.layout.simple_spinner_item);
                adapter_home.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_sub_category.setAdapter(adapter_home);

                Toast.makeText(getContext(), "Home and Personal Items", Toast.LENGTH_SHORT).show();
                break;
            case 5:
                adapter_leisure = ArrayAdapter.createFromResource(getContext(), R.array.leisure_category, android.R.layout.simple_spinner_item);
                adapter_leisure.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_sub_category.setAdapter(adapter_leisure);

                Toast.makeText(getContext(), "Leisure/Sport/Hobbies", Toast.LENGTH_SHORT).show();
                break;
            case 6:
                adapter_business = ArrayAdapter.createFromResource(getContext(), R.array.business_category, android.R.layout.simple_spinner_item);
                adapter_business.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_sub_category.setAdapter(adapter_business);

                Toast.makeText(getContext(), "Business to Business", Toast.LENGTH_SHORT).show();
                break;
            case 7:
                adapter_jobs = ArrayAdapter.createFromResource(getContext(), R.array.jobs_category, android.R.layout.simple_spinner_item);
                adapter_jobs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_sub_category.setAdapter(adapter_jobs);

                Toast.makeText(getContext(), "Jobs and Services", Toast.LENGTH_SHORT).show();
                break;
            case 8:
                adapter_travel = ArrayAdapter.createFromResource(getContext(), R.array.travel_category, android.R.layout.simple_spinner_item);
                adapter_travel.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_sub_category.setAdapter(adapter_travel);

                Toast.makeText(getContext(), "Travel", Toast.LENGTH_SHORT).show();
                break;
            case 9:
                adapter_other = ArrayAdapter.createFromResource(getContext(), R.array.other_category, android.R.layout.simple_spinner_item);
                adapter_other.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_sub_category.setAdapter(adapter_other);

                Toast.makeText(getContext(), "Other", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void UploadPicture(final String id, final String photo) {

        final String filename = this.edittext_ad_detail.getText().toString().trim();
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Uploading...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_UPLOAD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")) {
//                                Toast.makeText(getContext(), "Success!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Failed!" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Connection Error: " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("userid", id);
                params.put("filename", filename);
                params.put("photo", photo);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    public String getStringImage(Bitmap bitmap) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        byte[] imageByteArray = byteArrayOutputStream.toByteArray();
        String encodedImage = Base64.encodeToString(imageByteArray, Base64.DEFAULT);

        return encodedImage;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                upload_photo_img.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void Declare(View v) {
        enter_category = v.findViewById(R.id.enter_main_category);
        enter_ad_detail = v.findViewById(R.id.enter_ad_detail);
        enter_price = v.findViewById(R.id.enter_price);
        spinner_item_location = v.findViewById(R.id.spinner_item_location);
        accept_item = v.findViewById(R.id.accept_item);
        edittext_ad_detail = v.findViewById(R.id.edittext_ad_detail);
        accept_ad_detail = v.findViewById(R.id.accept_ad_detail);
        back_ad_detail = v.findViewById(R.id.back_ad_detail);
        spinner_main_category = v.findViewById(R.id.spinner_main_category);
        spinner_sub_category = v.findViewById(R.id.spinner_sub_category);
        accept_category = v.findViewById(R.id.accept_category);
        back_category = v.findViewById(R.id.back_category);
        upload_photo_img = v.findViewById(R.id.upload_photo);

        category_page_layout = v.findViewById(R.id.category_page_layout);
        ad_detail_page_layout = v.findViewById(R.id.ad_detail_page_layout);
        item_page_layout = v.findViewById(R.id.item_page_layout);

        adapter_item_location = ArrayAdapter.createFromResource(getContext(), R.array.location, android.R.layout.simple_spinner_item);
        adapter_item_location.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_item_location.setAdapter(adapter_item_location);

        adapter_category = ArrayAdapter.createFromResource(getContext(), R.array.main_category, android.R.layout.simple_spinner_item);
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
}