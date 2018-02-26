package com.example.alexzander.braintrainer;

import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    int answer;
    int rightAnswersCounter;
    int totalAnswersCounter;
    int secondsToSolve = 20;
    int timerCounter;
    boolean gameActive;

    Button theStartStopButton;
    List<Button> buttons;
    TextView questionText;
    TextView outText;
    TextView timerText;
    TextView scoreText;

    Random rnd;
    final Handler handler = new Handler();
    Runnable run;

    private static final int[] BUTTON_IDS = {
            R.id.answerButton1,
            R.id.answerButton2,
            R.id.answerButton3,
            R.id.answerButton4,
    };


    public String getANewQuestion() {
        int firstNumber = rnd.nextInt(10 );
        int secondNumber = rnd.nextInt(10);
        String out = Integer.toString(firstNumber) + " + " + Integer.toString(secondNumber) + " =";
        answer = firstNumber + secondNumber;
        Log.i("Right answer: ", Integer.toString(answer));
        return out;
    }

    public void fillInButtons() {
        int rightButtonNumber = rnd.nextInt(3) + 1;
        Log.i("the RightAnswerButton number should be: ", Integer.toString(rightButtonNumber));
        for(Button b: buttons) {
            if(b.getTag().equals(Integer.toString(rightButtonNumber))) {
                b.setText(Integer.toString(answer));
            } else {
                b.setText( Integer.toString(rnd.nextInt(20)) );
            }
        }
    }

    public void onTheAnswerButtonClick(View view) {
        Button pressedButton = (Button) view;
        if (pressedButton.getText().equals(Integer.toString(answer))) {
            //Log.i("Info: ", "The answer is right");
            rightAnswersCounter++;
            totalAnswersCounter++;
            scoreText.setText(Integer.toString(rightAnswersCounter) + "/" + Integer.toString(totalAnswersCounter));

            handler.removeCallbacks(run);
            timerCounter = secondsToSolve;
            handler.post(run);
            questionText.setText(getANewQuestion());
            fillInButtons();
        } else {
            //Log.i("Info: ", "The answer is wrong");
            totalAnswersCounter++;
            scoreText.setText(Integer.toString(rightAnswersCounter) + "/" + Integer.toString(totalAnswersCounter));

            handler.removeCallbacks(run);
            timerCounter = secondsToSolve;
            handler.post(run);
            questionText.setText(getANewQuestion());
            fillInButtons();
        }
    }

    public void onTheStartStopButtonClick(View view) {
        if (!gameActive) {
            outText.setVisibility(View.INVISIBLE);
            theStartStopButton.setText("STOP");
            timerCounter = secondsToSolve;
            gameActive = true;
            handler.post(run);

            questionText.setText(getANewQuestion());
            fillInButtons();

        } else {
            theStartStopButton.setText("START");
            timerCounter = 0;
            timerText.setText("0s");
            gameActive = false;
            handler.removeCallbacks(run);
            gameActive = false;
            outText.setText("Game Over!\nYour final score is " + rightAnswersCounter + " from " + totalAnswersCounter + "\nTo start game again - press the START button.");
            outText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize variables and all needed widgets
        totalAnswersCounter = 0;
        rightAnswersCounter = 0;
        gameActive = false;

        theStartStopButton = findViewById(R.id.theStartStopButton);
        buttons = new ArrayList<Button>(BUTTON_IDS.length);
        for (int id: BUTTON_IDS) {
            Button button = (Button)findViewById(id);
            buttons.add(button);
        }

        questionText = findViewById(R.id.theQuesionTextView);
        outText = findViewById(R.id.outTextView);
        timerText = findViewById(R.id.timerTextView);
        scoreText = findViewById(R.id.scoreTextView);
        rnd = new Random();

        run = new Runnable() {
            @Override
            public void run() {
                if (timerCounter > 0) {
                    timerCounter --;
                    timerText.setText(Integer.toString(timerCounter) + "s");
                    handler.postDelayed(this, 1000);
                } else {
                    handler.removeCallbacks(run);
                    theStartStopButton.setText("START");
                    gameActive = false;
                    outText.setText("Game Over!\nYour final score is " + rightAnswersCounter + " from " + totalAnswersCounter + "\nTo start game again - press the START button.");
                    outText.setVisibility(View.VISIBLE);
                }
            }
        };

    }
}
