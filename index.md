## Welcome to my CS499 Capstone 

Please go to the following URL to see project plans. 

[ProjectPlan](https://www.youtube.com/watch?v=ISxzL4y6zcE&feature=youtu.be)

### Upgraded Code

```markdown
Syntax highlighted code block

# MainActivity.java
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fittracker.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    FloatingActionButton add_button;
    ImageView empty_imageview;
    TextView no_data;

    //Configure DB for class usage 
    workoutDB myDB = new workoutDB(this);
    ArrayList<String> workout_id, workout_exercise, workout_sets, workout_reps, workout_volume;
    CustomAdapter customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Create recycler view for viewing database items
        recyclerView = findViewById(R.id.recyclerView);
        add_button = findViewById(R.id.add_button);
        empty_imageview = findViewById(R.id.empty_imageview);
        no_data = findViewById(R.id.no_data);
        //Navigate to the AddActivity.java to add workouts to database
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                startActivity(intent);
            }
        });

        //New array for workout information
        myDB = new workoutDB(MainActivity.this);
        workout_id = new ArrayList<>();
        workout_exercise = new ArrayList<>();
        workout_sets = new ArrayList<>();
        workout_reps = new ArrayList<>();
        workout_volume = new ArrayList<>();
        
        //Custom adapter class will be used to store array items and display them using Recycler view 
        customAdapter = new CustomAdapter(MainActivity.this,this, workout_id, workout_exercise, workout_sets,
                workout_reps, workout_volume);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            recreate();
        }
    }

    // Create array for data access
    void storeDataInArrays(){
        Cursor cursor = myDB.getData();
        if(cursor.getCount() == 0){
            empty_imageview.setVisibility(View.VISIBLE);
            no_data.setVisibility(View.VISIBLE);
        }else{
            while (cursor.moveToNext()){
                workout_id.add(cursor.getString(0));
                workout_exercise.add(cursor.getString(1));
                workout_sets.add(cursor.getString(2));
                workout_reps.add(cursor.getString(3));
                workout_volume.add(cursor.getString(4));
            }
            empty_imageview.setVisibility(View.GONE);
            no_data.setVisibility(View.GONE);
        }
    }

    //Load menu for main 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.delete_all){
            confirmDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    void confirmDialog(){
        // Create alert before user deletes database to confirm action
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete All?");
        builder.setMessage("Are you sure you want to delete all Data?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                workoutDB myDB = new workoutDB(MainActivity.this);
                myDB.deleteWorkoutDatabase();
                //Refresh Activity
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }
}
  

## loginDB.java
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;
import androidx.annotation.Nullable;

class loginDB extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "loginDB";
    private static final String TABLE_NAME = "loginTable";
    private static final int DATABASE_Version = 1;
    private static final String UID ="_id";
    private static final String NAME = "name";
    private static final String PASSWORD = "password";
    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("+UID+" " +
            " INTEGER PRIMARY KEY AUTOINCREMENT, "+NAME+" VARCHAR(255), "+ PASSWORD+ " VARCHAR(255));";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS "+TABLE_NAME;
    public Context context;


    loginDB(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_Version);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL((DROP_TABLE));
        onCreate(db);
    }

    //This provides a way to see if username is already used in database
    public void getUserName(String un, String[] columns) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + NAME + " =?";
        String[] whereArgs = {un};
        Cursor cursor = db.rawQuery(query, whereArgs);

        int count = cursor.getCount();
        cursor.close();
        if(count >= 1) {
            Toast.makeText(context, "Username Found", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(context, "Username not found", Toast.LENGTH_SHORT).show();
        }
    }

    /*Verify login information once username has been discovered*/
    public int verifyLoginInfo(String un, String pw) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + NAME +
                " = ?" + " AND " + PASSWORD + " = ?";
        String [] whereArgs = {un, pw};
        Cursor cursor = db.rawQuery(query, whereArgs);

        int count = cursor.getCount();
        cursor.close();
        /*If program finds a username than start MainActivity.class*/
        if (count >= 1) {
            return 1;
        }
        else {
            return 0;
        }

    }

    //Create new login information
    void insertData(String name, String pass) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();


        contentValues.put(NAME, name);
        contentValues.put(PASSWORD, pass);
        long id = db.insert(TABLE_NAME, null, contentValues);
        if(id == -1){
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Added Successfull!", Toast.LENGTH_SHORT).show();
        }
    }
}
  
  
### activity_create_account.xml
  <?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#F88F8F"
    tools:context=".login.CreateAccountActivity">

    //Input for username
    <EditText
        android:id="@+id/textUsername"
        style="@style/Widget.AppCompat.EditText"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginStart="59dp"
        android:layout_marginTop="90dp"
        android:layout_marginEnd="59dp"
        android:ems="10"
        android:fontFamily="monospace"
        android:hint="Username"
        android:inputType="textPersonName"
        android:singleLine="true"
        android:textAlignment="center"
        android:textColor="@color/black"
        android:textSize="25dp"
        android:textStyle="bold"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/text1" />
    //Input for username password
    <EditText
        android:id="@+id/textPassword"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginStart="59dp"
        android:layout_marginTop="45dp"
        android:layout_marginEnd="59dp"
        android:ems="10"
        android:hint="Password"
        android:inputType="textPassword"
        android:textAlignment="center"
        android:textColor="@color/black"
        android:textSize="25dp"
        android:textStyle="bold"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/textUsername" />
    //Create account 
    <Button
        android:id="@+id/b_createAccount"
        style="@style/Widget.MaterialComponents.ExtendedFloatingActionButton"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginStart="118dp"
        android:layout_marginEnd="118dp"
        android:layout_marginTop="40dp"
        android:text="Create Account"
        app:layout_constraintTop_toBottomOf="@+id/b_searchUN"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent" />
    //Search if username exist
    <Button
        android:id="@+id/b_searchUN"
        style="@style/Widget.MaterialComponents.ExtendedFloatingActionButton"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginStart="118dp"
        android:layout_marginEnd="118dp"
        android:layout_marginTop="40dp"
        android:text="Search UserName"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/textPassword" />
    //Banner
    <TextView
        android:id="@+id/text1"
        android:layout_width="369dp"
        android:layout_height="53dp"
        android:layout_marginStart="21dp"
        android:layout_marginTop="69dp"
        android:layout_marginEnd="21dp"
        android:background="#E44343"
        android:text="Start Your Fitness Journal!"
        android:textAlignment="center"
        android:textAppearance="@style/TextAppearance.AppCompat.Body2"
        android:textSize="30dp"
        android:textStyle="bold"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>

```


### Narratives 
*MainActivity.java*<br/>
The MainActivity.java class was created for displaying workout data and having the option to add or delete workout data. A menu resource was used in order to delete all workouts and a floating action button was used in order to direct the user to the AddActivity.java class for inserting details about the workout. I used a handful of different methods. The confirmDialog() was a learning point because I built, for the first time, an AlertDialog that would pop up if the user selected 'Delete All' from the menu resource. The pop up dialog asked if the user was certain they wanted to delete all the data, if yes than the database was deleted, if no than the user returned to the MainActivity.java class. The process of navigating from start, onCreateOptionsMenu(), to end, confirmDialog.onClick(), was a learning curve. I had to make sure that each operation had an alternative in case the user decided they no longer wanted to proceed and to make sure the database was communicating properly with the methods. I chose this as one of my reflecting because I wanted to use algorithms and showcase navigation skills within a program.<br/>
*LoginDB*<br/>
For the loginDB I wanted to create a database that would accept usernames and passwords, retrieve just usernames, and check user permissions from the loginActivity.java class. I created this class at the beginning of the project because it was my struggling point in the course I had orignally created the code for. The loginDB would crash each time a user tried to login, so creating a functioning database was the main objective. Through this process I learned a lot about the CRUD operations of SQLite databases. I had a difficult time at fist getting the verifyLoginInfo() method to work. I learned that by creating a query and whereArgs that I could use the included rawQuery() method to pass an argument that would search the database. Perfecting that argument took some time. SQLite can be very picky and there were times that the application crashed because the code was written as " =?" opposed to " = ?". Learning more about WHERE, AND, OR and other SQL operations has really helped my coding performance. I am happy to say this code worked perfectly on the create_account.java class and LoginActivity.java class. It was able to send toast messsages back giving user updates, match usernames to passwords, and direct user to MainActivity.java class if login information was correct. I chose to use this artifact because I want to show that I can operate the CRUD instructions of SQLite databases. <br/>
*create_account_activity.xml*<br/>
One of my goals was to become a better UI designer for this project. Sowftware engineering expands past creating powerful algorthims, there is also a need for displaying this data in a meaningful manner and one that appeals to the user. I used xml coding for this resource in order to create EditText, Buttons, and TextViews. The layout would allow the user to enter a username and password then hit select from 'Search Username' or 'Create Account'. I created this code towards the middle of the project once I had the database and LoginActivity.java class completed. This class accessed from the Login.menu resource which included navigation to 'Login', 'Help', and 'Create Account'. I wanted to use this resource in my portfolio to show that I am adapt with xml coding and most importantly I am able to access xml code from java classes to create algorithms. I did this by assigning each a unique id which I could identify in the .java class. I also enhanced the UI by creating a theme for my project that included banners, background color, and different color schemes. <br/>

### Proffesional Assesment 


