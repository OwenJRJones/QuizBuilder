package com.example.quizbuilder_assign2;

import androidx.appcompat.app.AppCompatActivity;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import android.content.*;
import java.io.*;

public class MainActivity extends AppCompatActivity {

    //Controls
    Button btnStart;
    EditText etUserName;
    TextView tvWhoops, tvError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Hookup controls
        btnStart = findViewById(R.id.btnStartQuiz);
        etUserName = findViewById(R.id.etUserName);
        tvWhoops = findViewById(R.id.tvWhoops);
        tvError = findViewById(R.id.tvError);

        //Set error messages invisible
        tvWhoops.setVisibility(View.INVISIBLE);
        tvError.setVisibility(View.INVISIBLE);

        //Check if user selected valid input file
        if(!checkData()){
            tvWhoops.setVisibility(View.VISIBLE);
            tvError.setVisibility(View.VISIBLE);
            btnStart.setEnabled(false);
            etUserName.setEnabled(false);
        }

        //Button handler
        btnStart.setOnClickListener(view -> {
            //Check if user entered valid name
            if (etUserName.getText().toString().isEmpty() || etUserName.getText().toString().matches("\\s+")) {
                etUserName.setText("");
                Toast.makeText(getBaseContext(), "Please enter a valid name.", Toast.LENGTH_SHORT).show();
            } else if (!etUserName.getText().toString().matches("^[a-zA-Z]+$")) {
                etUserName.setText("");
                Toast.makeText(getBaseContext(), "Please use letters only.", Toast.LENGTH_SHORT).show();
            } else {
                //Create Intent & Bundle to pass user's  name
                Intent i = new Intent("QuizActivity");
                Bundle extras = new Bundle();
                extras.putString("USERNAME", etUserName.getText().toString());
                i.putExtras(extras);
                startActivityForResult(i, 1);
            }
        });//End handler class
    }//End onCreate

    private boolean checkData() {
        //Tag variables
        final String IOTAG = "IO Error";
        final String GTAG = "General Error";
        final String FTAG = "File Alert";

        //Store quiz data
        String data = null;

        //For reading in data
        String str = null;
        BufferedReader br = null;

        //Read in data
        try {
            //Create InputStream and BufferReader
            InputStream is = getResources().openRawResource(R.raw.testdata);
            br = new BufferedReader(new InputStreamReader(is));

            //Signal the file is open
            Log.e(FTAG, "File in RAW is open.");

            while ((str = br.readLine()) != null) {
                //Check if this is the first time adding to data
                if(data == null){
                    data = str;
                }
                else {
                    //Otherwise add to data
                    data = data.concat(str);
                }
            }

            //Close InputStream
            is.close();

            //Signal file was closed
            Log.e(FTAG, "File in RAW was closed.");

        } catch (IOException e) {
            Log.e(IOTAG, e.getMessage());
        }   catch(Exception e) {
            Log.e(GTAG,e.getMessage());
        }

        //Split data into array for checking size
        String[] arrData = data.split(",");

        //Return T/F if equal number of terms and definitions
        return arrData.length % 2 == 0;
    }//End checkData
}//End Main