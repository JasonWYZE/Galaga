package edu.uchicago.gerber._08final.mvc.model;

import java.awt.*;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import edu.uchicago.gerber._08final.mvc.controller.CommandCenter;
import edu.uchicago.gerber._08final.mvc.controller.Game;
import edu.uchicago.gerber._08final.mvc.controller.GameOp;


public class BeeEnemy extends Sprite {

    public enum ImageState {

        ENEMY_NORMAL,
    }

    // Instance fields
    private int deltaX; // Speed and direction of horizontal movement
    private long lastFireTime = 0;
    public static final int MIN_RADIUS = 28;
    private final long fireInterval = 1400; // Fire every 1000 milliseconds (1 second), adjust as needed

    // Constructor
    public BeeEnemy() {

        setTeam(Team.FOE);
        // Set up the image for the enemy
        Map<ImageState, BufferedImage> rasterMap = new HashMap<>();
        rasterMap.put(ImageState.ENEMY_NORMAL, loadGraphic("/imgs/enemy/gala_bee.jpg")); // Path to enemy image

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
            Bullet bullet = new Bullet(this); // Assuming Bullet constructor takes Sprite, deltaX, deltaY
            CommandCenter.getInstance().getOpsQueue().enqueue(bullet, GameOp.Action.ADD);
            lastFireTime = currentTime;
        }
    }



    @Override
    public void draw(Graphics g) {

        ImageState imageState=ImageState.ENEMY_NORMAL;

        renderRaster((Graphics2D) g, getRasterMap().get(imageState));

    }
}
