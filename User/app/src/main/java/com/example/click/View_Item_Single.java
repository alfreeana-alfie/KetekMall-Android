package com.example.click;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.click.adapter.Item_Single_Adapter;
import com.example.click.data.Item_All_Details;
import com.example.click.data.SessionManager;
import com.example.click.data.UserDetails;
import com.example.click.pages.Chat;
import com.mhmtk.twowaygrid.TwoWayGridView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class View_Item_Single extends AppCompatActivity {

    private static String URL_READ_CART = "https://ketekmall.com/ketekmall/readall_seller.php";
    private static String URL_READ_SELLER = "https://ketekmall.com/ketekmall/read_order_done_seller.php";

    private static String URL_ADD_CART = "https://ketekmall.com/ketekmall/add_to_cart.php";
    private static String URL_READ = "https://ketekmall.com/ketekmall/read_detail.php";

    String userid, ad_detail, division, district, strMain_category, strSub_category, strPrice, photo, getId;
    Item_Single_Adapter adapter_item;
    List<Item_All_Details> itemList, itemList2;
    SessionManager sessionManager;
    private ImageView img_item, seller_image;
    private TextView ad_detail_item, price_item, sold_text, shipping_info,
            seller_name, seller_location, view_all;
    private Button btn_chat, add_to_cart_btn;
    private TwoWayGridView gridView_item;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_item_other);

        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();

        HashMap<String, String> user = sessionManager.getUserDetail();
        getId = user.get(SessionManager.ID);

        itemList = new ArrayList<>();
        itemList2 = new ArrayList<>();
        img_item = findViewById(R.id.img_item);
        ad_detail_item = findViewById(R.id.ad_details_item);
        price_item = findViewById(R.id.price_item);
        sold_text = findViewById(R.id.sold_text);
        shipping_info = findViewById(R.id.shipping_info);
        seller_name = findViewById(R.id.seller_name);
        seller_image = findViewById(R.id.seller_image);
        seller_location = findViewById(R.id.seller_location);
        view_all = findViewById(R.id.view_all);
        btn_chat = findViewById(R.id.btn_chat);
        gridView_item = findViewById(R.id.gridView_item);
        add_to_cart_btn = findViewById(R.id.add_to_cart_btn);

        final Intent intent = getIntent();
        userid = intent.getStringExtra("user_id");
        strMain_category = intent.getStringExtra("main_category");
        strSub_category = intent.getStringExtra("sub_category");
        ad_detail = intent.getStringExtra("ad_detail");
        strPrice = intent.getStringExtra("price");
        division = intent.getStringExtra("division");
        district = intent.getStringExtra("district");
        photo = intent.getStringExtra("photo");

        String Price_Text = "MYR" + strPrice;

        ad_detail_item.setText(ad_detail);
        price_item.setText(Price_Text);
        Picasso.get().load(photo).into(img_item);

        getUserDetail();
        View_Item();
        getSold();

        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddCart();
            }
        });

        view_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(View_Item_Single.this, Seller_Shop.class);
                startActivity(intent1);
            }
        });

        ToolbarSetting();
    }

    private void ToolbarSetting() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.find_actionbar);

        View view = getSupportActionBar().getCustomView();
        ImageButton back_button = view.findViewById(R.id.back_button);

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void AddCart() {
        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, URL_ADD_CART,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject1 = new JSONObject(response);
                            String success = jsonObject1.getString("success");

                            if (success.equals("1")) {
                                Toast.makeText(View_Item_Single.this, "Add To Cart", Toast.LENGTH_SHORT).show();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(View_Item_Single.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(View_Item_Single.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("customer_id", getId);
                params.put("main_category", strMain_category);
                params.put("sub_category", strSub_category);
                params.put("ad_detail", ad_detail);
                params.put("price", strPrice);
                params.put("division", division);
                params.put("district", district);
                params.put("photo", photo);
                params.put("seller_id", userid);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(View_Item_Single.this);
        requestQueue.add(stringRequest2);
    }

    private void getUserDetail() {
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
                                    final String strPhoto = object.getString("photo");
                                    final String strDivision = object.getString("division").trim();

                                    Picasso.get().load(strPhoto).into(seller_image);
                                    seller_name.setText(strName);
                                    seller_location.setText(strDivision);

                                    view_all.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent1 = new Intent(View_Item_Single.this, Seller_Shop.class);
                                            intent1.putExtra("id", userid);
                                            intent1.putExtra("seller_name", strName);
                                            intent1.putExtra("seller_photo", strPhoto);
                                            intent1.putExtra("seller_location", strDivision);
                                            startActivity(intent1);
                                        }
                                    });

                                    btn_chat.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            UserDetails.chatWith = strName;
                                            Intent intent = new Intent(View_Item_Single.this, Chat.class);
                                            startActivity(intent);
                                        }
                                    });

                                }
                            } else {
                                Toast.makeText(View_Item_Single.this, "Incorrect Information", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
//                            Toast.makeText(Homepage.this, "JSON Parsing Eror: " + e.toString(), Toast.LENGTH_SHORT).show();
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
                params.put("id", userid);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void View_Item() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_READ_CART,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            final JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            final JSONArray jsonArray = jsonObject.getJSONArray("read");

                            if (success.equals("1")) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);

                                    String id = object.getString("id").trim();
                                    String seller_id = object.getString("user_id").trim();
                                    String main_category = object.getString("main_category").trim();
                                    String sub_category = object.getString("sub_category").trim();
                                    String ad_detail = object.getString("ad_detail").trim();
                                    String price = object.getString("price").trim();
                                    String division = object.getString("division");
                                    String district = object.getString("district");
                                    String image_item = object.getString("photo");

                                    Item_All_Details item = new Item_All_Details(id, seller_id, main_category, sub_category, ad_detail, price, division, district, image_item);
                                    itemList.add(item);
                                }
                                adapter_item = new Item_Single_Adapter(itemList, View_Item_Single.this);
                                adapter_item.notifyDataSetChanged();
                                gridView_item.setAdapter(adapter_item);

                            } else {
                                Toast.makeText(View_Item_Single.this, "Login Failed! ", Toast.LENGTH_SHORT).show();
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
                params.put("user_id", userid);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(View_Item_Single.this);
        requestQueue.add(stringRequest);
    }

    private void getSold() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_READ_SELLER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            final JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            final JSONArray jsonArray = jsonObject.getJSONArray("read");

                            if (success.equals("1")) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);

                                    String id = object.getString("id").trim();
                                    String seller_id = object.getString("seller_id").trim();
                                    String main_category = object.getString("main_category").trim();
                                    String sub_category = object.getString("sub_category").trim();
                                    String ad_detail = object.getString("ad_detail").trim();
                                    String price = object.getString("price").trim();
                                    String division = object.getString("division");
                                    String district = object.getString("district");
                                    String image_item = object.getString("photo");

                                    Item_All_Details item = new Item_All_Details(id, seller_id, main_category, sub_category, ad_detail, price, division, district, image_item);
                                    itemList2.add(item);
                                }
                                String sold = String.valueOf(itemList2.size());
                                sold_text.setText(sold);

                            } else {
                                Toast.makeText(View_Item_Single.this, "Login Failed! ", Toast.LENGTH_SHORT).show();
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
                params.put("seller_id", userid);
                params.put("ad_detail", ad_detail);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(View_Item_Single.this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}