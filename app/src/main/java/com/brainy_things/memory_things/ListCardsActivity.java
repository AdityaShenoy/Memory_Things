package com.brainy_things.memory_things;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.PriorityQueue;

import static android.graphics.Typeface.BOLD;

public class ListCardsActivity extends AppCompatActivity {

    // This is the file where pq is stored
    private String FILE_NAME;

    // This is the priority queue used to store the cards
    private PriorityQueue<Card> pq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_cards);

        // Initialize file name
        FILE_NAME = getString(R.string.file_name);

        listCards();
    }

    // This function will list the cards present in the pq
    private void listCards() {
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

            // This is the layout where the list of cards are added
            LinearLayout verticalLayout = findViewById(R.id.list_cards_layout);

            // Remove all previous views before adding new views
            verticalLayout.removeAllViews();

            // Extract margin from dimens resource
            int margin = (int) getResources().getDimension(R.dimen.margin);

            // Border of each text view
            ShapeDrawable sd = new ShapeDrawable();
            sd.setShape(new RectShape());
            sd.getPaint().setColor(Color.BLACK);
            sd.getPaint().setStrokeWidth(1);
            sd.getPaint().setStyle(Paint.Style.STROKE);

            // Iterate through all the cards in pq
            for (final Card c : pq) {
                TextView tv = new TextView(this);

                // This is the text that will be shown in the list
                final SpannableStringBuilder textToDisplay = new SpannableStringBuilder(
                        String.format("%s\n%s", c.getCategory(), c.getQues())
                );
                final StyleSpan boldSpan = new StyleSpan(BOLD);

                // Bold face the category
                textToDisplay.setSpan(boldSpan,
                        0,
                        c.getCategory().length(),
                        Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                tv.setText(textToDisplay);

                tv.setTextSize(20);
                tv.setTextColor(Color.BLACK);
                tv.setPadding(margin, margin, margin, margin);
                tv.setBackground(sd); // Border

                // Show options to edit and delete the card
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(v.getContext())
                                .setTitle(R.string.list_cards_alert_dialog_title)
                                .setMessage(R.string.list_cards_alert_dialog_message)
                                .setPositiveButton(R.string.list_cards_alert_dialog_edit, editCallBack(v, c))
                                .setNegativeButton(R.string.list_cards_alert_dialog_delete,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                pq.remove(c);
                                                storePQ();
                                                listCards();
                                            }
                                        })
                                .show();
                    }
                });
                verticalLayout.addView(tv);
            }
        } catch (Exception ignore) {
        }
    }

    private DialogInterface.OnClickListener editCallBack(final View v, final Card c) {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent i = new Intent(v.getContext(), EditCardActivity.class);

                // Store the original priority so that it can be restored
                i.putExtra(EditCardActivity.MESSAGE, c.getPriority());

                // Make the priority of this card highest so that
                // Edit Card Activity can find it on top of pq
                c.setHighestPriority();

                storePQ();
                startActivity(i);
            }
        };
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
