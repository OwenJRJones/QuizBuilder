package com.example.quizbuilder_assign2;

import androidx.appcompat.app.AppCompatActivity;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.util.*;

public class QuizActivity extends AppCompatActivity {
    //Variables for storing quiz data
    ArrayList<String> arrDef = new ArrayList<>();
    ArrayList<String> arrTerm = new ArrayList<>();
    Map<String,String> quizMap = new HashMap<>();

    //Variables for holding active quiz data
    String currDef;
    String corrTerm;
    int score = 0;

    //Controls
    TextView tvQuizUser, tvScore, tvDefinition;
    Button btnT1, btnT2, btnT3, btnT4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        //Hookup controls
        tvQuizUser = findViewById(R.id.tvQuizUser);
        tvScore = findViewById(R.id.tvtScore);
        tvDefinition = findViewById(R.id.tvDefinition);
        btnT1 = findViewById(R.id.btnT1);
        btnT2 = findViewById(R.id.btnT2);
        btnT3 = findViewById(R.id.btnT3);
        btnT4 = findViewById(R.id.btnT4);

        //Set buttons to listener class
        btnT1.setOnClickListener(onButtonClicked);
        btnT2.setOnClickListener(onButtonClicked);
        btnT3.setOnClickListener(onButtonClicked);
        btnT4.setOnClickListener(onButtonClicked);

        //Code to accept bundles and use data accordingly
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            //Set user's name
            String username = extras.getString("USERNAME") + "'s Quiz";
            tvQuizUser.setText(username);
        } else {
            //Use as backup
            String unknown = "Unknown";
            tvQuizUser.setText(unknown);
        }

        //Read in and parse file data
        String[] fileData = readData().split(",");

        //Initialize quiz with file data
        initQuiz(fileData);

        //Execute quiz
        runQuiz();
    }//End onCreate

    //Button handler
    public View.OnClickListener onButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnT1:
                    if (btnT1.getText().equals("Restart Quiz?"))
                        restartQuiz();
                    else
                        checkAnswer((String)btnT1.getText(), corrTerm);
                    break;
                case R.id.btnT2:
                    checkAnswer((String)btnT2.getText(), corrTerm);
                    break;
                case R.id.btnT3:
                    checkAnswer((String)btnT3.getText(), corrTerm);
                    break;
                case R.id.btnT4:
                    checkAnswer((String)btnT4.getText(), corrTerm);
                    break;
            }//End switch
        }//End onClick method
    };//End button handler

    private void runQuiz() {
        //Check if quiz has been completed
        if(!arrDef.isEmpty()){
            //Set currDef and correct term
            currDef = arrDef.get(0);
            corrTerm = quizMap.get(currDef);

            //Create temporary ArrayList to shuffle terms to be displayed
            ArrayList<String> termPool = new ArrayList<>(3);

            //Add correct term to array
            termPool.add(corrTerm);

            //Get random incorrect terms (no dupes)
            for(int i = 0; i < 3; i++) {
                while(termPool.contains(arrTerm.get(i))){
                    Collections.shuffle(arrTerm, new Random());
                }
                termPool.add(arrTerm.get(i));
            }

            //Shuffle terms before displaying
            Collections.shuffle(termPool, new Random());

            //Assign def/terms accordingly
            tvDefinition.setText(currDef);
            btnT1.setText(termPool.get(0));
            btnT2.setText(termPool.get(1));
            btnT3.setText(termPool.get(2));
            btnT4.setText(termPool.get(3));

            //Delete used definition and shuffle remaining lists again
            arrDef.remove(currDef);
            Collections.shuffle(arrDef, new Random());
            Collections.shuffle(arrTerm, new Random());
        }
        else {
            tvDefinition.setText(getResources().getString(R.string.QuizComplete));
            btnT1.setText(getResources().getString(R.string.Restart));
            btnT2.setVisibility(View.INVISIBLE);
            btnT3.setVisibility(View.INVISIBLE);
            btnT4.setVisibility(View.INVISIBLE);
        }
    }//End runQuiz

    private void initQuiz(String[] quizData) {
        //Separate definitions and terms accordingly
        for(int i = 0; i < quizData.length; i++){
            if(i % 2 == 0){
                arrDef.add(quizData[i]);
            }
            else {
                arrTerm.add(quizData[i]);
            }
        }

        //Load data into hashmap
        for(int i = 0; i < arrDef.size(); i++) {
            quizMap.put(arrDef.get(i),arrTerm.get(i));
        }

        //Shuffle ArrayLists
        Collections.shuffle(arrDef, new Random());
        Collections.shuffle(arrTerm, new Random());

        //Set initial score
        updateScore(score,arrTerm.size());
    }//End initQuiz

    private String readData() {
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

        //Return file data
        return data;
    }//End readData

    private void checkAnswer(String selected, String corrTerm) {
        if(selected.equals(corrTerm)) {
            score += 1;
        }
        //Update score
        updateScore(score, arrTerm.size());
        //Continue quiz
        runQuiz();
    }//End checkAnswer

    private void updateScore(int score, int total) {
        tvScore.setText(String.format(getResources().getString(R.string.tvScore), score, total));
    }//End updateScore

    private void restartQuiz() {
        //Restart quiz
        arrDef.clear();
        arrTerm.clear();
        quizMap.clear();
        score = 0;
        btnT2.setVisibility(View.VISIBLE);
        btnT3.setVisibility(View.VISIBLE);
        btnT4.setVisibility(View.VISIBLE);
        String[] fileData = readData().split(",");
        initQuiz(fileData);
        runQuiz();
    }//End restartQuiz
}//End Main