package com.escom.escomsurfers;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class GameManager {

    private ArrayList<Obstacle> obstacles;
    private ArrayList<Coin> coins;
    private ArrayList<PowerUp> powerUps;

    private Random random;

    private int score;
    private int coinsCollected;

    private float speed;

    private boolean gameOver;

    private long lastObstacleSpawn;
    private long lastCoinSpawn;
    private long lastPowerUpSpawn;

    private long gameStartTime;

    private int difficultyLevel;

    private boolean slowMode;
    private long slowModeEndTime;
    private int lastSlowLevelTriggered;

    private boolean coffeeActive;
    private boolean vpnActive;

    private long coffeeEndTime;
    private long vpnEndTime;

    private String powerMessage;
    private long powerMessageEndTime;

    private String difficulty;

    private float initialSpeed;
    private float speedIncrease;
    private float maxSpeed;

    private long baseObstacleInterval;
    private long baseCoinInterval;
    private long basePowerUpInterval;

    private long minObstacleInterval;
    private long minCoinInterval;

    private float gapMultiplier;

    private boolean storyMode;
    private int storyLevelIndex;
    private long storyDurationMs;
    private long storyStartTime;
    private boolean levelCompleted;
    private int storyHearts;
    private long invulnerableEndTime;

    private static final long LEVEL_TIME = 20000;
    private static final long SLOW_MODE_DURATION = 6000;

    private static final long COFFEE_DURATION = 8000;
    private static final long VPN_DURATION = 9000;

    public GameManager() {
        this("MEDIO", false, 0);
    }

    public GameManager(String difficulty) {
        this(difficulty, false, 0);
    }

    public GameManager(String difficulty, boolean storyMode, int storyLevelIndex) {
        this.storyMode = storyMode;
        this.storyLevelIndex = storyLevelIndex;

        if (storyMode) {
            StoryLevel storyLevel = StoryManager.getLevel(storyLevelIndex);
            this.difficulty = storyLevel.getDifficulty();
            this.storyDurationMs = storyLevel.getDurationMs();
        } else {
            if (difficulty == null || difficulty.trim().isEmpty()) {
                this.difficulty = "MEDIO";
            } else {
                this.difficulty = difficulty;
            }
            this.storyDurationMs = 0;
        }

        configureDifficulty(this.difficulty);

        obstacles = new ArrayList<>();
        coins = new ArrayList<>();
        powerUps = new ArrayList<>();

        random = new Random();

        score = 0;
        coinsCollected = 0;
        speed = initialSpeed;
        gameOver = false;
        levelCompleted = false;

        difficultyLevel = 0;

        slowMode = false;
        slowModeEndTime = 0;
        lastSlowLevelTriggered = -1;

        coffeeActive = false;
        vpnActive = false;

        coffeeEndTime = 0;
        vpnEndTime = 0;

        powerMessage = "";
        powerMessageEndTime = 0;

        storyHearts = storyMode ? 3 : 0;
        invulnerableEndTime = 0;

        long currentTime = System.currentTimeMillis();

        gameStartTime = currentTime;
        storyStartTime = currentTime;
        lastObstacleSpawn = currentTime;
        lastCoinSpawn = currentTime;
        lastPowerUpSpawn = currentTime;
    }

    private void configureDifficulty(String difficulty) {
        if ("FACIL".equals(difficulty)) {
            initialSpeed = 8.0f;
            speedIncrease = 0.80f;
            maxSpeed = 19f;

            baseObstacleInterval = 2800;
            baseCoinInterval = 1400;
            basePowerUpInterval = 10500;

            minObstacleInterval = 1500;
            minCoinInterval = 950;

            gapMultiplier = 1.25f;

        } else if ("DIFICIL".equals(difficulty)) {
            initialSpeed = 13.0f;
            speedIncrease = 1.45f;
            maxSpeed = 30f;

            baseObstacleInterval = 1450;
            baseCoinInterval = 1000;
            basePowerUpInterval = 15000;

            minObstacleInterval = 650;
            minCoinInterval = 650;

            gapMultiplier = 0.85f;

        } else if ("EXTRAORDINARIO".equals(difficulty)) {
            initialSpeed = 15.0f;
            speedIncrease = 1.75f;
            maxSpeed = 34f;

            baseObstacleInterval = 1150;
            baseCoinInterval = 1150;
            basePowerUpInterval = 17500;

            minObstacleInterval = 520;
            minCoinInterval = 700;

            gapMultiplier = 0.72f;

        } else {
            initialSpeed = 10.0f;
            speedIncrease = 1.10f;
            maxSpeed = 24f;

            baseObstacleInterval = 2100;
            baseCoinInterval = 1500;
            basePowerUpInterval = 13000;

            minObstacleInterval = 1100;
            minCoinInterval = 900;

            gapMultiplier = 1.00f;
        }
    }

    public void update(Player player, int screenWidth, int screenHeight) {
        if (gameOver || levelCompleted) {
            return;
        }

        long currentTime = System.currentTimeMillis();

        updatePowerTimers(currentTime);
        updateDifficulty(currentTime);

        if (coffeeActive) {
            score += 3;
        } else {
            score++;
        }

        spawnObjects(screenWidth, screenHeight, currentTime);

        updateObstacles(player, screenHeight);
        updateCoins(player, screenHeight);
        updatePowerUps(player, screenHeight);

        if (storyMode && !gameOver && currentTime - storyStartTime >= storyDurationMs) {
            levelCompleted = true;
            showMessage("NIVEL COMPLETADO", currentTime);
        }
    }

    private void updatePowerTimers(long currentTime) {
        if (coffeeActive && currentTime >= coffeeEndTime) {
            coffeeActive = false;
            showMessage("CAFÉ terminado", currentTime);
        }

        if (vpnActive && currentTime >= vpnEndTime) {
            vpnActive = false;
            showMessage("VPN terminada", currentTime);
        }

        if (currentTime >= powerMessageEndTime) {
            powerMessage = "";
        }
    }

    private void updateDifficulty(long currentTime) {
        long elapsedTime = currentTime - gameStartTime;

        difficultyLevel = (int) (elapsedTime / LEVEL_TIME);

        if (difficultyLevel > 12) {
            difficultyLevel = 12;
        }

        if (difficultyLevel > 0 &&
                difficultyLevel % 3 == 0 &&
                difficultyLevel != lastSlowLevelTriggered &&
                !slowMode) {

            slowMode = true;
            slowModeEndTime = currentTime + SLOW_MODE_DURATION;
            lastSlowLevelTriggered = difficultyLevel;
            showMessage("Respiro de velocidad", currentTime);
        }

        if (slowMode && currentTime >= slowModeEndTime) {
            slowMode = false;
        }

        float baseSpeed = initialSpeed + (difficultyLevel * speedIncrease);

        if (slowMode) {
            baseSpeed *= 0.72f;
        }

        if (coffeeActive) {
            baseSpeed *= 1.18f;
        }

        if (baseSpeed > maxSpeed) {
            baseSpeed = maxSpeed;
        }

        speed = baseSpeed;
    }

    private void spawnObjects(int screenWidth, int screenHeight, long currentTime) {
        long obstacleInterval = baseObstacleInterval - (difficultyLevel * 70L);
        long coinInterval = baseCoinInterval - (difficultyLevel * 40L);
        long powerUpInterval = basePowerUpInterval + random.nextInt(3000);

        if ("DIFICIL".equals(difficulty)) {
            obstacleInterval -= 180;
        }

        if ("FACIL".equals(difficulty)) {
            obstacleInterval += 350;
        }

        if (obstacleInterval < minObstacleInterval) {
            obstacleInterval = minObstacleInterval;
        }

        if (coinInterval < minCoinInterval) {
            coinInterval = minCoinInterval;
        }

        if (slowMode || vpnActive) {
            obstacleInterval += 250;
        }

        if (currentTime - lastObstacleSpawn > obstacleInterval) {
            int lane = findSafeLaneForObstacle(screenHeight);

            if (lane != -1) {
                int type = random.nextInt(3);
                obstacles.add(new Obstacle(lane, type, screenWidth, screenHeight));
                lastObstacleSpawn = currentTime;
            }
        }

        if (currentTime - lastCoinSpawn > coinInterval) {
            int lane = findSafeLaneForCoin(screenHeight);

            if (lane != -1) {
                coins.add(new Coin(lane, screenWidth, screenHeight));
                lastCoinSpawn = currentTime;
            }
        }

        if (currentTime - lastPowerUpSpawn > powerUpInterval) {
            int lane = findSafeLaneForPowerUp(screenHeight);

            if (lane != -1) {
                int type = random.nextInt(2);
                powerUps.add(new PowerUp(lane, type, screenWidth, screenHeight));
                lastPowerUpSpawn = currentTime;
            }
        }
    }

    private int findSafeLaneForObstacle(int screenHeight) {
        int[] lanes = shuffleLanes();

        for (int lane : lanes) {
            if (canSpawnObstacleInLane(lane, screenHeight)) {
                return lane;
            }
        }

        return -1;
    }

    private int findSafeLaneForCoin(int screenHeight) {
        int[] lanes = shuffleLanes();

        for (int lane : lanes) {
            if (canSpawnCoinInLane(lane, screenHeight)) {
                return lane;
            }
        }

        return -1;
    }

    private int findSafeLaneForPowerUp(int screenHeight) {
        int[] lanes = shuffleLanes();

        for (int lane : lanes) {
            if (canSpawnPowerUpInLane(lane, screenHeight)) {
                return lane;
            }
        }

        return -1;
    }

    private int[] shuffleLanes() {
        int[] lanes = {0, 1, 2};

        for (int i = 0; i < lanes.length; i++) {
            int j = random.nextInt(lanes.length);
            int temp = lanes[i];
            lanes[i] = lanes[j];
            lanes[j] = temp;
        }

        return lanes;
    }

    private boolean canSpawnObstacleInLane(int lane, int screenHeight) {
        float minObstacleGap = screenHeight * 0.28f * gapMultiplier;
        float minCoinGap = screenHeight * 0.18f * gapMultiplier;
        float minPowerGap = screenHeight * 0.22f * gapMultiplier;

        for (Obstacle obstacle : obstacles) {
            if (obstacle.getLane() == lane && obstacle.getY() < minObstacleGap) {
                return false;
            }
        }

        for (Coin coin : coins) {
            if (coin.getLane() == lane && coin.getY() < minCoinGap) {
                return false;
            }
        }

        for (PowerUp powerUp : powerUps) {
            if (powerUp.getLane() == lane && powerUp.getY() < minPowerGap) {
                return false;
            }
        }

        return true;
    }

    private boolean canSpawnCoinInLane(int lane, int screenHeight) {
        float minObstacleGap = screenHeight * 0.24f * gapMultiplier;
        float minCoinGap = screenHeight * 0.14f * gapMultiplier;
        float minPowerGap = screenHeight * 0.18f * gapMultiplier;

        for (Obstacle obstacle : obstacles) {
            if (obstacle.getLane() == lane && obstacle.getY() < minObstacleGap) {
                return false;
            }
        }

        for (Coin coin : coins) {
            if (coin.getLane() == lane && coin.getY() < minCoinGap) {
                return false;
            }
        }

        for (PowerUp powerUp : powerUps) {
            if (powerUp.getLane() == lane && powerUp.getY() < minPowerGap) {
                return false;
            }
        }

        return true;
    }

    private boolean canSpawnPowerUpInLane(int lane, int screenHeight) {
        float minObstacleGap = screenHeight * 0.30f * gapMultiplier;
        float minCoinGap = screenHeight * 0.18f * gapMultiplier;
        float minPowerGap = screenHeight * 0.25f * gapMultiplier;

        for (Obstacle obstacle : obstacles) {
            if (obstacle.getLane() == lane && obstacle.getY() < minObstacleGap) {
                return false;
            }
        }

        for (Coin coin : coins) {
            if (coin.getLane() == lane && coin.getY() < minCoinGap) {
                return false;
            }
        }

        for (PowerUp powerUp : powerUps) {
            if (powerUp.getLane() == lane && powerUp.getY() < minPowerGap) {
                return false;
            }
        }

        return true;
    }

    private void updateObstacles(Player player, int screenHeight) {
        Iterator<Obstacle> iterator = obstacles.iterator();

        while (iterator.hasNext()) {
            Obstacle obstacle = iterator.next();

            obstacle.update(speed);

            if (obstacle.isOffScreen(screenHeight)) {
                iterator.remove();
                continue;
            }

            if (player.getLane() != obstacle.getLane()) {
                continue;
            }

            if (isColliding(player.getCollisionRect(), obstacle.getCollisionRect())) {
                if (vpnActive) {
                    iterator.remove();
                    score += 80;
                    continue;
                }

                if (!player.isJumping()) {
                    if (storyMode) {
                        long currentTime = System.currentTimeMillis();

                        if (currentTime < invulnerableEndTime) {
                            continue;
                        }

                        storyHearts--;
                        iterator.remove();
                        invulnerableEndTime = currentTime + 1600;

                        if (storyHearts <= 0) {
                            gameOver = true;
                        } else {
                            showMessage("Golpe recibido. Vidas: " + storyHearts, currentTime);
                        }

                        continue;
                    }

                    gameOver = true;
                }
            }
        }
    }

    private void updateCoins(Player player, int screenHeight) {
        Iterator<Coin> iterator = coins.iterator();

        while (iterator.hasNext()) {
            Coin coin = iterator.next();

            coin.update(speed);

            if (coin.isOffScreen(screenHeight)) {
                iterator.remove();
                continue;
            }

            if (isColliding(player.getCollisionRect(), coin.getCollisionRect())) {
                coinsCollected++;

                if (coffeeActive) {
                    score += 120;
                } else {
                    score += 50;
                }

                iterator.remove();
            }
        }
    }

    private void updatePowerUps(Player player, int screenHeight) {
        Iterator<PowerUp> iterator = powerUps.iterator();

        while (iterator.hasNext()) {
            PowerUp powerUp = iterator.next();

            powerUp.update(speed);

            if (powerUp.isOffScreen(screenHeight)) {
                iterator.remove();
                continue;
            }

            if (isColliding(player.getCollisionRect(), powerUp.getCollisionRect())) {
                activatePowerUp(powerUp.getType());
                iterator.remove();
            }
        }
    }

    private void activatePowerUp(int type) {
        long currentTime = System.currentTimeMillis();

        if (type == PowerUp.TYPE_COFFEE) {
            coffeeActive = true;
            coffeeEndTime = currentTime + COFFEE_DURATION;

            score += 150;
            showMessage("CAFÉ ACTIVADO: turbo + puntos", currentTime);

        } else if (type == PowerUp.TYPE_VPN) {
            vpnActive = true;
            vpnEndTime = currentTime + VPN_DURATION;

            score += 100;
            showMessage("VPN ACTIVADA: escudo", currentTime);
        }
    }

    private void showMessage(String message, long currentTime) {
        powerMessage = message;
        powerMessageEndTime = currentTime + 2200;
    }

    private boolean isColliding(RectF a, RectF b) {
        return RectF.intersects(a, b);
    }

    public void draw(
            Canvas canvas,
            Paint paint,
            Bitmap coinImage,
            Bitmap obstacleBugImage,
            Bitmap obstacleExamImage,
            Bitmap obstacleHomeworkImage,
            Bitmap coffeeImage,
            Bitmap vpnImage
    ) {
        for (Coin coin : coins) {
            coin.draw(canvas, paint, coinImage);
        }

        for (PowerUp powerUp : powerUps) {
            powerUp.draw(canvas, paint, coffeeImage, vpnImage);
        }

        for (Obstacle obstacle : obstacles) {
            obstacle.draw(canvas, paint, obstacleBugImage, obstacleExamImage, obstacleHomeworkImage);
        }
    }

    public void restart() {
        obstacles.clear();
        coins.clear();
        powerUps.clear();

        score = 0;
        coinsCollected = 0;
        speed = initialSpeed;
        gameOver = false;
        levelCompleted = false;

        difficultyLevel = 0;

        slowMode = false;
        slowModeEndTime = 0;
        lastSlowLevelTriggered = -1;

        coffeeActive = false;
        vpnActive = false;

        coffeeEndTime = 0;
        vpnEndTime = 0;

        powerMessage = "";
        powerMessageEndTime = 0;

        storyHearts = storyMode ? 3 : 0;
        invulnerableEndTime = 0;

        long currentTime = System.currentTimeMillis();

        gameStartTime = currentTime;
        storyStartTime = currentTime;
        lastObstacleSpawn = currentTime;
        lastCoinSpawn = currentTime;
        lastPowerUpSpawn = currentTime;
    }

    public int getScore() {
        return score;
    }

    public int getCoinsCollected() {
        return coinsCollected;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public int getDifficultyLevel() {
        return difficultyLevel + 1;
    }

    public boolean isCoffeeActive() {
        return coffeeActive;
    }

    public boolean isVpnActive() {
        return vpnActive;
    }

    public int getCoffeeRemainingSeconds() {
        if (!coffeeActive) {
            return 0;
        }

        long remaining = coffeeEndTime - System.currentTimeMillis();

        if (remaining < 0) {
            return 0;
        }

        return (int) Math.ceil(remaining / 1000.0);
    }

    public int getVpnRemainingSeconds() {
        if (!vpnActive) {
            return 0;
        }

        long remaining = vpnEndTime - System.currentTimeMillis();

        if (remaining < 0) {
            return 0;
        }

        return (int) Math.ceil(remaining / 1000.0);
    }


    public boolean isStoryMode() {
        return storyMode;
    }

    public boolean isLevelCompleted() {
        return levelCompleted;
    }

    public int getStoryLevelIndex() {
        return storyLevelIndex;
    }

    public int getStoryHearts() {
        return storyHearts;
    }

    public long getRemainingTimeMs() {
        if (!storyMode) {
            return 0;
        }

        long elapsed = System.currentTimeMillis() - storyStartTime;
        long remaining = storyDurationMs - elapsed;

        if (remaining < 0) {
            return 0;
        }

        return remaining;
    }

    public long getStoryElapsedTimeMs() {
        if (!storyMode) {
            return 0;
        }

        long elapsed = System.currentTimeMillis() - storyStartTime;

        if (elapsed < 0) {
            return 0;
        }

        if (elapsed > storyDurationMs) {
            return storyDurationMs;
        }

        return elapsed;
    }

    public String getPowerMessage() {
        return powerMessage;
    }
}
