package com.example.click.pages;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.click.Goto_Register_Page;
import com.example.click.Order;
import com.example.click.Order_SellerAdapter;
import com.example.click.R;
import com.example.click.Receipt;
import com.example.click.Selling_Detail;
import com.example.click.adapter.Seller_OrderAdapter;
import com.example.click.data.MySingleton;
import com.example.click.data.SessionManager;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Selling_Other extends Fragment {

    public static final String ID = "id";
    public static final String AD_DETAIL = "ad_detail";
    public static final String PRICE = "price";
    public static final String ITEM_LOCATION = "district";
    public static final String PHOTO = "photo";

    private static String URL_READ_ORDER = "https://ketekmall.com/ketekmall/read_order.php";

    private static String URL_READ_ORDER_DONE = "https://ketekmall.com/ketekmall/read_order_buyer_done_two.php";
    private static String URL_READ_APPROVAL = "https://ketekmall.com/ketekmall/read_detail_approval.php";
    private static String URL_DELETE_ORDER = "https://ketekmall.com/ketekmall/delete_order_seller.php";
    private static String URL_EDIT_ORDER = "https://ketekmall.com/ketekmall/edit_order.php";

    private static String URL_EDIT_RECEIPT = "https://ketekmall.com/ketekmall/edit_receipt.php";
    private static String URL_ACCEPT = "https://ketekmall.com/ketekmall/add_accept.php";
    private static String URL_REJECT = "https://ketekmall.com/ketekmall/add_reject.php";
    private static String URL_DELETE_APPROVAL = "https://ketekmall.com/ketekmall/delete_approval.php";
    private static String URL_READ = "https://ketekmall.com/ketekmall/read_detail.php";
    private static String URL_READ_RECEIPT = "https://ketekmall.com/ketekmall/read_detail_receipt.php";

    final String TAG = "NOTIFICATION TAG";
    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAA1e9WIaM:APA91bGoWyt9jVnxE08PH2SzgIqh2VgOOolPPBy_uGVkrNV7q8E-1ecG3staHzI73jDzygIisGIRG2XbxzBBQBVRf-rU-qSNb8Fu0Lwo3JDlQtmNrsIvGSec5V3ANVFyR3jcGhgEduH7";
    final private String contentType = "application/json";
    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;
    String TOPIC;

    RecyclerView recyclerView;
    Order_SellerAdapter adapter_item;
    List<Order> itemList;
    List<Receipt> receiptList;

    String getId;
    SessionManager sessionManager;
    TextView textView8, textView9;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_orders_other, container, false);
        Declare(view);

        sessionManager = new SessionManager(view.getContext());
        sessionManager.checkLogin();

        HashMap<String, String> user = sessionManager.getUserDetail();
        getId = user.get(SessionManager.ID);

        SellerCheck(getId);
        Approval_List(view);
        return view;
    }

    private void Declare(View v) {
        itemList = new ArrayList<>();
        receiptList = new ArrayList<>();
        recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        textView8 = v.findViewById(R.id.textView8);
        textView9 = v.findViewById(R.id.textView9);
    }

    private void Approval_List(final View view) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_READ_ORDER_DONE,
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
                                    final String tracking_no = object.getString("tracking_no").trim();

                                    Order item = new Order(id,
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
                                    item.setTracking_no(tracking_no);
                                    itemList.add(item);
                                }
                                adapter_item = new Order_SellerAdapter(getContext(), itemList);
                                adapter_item.notifyDataSetChanged();
                                recyclerView.setAdapter(adapter_item);
                                adapter_item.sortArrayHighest();
                                adapter_item.setOnItemClickListener(new Order_SellerAdapter.OnItemClickListener() {
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

                                        final String remarks = "ACCEPT";
                                        Update_Order(view, strOrder_Date, remarks, strCustomer_id);

//                                        Accept(view, strOrder_Id, strOrder_Date, remarks);

//                                        Delete_Order(view, strOrder_Id);

                                        itemList.remove(position);
                                        adapter_item.notifyDataSetChanged();
                                        recyclerView.setAdapter(adapter_item);
                                    }

                                    @Override
                                    public void onRejectClick(int position) {
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

                                        final String remarks = "Reject";
                                        Update_Order_Reject(view, strOrder_Date, remarks, strCustomer_id);

//                                        Delete_Order(view, strOrder_Id);

                                        adapter_item.notifyDataSetChanged();
                                        recyclerView.setAdapter(adapter_item);
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

                                        Intent intent1 = new Intent(getContext(), Selling_Detail.class);
                                        intent1.putExtra("id", strOrder_Id);
                                        intent1.putExtra("photo", strPhoto);
                                        intent1.putExtra("ad_detail", strAd_Detail);
                                        intent1.putExtra("price", String.format("%.2f", strPrice));
                                        intent1.putExtra("quantity", strQuantity);
                                        intent1.putExtra("division", strDivision);
                                        intent1.putExtra("order_date", strOrder_Date);
                                        intent1.putExtra("status", strStatus);
                                        intent1.putExtra("tracking_no", strTracking_NO);
                                        getActivity().startActivity(intent1);
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "JSON Parsing Error: " + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "JSON Parsing Error: " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("seller_id", getId);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(view.getContext());
        requestQueue.add(stringRequest);
    }

    private void Update_Order(View view, final String strOrder_Date, final String remarks, final String Customer_id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_EDIT_ORDER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")) {
                                Toast.makeText(getContext(), "Successfully Updated", Toast.LENGTH_SHORT).show();
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

                                                            String url = "https://click-1595830894120.firebaseio.com/users.json";

                                                            StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                                                                @Override
                                                                public void onResponse(String s) {
                                                                    try {
                                                                        JSONObject obj = new JSONObject(s);

                                                                        TOPIC = obj.getJSONObject(strName).get("token").toString();
                                                                        NOTIFICATION_TITLE = "KetekMall";
                                                                        NOTIFICATION_MESSAGE = "Hi, your order is being accepted!";

                                                                        JSONObject notification = new JSONObject();
                                                                        JSONObject notifcationBody = new JSONObject();
                                                                        try {
                                                                            notifcationBody.put("title", NOTIFICATION_TITLE);
                                                                            notifcationBody.put("message", NOTIFICATION_MESSAGE);

                                                                            notification.put("to", TOPIC);
                                                                            notification.put("data", notifcationBody);
                                                                            sendNotification(notification);

                                                                            Log.d(TAG, "onCreate: " + NOTIFICATION_MESSAGE + NOTIFICATION_TITLE);
                                                                        } catch (JSONException e) {
                                                                            Log.e(TAG, "onCreate: " + e.getMessage());
                                                                        }
                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                            }, new Response.ErrorListener() {
                                                                @Override
                                                                public void onErrorResponse(VolleyError volleyError) {
                                                                    System.out.println("" + volleyError);
                                                                }
                                                            });
                                                            RequestQueue rQueue = Volley.newRequestQueue(getContext());
                                                            rQueue.add(request);


                                                        }
                                                    } else {
                                                        Toast.makeText(getContext(), "Incorrect Information", Toast.LENGTH_SHORT).show();
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
                                        params.put("id", Customer_id);
                                        return params;
                                    }
                                };
                                RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                                requestQueue.add(stringRequest);
                            } else {
                                Toast.makeText(getContext(), "Failed to read", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "JSON Parsing Error: " + e.toString(), Toast.LENGTH_SHORT).show();
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
                params.put("order_date", strOrder_Date);
                params.put("remarks", remarks);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(view.getContext());
        requestQueue.add(stringRequest);
    }

    private void Update_Order_Reject(View view, final String strOrder_Date, final String remarks, final String Customer_id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_EDIT_ORDER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")) {
                                Toast.makeText(getContext(), "Successfully Updated", Toast.LENGTH_SHORT).show();
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

                                                            String url = "https://click-1595830894120.firebaseio.com/users.json";

                                                            StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                                                                @Override
                                                                public void onResponse(String s) {
                                                                    try {
                                                                        JSONObject obj = new JSONObject(s);

                                                                        TOPIC = obj.getJSONObject(strName).get("token").toString();
                                                                        NOTIFICATION_TITLE = "KetekMall";
                                                                        NOTIFICATION_MESSAGE = "Hi, your order is being rejected!";

                                                                        JSONObject notification = new JSONObject();
                                                                        JSONObject notifcationBody = new JSONObject();
                                                                        try {
                                                                            notifcationBody.put("title", NOTIFICATION_TITLE);
                                                                            notifcationBody.put("message", NOTIFICATION_MESSAGE);

                                                                            notification.put("to", TOPIC);
                                                                            notification.put("data", notifcationBody);
                                                                            sendNotification(notification);

                                                                            Log.d(TAG, "onCreate: " + NOTIFICATION_MESSAGE + NOTIFICATION_TITLE);
                                                                        } catch (JSONException e) {
                                                                            Log.e(TAG, "onCreate: " + e.getMessage());
                                                                        }
                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                            }, new Response.ErrorListener() {
                                                                @Override
                                                                public void onErrorResponse(VolleyError volleyError) {
                                                                    System.out.println("" + volleyError);
                                                                }
                                                            });
                                                            RequestQueue rQueue = Volley.newRequestQueue(getContext());
                                                            rQueue.add(request);


                                                        }
                                                    } else {
                                                        Toast.makeText(getContext(), "Incorrect Information", Toast.LENGTH_SHORT).show();
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
                                        params.put("id", Customer_id);
                                        return params;
                                    }
                                };
                                RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                                requestQueue.add(stringRequest);
                            } else {
                                Toast.makeText(getContext(), "Failed to read", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "JSON Parsing Error: " + e.toString(), Toast.LENGTH_SHORT).show();
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
                params.put("order_date", strOrder_Date);
                params.put("remarks", remarks);
                params.put("status", remarks);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(view.getContext());
        requestQueue.add(stringRequest);
    }

    private void Delete_Order(final View view, final String strOrder_ID){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_DELETE_ORDER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")) {
                                Toast.makeText(getContext(), "Your order has been updated", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Failed to read", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "JSON Parsing Error: " + e.toString(), Toast.LENGTH_SHORT).show();
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
                params.put("id", strOrder_ID);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(view.getContext());
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
                        Toast.makeText(getContext(), "Request error", Toast.LENGTH_LONG).show();
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
        MySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    private void SellerCheck(final String user_id){
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
                                Toast.makeText(getContext(), "Incorrect Information", Toast.LENGTH_SHORT).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }
}
