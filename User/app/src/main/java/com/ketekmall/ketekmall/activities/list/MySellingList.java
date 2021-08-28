package com.ketekmall.ketekmall.activities.list;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ketekmall.ketekmall.R;
import com.ketekmall.ketekmall.activities.order.MySellingDetails;
import com.ketekmall.ketekmall.adapters.MySellingListAdapter;
import com.ketekmall.ketekmall.models.MySingleton;
import com.ketekmall.ketekmall.models.Order;
import com.ketekmall.ketekmall.models.Receipt;
import com.ketekmall.ketekmall.models.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ketekmall.ketekmall.activities.main.Home;
import com.ketekmall.ketekmall.activities.main.Me;
import com.ketekmall.ketekmall.activities.main.Notification;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ketekmall.ketekmall.configs.Link.*;

public class MySellingList extends AppCompatActivity {

    public static final String ID = "id";
    public static final String AD_DETAIL = "ad_detail";
    public static final String PRICE = "price";
    public static final String ITEM_LOCATION = "district";
    public static final String PHOTO = "photo";

    final String TAG = "NOTIFICATION TAG";
    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAA1e9WIaM:APA91bGoWyt9jVnxE08PH2SzgIqh2VgOOolPPBy_uGVkrNV7q8E-1ecG3staHzI73jDzygIisGIRG2XbxzBBQBVRf-rU-qSNb8Fu0Lwo3JDlQtmNrsIvGSec5V3ANVFyR3jcGhgEduH7";
    final private String contentType = "application/json";
    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;
    String TOPIC;

    RecyclerView recyclerView;
    MySellingListAdapter adapter_item;
    List<Order> itemList;
    List<Receipt> receiptList;

