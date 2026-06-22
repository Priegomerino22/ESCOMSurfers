package com.escom.escomsurfers;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {

    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        String difficulty = getIntent().getStringExtra("difficulty");

        if (difficulty == null || difficulty.isEmpty()) {
            difficulty = "MEDIO";
        }

        String gameMode = getIntent().getStringExtra("gameMode");
        boolean storyMode = "STORY".equals(gameMode);
        int storyLevel = getIntent().getIntExtra("storyLevel", 1);

        gameView = new GameView(this, difficulty, storyMode, storyLevel);
        setContentView(gameView);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (gameView != null) {
            gameView.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (gameView != null) {
            gameView.resume();
        }
    }
}
