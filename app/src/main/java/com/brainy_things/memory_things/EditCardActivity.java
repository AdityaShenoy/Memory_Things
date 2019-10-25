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

public class EditCardActivity extends AppCompatActivity {

    // This is the file where pq is stored
    private String FILE_NAME;

    // This is the priority queue of cards
    private PriorityQueue<Card> pq;

    // This is the current card that is being edited
    private Card currentCard;

    // This is the MESSAGE where the source activity will
    // store the original priority of the card being edited
    final static String MESSAGE = "Original Priority";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_card);

        // Initialize the file name
        FILE_NAME = getString(R.string.file_name);

        showContents();
    }



    // This will set the text of the input fields to the contents of current card
    private void showContents() {
        try {

            // Open Input Streams
            FileInputStream fis = openFileInput(FILE_NAME);
            ObjectInputStream ois = new ObjectInputStream(fis);

            // Read pq
            // noinspection unchecked
            pq = (PriorityQueue<Card>) ois.readObject();

            // Close the Input Streams
            ois.close();
            fis.close();
        } catch (Exception ignore) {
        }


        EditText editCardCategoryET = findViewById(R.id.edit_card_category_et);
        EditText editCardQuesET = findViewById(R.id.edit_card_ques_et);
        EditText editCardAnsET = findViewById(R.id.edit_card_ans_et);
        EditText editCardDescET = findViewById(R.id.edit_card_desc_et);

        // The current card to be edited is at the top of the heap
        currentCard = pq.peek();

        // The current card information will be shown in the edit text
        // noinspection ConstantConditions
        editCardCategoryET.setText(currentCard.getCategory());
        editCardQuesET.setText(currentCard.getQues());
        editCardAnsET.setText(currentCard.getAns());
        editCardDescET.setText(currentCard.getDesc());
    }

    // This will discard the edits in the card and start the Main Activity
    public void cancel(View v) {

        // This is the source intent which will have the original priority of the card
        Intent srcIntent = getIntent();

        // Revert back to original priority of the card
        // The second parameter is the default value
        // If the main activity was the source activity,
        // the priority remains unchanged
        currentCard.setPriority(srcIntent.getIntExtra(MESSAGE, currentCard.getPriority()));

        // This is to call heapify function of pq
        pq.add(pq.poll());

        storePQ();

        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    // This will update the current card and pq
    public void confirm(View v) {
        EditText editCardCategoryET = findViewById(R.id.edit_card_category_et);
        EditText editCardQuesET = findViewById(R.id.edit_card_ques_et);
        EditText editCardAnsET = findViewById(R.id.edit_card_ans_et);
        EditText editCardDescET = findViewById(R.id.edit_card_desc_et);

        // Any of the above edit text fields cannot be empty
        // If any field is empty it will be focused
        // It will also display the hint that the fields cannot be empty
        if (editCardCategoryET.getText().toString().isEmpty()) {
            editCardCategoryET.setHint(R.string.edit_card_category_et_hint_1);
            editCardCategoryET.requestFocus();
        } else if (editCardQuesET.getText().toString().isEmpty()) {
            editCardQuesET.setHint(R.string.edit_card_ques_et_hint_1);
            editCardQuesET.requestFocus();
        } else if (editCardAnsET.getText().toString().isEmpty()) {
            editCardAnsET.setHint(R.string.edit_card_ans_et_hint_1);
            editCardAnsET.requestFocus();
        } else if (editCardDescET.getText().toString().isEmpty()) {
            editCardDescET.setHint(R.string.edit_card_desc_et_hint_1);
            editCardDescET.requestFocus();
        } else {

            // Remove the card from pq before updating it
            pq.poll();

            // Update the contents of the current card with the inputs
            currentCard.setCategory(editCardCategoryET.getText().toString());
            currentCard.setQues(editCardQuesET.getText().toString());
            currentCard.setAns(editCardAnsET.getText().toString());
            currentCard.setDesc(editCardDescET.getText().toString());

            // Get the source intent and restore the priority of the card
            // being edited back to original priority
            Intent srcIntent = getIntent();
            currentCard.setPriority(srcIntent.getIntExtra(MESSAGE, currentCard.getPriority()));

            // Put the updated current card back into pq
            pq.add(currentCard);

            storePQ();

            // Start the main activity
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }
    }

    // This is a helper function which will store pq into file system
    private void storePQ() {
        try {

            // Create the file in private mode and open Output Stream to write
            FileOutputStream fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            //Write the pq into the FILE_NAME file
            oos.writeObject(pq);

            // Close the Output Streams
            oos.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
