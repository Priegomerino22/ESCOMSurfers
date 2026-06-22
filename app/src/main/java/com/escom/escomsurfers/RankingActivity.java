package com.escom.escomsurfers;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RankingActivity extends Activity {

    private LinearLayout listContainer;
    private TextView statusText;
    private String serverBaseUrl;
    private String selectedDifficulty = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        serverBaseUrl = getString(R.string.server_base_url).trim();

        createLayout();
        loadRanking();
    }

    private void createLayout() {
        ScrollView scrollView = new ScrollView(this);
        scrollView.setBackgroundColor(Color.rgb(15, 20, 30));

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(22), dp(42), dp(22), dp(25));
        root.setBackgroundColor(Color.rgb(15, 20, 30));

        scrollView.addView(root);

        TextView title = new TextView(this);
        title.setText("RANKING ESCOM SURFERS");
        title.setTextColor(Color.WHITE);
        title.setTextSize(27);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, dp(18));
        root.addView(title);

        Button allButton = createButton("TODAS", Color.rgb(0, 90, 180));
        Button easyButton = createButton("FÁCIL", Color.rgb(0, 130, 80));
        Button mediumButton = createButton("MEDIO", Color.rgb(0, 90, 180));
        Button hardButton = createButton("DIFÍCIL", Color.rgb(160, 40, 40));
        Button extraButton = createButton("EXTRAORDINARIO", Color.rgb(120, 0, 160));
        Button backButton = createButton("Volver al menú", Color.rgb(80, 80, 80));

        root.addView(allButton);
        root.addView(easyButton);
        root.addView(mediumButton);
        root.addView(hardButton);
        root.addView(extraButton);

        statusText = new TextView(this);
        statusText.setText("Cargando ranking...");
        statusText.setTextColor(Color.rgb(220, 220, 220));
        statusText.setTextSize(16);
        statusText.setGravity(Gravity.CENTER);
        statusText.setPadding(0, dp(18), 0, dp(14));
        root.addView(statusText);

        listContainer = new LinearLayout(this);
        listContainer.setOrientation(LinearLayout.VERTICAL);
        root.addView(listContainer);

        root.addView(backButton);

        allButton.setOnClickListener(v -> {
            selectedDifficulty = "";
            loadRanking();
        });

        easyButton.setOnClickListener(v -> {
            selectedDifficulty = "FACIL";
            loadRanking();
        });

        mediumButton.setOnClickListener(v -> {
            selectedDifficulty = "MEDIO";
            loadRanking();
        });

        hardButton.setOnClickListener(v -> {
            selectedDifficulty = "DIFICIL";
            loadRanking();
        });

        extraButton.setOnClickListener(v -> {
            selectedDifficulty = "EXTRAORDINARIO";
            loadRanking();
        });

        backButton.setOnClickListener(v -> finish());

        setContentView(scrollView);
    }

    private Button createButton(String text, int color) {
        Button button = new Button(this);
        button.setText(text);
        button.setTextColor(Color.WHITE);
        button.setTextSize(15);
        button.setTypeface(Typeface.DEFAULT_BOLD);
        button.setBackgroundColor(color);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(48)
        );

        params.setMargins(0, dp(5), 0, dp(5));
        button.setLayoutParams(params);

        return button;
    }

    private void loadRanking() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Toast.makeText(this, "Debes iniciar sesión", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (serverBaseUrl.isEmpty()) {
            statusText.setText("Configura server_base_url en strings.xml");
            return;
        }

        statusText.setText("Cargando ranking...");
        listContainer.removeAllViews();

        user.getIdToken(true)
                .addOnSuccessListener(tokenResult -> {
                    String idToken = tokenResult.getToken();

                    if (idToken == null || idToken.isEmpty()) {
                        runOnUiThread(() -> statusText.setText("No se pudo obtener token"));
                        return;
                    }

                    new Thread(() -> requestRanking(idToken)).start();
                })
                .addOnFailureListener(e -> runOnUiThread(() ->
                        statusText.setText("No se pudo obtener token")));
    }

    private void requestRanking(String idToken) {
        HttpURLConnection connection = null;

        try {
            String endpoint = serverBaseUrl + "/api/ranking?limit=10";

            if (selectedDifficulty != null && !selectedDifficulty.isEmpty()) {
                endpoint += "&difficulty=" + selectedDifficulty;
            }

            URL url = new URL(endpoint);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestProperty("Authorization", "Bearer " + idToken);

            int responseCode = connection.getResponseCode();
            String response = readResponse(connection, responseCode);

            if (responseCode >= 200 && responseCode < 300) {
                JSONObject json = new JSONObject(response);
                JSONArray ranking = json.getJSONArray("ranking");

                runOnUiThread(() -> showRanking(ranking));
            } else {
                runOnUiThread(() ->
                        statusText.setText("Error consultando ranking: " + responseCode));
            }

        } catch (Exception e) {
            runOnUiThread(() ->
                    statusText.setText("No se pudo conectar con el servidor"));
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private String readResponse(HttpURLConnection connection, int responseCode) throws Exception {
        InputStream inputStream;

        if (responseCode >= 200 && responseCode < 300) {
            inputStream = connection.getInputStream();
        } else {
            inputStream = connection.getErrorStream();
        }

        if (inputStream == null) {
            return "";
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder builder = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }

        reader.close();
        return builder.toString();
    }

    private void showRanking(JSONArray ranking) {
        listContainer.removeAllViews();

        if (ranking.length() == 0) {
            statusText.setText("Todavía no hay puntajes registrados.");
            return;
        }

        if (selectedDifficulty == null || selectedDifficulty.isEmpty()) {
            statusText.setText("Top 10 global");
        } else {
            statusText.setText("Top 10: " + selectedDifficulty);
        }

        try {
            for (int i = 0; i < ranking.length(); i++) {
                JSONObject item = ranking.getJSONObject(i);

                int position = item.optInt("position", i + 1);
                String playerName = item.optString("playerName", "Jugador ESCOM");
                int score = item.optInt("score", 0);
                int credits = item.optInt("credits", 0);
                int level = item.optInt("level", 1);
                String difficulty = item.optString("difficulty", "MEDIO");

                TextView row = new TextView(this);
                row.setText(position + ". " + playerName + "\n" +
                        score + " pts  |  Créditos: " + credits + "  |  Nivel: " + level + "\n" +
                        "Modo: " + difficulty);
                row.setTextColor(Color.WHITE);
                row.setTextSize(16);
                row.setPadding(dp(14), dp(12), dp(14), dp(12));
                row.setBackgroundColor(Color.rgb(35, 42, 55));

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

                params.setMargins(0, dp(6), 0, dp(6));
                row.setLayoutParams(params);

                listContainer.addView(row);
            }
        } catch (Exception e) {
            statusText.setText("Error mostrando ranking");
        }
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
}