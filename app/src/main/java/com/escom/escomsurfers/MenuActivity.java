package com.escom.escomsurfers;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

public class MenuActivity extends Activity {

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        preferences = getSharedPreferences("ESCOM_SURFERS_USER", MODE_PRIVATE);

        createLayout();
    }

    private void createLayout() {
        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        scrollView.setBackgroundResource(R.drawable.bg_screen_gradient);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(dp(26), dp(24), dp(26), dp(26));
        root.setBackgroundResource(R.drawable.bg_screen_gradient);

        scrollView.addView(root, new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        String username = preferences.getString("username", "Jugador ESCOM");

        ImageView logo = new ImageView(this);
        logo.setImageResource(R.drawable.escom_ui_logo);
        logo.setAdjustViewBounds(true);
        logo.setScaleType(ImageView.ScaleType.FIT_CENTER);

        LinearLayout.LayoutParams logoParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(115)
        );
        logoParams.setMargins(0, 0, 0, dp(8));
        root.addView(logo, logoParams);

        TextView welcome = new TextView(this);
        welcome.setText("Bienvenido, " + username);
        welcome.setTextColor(Color.rgb(220, 230, 245));
        welcome.setTextSize(16);
        welcome.setGravity(Gravity.CENTER);
        welcome.setPadding(0, 0, 0, dp(16));
        root.addView(welcome);

        TextView select = new TextView(this);
        select.setText("Selecciona dificultad");
        select.setTextColor(Color.WHITE);
        select.setTextSize(22);
        select.setTypeface(Typeface.DEFAULT_BOLD);
        select.setGravity(Gravity.CENTER);
        select.setPadding(0, 0, 0, dp(14));
        root.addView(select);

        Button storyButton = createButton("MODO HISTORIA", R.drawable.bg_button_purple);
        Button easyButton = createButton("FÁCIL", R.drawable.bg_button_green);
        Button mediumButton = createButton("MEDIO", R.drawable.bg_button_blue);
        Button hardButton = createButton("DIFÍCIL", R.drawable.bg_button_red);
        Button extraordinaryButton = createButton("EXTRAORDINARIO", R.drawable.bg_button_purple);
        Button rankingButton = createButton("RANKING GLOBAL", R.drawable.bg_button_gold);
        Button logoutButton = createButton("CERRAR SESIÓN", R.drawable.bg_button_gray);

        root.addView(storyButton);
        root.addView(easyButton);
        root.addView(mediumButton);
        root.addView(hardButton);
        root.addView(extraordinaryButton);
        root.addView(rankingButton);

        TextView help = new TextView(this);
        help.setText(
                "Fácil: velocidad baja y menos obstáculos\n" +
                        "Medio: dificultad normal\n" +
                        "Difícil: empieza rápido y aparecen más obstáculos\n" +
                        "Extraordinario: máxima velocidad y menos ayudas"
        );
        help.setTextColor(Color.rgb(190, 205, 225));
        help.setTextSize(13);
        help.setGravity(Gravity.CENTER);
        help.setPadding(dp(14), dp(14), dp(14), dp(14));
        help.setBackgroundResource(R.drawable.bg_panel);

        LinearLayout.LayoutParams helpParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        helpParams.setMargins(0, dp(14), 0, dp(14));
        root.addView(help, helpParams);

        root.addView(logoutButton);

        storyButton.setOnClickListener(v -> openStoryMode());
        easyButton.setOnClickListener(v -> startGame("FACIL"));
        mediumButton.setOnClickListener(v -> startGame("MEDIO"));
        hardButton.setOnClickListener(v -> startGame("DIFICIL"));
        extraordinaryButton.setOnClickListener(v -> startGame("EXTRAORDINARIO"));
        rankingButton.setOnClickListener(v -> openRanking());

        logoutButton.setOnClickListener(v -> logout());

        setContentView(scrollView);
    }

    private Button createButton(String text, int backgroundRes) {
        Button button = new Button(this);
        button.setText(text);
        button.setTextColor(Color.WHITE);
        button.setTextSize(16);
        button.setTypeface(Typeface.DEFAULT_BOLD);
        button.setAllCaps(false);
        button.setBackgroundResource(backgroundRes);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            button.setBackgroundTintList(null);
        }

        button.setPadding(dp(10), 0, dp(10), 0);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(56)
        );

        params.setMargins(0, dp(7), 0, dp(7));
        button.setLayoutParams(params);

        return button;
    }

    private void startGame(String difficulty) {
        Intent intent = new Intent(MenuActivity.this, MainActivity.class);
        intent.putExtra("difficulty", difficulty);
        startActivity(intent);
    }

    private void openStoryMode() {
        Intent intent = new Intent(MenuActivity.this, StoryModeActivity.class);
        startActivity(intent);
    }

    private void openRanking() {
        Intent intent = new Intent(MenuActivity.this, RankingActivity.class);
        startActivity(intent);
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignIn.getClient(this, gso).signOut();

        preferences.edit().clear().apply();

        Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
}