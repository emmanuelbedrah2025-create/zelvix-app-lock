package com.zelvix.applock;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;

public class MainActivity extends Activity {

    private final int BLACK = Color.rgb(12, 12, 12);
    private final int RED = Color.rgb(220, 25, 35);
    private EditText passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(32, 32, 32, 32);
        layout.setBackgroundColor(BLACK);

        TextView logo = new TextView(this);
        logo.setText("ZELVIX");
        logo.setTextColor(RED);
        logo.setTextSize(36);
        logo.setTypeface(null, Typeface.BOLD);
        logo.setGravity(Gravity.CENTER);

        TextView subtitle = new TextView(this);
        subtitle.setText("APP LOCK");
        subtitle.setTextColor(Color.WHITE);
        subtitle.setTextSize(17);
        subtitle.setGravity(Gravity.CENTER);

        passwordInput = new EditText(this);
        passwordInput.setHint("Create your password");
        passwordInput.setHintTextColor(Color.GRAY);
        passwordInput.setTextColor(Color.WHITE);
        passwordInput.setSingleLine(true);

        Button setupButton = new Button(this);
        setupButton.setText("SET PASSWORD");

        TextView status = new TextView(this);
        status.setTextColor(Color.LTGRAY);
        status.setTextSize(14);
        status.setGravity(Gravity.CENTER);

        setupButton.setOnClickListener(v -> {
            String password = passwordInput.getText().toString();

            if (password.length() < 6) {
                status.setText("Use at least 6 characters.");
            } else {
                status.setText("Password setup screen is working.");
            }
        });

        layout.addView(logo);
        layout.addView(subtitle);
        layout.addView(passwordInput);
        layout.addView(setupButton);
        layout.addView(status);

        setContentView(layout);
    }
}
