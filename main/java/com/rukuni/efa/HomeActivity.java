package com.rukuni.efa;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class HomeActivity extends Activity {

    ImageView btnLogout;
    ImageView btnReport;
    ImageView btnSearch;
    ImageView btnLinkToHome;
    Button btnLinkToHome2;
    ImageView btnPayments;

    private SQLiteHandler db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnLogout = findViewById(R.id.imgLogout);
        btnReport = findViewById(R.id.imgTips);
        btnSearch = findViewById(R.id.imgCalendar);
        btnLinkToHome = findViewById(R.id.imgSteps);
        btnLinkToHome2 = findViewById(R.id.btnLinkToHomeScreen);
        btnPayments = findViewById(R.id.imgPayments);

        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertActivity();
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity();
            }
        });

        btnLinkToHome.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, HomeActivity.class);
                finish();
                overridePendingTransition(0, 0);
                startActivity(i);
                overridePendingTransition(0, 0);
            }
        });

        btnLinkToHome2.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, HomeActivity.class);
                finish();
                overridePendingTransition(0, 0);
                startActivity(i);
                overridePendingTransition(0, 0);
            }
        });
    }

    public void insertActivity()
    {
        Intent intent = new Intent(this, InsertActivity.class);
        startActivity(intent);
    }

    public void mainActivity()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}

