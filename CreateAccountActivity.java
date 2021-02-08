package com.example.fittracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.example.fittracker.db.oldDB;

public class CreateAccountActivity extends AppCompatActivity {

    oldDB myDB;
    EditText Name, Pass, ConPass;
    Button add;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        Name = findViewById(R.id.textUsername);
        Pass = findViewById(R.id.textPassword);
        ConPass = findViewById(R.id.confirmPassword);
        add = findViewById(R.id.b_createAccount);

        String name = Name.getText().toString().trim();
        String pass = Pass.getText().toString().trim();
        String conpass = ConPass.getText().toString().trim();

        add.setOnClickListener(new View.OnClickListener() {
            private Context context;

            @Override
            public void onClick(View v) {
                if(pass.equals(conpass)) {
                    boolean un = myDB.getUserName(name, null);
                    if (!un) {
                        myDB = new oldDB(CreateAccountActivity.this);
                        myDB.insertData(name, pass);
                    }
                    else {
                        Toast.makeText(context, "Username already exist!", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show();
                }
            }

        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.login_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Handle items in menu
        switch (item.getItemId()) {
            //open create account fragment or help fragment from menu
            case R.id.loginPage:
                loginPage();
                return true;
            case R.id.helpMenu:
                getHelp();
                return true;
            default :
                return super.onOptionsItemSelected(item);
        }
    }

    public void loginPage() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }


    public void getHelp() {
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }

    public void addUser() {

    }

}