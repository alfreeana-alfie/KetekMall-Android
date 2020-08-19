package com.example.click;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.click.data.SessionManager;
import com.example.click.pages.Main_Order_Other;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Review_Page extends AppCompatActivity {

    private static String URL_REVIEW = "https://ketekmall.com/ketekmall/add_review.php";
    private static String URL_READ = "https://ketekmall.com/ketekmall/read_detail.php";
    private static String URL_EDIT = "https://ketekmall.com/ketekmall/edit_remarks_done.php";

    TextView ordered, pending, shipped, received, name_display, tracking_notext, order_datetext;
    EditText edit_review;
    Button btn_submit, btn_cancel, btn_received;
    String getId, strName1;
    SessionManager sessionManager;
    RelativeLayout order_layout, review_layout;
    RatingBar ratingBar;
    int numofStar;
    float getRating;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_page);

        getSession();
        ToolbarSetting();

        Intent intent = getIntent();
        final String strSeller_ID = intent.getStringExtra("seller_id");
        String strCustomer_ID = intent.getStringExtra("customer_id");
        final String strItem_ID = intent.getStringExtra("item_id");
        final String remarks = intent.getStringExtra("remarks");
        final String order_date = intent.getStringExtra("order_date");
        final String order_id = intent.getStringExtra("order_id");
        final String strTracking = intent.getStringExtra("tracking_no");
        final String strDelivery_Date = intent.getStringExtra("delivery_date");

        edit_review = findViewById(R.id.editText_review);
        btn_received = findViewById(R.id.btn_received);
        btn_submit = findViewById(R.id.btn_submit);
        btn_cancel = findViewById(R.id.btn_cancel);

        ordered = findViewById(R.id.ordered);
        pending = findViewById(R.id.pending);
        shipped = findViewById(R.id.shipped);
        received = findViewById(R.id.received);
        name_display = findViewById(R.id.name_display);
        tracking_notext = findViewById(R.id.tracking_id);
        order_datetext = findViewById(R.id.order_date);
        ratingBar = findViewById(R.id.ratingBar);

        order_layout = findViewById(R.id.order_layout);
        review_layout = findViewById(R.id.review_layout);

        tracking_notext.setText("KM" + strTracking);
        order_datetext.setText(strDelivery_Date);
        tracking_notext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Intent.ACTION_VIEW);
                intent1.setData(Uri.parse("https://www.tracking.my/poslaju/" + "PL"+ strTracking));
                startActivity(intent1);
            }
        });

        getUserDetail(getId);

        if(remarks.equals("Ordered")){
            ordered.setTextColor(getResources().getColor(R.color.colorGreen));
        }
        if(remarks.equals("Pending")){
            ordered.setTextColor(getResources().getColor(R.color.colorGreen));
            pending.setTextColor(getResources().getColor(R.color.colorGreen));
        }
        if(remarks.equals("Shipped")){
            ordered.setTextColor(getResources().getColor(R.color.colorGreen));
            pending.setTextColor(getResources().getColor(R.color.colorGreen));
            shipped.setTextColor(getResources().getColor(R.color.colorGreen));
        }
        if(remarks.equals("Received")){
            ordered.setTextColor(getResources().getColor(R.color.colorGreen));
            pending.setTextColor(getResources().getColor(R.color.colorGreen));
            shipped.setTextColor(getResources().getColor(R.color.colorGreen));
            received.setTextColor(getResources().getColor(R.color.colorGreen));
        }

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewList2(strSeller_ID, getId, strItem_ID);
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Review_Page.this, Main_Order_Other.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent1);
            }
        });

        btn_received.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Received(order_date);
                order_layout.setVisibility(View.GONE);
                review_layout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void ToolbarSetting(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Back");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Review_Page.this, Main_Order_Other.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent1);
            }
        });
    }

    private void ViewList2(final String strSeller_ID, final String strCustomer_ID, final String strItem_ID) {
        final String reviewtext = edit_review.getText().toString();
        numofStar = ratingBar.getNumStars();
        getRating = ratingBar.getRating();

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
                                    JSONObject object = jsonArray.getJSONObject(i);

                                    final String strName = object.getString("name").trim();
                                    String strEmail = object.getString("email").trim();
                                    String strPhoto = object.getString("photo");

                                    StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_REVIEW,
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    try {
                                                        JSONObject jsonObject = new JSONObject(response);
                                                        String success = jsonObject.getString("success");

                                                        if (success.equals("1")) {
                                                            Toast.makeText(Review_Page.this, "Saved", Toast.LENGTH_SHORT).show();

                                                            Intent intent = new Intent(Review_Page.this, Main_Order_Other.class);
                                                            startActivity(intent);
                                                        } else {
                                                            Toast.makeText(Review_Page.this, "Failed to Save Product", Toast.LENGTH_SHORT).show();
                                                        }
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                            Toast.makeText(Review_Page.this, "JSON Parsing Error: " + e.toString(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            },
                                            new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    Toast.makeText(Review_Page.this, "JSON Parsing Error: " + error.toString(), Toast.LENGTH_SHORT).show();
                                                }
                                            }) {

                                        @Override
                                        protected Map<String, String> getParams() throws AuthFailureError {
                                            Map<String, String> params = new HashMap<>();
                                            params.put("seller_id", strSeller_ID);
                                            params.put("customer_id", getId);
                                            params.put("customer_name", strName);
                                            params.put("item_id", strItem_ID);
                                            params.put("review", reviewtext);
                                            params.put("rating", String.valueOf(getRating));

                                            return params;
                                        }
                                    };
                                    RequestQueue requestQueue = Volley.newRequestQueue(Review_Page.this);
                                    requestQueue.add(stringRequest);
                                }
                            } else {
                                Toast.makeText(Review_Page.this, "Incorrect Information", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                                            Toast.makeText(Rev.this, "Connection Error", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", getId);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(Review_Page.this);
        requestQueue.add(stringRequest);

    }

    private void getSession() {
        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();

        HashMap<String, String> user = sessionManager.getUserDetail();
        getId = user.get(SessionManager.ID);
    }

    private void Received(final String strOrder_Date){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_EDIT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")) {
                                Toast.makeText(Review_Page.this, "Saved", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Review_Page.this, "Failed to Save Product", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
//                                    Toast.makeText(Sell_Items_Other.this, "JSON Parsing Error: " + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("order_date", strOrder_Date);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(Review_Page.this);
        requestQueue.add(stringRequest);
    }

    private void getUserDetail(final String user_id) {
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
                                    JSONObject object = jsonArray.getJSONObject(i);

                                    String strName = object.getString("name").trim();
                                    String strEmail = object.getString("email").trim();
                                    String strPhoto = object.getString("photo");

                                    name_display.setText(strName);
                                }
                            } else {
                                Toast.makeText(Review_Page.this, "Incorrect Information", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(Homepage.this, "Connection Error", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", user_id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
