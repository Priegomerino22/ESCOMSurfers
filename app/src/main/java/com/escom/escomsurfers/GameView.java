package com.escom.escomsurfers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private GameThread gameThread;

    private Player player;
    private GameManager gameManager;
    private ScoreManager scoreManager;
    private FirebaseManager firebaseManager;

    private Paint paint;
    private Paint textPaint;
    private Paint titlePaint;

    private Bitmap bgEscom;
    private Bitmap playerImage;
    private Bitmap coinImage;
    private Bitmap obstacleBugImage;
    private Bitmap obstacleExamImage;
    private Bitmap obstacleHomeworkImage;
    private Bitmap powerupCoffeeImage;
    private Bitmap powerupVpnImage;

    private Rect bgDestRect;

    private int screenWidth;
    private int screenHeight;

    private float touchStartX;
    private float touchStartY;

    private boolean scoreSaved;
    private boolean storyCompletionHandled;

    private String difficulty;
    private final Context context;
    private boolean storyMode;
    private int storyLevelIndex;
    private StoryLevel storyLevel;
    private Handler mainHandler;

    private int lastStoryMessageIndex;
    private String storySpeaker;
    private String storyDialogue;
    private long storyDialogueEndTime;

    public GameView(Context context, String difficulty) {
        this(context, difficulty, false, 0);
    }

    public GameView(Context context, String difficulty, boolean storyMode, int storyLevelIndex) {
        super(context);

        this.context = context;
        this.storyMode = storyMode;
        this.storyLevelIndex = storyLevelIndex;
        this.storyLevel = storyMode ? StoryManager.getLevel(storyLevelIndex) : null;
        this.difficulty = storyMode && storyLevel != null ? storyLevel.getDifficulty() : difficulty;
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.lastStoryMessageIndex = -1;
        this.storySpeaker = "";
        this.storyDialogue = "";
        this.storyDialogueEndTime = 0;

        getHolder().addCallback(this);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(42);
        textPaint.setFakeBoldText(true);

        titlePaint = new Paint();
        titlePaint.setAntiAlias(true);
        titlePaint.setColor(Color.WHITE);
        titlePaint.setTextSize(60);
        titlePaint.setFakeBoldText(true);
        titlePaint.setTextAlign(Paint.Align.CENTER);

        bgDestRect = new Rect();

        int backgroundResId = R.drawable.bg_escom;
        int obstacleOneResId = R.drawable.obstacle_bug;
        int obstacleTwoResId = R.drawable.obstacle_exam;
        int obstacleThreeResId = R.drawable.obstacle_homework;

        if (storyMode && storyLevel != null) {
            backgroundResId = storyLevel.getBackgroundResId();
            int[] storyObstacles = storyLevel.getObstacleResIds();

            if (storyObstacles != null && storyObstacles.length >= 3) {
                obstacleOneResId = storyObstacles[0];
                obstacleTwoResId = storyObstacles[1];
                obstacleThreeResId = storyObstacles[2];
            }
        }

        bgEscom = BitmapFactory.decodeResource(getResources(), backgroundResId);
        playerImage = SpriteUtils.decodeTrimmed(getResources(), R.drawable.player_escom);
        coinImage = SpriteUtils.decodeTrimmed(getResources(), R.drawable.coin_credit);
        obstacleBugImage = SpriteUtils.decodeTrimmed(getResources(), obstacleOneResId);
        obstacleExamImage = SpriteUtils.decodeTrimmed(getResources(), obstacleTwoResId);
        obstacleHomeworkImage = SpriteUtils.decodeTrimmed(getResources(), obstacleThreeResId);
        powerupCoffeeImage = SpriteUtils.decodeTrimmed(getResources(), R.drawable.powerup_coffee);
        powerupVpnImage = SpriteUtils.decodeTrimmed(getResources(), R.drawable.powerup_vpn);

        scoreManager = new ScoreManager(context);
        firebaseManager = new FirebaseManager(context);

        setFocusable(true);
        requestFocus();

        scoreSaved = false;
        storyCompletionHandled = false;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        startGameThread();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        screenWidth = width;
        screenHeight = height;

        if (player == null) {
            player = new Player(screenWidth, screenHeight);
        }

        if (gameManager == null) {
            gameManager = new GameManager(difficulty, storyMode, storyLevelIndex);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        pause();
    }

    private void startGameThread() {
        if (gameThread == null) {
            gameThread = new GameThread(getHolder(), this);
            gameThread.setRunning(true);
            gameThread.start();
        }
    }

    public void resume() {
        if (getHolder().getSurface().isValid() && gameThread == null) {
            startGameThread();
        }
    }

    public void pause() {
        if (gameThread != null) {
            gameThread.setRunning(false);

            try {
                gameThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            gameThread = null;
        }
    }

    public void update() {
        if (screenWidth == 0 || screenHeight == 0) {
            return;
        }

        if (player == null) {
            player = new Player(screenWidth, screenHeight);
        }

        if (gameManager == null) {
            gameManager = new GameManager(difficulty, storyMode, storyLevelIndex);
        }

        if (!gameManager.isGameOver() && !gameManager.isLevelCompleted()) {
            player.update();
            gameManager.update(player, screenWidth, screenHeight);
            updateStoryMessages();
            scoreSaved = false;
        } else if (gameManager.isLevelCompleted()) {
            handleStoryCompleted();
        } else {
            if (!scoreSaved) {
                scoreManager.saveHighScore(gameManager.getScore());

                firebaseManager.saveScore(
                        gameManager.getScore(),
                        gameManager.getCoinsCollected(),
                        gameManager.getDifficultyLevel(),
                        difficulty
                );

                scoreSaved = true;
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (canvas == null) {
            return;
        }

        drawBackground(canvas);

        // IMPORTANTE: reiniciar alpha del Paint.
        // Las líneas de perspectiva usan transparencia y, si no se reinicia,
        // monedas, obstáculos y jugador se dibujan opacos.
        paint.setAlpha(255);
        paint.setColor(Color.WHITE);
        paint.setColorFilter(null);
        paint.setStyle(Paint.Style.FILL);

        if (gameManager != null) {
            gameManager.draw(
                    canvas,
                    paint,
                    coinImage,
                    obstacleBugImage,
                    obstacleExamImage,
                    obstacleHomeworkImage,
                    powerupCoffeeImage,
                    powerupVpnImage
            );
        }

        if (player != null) {
            player.draw(canvas, paint, playerImage);
        }

        drawUI(canvas);
        drawStoryDialogue(canvas);

        if (gameManager != null && gameManager.isGameOver()) {
            drawGameOver(canvas);
        }
    }

    private void drawBackground(Canvas canvas) {
        if (bgEscom != null) {
            bgDestRect.set(0, 0, screenWidth, screenHeight);
            canvas.drawBitmap(bgEscom, null, bgDestRect, paint);
        } else {
            canvas.drawColor(Color.rgb(22, 28, 36));
        }

        // Luz muy suave para evitar que el fondo y los sprites se quemen.
        paint.setAlpha(255);
        paint.setColorFilter(null);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.argb(10, 120, 190, 255));
        canvas.drawRect(0, 0, screenWidth, screenHeight, paint);

        paint.setColor(Color.argb(6, 255, 210, 110));
        canvas.drawRect(0, screenHeight * 0.24f, screenWidth, screenHeight, paint);

        float margin = dp(10);
        RectF topBar = new RectF(margin, dp(8), screenWidth - margin, dp(58));

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(121, 22, 50));
        canvas.drawRoundRect(topBar, dp(12), dp(12), paint);

        paint.setColor(Color.WHITE);
        paint.setTextSize(dp(16));
        paint.setFakeBoldText(true);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("ESCOM SURFERS", screenWidth / 2f, dp(38), paint);

        // Líneas blancas más cortas: ya no van desde el fondo completo hasta abajo.
        // Empiezan más cerca del jugador para que encajen mejor con el fondo.
        float fullTop = PerspectiveHelper.getHorizonY(screenHeight);
        float fullBottom = PerspectiveHelper.getBottomY(screenHeight);

        float yTop = fullTop + (fullBottom - fullTop) * 0.28f;
        float yBottom = fullBottom - screenHeight * 0.035f;

        float leftDividerTop = PerspectiveHelper.getLeftDividerX(yTop, screenWidth, screenHeight);
        float rightDividerTop = PerspectiveHelper.getRightDividerX(yTop, screenWidth, screenHeight);

        float leftDividerBottom = PerspectiveHelper.getLeftDividerX(yBottom, screenWidth, screenHeight);
        float rightDividerBottom = PerspectiveHelper.getRightDividerX(yBottom, screenWidth, screenHeight);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp(2));
        paint.setColor(Color.argb(58, 255, 255, 255));

        canvas.drawLine(leftDividerTop, yTop, leftDividerBottom, yBottom, paint);
        canvas.drawLine(rightDividerTop, yTop, rightDividerBottom, yBottom, paint);

        paint.setAlpha(255);
        paint.setColorFilter(null);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.LEFT);
    }

    private void drawUI(Canvas canvas) {
        if (gameManager == null) {
            return;
        }

        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(dp(12));
        textPaint.setFakeBoldText(true);
        textPaint.setTextAlign(Paint.Align.LEFT);

        canvas.drawText("Puntos: " + gameManager.getScore(), dp(18), dp(78), textPaint);
        canvas.drawText("Créditos: " + gameManager.getCoinsCollected(), dp(18), dp(95), textPaint);
        canvas.drawText("Nivel: " + gameManager.getDifficultyLevel(), dp(18), dp(112), textPaint);
        canvas.drawText("Modo: " + difficulty, dp(18), dp(129), textPaint);

        int nextY = dp(146);

        if (storyMode && storyLevel != null) {
            textPaint.setColor(Color.rgb(255, 220, 95));
            canvas.drawText("Historia " + storyLevel.getIndex() + ": " + storyLevel.getTitle(), dp(18), nextY, textPaint);
            nextY += dp(18);

            textPaint.setColor(Color.WHITE);
            canvas.drawText("Tiempo: " + formatTime(gameManager.getRemainingTimeMs()), dp(18), nextY, textPaint);
            nextY += dp(18);

            textPaint.setColor(Color.rgb(255, 110, 120));
            canvas.drawText("Vidas: " + getHeartsText(gameManager.getStoryHearts()), dp(18), nextY, textPaint);
            nextY += dp(18);
        }

        if (gameManager.isCoffeeActive()) {
            textPaint.setColor(Color.rgb(255, 210, 80));
            canvas.drawText("CAFÉ: " + gameManager.getCoffeeRemainingSeconds() + "s", dp(18), nextY, textPaint);
            nextY += dp(18);
        }

        if (gameManager.isVpnActive()) {
            textPaint.setColor(Color.rgb(80, 220, 255));
            canvas.drawText("VPN: " + gameManager.getVpnRemainingSeconds() + "s", dp(18), nextY, textPaint);
        }

        textPaint.setColor(Color.WHITE);
        textPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("Récord: " + scoreManager.getHighScore(), screenWidth - dp(18), dp(78), textPaint);

        textPaint.setTextAlign(Paint.Align.CENTER);

        if (gameManager.getPowerMessage() != null && !gameManager.getPowerMessage().isEmpty()) {
            textPaint.setTextSize(dp(16));
            textPaint.setColor(Color.rgb(255, 235, 120));
            canvas.drawText(gameManager.getPowerMessage(), screenWidth / 2f, screenHeight * 0.28f, textPaint);
        }

        textPaint.setTextAlign(Paint.Align.LEFT);
    }

    private void drawGameOver(Canvas canvas) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.argb(185, 0, 0, 0));
        canvas.drawRect(0, 0, screenWidth, screenHeight, paint);

        titlePaint.setColor(Color.WHITE);
        titlePaint.setTextSize(dp(28));
        titlePaint.setFakeBoldText(true);
        canvas.drawText(storyMode ? "NIVEL FALLIDO" : "GAME OVER", screenWidth / 2f, screenHeight / 2f - dp(70), titlePaint);

        titlePaint.setTextSize(dp(18));
        canvas.drawText("Puntaje: " + gameManager.getScore(), screenWidth / 2f, screenHeight / 2f - dp(24), titlePaint);
        canvas.drawText("Modo: " + difficulty, screenWidth / 2f, screenHeight / 2f + dp(6), titlePaint);
        canvas.drawText("Récord: " + scoreManager.getHighScore(), screenWidth / 2f, screenHeight / 2f + dp(36), titlePaint);

        titlePaint.setTextSize(dp(16));
        canvas.drawText("Toca la pantalla para reiniciar", screenWidth / 2f, screenHeight / 2f + dp(84), titlePaint);
    }


    private void updateStoryMessages() {
        if (!storyMode || storyLevel == null || gameManager == null) {
            return;
        }

        StoryMessage[] messages = storyLevel.getInGameMessages();

        if (messages == null || messages.length == 0) {
            return;
        }

        long elapsedSeconds = gameManager.getStoryElapsedTimeMs() / 1000L;
        int nextIndex = lastStoryMessageIndex + 1;

        if (nextIndex < messages.length && elapsedSeconds >= messages[nextIndex].getTriggerSecond()) {
            StoryMessage message = messages[nextIndex];
            storySpeaker = message.getSpeaker();
            storyDialogue = message.getText();
            storyDialogueEndTime = System.currentTimeMillis() + 3600;
            lastStoryMessageIndex = nextIndex;
        }

        if (System.currentTimeMillis() > storyDialogueEndTime) {
            storySpeaker = "";
            storyDialogue = "";
        }
    }

    private void drawStoryDialogue(Canvas canvas) {
        if (!storyMode || storyDialogue == null || storyDialogue.isEmpty()) {
            return;
        }

        float margin = dp(14);
        float panelHeight = dp(86);
        float top = screenHeight - panelHeight - dp(22);
        RectF panel = new RectF(margin, top, screenWidth - margin, top + panelHeight);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.argb(210, 10, 16, 30));
        canvas.drawRoundRect(panel, dp(16), dp(16), paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp(1));
        paint.setColor(Color.argb(210, 255, 220, 95));
        canvas.drawRoundRect(panel, dp(16), dp(16), paint);

        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setFakeBoldText(true);
        textPaint.setTextSize(dp(14));
        textPaint.setColor(Color.rgb(255, 220, 95));
        canvas.drawText(storySpeaker, margin + dp(16), top + dp(26), textPaint);

        textPaint.setFakeBoldText(false);
        textPaint.setTextSize(dp(13));
        textPaint.setColor(Color.WHITE);
        drawMultilineText(canvas, storyDialogue, margin + dp(16), top + dp(50), screenWidth - dp(60), dp(16));
        textPaint.setFakeBoldText(true);
    }

    private void drawMultilineText(Canvas canvas, String text, float x, float y, float maxWidth, float lineHeight) {
        if (text == null) {
            return;
        }

        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        float currentY = y;

        for (String word : words) {
            String testLine = line.length() == 0 ? word : line + " " + word;

            if (textPaint.measureText(testLine) > maxWidth && line.length() > 0) {
                canvas.drawText(line.toString(), x, currentY, textPaint);
                line = new StringBuilder(word);
                currentY += lineHeight;
            } else {
                line = new StringBuilder(testLine);
            }
        }

        if (line.length() > 0) {
            canvas.drawText(line.toString(), x, currentY, textPaint);
        }
    }

    private void handleStoryCompleted() {
        if (!storyMode || storyLevel == null || storyCompletionHandled) {
            return;
        }

        storyCompletionHandled = true;
        scoreManager.saveHighScore(gameManager.getScore());

        firebaseManager.saveScore(
                gameManager.getScore(),
                gameManager.getCoinsCollected(),
                gameManager.getDifficultyLevel(),
                difficulty
        );

        StoryProgressManager progressManager = new StoryProgressManager(context);
        progressManager.completeLevel(storyLevel.getIndex());

        mainHandler.postDelayed(() -> {
            Intent intent = new Intent(context, CutsceneActivity.class);
            intent.putExtra("levelIndex", storyLevel.getIndex());
            intent.putExtra("phase", "outro");
            context.startActivity(intent);

            if (context instanceof Activity) {
                ((Activity) context).finish();
            }
        }, 650);
    }

    private String formatTime(long ms) {
        long totalSeconds = Math.max(0, (long) Math.ceil(ms / 1000.0));
        long minutes = totalSeconds / 60L;
        long seconds = totalSeconds % 60L;
        return String.format("%d:%02d", minutes, seconds);
    }

    private String getHeartsText(int hearts) {
        if (hearts <= 0) {
            return "sin vidas";
        }

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < hearts; i++) {
            builder.append("♥");
        }

        return builder.toString();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (player == null || gameManager == null) {
            return true;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchStartX = event.getX();
                touchStartY = event.getY();
                return true;

            case MotionEvent.ACTION_UP:
                float touchEndX = event.getX();
                float touchEndY = event.getY();

                float deltaX = touchEndX - touchStartX;
                float deltaY = touchEndY - touchStartY;

                if (gameManager.isGameOver()) {
                    restartGame();
                    return true;
                }

                handleControls(deltaX, deltaY, touchEndX);
                return true;
        }

        return true;
    }

    private void handleControls(float deltaX, float deltaY, float touchX) {
        float minSwipeDistance = 80;

        if (Math.abs(deltaX) > Math.abs(deltaY) && Math.abs(deltaX) > minSwipeDistance) {
            if (deltaX > 0) {
                player.moveRight();
            } else {
                player.moveLeft();
            }
        } else if (Math.abs(deltaY) > minSwipeDistance) {
            if (deltaY < 0) {
                player.jump();
            } else {
                player.slide();
            }
        } else {
            if (touchX < screenWidth / 3f) {
                player.moveLeft();
            } else if (touchX > screenWidth * 2f / 3f) {
                player.moveRight();
            } else {
                player.jump();
            }
        }
    }

    private void restartGame() {
        player = new Player(screenWidth, screenHeight);
        gameManager.restart();
        scoreSaved = false;
        storyCompletionHandled = false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (player == null || gameManager == null) {
            return true;
        }

        if (gameManager.isGameOver()) {
            restartGame();
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_A || keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            player.moveLeft();
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_D || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            player.moveRight();
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_W || keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_SPACE) {
            player.jump();
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_S || keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            player.slide();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
}
