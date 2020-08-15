package com.example.click;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.click.data.SessionManager;
import com.example.click.pages.Homepage;
import com.example.click.pages.Row_Add;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ActivityDelivery extends AppCompatActivity {

    private static String URL_READ_DELIVERY = "https://ketekmall.com/ketekmall/read_delivery.php";
    private static String URL_DELETE = "https://ketekmall.com/ketekmall/delete_delivery_two.php";
    private static String URL_READ_PRODUCT_SINGLE = "https://ketekmall.com/ketekmall/read_products.php";


    RecyclerView recyclerCricketers;
    ArrayList<Delivery> cricketersList = new ArrayList<>();
    DeliveryAdapter deliveryAdapter;

    String getId;
    SessionManager sessionManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);

        Intent intent = getIntent();
        String ad_detail = intent.getStringExtra("ad_detail");

        recyclerCricketers = findViewById(R.id.recycler_cricketers);

        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();

        HashMap<String, String> user = sessionManager.getUserDetail();
        getId = user.get(SessionManager.ID);

        recyclerCricketers.setHasFixedSize(true);
        recyclerCricketers.setLayoutManager(new LinearLayoutManager(ActivityDelivery.this));
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerCricketers.setLayoutManager(layoutManager);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Setup Delivery");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Homepage.class));
            }
        });

        Read_Delivery();
        Read_Product(ad_detail);
    }

    private void Read_Product(final String ad_detail) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_READ_PRODUCT_SINGLE,
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

                                    final String id = object.getString("id").trim();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ActivityDelivery.this, "JSON Parsing Error: " + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ActivityDelivery.this, "Connection Error", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", getId);
                params.put("ad_detail", ad_detail);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(ActivityDelivery.this);
        requestQueue.add(stringRequest);
    }

    private void Read_Delivery() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_READ_DELIVERY,
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

                                    final String id = object.getString("id").trim();
                                    final String user_id = object.getString("user_id").trim();
                                    final String division = object.getString("division").trim();
                                    final Double price = Double.valueOf(object.getString("price").trim());
                                    final String days = object.getString("days");

                                    Delivery delivery = new Delivery(division, String.format("%.2f", price), days);
                                    delivery.setId(id);
                                    cricketersList.add(delivery);
                                }
                                if (cricketersList.size() == 0) {
                                    TextView textView = findViewById(R.id.textView4);
                                    textView.setText("Opps, No delivery is added");

                                    Button Add_Delivery = findViewById(R.id.btn_goto_delivery);
                                    Button Edit = findViewById(R.id.btn_add);
                                    Edit.setVisibility(View.GONE);
                                    Add_Delivery.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent1 = getIntent();
                                            final String ad_detail = intent1.getStringExtra("ad_detail");

                                            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_READ_PRODUCT_SINGLE,
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

                                                                        final String id = object.getString("id").trim();

                                                                        Intent intent = new Intent(ActivityDelivery.this, Row_Add.class);
                                                                        intent.putExtra("item_id", id);
                                                                        startActivity(intent);
                                                                    }
                                                                }
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                                Toast.makeText(ActivityDelivery.this, "JSON Parsing Error: " + e.toString(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    },
                                                    new Response.ErrorListener() {
                                                        @Override
                                                        public void onErrorResponse(VolleyError error) {
                                                            Toast.makeText(ActivityDelivery.this, "Connection Error", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }) {
                                                @Override
                                                protected Map<String, String> getParams() throws AuthFailureError {
                                                    Map<String, String> params = new HashMap<>();
                                                    params.put("user_id", getId);
                                                    params.put("ad_detail", ad_detail);
                                                    return params;
                                                }
                                            };
                                            RequestQueue requestQueue = Volley.newRequestQueue(ActivityDelivery.this);
                                            requestQueue.add(stringRequest);


                                        }
                                    });
                                    recyclerCricketers.setVisibility(View.GONE);
                                } else {
                                    TextView textView = findViewById(R.id.textView4);

                                    final Button btn_add = findViewById(R.id.btn_add);
                                    btn_add.setVisibility(View.VISIBLE);
                                    btn_add.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(ActivityDelivery.this, Row_Add.class);
                                            startActivity(intent);
                                        }
                                    });
                                    final Button Add_Delivery = findViewById(R.id.btn_goto_delivery);
                                    textView.setVisibility(View.GONE);
                                    Add_Delivery.setVisibility(View.GONE);
                                    deliveryAdapter = new DeliveryAdapter(cricketersList);
                                    recyclerCricketers.setAdapter(deliveryAdapter);
                                    deliveryAdapter.setOnItemClickListener(new DeliveryAdapter.OnItemClickListener() {
                                        @Override
                                        public void onEditClick(int position) {
                                            Delivery delivery = cricketersList.get(position);
                                            Intent intent = new Intent(ActivityDelivery.this, Edit_Delivery.class);

                                            intent.putExtra("id", delivery.getId());
                                            intent.putExtra("division", delivery.getDivision());
                                            intent.putExtra("price", delivery.getPrice());
                                            intent.putExtra("days", delivery.getDays());

                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                        }

                                        @Override
                                        public void onDeleteClick(final int position) {
                                            final Delivery delivery = cricketersList.get(position);

                                            final String id = delivery.getId();

                                            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_DELETE,
                                                    new Response.Listener<String>() {
                                                        @Override
                                                        public void onResponse(String response) {
                                                            try {
                                                                JSONObject jsonObject = new JSONObject(response);
                                                                String success = jsonObject.getString("success");
                                                                if (success.equals("1")) {
                                                                    Toast.makeText(ActivityDelivery.this, "Item Updated", Toast.LENGTH_SHORT).show();
                                                                    cricketersList.remove(position);
                                                                    deliveryAdapter.notifyDataSetChanged();
                                                                } else {
                                                                    Toast.makeText(ActivityDelivery.this, "Failed to Update", Toast.LENGTH_SHORT).show();
                                                                }
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
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
                                                    params.put("id", id);
                                                    return params;
                                                }
                                            };
                                            RequestQueue requestQueue = Volley.newRequestQueue(ActivityDelivery.this);
                                            requestQueue.add(stringRequest);
                                        }
                                    });

                                }
                            } else {
                                Toast.makeText(ActivityDelivery.this, "Failed to read", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ActivityDelivery.this, "JSON Parsing Error: " + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ActivityDelivery.this, "Connection Error", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", getId);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(ActivityDelivery.this);
        requestQueue.add(stringRequest);
    }
}
