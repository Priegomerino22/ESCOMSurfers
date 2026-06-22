package com.escom.escomsurfers;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class CutsceneActivity extends Activity {

    private StoryLevel level;
    private DialogueLine[] lines;
    private int currentIndex;

    private ImageView backgroundView;
    private ImageView characterView;
    private TextView titleText;
    private TextView speakerText;
    private TextView dialogueText;
    private Button nextButton;

    private Handler handler;
    private String fullText;
    private int textIndex;
    private boolean typing;
    private String phase;

    private final Runnable typeRunnable = new Runnable() {
        @Override
        public void run() {
            if (fullText == null) {
                typing = false;
                return;
            }

            if (textIndex < fullText.length()) {
                textIndex++;
                dialogueText.setText(fullText.substring(0, textIndex));
                handler.postDelayed(this, 22);
            } else {
                typing = false;
                nextButton.setText(isLastLine() ? getFinalButtonText() : "Siguiente");
            }
        }
    };

    private final Runnable characterAnimationRunnable = new Runnable() {
        private boolean toggle;

        @Override
        public void run() {
            if (lines == null || currentIndex < 0 || currentIndex >= lines.length) {
                return;
            }

            DialogueLine line = lines[currentIndex];
            String speaker = line.getSpeaker();

            if ("Credi".equals(speaker)) {
                characterView.setImageResource(toggle ? R.drawable.story_credi_1 : R.drawable.story_credi_2);
            } else if ("Nico".equals(speaker)) {
                characterView.setImageResource(toggle ? R.drawable.story_nico_idle_1 : R.drawable.story_nico_idle_2);
            } else if ("Profesor Null".equals(speaker)) {
                characterView.setImageResource(toggle ? R.drawable.story_prof_null_1 : R.drawable.story_prof_null_2);
            } else {
                characterView.setImageResource(line.getCharacterResId());
            }

            toggle = !toggle;
            handler.postDelayed(this, typing ? 240 : 700);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handler = new Handler(Looper.getMainLooper());

        int levelIndex = getIntent().getIntExtra("levelIndex", 1);
        phase = getIntent().getStringExtra("phase");

        if (phase == null || phase.trim().isEmpty()) {
            phase = "intro";
        }

        level = StoryManager.getLevel(levelIndex);
        lines = "outro".equals(phase) ? level.getOutroLines() : level.getIntroLines();
        currentIndex = 0;

        createLayout();
        showCurrentLine();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    private void createLayout() {
        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.rgb(7, 11, 20));

        backgroundView = new ImageView(this);
        backgroundView.setImageResource(level.getBackgroundResId());
        backgroundView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        root.addView(backgroundView, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        TextView overlay = new TextView(this);
        overlay.setBackgroundColor(Color.argb(55, 0, 0, 0));
        root.addView(overlay, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        titleText = new TextView(this);
        titleText.setText("Nivel " + level.getIndex() + ": " + level.getTitle());
        titleText.setTextColor(Color.WHITE);
        titleText.setTextSize(21);
        titleText.setTypeface(Typeface.DEFAULT_BOLD);
        titleText.setGravity(Gravity.CENTER);
        titleText.setPadding(dp(16), dp(22), dp(16), dp(8));
        titleText.setBackgroundColor(Color.argb(120, 5, 10, 25));
        FrameLayout.LayoutParams titleParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        titleParams.gravity = Gravity.TOP;
        root.addView(titleText, titleParams);

        characterView = new ImageView(this);
        characterView.setAdjustViewBounds(true);
        characterView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        FrameLayout.LayoutParams characterParams = new FrameLayout.LayoutParams(
                dp(270),
                dp(270)
        );
        characterParams.gravity = Gravity.BOTTOM | Gravity.LEFT;
        characterParams.setMargins(dp(8), 0, 0, dp(150));
        root.addView(characterView, characterParams);

        FrameLayout dialoguePanel = new FrameLayout(this);
        dialoguePanel.setBackgroundResource(R.drawable.bg_panel);
        dialoguePanel.setPadding(dp(18), dp(14), dp(18), dp(12));
        FrameLayout.LayoutParams panelParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(170)
        );
        panelParams.gravity = Gravity.BOTTOM;
        panelParams.setMargins(dp(14), 0, dp(14), dp(14));
        root.addView(dialoguePanel, panelParams);

        speakerText = new TextView(this);
        speakerText.setTextColor(Color.rgb(255, 220, 95));
        speakerText.setTextSize(16);
        speakerText.setTypeface(Typeface.DEFAULT_BOLD);
        FrameLayout.LayoutParams speakerParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        speakerParams.gravity = Gravity.TOP;
        dialoguePanel.addView(speakerText, speakerParams);

        dialogueText = new TextView(this);
        dialogueText.setTextColor(Color.WHITE);
        dialogueText.setTextSize(17);
        dialogueText.setLineSpacing(0, 1.08f);
        FrameLayout.LayoutParams dialogueParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        dialogueParams.gravity = Gravity.TOP;
        dialogueParams.setMargins(0, dp(32), 0, 0);
        dialoguePanel.addView(dialogueText, dialogueParams);

        nextButton = new Button(this);
        nextButton.setText("Siguiente");
        nextButton.setTextColor(Color.WHITE);
        nextButton.setTextSize(14);
        nextButton.setTypeface(Typeface.DEFAULT_BOLD);
        nextButton.setBackgroundResource(R.drawable.bg_button_blue);
        nextButton.setAllCaps(false);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            nextButton.setBackgroundTintList(null);
        }
        FrameLayout.LayoutParams buttonParams = new FrameLayout.LayoutParams(
                dp(145),
                dp(46)
        );
        buttonParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        dialoguePanel.addView(nextButton, buttonParams);

        nextButton.setOnClickListener(v -> next());

        setContentView(root);
    }

    private void showCurrentLine() {
        handler.removeCallbacks(typeRunnable);
        handler.removeCallbacks(characterAnimationRunnable);

        DialogueLine line = lines[currentIndex];
        speakerText.setText(line.getSpeaker());
        characterView.setImageResource(line.getCharacterResId());
        fullText = line.getText();
        textIndex = 0;
        typing = true;
        dialogueText.setText("");
        nextButton.setText("...");

        handler.post(typeRunnable);
        handler.post(characterAnimationRunnable);
    }

    private void next() {
        if (typing) {
            handler.removeCallbacks(typeRunnable);
            dialogueText.setText(fullText);
            typing = false;
            nextButton.setText(isLastLine() ? getFinalButtonText() : "Siguiente");
            return;
        }

        if (!isLastLine()) {
            currentIndex++;
            showCurrentLine();
        } else {
            finishCutscene();
        }
    }

    private boolean isLastLine() {
        return currentIndex >= lines.length - 1;
    }

    private String getFinalButtonText() {
        if ("outro".equals(phase)) {
            return level.getIndex() >= StoryManager.getLevelCount() ? "Finalizar" : "Continuar";
        }
        return "Jugar";
    }

    private void finishCutscene() {
        if ("intro".equals(phase)) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("difficulty", level.getDifficulty());
            intent.putExtra("gameMode", "STORY");
            intent.putExtra("storyLevel", level.getIndex());
            startActivity(intent);
            finish();
            return;
        }

        Intent intent = new Intent(this, StoryModeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
}