    String getId;
    SessionManager sessionManager;
    TextView textView8, textView9, getTextView10;
    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myselling);
        Declare();
        ToolbarSettings();

        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();

        HashMap<String, String> user = sessionManager.getUserDetail();
        getId = user.get(SessionManager.ID);

        SellerCheck(getId);
        Approval_List();

    }

    private void Declare() {
        itemList = new ArrayList<>();
        receiptList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        textView8 = findViewById(R.id.textView8);
        textView9 = findViewById(R.id.textView9);
        getTextView10 = findViewById(R.id.textView10);
        bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.getMenu().getItem(0).setCheckable(false);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        Intent intent4 = new Intent(MySellingList.this, Home.class);
                        intent4.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent4);
                        break;


                    case R.id.nav_noti:
                        Intent intent6 = new Intent(MySellingList.this, Notification.class);
                        intent6.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent6);
                        break;

                    case R.id.nav_edit_profile:
                        Intent intent1 = new Intent(MySellingList.this, Me.class);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent1);
                        break;
                }

                return true;
            }
        });
    }

    private void ToolbarSettings(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Selling");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MySellingList.this, Me.class));
                finish();
            }
        });
    }

    private void Approval_List() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GET_SELLER_ORDER_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("read");

                            if (success.equals("1")) {
                                if(jsonArray.length()==0){
                                    getTextView10.setVisibility(View.VISIBLE);
                                }
                                getTextView10.setVisibility(View.GONE);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);

                                    final String id = object.getString("id").trim();
                                    final String customer_id = object.getString("customer_id").trim();
                                    final String seller_id = object.getString("seller_id").trim();
                                    final String main_category = object.getString("main_category").trim();
                                    final String sub_category = object.getString("sub_category").trim();
                                    final String ad_detail = object.getString("ad_detail").trim();
                                    final Double price = Double.valueOf(object.getString("price").trim());
                                    final String division = object.getString("division");
                                    final String district = object.getString("district");
                                    final String image_item = object.getString("photo");
                                    final String item_id = object.getString("item_id").trim();
                                    final String order_date = object.getString("order_date").trim();
                                    final String date = object.getString("date").trim();
                                    final String quantity = object.getString("quantity").trim();
                                    final String status = object.getString("status").trim();
                                    final String delivery_price = object.getString("delivery_price");
                                    final String weight = object.getString("weight");
                                    final String refno = object.getString("refno");
                                    final String tracking_no = object.getString("tracking_no").trim();
                                    final String pick_up = object.getString("pick_up").trim();

                                    Log.i("REFNO", refno);

                                    Spanned status1;

                                    if(refno != null){
                                        StringRequest stringRequest1 = new StringRequest(Request.Method.POST, GET_PAYMENT_DETAILS,
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

                                                                    final String PaymentID = object.getString("id").trim();
                                                                    final String PaymentRefno = object.getString("OrderID").trim();
                                                                    final String PaymentStatus = object.getString("Status").trim();
                                                                    final String CreatedAt = object.getString("CreatedAt").trim();

                                                                    if(PaymentStatus.contains("Unsuccessful")){
                                                                        final Order item = new Order(id,
                                                                                seller_id,
                                                                                ad_detail,
                                                                                main_category,
                                                                                sub_category,
                                                                                String.format("%.2f", price),
                                                                                division,
                                                                                district,
                                                                                image_item,
                                                                                item_id,
                                                                                customer_id,
                                                                                order_date,
                                                                                date,
                                                                                quantity,
                                                                                PaymentStatus);
                                                                        item.setDelivery_price(delivery_price);
                                                                        item.setWeight(weight);
                                                                        item.setRefNo(refno);
                                                                        String delivery_text;

                                                                        delivery_text = "<font color='#FF3333'>Unsuccessful</font>";
                                                                        item.setStatus1(Html.fromHtml(delivery_text));
                                                                        item.setTracking_no(tracking_no);
                                                                        item.setPickUp(pick_up);
                                                                        itemList.add(item);
                                                                    }else{
                                                                        final Order item = new Order(id,
                                                                                seller_id,
                                                                                ad_detail,
                                                                                main_category,
                                                                                sub_category,
                                                                                String.format("%.2f", price),
                                                                                division,
                                                                                district,
                                                                                image_item,
                                                                                item_id,
                                                                                customer_id,
                                                                                order_date,
                                                                                date,
                                                                                quantity,
                                                                                status);
                                                                        item.setDelivery_price(delivery_price);
                                                                        item.setWeight(weight);
                                                                        item.setRefNo(refno);
                                                                        item.setPickUp(pick_up);
                                                                        if(status.equals("Rejected")){
                                                                            String delivery_text;

                                                                            delivery_text = "<font color='#FF3333'>Rejected</font>";
                                                                            item.setStatus1(Html.fromHtml(delivery_text));
                                                                        }
                                                                        item.setTracking_no(tracking_no);
                                                                        itemList.add(item);
                                                                    }
                                                                }
                                                                adapter_item = new MySellingListAdapter(MySellingList.this, itemList);
                                                                recyclerView.setAdapter(adapter_item);
                                                                adapter_item.notifyDataSetChanged();
                                                                adapter_item.sortArrayHighest();
                                                                adapter_item.setOnItemClickListener(new MySellingListAdapter.OnItemClickListener() {
                                                                    @Override
                                                                    public void onAcceptClick(int position) {
                                                                        Order order = itemList.get(position);

                                                                        final String strOrder_Id = order.getId();
                                                                        final String strSeller_id = order.getSeller_id();
                                                                        final String strCustomer_id = order.getCustomer_id();
                                                                        final String strItem_id = order.getItem_id();
                                                                        final String strMain_category = order.getMain_category();
                                                                        final String strSub_category = order.getSub_category();
                                                                        final String strAd_Detail = order.getAd_detail();
                                                                        final Double strPrice = Double.valueOf(order.getPrice());
                                                                        final String strDivision = order.getDivision();
                                                                        final String strDistrict = order.getDistrict();
                                                                        final String strPhoto = order.getPhoto();
                                                                        final String strOrder_Date = order.getOrder_date();
                                                                        final String strDate = order.getDate();
                                                                        final String strQuantity = order.getQuantity();
                                                                        final String strStatus = order.getStatus();
                                                                        final String strRefno = order.getRefNo();

                                                                        final String remarks = "ACCEPT";
                                                                        Update_Order(strOrder_Date, remarks, strCustomer_id, strOrder_Id, strRefno);
                                                                        itemList.remove(position);
                                                                        adapter_item.notifyDataSetChanged();
                                                                        recyclerView.setAdapter(adapter_item);
                                                                    }

                                                                    @Override
                                                                    public void onRejectClick(final int position) {
                                                                        AlertDialog.Builder builder = new AlertDialog.Builder(MySellingList.this, R.style.MyDialogTheme);
                                                                        builder.setTitle("Are you sure?");
                                                                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(DialogInterface dialog, int which) {

                                                                                Order order = itemList.get(position);

                                                                                final String strOrder_Id = order.getId();
                                                                                final String strSeller_id = order.getSeller_id();
                                                                                final String strCustomer_id = order.getCustomer_id();
                                                                                final String strItem_id = order.getItem_id();
                                                                                final String strMain_category = order.getMain_category();
                                                                                final String strSub_category = order.getSub_category();
                                                                                final String strAd_Detail = order.getAd_detail();
                                                                                final Double strPrice = Double.valueOf(order.getPrice());
                                                                                final String strDivision = order.getDivision();
                                                                                final String strDistrict = order.getDistrict();
                                                                                final String strPhoto = order.getPhoto();
                                                                                final String strOrder_Date = order.getOrder_date();
                                                                                final String strDate = order.getDate();
                                                                                final String strQuantity = order.getQuantity();
                                                                                final String strStatus = order.getStatus();
                                                                                final String strRefNo = order.getRefNo();

                                                                                final String remarks = "Rejected";
                                                                                Log.i("REFNO", strRefNo);

//                                                Update_Order_Reject(strOrder_Date, remarks, strCustomer_id, strOrder_Id);
                                                                                updateOrder(strCustomer_id, strOrder_Id, remarks, strRefNo);
                                                                                getCustomerDetail(strCustomer_id, strOrder_Id);

                                                                                adapter_item.notifyDataSetChanged();
                                                                                recyclerView.setAdapter(adapter_item);

                                                                                finish();
                                                                                startActivity(getIntent());
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

                                                                    @Override
                                                                    public void onViewClick(int position) {
                                                                        Order order = itemList.get(position);

                                                                        final String strOrder_Id = order.getId();
                                                                        final String strSeller_id = order.getSeller_id();
                                                                        final String strCustomer_id = order.getCustomer_id();
                                                                        final String strItem_id = order.getItem_id();
                                                                        final String strMain_category = order.getMain_category();
                                                                        final String strSub_category = order.getSub_category();
                                                                        final String strAd_Detail = order.getAd_detail();
                                                                        final Double strPrice = Double.valueOf(order.getPrice());
                                                                        final String strDivision = order.getDivision();
                                                                        final String strDistrict = order.getDistrict();
                                                                        final String strPhoto = order.getPhoto();
                                                                        final String strOrder_Date = order.getOrder_date();
                                                                        final String strDate = order.getDate();
                                                                        final String strQuantity = order.getQuantity();
                                                                        final String strStatus = order.getStatus();
                                                                        final String strTracking_NO = order.getTracking_no();
                                                                        final Double strDeliveryPrice = Double.valueOf(order.getDelivery_price());
                                                                        final String Weight = order.getWeight();
                                                                        final String PickUp = order.getPickUp();


                                                                        double TotalAmountPartOne = strPrice + strDeliveryPrice;
                                                                        double TotalAmount = Integer.parseInt(strQuantity) * TotalAmountPartOne;

                                                                        Intent intent1 = new Intent(MySellingList.this, MySellingDetails.class);
                                                                        intent1.putExtra("id", strOrder_Id);
                                                                        intent1.putExtra("photo", strPhoto);
                                                                        intent1.putExtra("ad_detail", strAd_Detail);
                                                                        intent1.putExtra("price", String.format("%.2f", strPrice));
                                                                        intent1.putExtra("quantity", strQuantity);
                                                                        intent1.putExtra("division", strDivision);
                                                                        intent1.putExtra("order_date", strOrder_Date);
                                                                        intent1.putExtra("status", strStatus);
                                                                        intent1.putExtra("tracking_no", strTracking_NO);
                                                                        intent1.putExtra("customer_id", strCustomer_id);
                                                                        intent1.putExtra("TotalAmount", String.format("%.2f", TotalAmount));
                                                                        intent1.putExtra("Weight", Weight);
                                                                        intent1.putExtra("pick_up", PickUp);
                                                                        startActivity(intent1);
                                                                    }
                                                                });
                                                            }
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                            Log.i("CONNOTE", e.toString());
                                                        }
                                                    }
                                                },
                                                new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        try {

                                                            if (error instanceof TimeoutError ) {
                                                                //Time out error

                                                            }else if(error instanceof NoConnectionError){
                                                                //net work error

                                                            } else if (error instanceof AuthFailureError) {
                                                                //error

                                                            } else if (error instanceof ServerError) {
                                                                //Erroor
                                                            } else if (error instanceof NetworkError) {
                                                                //Error

                                                            } else if (error instanceof ParseError) {
                                                                //Error

                                                            }else{
                                                                //Error
                                                            }
                                                            //End


                                                        } catch (Exception e) {


                                                        }
                                                    }
                                                }) {

                                            @Override
                                            protected Map<String, String> getParams() throws AuthFailureError {
                                                Map<String, String> params = new HashMap<>();
                                                params.put("OrderID", refno);
                                                return params;
                                            }
                                        };
                                        RequestQueue requestQueue1 = Volley.newRequestQueue(MySellingList.this);
                                        requestQueue1.add(stringRequest1);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
