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

import com.example.fittracker.db.loginDB;
import com.example.fittracker.main.MainActivity;

public class LoginActivity extends AppCompatActivity {
    private Context context;
    EditText username, password;
    loginDB myDB = new loginDB(LoginActivity.this);
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = findViewById(R.id.textUsername);
        password = findViewById(R.id.textPassword);
        loginButton = findViewById(R.id.b_login);

        String userName = username.getText().toString().trim();
        String passWord = password.getText().toString().trim();

        if (userName.isEmpty() || passWord.isEmpty()) {
            loginButton.setEnabled(false);
        }
        else {
            loginButton.setEnabled(true);
        }
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyLogin(userName, passWord);
            }
        });
    }

    private void verifyLogin(String userName, String passWord) {
        boolean a = myDB.getUserName(userName, null);
        if (a) {
            myDB.verifyLoginInfo(userName, passWord);
        }
        else {
            Toast.makeText(context, "Username does not exist!", Toast.LENGTH_SHORT).show();        }
    }

    //Create menu on login page
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
            case R.id.createAccountMenu:
                newAccount();
                return true;
            case R.id.helpMenu:
                getHelp();
                return true;
            default :
                return super.onOptionsItemSelected(item);

        }
    }

    // direct user to Help page from menu item
    public void getHelp() {

    }

    // direct user to Create Account page from menu item
    public void newAccount() {
        Intent intent = new Intent(this, CreateAccountActivity.class);
        startActivity(intent);
    }

    public void login() {
        Intent intent = new Intent (this, MainActivity.class );
        startActivity(intent);
    }


}