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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SplashActivity extends Activity {

    private static final long SPLASH_DELAY = 1300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(dp(28), dp(28), dp(28), dp(28));
        root.setBackgroundResource(R.drawable.bg_screen_gradient);

        ImageView logo = new ImageView(this);
        logo.setImageResource(R.drawable.escom_ui_logo);
        logo.setAdjustViewBounds(true);
        logo.setScaleType(ImageView.ScaleType.FIT_CENTER);

        LinearLayout.LayoutParams logoParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(150)
        );
        logoParams.setMargins(0, 0, 0, dp(18));
        root.addView(logo, logoParams);

        TextView message = new TextView(this);
        message.setText("Preparando partida...");
        message.setTextColor(Color.rgb(220, 230, 245));
        message.setTextSize(17);
        message.setTypeface(Typeface.DEFAULT_BOLD);
        message.setGravity(Gravity.CENTER);
        root.addView(message);

        TextView subtitle = new TextView(this);
        subtitle.setText("Login • Dificultades • Ranking en la nube");
        subtitle.setTextColor(Color.rgb(170, 185, 210));
        subtitle.setTextSize(13);
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(0, dp(8), 0, 0);
        root.addView(subtitle);

        setContentView(root);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_DELAY);
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
}