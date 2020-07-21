package com.rukuni.efa;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InsertActivity extends Activity {

    String ServerURL = "http://192.168.1.101/efa.com/include/process.inc.php";

    EditText meter, contact, problem;
    Button submitButton, btnReport, btnLogout, btnLinkToHome;

    String TempMeter;
    String TempContact;
    String TempProblem;

    private SQLiteHandler db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);

        meter = findViewById(R.id.editMeter);
        contact = findViewById(R.id.editContact);
        problem = findViewById(R.id.editProblem);

        btnLinkToHome = findViewById(R.id.btnLinkToHomeScreen);
        btnReport = findViewById(R.id.btnReport);
        btnLogout = findViewById(R.id.btnLogout);
        submitButton = findViewById(R.id.submitButton);

        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                GetData();
                InsertData(TempMeter, TempContact, TempProblem);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity();
            }
        });

        btnLinkToHome.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(InsertActivity.this, InsertActivity.class);
                finish();
                overridePendingTransition(0, 0);
                startActivity(i);
                overridePendingTransition(0, 0);
            }
        });
    }

    public void viewActivity() {
        Intent intent = new Intent(InsertActivity.this, ViewActivity.class);
        startActivity(intent);
    }

    public void mainActivity() {
        Intent intent = new Intent(InsertActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    public void insertActivity() {
        Intent intent = new Intent(InsertActivity.this, InsertActivity.class);
        startActivity(intent);
    }

    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        Intent intent = new Intent(InsertActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void GetData()
    {
        TempMeter = meter.getText().toString();
        TempContact = contact.getText().toString();
        TempProblem = problem.getText().toString();

        if(TempMeter.isEmpty() || TempContact.isEmpty() || TempProblem.isEmpty())
        {
            Toast.makeText(InsertActivity.this, "Missing Fields !!", Toast.LENGTH_LONG).show();
            insertActivity();
        }
    }

    public void InsertData(final String meter, final String contact, final String problem) {
        @SuppressLint("StaticFieldLeak")
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String>
        {
            @Override
            protected void onPreExecute()
            {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params)
            {
                List<NameValuePair> nameValuePairs = new ArrayList<>();

                nameValuePairs.add(new BasicNameValuePair("meter", meter));
                nameValuePairs.add(new BasicNameValuePair("contact", contact));
                nameValuePairs.add(new BasicNameValuePair("problem", problem));

                try
                {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(ServerURL);
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    httpResponse.getEntity();
                }
                catch (ClientProtocolException ignored)
                {

                }
                catch (IOException ignored)
                {

                }
                return "Report Successful";
            }

            @Override
            protected void onPostExecute(String result)
            {
                super.onPostExecute(result);
                if(!TempMeter.isEmpty() && !TempContact.isEmpty() && !TempProblem.isEmpty())
                {
                    Toast.makeText(InsertActivity.this, "Processing Report... ", Toast.LENGTH_SHORT).show();
                    viewActivity();
                }
            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(meter, contact, problem);
    }
}