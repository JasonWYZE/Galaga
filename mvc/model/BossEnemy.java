package edu.uchicago.gerber._08final.mvc.model;


import java.awt.*;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import edu.uchicago.gerber._08final.mvc.controller.CommandCenter;
import edu.uchicago.gerber._08final.mvc.controller.Game;
import edu.uchicago.gerber._08final.mvc.controller.GameOp;


public class BossEnemy extends Sprite {

    public enum ImageState {

        ENEMY_NORMAL,
    }

    // Instance fields
    private int deltaX; // Speed and direction of horizontal movement
    private long lastFireTime = 0;
    public static final int MIN_RADIUS = 56;
    private final long fireInterval = 1400; // Fire every 1000 milliseconds (1 second), adjust as needed

    // Constructor
    public BossEnemy() {

        setTeam(Team.FOE);
        // Set up the image for the enemy
        Map<ImageState, BufferedImage> rasterMap = new HashMap<>();
        rasterMap.put(ImageState.ENEMY_NORMAL, loadGraphic("/imgs/enemy/boss.jpeg")); // Path to enemy image

        setRasterMap(rasterMap);

        int initialY = Game.R.nextInt(Game.DIM.height);
        setCenter(new Point(-50, initialY)); // X-coordinate is off-screen

        // Set movement to move horizontally towards the middle
        setDeltaY(0);

    }

    @Override
    public void move() {

        super.move();

        Point currentCenter = getCenter();
        setCenter(new Point(currentCenter.x + deltaX, currentCenter.y));

        // Check if the middle of the screen is reached

        autoFire();

    }

    // Method to handle automatic firing
    private void autoFire() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFireTime > fireInterval) {
            int originalOrientation = this.getOrientation(); // Save the original orientation

            for (int i = 0; i < 6; i++) {
                // Set orientation for each bullet to be evenly spaced out over 360 degrees
                this.setOrientation((originalOrientation + i * 60) % 360);

                Bullet bullet = new Bullet(this); // Assuming Bullet constructor takes Sprite, deltaX, deltaY
                CommandCenter.getInstance().getOpsQueue().enqueue(bullet, GameOp.Action.ADD);
            }

            this.setOrientation(originalOrientation); // Reset to original orientation
            lastFireTime = currentTime;
        }

    }



    @Override
    public void draw(Graphics g) {

        ImageState imageState=ImageState.ENEMY_NORMAL;

        renderRaster((Graphics2D) g, getRasterMap().get(imageState));

    }
}
