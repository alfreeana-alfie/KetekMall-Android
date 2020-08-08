package com.example.click.page;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.example.click.R;
import com.example.click.adapter.CartAdapter;
import com.example.click.item.Item_All_Details;
import com.example.click.user.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Cart extends AppCompatActivity {

    private static String URL_CART = "https://ketekmall.com/ketekmall/readcart.php";
    private static String URL_CHECKOUT = "https://ketekmall.com/ketekmall/add_to_checkout.php";
    private static String URL_DELETE = "https://ketekmall.com/ketekmall/delete_cart.php";

    ArrayList<Item_All_Details> itemAllDetailsArrayList;

    RecyclerView recyclerView;
    CartAdapter _cart_adapter;
    Button Button_Checkout;

    String getId;
    SessionManager sessionManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart);
        Declare();

        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();

        HashMap<String, String> user = sessionManager.getUserDetail();
        getId = user.get(SessionManager.ID);

        View_Item();
    }

    private void Declare() {
        Button_Checkout = findViewById(R.id.btn_checkout);
        Button_Checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_CART,
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
                                            final String seller_id = object.getString("customer_id").trim();
                                            final String main_category = object.getString("main_category").trim();
                                            final String sub_category = object.getString("sub_category").trim();
                                            final String ad_detail = object.getString("ad_detail").trim();
                                            final Double price = Double.valueOf(object.getString("price").trim());
                                            final String division = object.getString("division");
                                            final String district = object.getString("district");
                                            final String image_item = object.getString("photo");

                                            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_CHECKOUT,
                                                    new Response.Listener<String>() {
                                                        @Override
                                                        public void onResponse(String response) {
                                                            try {
                                                                final JSONObject Object = new JSONObject(response);
                                                                String success = Object.getString("success");

                                                                if (success.equals("1")) {
                                                                    Toast.makeText(Cart.this, "Success", Toast.LENGTH_SHORT).show();

                                                                } else {
                                                                    Toast.makeText(Cart.this, "Failed to read", Toast.LENGTH_SHORT).show();
                                                                }

                                                            }catch (JSONException e){
                                                                e.printStackTrace();
                                                                Toast.makeText(Cart.this, "JSON Parsing Error: " + e.toString(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    },
                                                    new Response.ErrorListener() {
                                                        @Override
                                                        public void onErrorResponse(VolleyError error) {
                                                            Toast.makeText(Cart.this, "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }){
                                                @Override
                                                protected Map<String, String> getParams() throws AuthFailureError {
                                                    Map<String, String> params = new HashMap<>();
                                                    params.put("seller_id", seller_id);
                                                    params.put("customer_id", getId);
                                                    params.put("ad_detail", ad_detail);
                                                    params.put("main_category", main_category);
                                                    params.put("sub_category", sub_category);
                                                    params.put("price", String.format("%.2f", price));
                                                    params.put("division", division);
                                                    params.put("district", district);
                                                    params.put("photo", image_item);
                                                    params.put("item_id", id);
                                                    return params;
                                                }
                                            };
                                            RequestQueue requestQueue = Volley.newRequestQueue(v.getContext());
                                            requestQueue.add(stringRequest);
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(Cart.this, "JSON Parsing Error: " + e.toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(Cart.this, "JSON Parsing Error: " + error.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("customer_id", getId);
                        return params;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(v.getContext());
                requestQueue.add(stringRequest);
            }
        });
        recyclerView = findViewById(R.id.cart_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(Cart.this));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Shopping Cart");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Homepage.class));
            }
        });
        itemAllDetailsArrayList = new ArrayList<>();
    }

    private void View_Item() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_CART,
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

                                    String id = object.getString("id").trim();
                                    final String seller_id = object.getString("customer_id").trim();
                                    final String main_category = object.getString("main_category").trim();
                                    final String sub_category = object.getString("sub_category").trim();
                                    final String ad_detail = object.getString("ad_detail").trim();
                                    final String price = object.getString("price").trim();
                                    final String division = object.getString("division");
                                    final String district = object.getString("district");
                                    final String image_item = object.getString("photo");

                                    Item_All_Details item = new Item_All_Details(id, seller_id, main_category, sub_category, ad_detail, price, division, district, image_item);
                                    itemAllDetailsArrayList.add(item);
                                }
                                _cart_adapter = new CartAdapter(Cart.this, itemAllDetailsArrayList);
                                recyclerView.setAdapter(_cart_adapter);
                                _cart_adapter.setOnItemClickListener(new CartAdapter.OnItemClickListener() {
                                    @Override
                                    public void onDeleteClick(final int position) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(Cart.this, R.style.MyDialogTheme);
                                        builder.setTitle("Are you sure?");
                                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                final Item_All_Details item = itemAllDetailsArrayList.get(position);

                                                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_DELETE,
                                                        new Response.Listener<String>() {
                                                            @Override
                                                            public void onResponse(String response) {
                                                                try {
                                                                    JSONObject jsonObject = new JSONObject(response);
                                                                    String success = jsonObject.getString("success");

                                                                    if (success.equals("1")) {
                                                                        itemAllDetailsArrayList.remove(position);
                                                                        _cart_adapter.notifyItemRemoved(position);
                                                                    } else {
//                                                                        Toast.makeText(Cart.this, "Failed to read", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                    Toast.makeText(Cart.this, "JSON Parsing Error: " + e.toString(), Toast.LENGTH_SHORT).show();
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
                                                        params.put("id", item.getId());
                                                        return params;
                                                    }
                                                };
                                                RequestQueue requestQueue = Volley.newRequestQueue(Cart.this);
                                                requestQueue.add(stringRequest);
                                            }
                                        });

                                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        });
                                        AlertDialog alertDialog = builder.create();
                                        alertDialog.show();
                                    }

                                });

                            }
                            _cart_adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
//                            Toast.makeText(Cart.this, "JSON Parsing Error: " + e.toString(), Toast.LENGTH_SHORT).show();
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
                params.put("customer_id", getId);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        getSupportFragmentManager().getBackStackEntryCount();
    }
}