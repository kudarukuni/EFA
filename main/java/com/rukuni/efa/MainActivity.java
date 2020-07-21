package com.rukuni.efa;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends Activity {

    TextView txtName;
    TextView txtEmail;

    EditText search;

    ListView listView;

    Button btnLogout;
    Button btnReport;
    Button btnSearch;
    Button btnLinkToHome;

    private SQLiteHandler db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtName = findViewById(R.id.name);
        txtEmail = findViewById(R.id.email);

        search = findViewById(R.id.search);

        btnLogout = findViewById(R.id.btnLogout);
        btnReport = findViewById(R.id.btnReport);
        btnSearch = findViewById(R.id.btnSearch);
        btnLinkToHome = findViewById(R.id.btnLinkToHomeScreen);

        listView = findViewById(R.id.listView);

        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        HashMap<String,String> user = db.getUserDetails();

        String name = user.get("name");
        String email = user.get("email");

        txtName.setText(name);
        txtEmail.setText(email);

        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeActivity();
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getData();
            }
        });

        btnLinkToHome.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, MainActivity.class);
                finish();
                overridePendingTransition(0, 0);
                startActivity(i);
                overridePendingTransition(0, 0);
            }
        });
    }

    private void getData() {

        String value = search.getText().toString().trim();

        if (value.equals("")) {
            Toast.makeText(this, "Please Enter Meter Number !!", Toast.LENGTH_LONG).show();
            return;
        }

        String url = Config5.DATA_URL + search.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {

                showJSON(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void showJSON(String response) {
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        try
        {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(Config5.JSON_ARRAY);

            if(result.length() < 1)
            {
                Toast.makeText(this, "Meter Number Not Registered!!", Toast.LENGTH_LONG).show();
                logoutUser();
                registerActivity();
                return;
            }

            for (int i = 0; i < result.length(); i++)
            {
                JSONObject jo = result.getJSONObject(i);
                String meter = jo.getString(Config5.KEY_METER);
                String problem = jo.getString(Config5.KEY_PROBLEM);
                String contact = jo.getString(Config5.KEY_CONTACT);
                String process_id = jo.getString(Config5.KEY_PID);

                final HashMap<String, String> searchHistory = new HashMap<>();
                searchHistory.put(Config5.KEY_METER,  "Meter Number: " + meter);
                searchHistory.put(Config5.KEY_PROBLEM, problem);
                searchHistory.put(Config5.KEY_CONTACT, contact);
                searchHistory.put(Config5.KEY_PID, "Report ID: " + process_id);

                list.add(searchHistory);
            }

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        ListAdapter adapter = new SimpleAdapter(MainActivity.this, list, R.layout.activity_mylist, new String[] {
                        Config5.KEY_METER, Config5.KEY_PROBLEM, Config5.KEY_CONTACT, Config5.KEY_PID
                }, new int[] {
                        R.id.meter, R.id.problem, R.id.contact, R.id.pid
                });
        listView.setAdapter(adapter);
    }

    public void homeActivity()
    {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void registerActivity()
    {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}

