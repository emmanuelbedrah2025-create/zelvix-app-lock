package com.zelvix.applock;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.*;

import java.security.SecureRandom;
import java.security.spec.KeySpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import android.util.Base64;

public class MainActivity extends Activity {

    private final int BLACK = Color.rgb(12, 12, 12);
    private final int RED = Color.rgb(220, 25, 35);

    private android.content.SharedPreferences prefs;
    private EditText passwordInput;
    private EditText confirmInput;
    private TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences("zelvix_security", MODE_PRIVATE);

        if (prefs.contains("password_hash")) {
            showLogin();
        } else {
            showSetup();
        }
    }

    private String hashPassword(String password, byte[] salt)
            throws Exception {

        KeySpec spec = new PBEKeySpec(
                password.toCharArray(), salt, 210000, 256);

        SecretKeyFactory factory =
                SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

        byte[] hash = factory.generateSecret(spec).getEncoded();

        return Base64.encodeToString(hash, Base64.NO_WRAP);
    }

    private void showSetup() {

        LinearLayout layout = createLayout();

        addTitle(layout, "ZELVIX", "CREATE YOUR PASSWORD");

        passwordInput = createPasswordInput("Create password");
        confirmInput = createPasswordInput("Confirm password");

        Button button = createButton("CREATE PASSWORD");

        status = createStatus();

        button.setOnClickListener(v -> {
            String password = passwordInput.getText().toString();
            String confirm = confirmInput.getText().toString();

            if (password.length() < 8) {
                status.setText("Use at least 8 characters.");
                return;
            }

            if (!password.equals(confirm)) {
                status.setText("Passwords do not match.");
                return;
            }

            try {
                byte[] salt = new byte[16];
                new SecureRandom().nextBytes(salt);

                String hash = hashPassword(password, salt);
                String encodedSalt =
                        Base64.encodeToString(salt, Base64.NO_WRAP);

                prefs.edit()
                        .putString("password_hash", hash)
                        .putString("password_salt", encodedSalt)
                        .apply();

                showLogin();

            } catch (Exception e) {
                status.setText("Password setup failed.");
            }
        });

        layout.addView(passwordInput);
        layout.addView(confirmInput);
        layout.addView(button);
        layout.addView(status);

        setContentView(layout);
    }

    private void showLogin() {

        LinearLayout layout = createLayout();

        addTitle(layout, "ZELVIX", "ENTER YOUR PASSWORD");

        passwordInput = createPasswordInput("Enter password");

        Button button = createButton("UNLOCK");

        status = createStatus();

        button.setOnClickListener(v -> {
            String password = passwordInput.getText().toString();

            try {
                String savedSalt = prefs.getString("password_salt", "");
                String savedHash = prefs.getString("password_hash", "");

                byte[] salt =
                        Base64.decode(savedSalt, Base64.NO_WRAP);

                String enteredHash = hashPassword(password, salt);

                if (enteredHash.equals(savedHash)) {
                    showUnlocked();
                } else {
                    status.setText("Incorrect password. Try again.");
                    passwordInput.setText("");
                }

            } catch (Exception e) {
                status.setText("Unable to verify password.");
            }
        });

        layout.addView(passwordInput);
        layout.addView(button);
        layout.addView(status);

        setContentView(layout);
    }

    private void showUnlocked() {

        LinearLayout layout = createLayout();

        addTitle(layout, "ZELVIX", "APP LOCK");

        TextView message = new TextView(this);
        message.setText("You are successfully authenticated.");
        message.setTextColor(Color.WHITE);
        message.setTextSize(16);
        message.setGravity(Gravity.CENTER);

        Button lockButton = createButton("LOCK ZELVIX");

        lockButton.setOnClickListener(v -> showLogin());

        layout.addView(message);
        layout.addView(lockButton);

        setContentView(layout);
    }

    private LinearLayout createLayout() {

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(32, 32, 32, 32);
        layout.setBackgroundColor(BLACK);

        return layout;
    }

    private void addTitle(LinearLayout layout,
                          String title, String subtitle) {

        TextView logo = new TextView(this);
        logo.setText(title);
        logo.setTextColor(RED);
        logo.setTextSize(36);
        logo.setTypeface(null, Typeface.BOLD);
        logo.setGravity(Gravity.CENTER);

        TextView heading = new TextView(this);
        heading.setText(subtitle);
        heading.setTextColor(Color.WHITE);
        heading.setTextSize(17);
        heading.setGravity(Gravity.CENTER);

        layout.addView(logo);
        layout.addView(heading);
    }

    private EditText createPasswordInput(String hint) {

        EditText input = new EditText(this);
        input.setHint(hint);
        input.setHintTextColor(Color.GRAY);
        input.setTextColor(Color.WHITE);
        input.setSingleLine(true);
        input.setInputType(129);

        return input;
    }

    private Button createButton(String label) {

        Button button = new Button(this);
        button.setText(label);

        return button;
    }

    private TextView createStatus() {

        TextView text = new TextView(this);
        text.setTextColor(Color.LTGRAY);
        text.setGravity(Gravity.CENTER);

        return text;
    }
}
