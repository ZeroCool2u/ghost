package com.google.engedu.ghost;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;


public class GhostActivity extends AppCompatActivity {
    private static final String COMPUTER_TURN = "Alright, it's the computer's turn.";
    private static final String USER_TURN = "Okay, your turn.";
    private static final String SUCCESS = "Oh shit, that's a word!";
    private static final String computerWin = "Yeah, you lost.";
    private static final String userWin = "Oh, you actually won!";
    private static final String WORD_STATE = "CurrentWordFragment";
    private static final String STATUS_STATE = "CurrentGameStatus";
    private static final String TURN_STATE = "IsUserTurnState";
    private static final String WHO_WENT_FIRST = "WhichPlayerWentFirst";

    protected static boolean USER_FIRST_TURN = false;
    protected static boolean RESTORING_INSTANCE = false;
    private GhostDictionary dictionary;
    private boolean userTurn = false;
    private Random random = new Random();
    private static String currentFrag = "";
    private static String currentState = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null){
            //Restore
            currentFrag = savedInstanceState.getString(WORD_STATE);
            currentState = savedInstanceState.getString(STATUS_STATE);
            userTurn = savedInstanceState.getBoolean(TURN_STATE);
            USER_FIRST_TURN = savedInstanceState.getBoolean(WHO_WENT_FIRST);
            RESTORING_INSTANCE = true;
        }
        setContentView(R.layout.activity_ghost);
        AssetManager assetManager = getAssets();
        try {
            InputStream inputStream = assetManager.open("words.txt");
            dictionary = new SimpleDictionary(inputStream);
        } catch (IOException e) {
            Toast toast = Toast.makeText(this, "Could not load dictionary", Toast.LENGTH_LONG);
            toast.show();
        }
        onStart(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ghost, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Handler for the "Reset" button.
     * Randomly determines whether the game starts with a user turn or a computer turn.
     */
    public void onStart(View view) {
        if (RESTORING_INSTANCE){
            TextView gameWord = (TextView) findViewById(R.id.ghostText);
            gameWord.setText(currentFrag);
            TextView gameStatus = (TextView) findViewById(R.id.gameStatus);
            if (userTurn) {
                gameStatus.setText(USER_TURN);
                currentState = gameStatus.getText().toString();
            } else {
                gameStatus.setText(COMPUTER_TURN);
                currentState = gameStatus.getText().toString();
                computerTurn();
            }
        }else{
            userTurn = random.nextBoolean();
            USER_FIRST_TURN = userTurn;
            TextView gameWord = (TextView) findViewById(R.id.ghostText);
            gameWord.setText("");
            currentFrag = gameWord.getText().toString();
            TextView gameStatus = (TextView) findViewById(R.id.gameStatus);
            if (userTurn) {
                gameStatus.setText(USER_TURN);
                currentState = gameStatus.getText().toString();
            } else {
                gameStatus.setText(COMPUTER_TURN);
                currentState = gameStatus.getText().toString();
                computerTurn();
            }}
    }

    private void computerTurn() {
        TextView gameStatus = (TextView) findViewById(R.id.gameStatus);
        TextView gameWordView = (TextView) findViewById(R.id.ghostText);
        String gameWord = gameWordView.getText().toString();
        if(dictionary.isWord(gameWord) && gameWord.length() >= 4){
            gameStatus.setText(computerWin);
            currentState = gameStatus.getText().toString();
        }
        else{
            String newWord = dictionary.getGoodWordStartingWith(gameWord);
            if(newWord == null){
                gameStatus.setText(computerWin);
                currentState = gameStatus.getText().toString();
            }
            else {
                int gameWordLength = gameWord.length();
                String nextLetter = Character.toString(newWord.charAt(gameWordLength));
                String nextFrag = gameWord + nextLetter;
                gameWordView.setText(nextFrag);
                userTurn = true;
                gameStatus.setText(USER_TURN);
                currentState = gameStatus.getText().toString();
            }
        }

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event){
        TextView gameStatus = (TextView) findViewById(R.id.gameStatus);
        keyCode = event.getUnicodeChar();
        String letter = Character.toString((char) keyCode);
        if(!Character.isLetter(keyCode)){
            return super.onKeyUp(keyCode,event);
        }
        TextView gameWord = (TextView) findViewById(R.id.ghostText);
        gameWord.append(letter);
        currentFrag = gameWord.getText().toString();
        if(dictionary.isWord(gameWord.getText().toString())){
            gameStatus.setText(SUCCESS);
            currentState = gameStatus.getText().toString();
        }
        userTurn = false;
        gameStatus.setText(COMPUTER_TURN);
        currentState = gameStatus.getText().toString();
        computerTurn();
        return true;
    }

    public void challenge(View view){
        TextView gameWordView = (TextView) findViewById(R.id.ghostText);
        String gameWord = gameWordView.getText().toString();
        TextView gameStatus = (TextView) findViewById(R.id.gameStatus);
        if (dictionary.isWord(gameWord) && gameWord.length() >= 4){
            gameStatus.setText(userWin);
            currentState = gameStatus.getText().toString();
        }
        else if(dictionary.getGoodWordStartingWith(gameWord) == null){
            gameStatus.setText(userWin);
            currentState = gameStatus.getText().toString();
        }
        else{
            gameStatus.setText(computerWin);
            currentState = gameStatus.getText().toString();
            gameWordView.setText(dictionary.getGoodWordStartingWith(gameWord));
        }
    }

    //Save game state
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        // Save the user's current game state
        savedInstanceState.putString(WORD_STATE, currentFrag);
        savedInstanceState.putString(STATUS_STATE, currentState);
        savedInstanceState.putBoolean(TURN_STATE, userTurn);
        savedInstanceState.putBoolean(WHO_WENT_FIRST, USER_FIRST_TURN);

        // Calling the superclass so it can save the view hierarchy state.
        super.onSaveInstanceState(savedInstanceState);
    }

}
