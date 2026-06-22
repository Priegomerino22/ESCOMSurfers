package com.escom.escomsurfers;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends Activity {

    private static final int RC_SIGN_IN = 1001;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private GoogleSignInClient googleSignInClient;

    private EditText usernameInput;
    private EditText emailInput;
    private EditText passwordInput;

    private Button googleButton;
    private Button loginButton;
    private Button registerButton;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        preferences = getSharedPreferences("ESCOM_SURFERS_USER", MODE_PRIVATE);

        configureGoogleSignIn();

        if (auth.getCurrentUser() != null) {
            openMenu();
            return;
        }

        createLayout();
    }

    private void configureGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void createLayout() {
        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        scrollView.setBackgroundResource(R.drawable.bg_screen_gradient);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(dp(28), dp(26), dp(28), dp(28));
        root.setBackgroundResource(R.drawable.bg_screen_gradient);

        scrollView.addView(root, new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        ImageView logo = new ImageView(this);
        logo.setImageResource(R.drawable.escom_ui_logo);
        logo.setAdjustViewBounds(true);
        logo.setScaleType(ImageView.ScaleType.FIT_CENTER);
        LinearLayout.LayoutParams logoParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(120)
        );
        logoParams.setMargins(0, 0, 0, dp(8));
        root.addView(logo, logoParams);

        TextView subtitle = new TextView(this);
        subtitle.setText("Inicia sesión para guardar tu ranking en la nube");
        subtitle.setTextColor(Color.rgb(220, 230, 245));
        subtitle.setTextSize(15);
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(0, 0, 0, dp(18));
        root.addView(subtitle);

        googleButton = createButton("INICIAR CON GOOGLE", R.drawable.bg_button_google, Color.rgb(25, 28, 35));
        root.addView(googleButton);

        TextView divider = new TextView(this);
        divider.setText("o usa correo y contraseña");
        divider.setTextColor(Color.rgb(185, 198, 220));
        divider.setTextSize(13);
        divider.setGravity(Gravity.CENTER);
        divider.setPadding(0, dp(16), 0, dp(8));
        root.addView(divider);

        usernameInput = createInput("Nombre de usuario para registro");
        emailInput = createInput("Correo electrónico");
        passwordInput = createInput("Contraseña");

        emailInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        root.addView(usernameInput);
        root.addView(emailInput);
        root.addView(passwordInput);

        loginButton = createButton("INICIAR SESIÓN", R.drawable.bg_button_blue, Color.WHITE);
        registerButton = createButton("REGISTRARME", R.drawable.bg_button_red, Color.WHITE);

        root.addView(loginButton);
        root.addView(registerButton);

        TextView note = new TextView(this);
        note.setText("Con Google no necesitas contraseña.\nCon correo, la contraseña debe tener mínimo 6 caracteres.");
        note.setTextColor(Color.rgb(180, 194, 215));
        note.setTextSize(12);
        note.setGravity(Gravity.CENTER);
        note.setPadding(0, dp(18), 0, 0);
        root.addView(note);

        googleButton.setOnClickListener(v -> signInWithGoogle());
        loginButton.setOnClickListener(v -> loginUser());
        registerButton.setOnClickListener(v -> registerUser());

        setContentView(scrollView);
    }

    private EditText createInput(String hint) {
        EditText input = new EditText(this);
        input.setHint(hint);
        input.setHintTextColor(Color.rgb(165, 178, 200));
        input.setTextColor(Color.WHITE);
        input.setTextSize(15);
        input.setSingleLine(true);
        input.setPadding(dp(15), dp(13), dp(15), dp(13));
        input.setBackgroundResource(R.drawable.bg_input);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            input.setBackgroundTintList(null);
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        params.setMargins(0, dp(7), 0, dp(7));
        input.setLayoutParams(params);

        return input;
    }

    private Button createButton(String text, int backgroundRes, int textColor) {
        Button button = new Button(this);
        button.setText(text);
        button.setTextColor(textColor);
        button.setTextSize(15);
        button.setTypeface(Typeface.DEFAULT_BOLD);
        button.setAllCaps(false);
        button.setBackgroundResource(backgroundRes);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            button.setBackgroundTintList(null);
        }
        button.setPadding(dp(10), 0, dp(10), 0);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(54)
        );

        params.setMargins(0, dp(8), 0, dp(4));
        button.setLayoutParams(params);

        return button;
    }

    private void signInWithGoogle() {
        setButtonsEnabled(false);
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                if (account != null) {
                    firebaseAuthWithGoogle(account);
                } else {
                    setButtonsEnabled(true);
                    Toast.makeText(this, "No se pudo obtener la cuenta de Google", Toast.LENGTH_SHORT).show();
                }

            } catch (ApiException e) {
                setButtonsEnabled(true);
                Toast.makeText(this, "Error con Google: " + e.getStatusCode(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        auth.signInWithCredential(credential)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = auth.getCurrentUser();

                    if (user != null) {
                        String username = user.getDisplayName();

                        if (username == null || username.trim().isEmpty()) {
                            if (user.getEmail() != null) {
                                username = user.getEmail().split("@")[0];
                            } else {
                                username = "Jugador ESCOM";
                            }
                        }

                        String email = user.getEmail();

                        if (email == null) {
                            email = "";
                        }

                        saveUserProfile(user.getUid(), username, email);
                    } else {
                        setButtonsEnabled(true);
                        Toast.makeText(this, "No se pudo iniciar con Google", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    setButtonsEnabled(true);
                    Toast.makeText(this, "Error Firebase Google: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void loginUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Escribe correo y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        setButtonsEnabled(false);

        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = auth.getCurrentUser();

                    if (user != null) {
                        loadUserProfile(user.getUid(), email);
                    } else {
                        setButtonsEnabled(true);
                        Toast.makeText(this, "No se pudo iniciar sesión", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    setButtonsEnabled(true);
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void registerUser() {
        String username = usernameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (username.isEmpty()) {
            Toast.makeText(this, "Escribe un nombre de usuario", Toast.LENGTH_SHORT).show();
            return;
        }

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Escribe correo y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "La contraseña debe tener mínimo 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        setButtonsEnabled(false);

        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = auth.getCurrentUser();

                    if (user != null) {
                        saveUserProfile(user.getUid(), username, email);
                    } else {
                        setButtonsEnabled(true);
                        Toast.makeText(this, "No se pudo registrar", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    setButtonsEnabled(true);
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void saveUserProfile(String uid, String username, String email) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("uid", uid);
        userData.put("username", username);
        userData.put("email", email);

        db.collection("users")
                .document(uid)
                .set(userData)
                .addOnSuccessListener(unused -> {
                    saveLocalUser(username, email);
                    Toast.makeText(this, "Bienvenido " + username, Toast.LENGTH_SHORT).show();
                    openMenu();
                })
                .addOnFailureListener(e -> {
                    setButtonsEnabled(true);
                    Toast.makeText(this, "Error al guardar usuario: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void loadUserProfile(String uid, String email) {
        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String username = email.split("@")[0];

                    if (documentSnapshot.exists() && documentSnapshot.getString("username") != null) {
                        username = documentSnapshot.getString("username");
                    }

                    saveLocalUser(username, email);
                    Toast.makeText(this, "Bienvenido " + username, Toast.LENGTH_SHORT).show();
                    openMenu();
                })
                .addOnFailureListener(e -> {
                    String username = email.split("@")[0];
                    saveLocalUser(username, email);
                    openMenu();
                });
    }

    private void saveLocalUser(String username, String email) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("username", username);
        editor.putString("email", email);
        editor.apply();
    }

    private void openMenu() {
        Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
        startActivity(intent);
        finish();
    }

    private void setButtonsEnabled(boolean enabled) {
        if (googleButton != null) {
            googleButton.setEnabled(enabled);
        }
        if (loginButton != null) {
            loginButton.setEnabled(enabled);
        }
        if (registerButton != null) {
            registerButton.setEnabled(enabled);
        }
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
}
