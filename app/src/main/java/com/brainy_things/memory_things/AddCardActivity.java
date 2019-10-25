package com.brainy_things.memory_things;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.PriorityQueue;

public class AddCardActivity extends AppCompatActivity {

    // This is the file where pq is stored
    private String FILE_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        // Initializing values from resources
        FILE_NAME = getString(R.string.file_name);
    }

    // This function will add the card into the file system
    public void confirm(View v) {
        EditText addCardCategoryET = findViewById(R.id.add_card_category_et);
        EditText addCardQuesET = findViewById(R.id.add_card_ques_et);
        EditText addCardAnsET = findViewById(R.id.add_card_ans_et);
        EditText addCardDescET = findViewById(R.id.add_card_desc_et);

        // Any of the above edit text fields cannot be empty
        // If any field is empty it will be focused
        // Show the hint to users that the fields cannot be empty
        if (addCardCategoryET.getText().toString().isEmpty()) {
            addCardCategoryET.setHint(R.string.add_card_category_et_hint_1);
            addCardCategoryET.requestFocus();
        } else if (addCardQuesET.getText().toString().isEmpty()) {
            addCardQuesET.setHint(R.string.add_card_ques_et_hint_1);
            addCardQuesET.requestFocus();
        } else if (addCardAnsET.getText().toString().isEmpty()) {
            addCardAnsET.setHint(R.string.add_card_ans_et_hint_1);
            addCardAnsET.requestFocus();
        } else if (addCardDescET.getText().toString().isEmpty()) {
            addCardDescET.setHint(R.string.add_card_desc_et_hint_1);
            addCardDescET.requestFocus();
        } else {
            try {

                // Open Input Streams to read the pq
                FileInputStream fis = openFileInput(FILE_NAME);
                ObjectInputStream ois = new ObjectInputStream(fis);

                // Read the pq from file system
                // noinspection unchecked
                PriorityQueue<Card> pq = (PriorityQueue<Card>) ois.readObject();

                // Close the input streams
                ois.close();
                fis.close();

                // Create a new card and add it to the pq
                Card c = new Card(addCardCategoryET.getText().toString(),
                        addCardQuesET.getText().toString(),
                        addCardAnsET.getText().toString(),
                        addCardDescET.getText().toString(),
                        5);
                pq.add(c);

                //Open Output Streams to store pq
                FileOutputStream fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
                ObjectOutputStream oos = new ObjectOutputStream(fos);

                // Write pq into file system
                oos.writeObject(pq);

                // Close the Output Streams
                oos.close();
                fos.close();
            } catch (Exception ignore) {
            }

            // Go back to the main activity
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }
    }

    // This function will discard the inputs and go back to main activity
    public void cancel(View v) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}
