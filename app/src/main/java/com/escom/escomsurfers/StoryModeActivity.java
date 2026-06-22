package com.escom.escomsurfers;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class StoryModeActivity extends Activity {

    private StoryProgressManager progressManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        progressManager = new StoryProgressManager(this);
        createLayout();
    }

    @Override
    protected void onResume() {
        super.onResume();
        createLayout();
    }

    private void createLayout() {
        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        scrollView.setBackgroundResource(R.drawable.bg_screen_gradient);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(dp(24), dp(24), dp(24), dp(26));
        root.setBackgroundResource(R.drawable.bg_screen_gradient);

        scrollView.addView(root, new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        ImageView logo = new ImageView(this);
        logo.setImageResource(R.drawable.escom_ui_logo);
        logo.setAdjustViewBounds(true);
        logo.setScaleType(ImageView.ScaleType.FIT_CENTER);
        root.addView(logo, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(105)
        ));

        TextView title = new TextView(this);
        title.setText("MODO HISTORIA");
        title.setTextColor(Color.WHITE);
        title.setTextSize(28);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, dp(8), 0, dp(4));
        root.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("El Extraordinario del Destino");
        subtitle.setTextColor(Color.rgb(255, 220, 95));
        subtitle.setTextSize(17);
        subtitle.setTypeface(Typeface.DEFAULT_BOLD);
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(0, 0, 0, dp(14));
        root.addView(subtitle);

        TextView description = new TextView(this);
        description.setText("Ayuda a Nico a llegar desde su casa hasta el Aula 404 antes de que el Profesor Null convierta el examen en jefe final.");
        description.setTextColor(Color.rgb(200, 215, 235));
        description.setTextSize(13);
        description.setGravity(Gravity.CENTER);
        description.setPadding(dp(14), dp(12), dp(14), dp(12));
        description.setBackgroundResource(R.drawable.bg_panel);
        root.addView(description, fullWidthParams(dp(8), dp(12)));

        int unlockedLevel = progressManager.getUnlockedLevel();
        StoryLevel[] levels = StoryManager.getLevels();

        TextView progress = new TextView(this);
        progress.setText("Progreso: Nivel " + unlockedLevel + " de " + StoryManager.getLevelCount());
        progress.setTextColor(Color.WHITE);
        progress.setTextSize(15);
        progress.setTypeface(Typeface.DEFAULT_BOLD);
        progress.setGravity(Gravity.CENTER);
        progress.setPadding(0, dp(4), 0, dp(10));
        root.addView(progress);

        for (StoryLevel level : levels) {
            boolean unlocked = progressManager.isLevelUnlocked(level.getIndex());
            Button button = createLevelButton(level, unlocked);
            root.addView(button);
        }

        Button backButton = createSimpleButton("Volver al menú", R.drawable.bg_button_gray);
        backButton.setOnClickListener(v -> finish());
        root.addView(backButton);

        setContentView(scrollView);
    }

    private Button createLevelButton(StoryLevel level, boolean unlocked) {
        String text;

        if (unlocked) {
            text = "Nivel " + level.getIndex() + ": " + level.getTitle() + "\n" +
                    level.getDurationText() + "  |  " + level.getDifficulty();
        } else {
            text = "🔒 Nivel " + level.getIndex() + ": " + level.getTitle() + "\nBloqueado";
        }

        Button button = createSimpleButton(text, unlocked ? R.drawable.bg_button_blue : R.drawable.bg_button_gray);
        button.setEnabled(unlocked);
        button.setAllCaps(false);

        if (unlocked) {
            button.setOnClickListener(v -> openIntro(level.getIndex()));
        } else {
            button.setOnClickListener(v -> Toast.makeText(this, "Completa el nivel anterior", Toast.LENGTH_SHORT).show());
        }

        return button;
    }

    private Button createSimpleButton(String text, int backgroundRes) {
        Button button = new Button(this);
        button.setText(text);
        button.setTextColor(Color.WHITE);
        button.setTextSize(14);
        button.setTypeface(Typeface.DEFAULT_BOLD);
        button.setBackgroundResource(backgroundRes);
        button.setAllCaps(false);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            button.setBackgroundTintList(null);
        }

        button.setPadding(dp(8), 0, dp(8), 0);
        button.setLayoutParams(fullWidthParams(dp(6), dp(6)));
        return button;
    }

    private LinearLayout.LayoutParams fullWidthParams(int top, int bottom) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(62)
        );
        params.setMargins(0, top, 0, bottom);
        return params;
    }

    private void openIntro(int levelIndex) {
        Intent intent = new Intent(this, CutsceneActivity.class);
        intent.putExtra("levelIndex", levelIndex);
        intent.putExtra("phase", "intro");
        startActivity(intent);
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
}
