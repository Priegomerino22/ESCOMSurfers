package com.escom.escomsurfers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class FirebaseManager {

    private static final String TAG = "FirebaseManager";

    private final FirebaseAuth auth;
    private final SharedPreferences preferences;
    private final String serverBaseUrl;

    public FirebaseManager(Context context) {
        auth = FirebaseAuth.getInstance();
        preferences = context.getSharedPreferences("ESCOM_SURFERS_USER", Context.MODE_PRIVATE);
        serverBaseUrl = context.getString(R.string.server_base_url).trim();
    }

    public void saveScore(int score, int credits, int level, String difficulty) {
        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {
            Log.e(TAG, "No hay usuario autenticado. No se guardó el puntaje.");
            return;
        }

        if (serverBaseUrl.isEmpty()) {
            Log.e(TAG, "server_base_url está vacío. Configura la URL del servidor en strings.xml.");
            return;
        }

        user.getIdToken(true)
                .addOnSuccessListener(getTokenResult -> {
                    String idToken = getTokenResult.getToken();

                    if (idToken == null || idToken.isEmpty()) {
                        Log.e(TAG, "No se pudo obtener el token de Firebase Auth.");
                        return;
                    }

                    new Thread(() -> sendScoreToServer(idToken, score, credits, level, difficulty)).start();
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error obteniendo token de Firebase Auth", e));
    }

    private void sendScoreToServer(String idToken, int score, int credits, int level, String difficulty) {
        HttpURLConnection connection = null;

        try {
            String playerName = preferences.getString("username", "Jugador ESCOM");
            String endpoint = serverBaseUrl + "/api/scores";

            JSONObject body = new JSONObject();
            body.put("score", score);
            body.put("credits", credits);
            body.put("level", level);
            body.put("difficulty", difficulty);
            body.put("playerName", playerName);

            URL url = new URL(endpoint);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Authorization", "Bearer " + idToken);

            OutputStream outputStream = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            writer.write(body.toString());
            writer.flush();
            writer.close();
            outputStream.close();

            int responseCode = connection.getResponseCode();
            String responseBody = readResponseBody(connection, responseCode);

            if (responseCode >= 200 && responseCode < 300) {
                Log.d(TAG, "Puntaje guardado en servidor: " + responseBody);
            } else {
                Log.e(TAG, "Error guardando puntaje. Código: " + responseCode + " Respuesta: " + responseBody);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error enviando puntaje al servidor", e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private String readResponseBody(HttpURLConnection connection, int responseCode) {
        StringBuilder result = new StringBuilder();

        try {
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
            String line;

            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            reader.close();
        } catch (Exception e) {
            Log.e(TAG, "No se pudo leer respuesta del servidor", e);
        }

        return result.toString();
    }
}