//                            Toast.makeText(MySelling.this, "JSON Parsing Error: " + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {

                            if (error instanceof TimeoutError) {
                                //Time out error

                            }else if(error instanceof NoConnectionError){
                                //net work error

                            } else if (error instanceof AuthFailureError) {
                                //error

                            } else if (error instanceof ServerError) {
                                //Erroor
                            } else if (error instanceof NetworkError) {
                                //Error

                            } else if (error instanceof ParseError) {
                                //Error

                            }else{
                                //Error
                            }
                            //End


                        } catch (Exception e) {


                        }
//                        Toast.makeText(MySelling.this, "JSON Parsing Error: " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("seller_id", getId);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(MySellingList.this);
        requestQueue.add(stringRequest);
    }

    private void Update_Order(final String strOrder_Date, final String remarks, final String Customer_id, final String strID, final String refno) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, EDIT_ORDER_DETAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")) {
                                Toast.makeText(MySellingList.this, R.string.success_update, Toast.LENGTH_SHORT).show();
                                StringRequest stringRequest = new StringRequest(Request.Method.POST, GET_PROFILE_DETAILS,
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

                                                            GetPlayerData(Customer_id, strID);
                                                        }
                                                    } else {
                                                        Toast.makeText(MySellingList.this, R.string.failed, Toast.LENGTH_SHORT).show();
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                try {

                                                    if (error instanceof TimeoutError ) {
                                                        //Time out error

                                                    }else if(error instanceof NoConnectionError){
                                                        //net work error

                                                    } else if (error instanceof AuthFailureError) {
                                                        //error

                                                    } else if (error instanceof ServerError) {
                                                        //Erroor
                                                    } else if (error instanceof NetworkError) {
                                                        //Error

                                                    } else if (error instanceof ParseError) {
                                                        //Error

                                                    }else{
                                                        //Error
                                                    }
                                                    //End


                                                } catch (Exception e) {


                                                }
                                            }
                                        }) {
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        Map<String, String> params = new HashMap<>();
                                        params.put("id", Customer_id);
                                        return params;
                                    }
                                };
                                RequestQueue requestQueue = Volley.newRequestQueue(MySellingList.this);
                                requestQueue.add(stringRequest);
                            } else {
                                Toast.makeText(MySellingList.this, R.string.failed, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
//                            Toast.makeText(MySelling.this, "JSON Parsing Error: " + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {

                            if (error instanceof TimeoutError ) {
                                //Time out error

                            }else if(error instanceof NoConnectionError){
                                //net work error

                            } else if (error instanceof AuthFailureError) {
                                //error

                            } else if (error instanceof ServerError) {
                                //Erroor
                            } else if (error instanceof NetworkError) {
                                //Error

                            } else if (error instanceof ParseError) {
                                //Error

                            }else{
                                //Error
                            }
                            //End


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("order_date", strOrder_Date);
                params.put("remarks", remarks);
                params.put("status", remarks);
                params.put("refno", refno);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(MySellingList.this);
        requestQueue.add(stringRequest);
    }

    private void updateOrder(final String CustomerID, final String OrderID, final String remarks, final String refno) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPDATE_ORDER_DETAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")) {
                                Toast.makeText(MySellingList.this, R.string.success_update, Toast.LENGTH_SHORT).show();
                                GetPlayerData(CustomerID, OrderID);
                            } else {
                                Toast.makeText(MySellingList.this, R.string.failed, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
//                            Toast.makeText(MyBuying.this, "JSON Parsing Error: " + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", OrderID);
                params.put("remarks", remarks);
                params.put("status", remarks);
                params.put("refno", refno);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void sendNotification(JSONObject notification) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "onResponse: " + response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MySellingList.this, "Request error", Toast.LENGTH_LONG).show();
                        Log.i(TAG, "onErrorResponse: Didn't work");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    private void SellerCheck(final String user_id){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GET_PROFILE_DETAILS,
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

                                    int strVerify = Integer.valueOf(object.getString("verification"));
                                    if(strVerify == 0){
                                        textView8.setVisibility(View.VISIBLE);
                                        textView9.setVisibility(View.VISIBLE);
                                        recyclerView.setVisibility(View.GONE);
                                    }else{
                                        textView8.setVisibility(View.GONE);
                                        textView9.setVisibility(View.GONE);
                                        recyclerView.setVisibility(View.VISIBLE);
                                    }

                                }
                            } else {
                                Toast.makeText(MySellingList.this, R.string.failed, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {

                            if (error instanceof TimeoutError ) {
                                //Time out error
                                System.out.println("" + error);
                            }else if(error instanceof NoConnectionError){
                                //net work error
                                System.out.println("" + error);
                            } else if (error instanceof AuthFailureError) {
                                //error
                                System.out.println("" + error);
                            } else if (error instanceof ServerError) {
                                //Erroor
                                System.out.println("" + error);
                            } else if (error instanceof NetworkError) {
                                //Error
                                System.out.println("" + error);
                            } else if (error instanceof ParseError) {
                                //Error
                                System.out.println("" + error);
                            }else{
                                //Error
                                System.out.println("" + error);
                            }
                            //End


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
        RequestQueue requestQueue = Volley.newRequestQueue(MySellingList.this);
        requestQueue.add(stringRequest);
    }

    private void getCustomerDetail(final String customerID, final String OrderID) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GET_PROFILE_DETAILS,
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

                                    String strEmail = object.getString("email");

                                    sendEmail(strEmail, OrderID);
                                }
                            } else {
                                Toast.makeText(MySellingList.this, R.string.failed, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {

                            if (error instanceof TimeoutError ) {
                                //Time out error
                                System.out.println("" + error);
                            }else if(error instanceof NoConnectionError){
                                //net work error
                                System.out.println("" + error);
                            } else if (error instanceof AuthFailureError) {
                                //error
                                System.out.println("" + error);
                            } else if (error instanceof ServerError) {
                                //Erroor
                                System.out.println("" + error);
                            } else if (error instanceof NetworkError) {
                                //Error
                                System.out.println("" + error);
                            } else if (error instanceof ParseError) {
                                //Error
                                System.out.println("" + error);
                            }else{
                                //Error
                                System.out.println("" + error);
                            }
                            //End


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
//                        Toast.makeText(Homepage.this, "Connection Error", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", customerID);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(MySellingList.this);
        requestQueue.add(stringRequest);
    }

    private void GetPlayerData(final String CustomerUserID, final String OrderID){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GET_PLAYER_ID,
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

                                    String PlayerID = object.getString("PlayerID");
                                    String Name = object.getString("Name");
                                    String UserID = object.getString("UserID");

                                    OneSignalNoti(PlayerID, Name, OrderID);
                                }
                            } else {
                                Toast.makeText(MySellingList.this, "Incorrect Information", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {

                            if (error instanceof TimeoutError ) {
                                //Time out error
                                System.out.println("" + error);
                            }else if(error instanceof NoConnectionError){
                                //net work error
                                System.out.println("" + error);
                            } else if (error instanceof AuthFailureError) {
                                //error
                                System.out.println("" + error);
                            } else if (error instanceof ServerError) {
                                //Erroor
                                System.out.println("" + error);
                            } else if (error instanceof NetworkError) {
                                //Error
                                System.out.println("" + error);
                            } else if (error instanceof ParseError) {
                                //Error
                                System.out.println("" + error);
                            }else{
                                //Error
                                System.out.println("" + error);
                            }
                            //End


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
//                        Toast.makeText(Homepage.this, "Connection Error", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("UserID", CustomerUserID);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void OneSignalNoti(final String PlayerUserID, final String Name, final String OrderID){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, SEND_NOTIFICATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("POST", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {
                            if (error instanceof TimeoutError) {//Time out error
                                System.out.println("" + error);
                            } else if (error instanceof NoConnectionError) {
                                //net work error
                                System.out.println("" + error);
                            } else if (error instanceof AuthFailureError) {
                                //error
                                System.out.println("" + error);
                            } else if (error instanceof ServerError) {
                                //Error
                                System.out.println("" + error);
                            } else if (error instanceof NetworkError) {
                                //Error
                                System.out.println("" + error);
                            } else if (error instanceof ParseError) {
                                //Error
                                System.out.println("" + error);
                            } else {
                                //Error
                                System.out.println("" + error);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("PlayerID", PlayerUserID);
                params.put("Name", Name);
                params.put("Words", "Your order KM"+ OrderID +" have been rejected. Please contact respective seller for more details.");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void sendEmail(final String email, final String OrderID){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, EMAIL_ORDER_REJECTED,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {

                            if (error instanceof TimeoutError ) {
                                //Time out error
                                System.out.println("" + error);
                            }else if(error instanceof NoConnectionError){
                                //net work error
                                System.out.println("" + error);
                            } else if (error instanceof AuthFailureError) {
                                //error
                                System.out.println("" + error);
                            } else if (error instanceof ServerError) {
                                //Erroor
                                System.out.println("" + error);
                            } else if (error instanceof NetworkError) {
                                //Error
                                System.out.println("" + error);
                            } else if (error instanceof ParseError) {
                                //Error
                                System.out.println("" + error);
                            }else{
                                //Error
                                System.out.println("" + error);
                            }
                            //End


                        } catch (Exception e) {


                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("order_id", OrderID);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(MySellingList.this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(MySellingList.this, Me.class));

        finish();
    }
}