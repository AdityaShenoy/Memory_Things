package com.brainy_things.memory_things;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Locale;
import java.util.PriorityQueue;

public class MainActivity extends AppCompatActivity {

    // This is the file where pq is stored
    private String FILE_NAME;

    // This is the priority queue of cards
    private PriorityQueue<Card> pq;

    // This is the current card that is displayed
    private Card currentCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing values from resources
        FILE_NAME = getString(R.string.file_name);

        initPQ();
        showNextCard();
    }

    // This function will create new pq when the application is run for the first time
    // or load pq from existing file
    private void initPQ() {

        // If the file is not present in the current file directory
        if (!Arrays.asList(fileList()).contains(FILE_NAME)) {

            // Initialize an empty priority queue
            pq = new PriorityQueue<>();

            // Store the pq into the file system
            storePQ();
        } else {
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
        }
    }

    // This function will find and display next card from pq
    private void showNextCard() {
        TextView mainCategoryTV = findViewById(R.id.main_category_tv);
        TextView mainPriorityTV = findViewById(R.id.main_priority_tv);
        TextView mainQuesTV = findViewById(R.id.main_ques_tv);
        TextView mainAnsTV = findViewById(R.id.main_ans_tv);
        TextView mainDescTV = findViewById(R.id.main_desc_tv);
        EditText mainAnsET = findViewById(R.id.main_ans_et);
        final Button mainAnsBtn = findViewById(R.id.main_ans_btn);
        Button mainDelCardBtn = findViewById(R.id.main_del_card_btn);
        Button mainEditCardBtn = findViewById(R.id.main_edit_card_btn);
        Button mainListCardsBtn = findViewById(R.id.main_list_cards_btn);

        // If there are no cards in pq, show the 0th card
        if (pq.isEmpty()) {
            mainCategoryTV.setText(R.string.category0);
            mainPriorityTV.setText(R.string.priority0);
            mainQuesTV.setText(R.string.ques0);
            mainAnsTV.setText(R.string.ans0);
            mainDescTV.setText(R.string.desc0);

            // As there are no card in pq, there is no use of delete,
            // edit and list card button so we disable them
            mainDelCardBtn.setEnabled(false);
            mainEditCardBtn.setEnabled(false);
            mainListCardsBtn.setEnabled(false);
        } else {

            // Peek the card from the pq without removing it
            currentCard = pq.peek();

            // Show the card priority, question, answer and description in layout
            // noinspection ConstantConditions
            mainCategoryTV.setText(
                    String.format(Locale.US, "Category: %s", currentCard.getCategory()));
            mainPriorityTV.setText(
                    String.format(Locale.US, "Priority: %s", currentCard.getPriority()));
            mainQuesTV.setText(currentCard.getQues());
            mainAnsTV.setText(currentCard.getAns());
            mainDescTV.setText(currentCard.getDesc());

            // Enable deleting, editing and list card buttons
            mainDelCardBtn.setEnabled(true);
            mainEditCardBtn.setEnabled(true);
            mainListCardsBtn.setEnabled(true);
        }

        // Hide the answers and descriptions
        mainAnsTV.setVisibility(View.INVISIBLE);
        mainDescTV.setVisibility(View.INVISIBLE);

        // Make the input field empty
        mainAnsET.setText("");

        // Set the text and call back for the check answer button
        mainAnsBtn.setText(R.string.main_ans_btn_txt);
        mainAnsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAns(mainAnsBtn);
            }
        });
    }

    // This function will check the answer of card and the input
    public void checkAns(View v) {

        // If the 0th card is not being displayed
        if (!pq.isEmpty()) {

            // This is the Edit Text where the user has written their answer
            EditText mainAnsET = findViewById(R.id.main_ans_et);

            // Remove the card from pq before updating it
            pq.poll();

            // Check if the answer in the Edit Text and Card matches
            if (currentCard.getAns().equals(mainAnsET.getText().toString())) {

                // Decrease the priority number by 1
                // Lower priority number has lower priority
                currentCard.setPriority(currentCard.getPriority() - 1);
            } else {

                // Increase the priority number by 1
                // Higher priority number has higher priority
                currentCard.setPriority(currentCard.getPriority() + 1);
            }

            // Put the updated current card back into pq
            pq.add(currentCard);

            // Store pq into file system
            storePQ();
        }
        TextView mainAnsTV = findViewById(R.id.main_ans_tv);
        TextView mainDescTV = findViewById(R.id.main_desc_tv);
        final Button mainAnsBtn = findViewById(R.id.main_ans_btn);

        // Reveal the answers and description
        mainAnsTV.setVisibility(View.VISIBLE);
        mainDescTV.setVisibility(View.VISIBLE);

        // Change the text and call back function of the button
        mainAnsBtn.setText(R.string.main_ans_btn_txt_1);
        mainAnsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Shows the next card
                showNextCard();
            }
        });
    }

    // This function will start the AddCard activity
    public void addCard(View v) {
        Intent i = new Intent(this, AddCardActivity.class);
        startActivity(i);
    }

    // This function will delete the shown card
    public void delCard(View v) {

        // This will create a alert dialog which will
        // confirm the deletion of the current card
        new AlertDialog.Builder(this)
                .setTitle(R.string.del_card_alert_dialog_del)
                .setMessage(R.string.del_card_alert_dialog_msg)
                .setPositiveButton(R.string.del_card_alert_dialog_del,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                // Remove the current card from the pq
                                pq.poll();

                                // Store the updated pq into the file system
                                storePQ();

                                // Proceed to show the next card
                                showNextCard();
                            }
                        })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // This function will start the EditCard Activity
    public void editCard(View v) {
        Intent i = new Intent(this, EditCardActivity.class);
        startActivity(i);
    }

    // This function will start the ListCards Activity
    public void listCards(View v) {
        Intent i = new Intent(this, ListCardsActivity.class);
        startActivity(i);
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