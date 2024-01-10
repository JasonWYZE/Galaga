package edu.uchicago.gerber._08final.mvc.controller;

import edu.uchicago.gerber._08final.mvc.model.*;
import edu.uchicago.gerber._08final.mvc.view.GamePanel;


import javax.sound.sampled.Clip;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;
import java.util.Random;


// ===============================================
// == This Game class is the CONTROLLER
// ===============================================

public class Game implements Runnable, KeyListener {

    // ===============================================
    // FIELDS
    // ===============================================

    public static final Dimension DIM = new Dimension(700, 800); //the dimension of the game.
    private final GamePanel gamePanel;
    //this is used throughout many classes.
    public static final Random R = new Random();

    public final static int ANIMATION_DELAY = 40; // milliseconds between frames

    public final static int FRAMES_PER_SECOND = 1000 / ANIMATION_DELAY;

    private final Thread animationThread;


    //key-codes
    private static final int
            PAUSE = 80, // p key
            QUIT = 81, // q key
            LEFT = 37, // rotate left; left arrow
            RIGHT = 39, // rotate right; right arrow
            UP = 38, // thrust; up arrow
            DOWN = 40,// down; down arrow
            START = 83, // s key
            FIRE = 32, // space key
            MUTE = 77, // m-key mute

            NUKE = 78; // n-key mute

    // for possible future use
    // HYPER = 68, 					// D key
    //ALIEN = 65;                // A key
    // SPECIAL = 70; 					// fire special weapon;  F key

    private final Clip soundThrust;
    private final Clip soundBackground;

    private long lastFireTime = 0;
    private final long fireInterval = 500; // adjust as needed



    // ===============================================
    // ==CONSTRUCTOR
    // ===============================================

    public Game() {

        gamePanel = new GamePanel(DIM);
        gamePanel.addKeyListener(this); //Game object implements KeyListener
        soundThrust = Sound.clipForLoopFactory("whitenoise.wav");
        soundBackground = Sound.clipForLoopFactory("music-background.wav");

        //fire up the animation thread
        animationThread = new Thread(this); // pass the animation thread a runnable object, the Game object
        animationThread.start();


    }

    // ===============================================
    // ==METHODS
    // ===============================================

    public static void main(String[] args) {
        //typical Swing application start; we pass EventQueue a Runnable object.
        EventQueue.invokeLater(Game::new);
    }

    // Game implements runnable, and must have run method
    @Override
    public void run() {

        // lower animation thread's priority, thereby yielding to the "main" aka 'Event Dispatch'
        // thread which listens to keystrokes
        animationThread.setPriority(Thread.MIN_PRIORITY);

        // and get the current time
        long startTime = System.currentTimeMillis();

        // this thread animates the scene
        while (Thread.currentThread() == animationThread) {


            //this call will cause all movables to move() and draw() themselves every ~40ms
            // see GamePanel class for details


            gamePanel.update(gamePanel.getGraphics());

            checkCollisions();
            checkNewLevel();
            checkFloaters();




            //keep track of the frame for development purposes
            CommandCenter.getInstance().incrementFrame();


            long currentTime = System.currentTimeMillis();
            if (currentTime - lastFireTime > fireInterval) {
                Falcon falcon = CommandCenter.getInstance().getFalcon();
                if (falcon != null) {
                    falcon.autoFire(); // Trigger automatic firing
                    lastFireTime = currentTime;
                }
            }

            // surround the sleep() in a try/catch block
            // this simply controls delay time between
            // the frames of the animation
            try {
                // The total amount of time is guaranteed to be at least ANIMATION_DELAY long.  If processing (update)
                // between frames takes longer than ANIMATION_DELAY, then the difference between startTime -
                // System.currentTimeMillis() will be negative, then zero will be the sleep time
                startTime += ANIMATION_DELAY;

                Thread.sleep(Math.max(0,
                        startTime - System.currentTimeMillis()));
            } catch (InterruptedException e) {
                // do nothing (bury the exception), and just continue, e.g. skip this frame -- no big deal
            }
        } // end while
    } // end run

    private void checkFloaters() {
        spawnFireFloater();
        spawnShieldFloater();
        spawnNukeFloater();
    }


    private void checkCollisions() {

        Point pntFriendCenter, pntFoeCenter;
        int radFriend, radFoe;

        //This has order-of-growth of O(n^2), there is no way around this.
        for (Movable movFriend : CommandCenter.getInstance().getMovFriends()) {
            for (Movable movFoe : CommandCenter.getInstance().getMovFoes()) {

                pntFriendCenter = movFriend.getCenter();
                pntFoeCenter = movFoe.getCenter();
                radFriend = movFriend.getRadius();
                radFoe = movFoe.getRadius();

                //detect collision
                if (pntFriendCenter.distance(pntFoeCenter) < (radFriend + radFoe)) {
                    //remove the friend (so long as he is not protected)
                    if (!movFriend.isProtected()) {
                        CommandCenter.getInstance().getOpsQueue().enqueue(movFriend, GameOp.Action.REMOVE);
                    }

                    //remove the foe
                    CommandCenter.getInstance().getOpsQueue().enqueue(movFoe, GameOp.Action.REMOVE);


                        CommandCenter.getInstance().setScore(CommandCenter.getInstance().getScore() + 10);
                        Sound.playSound("kapow.wav");

                }

            }//end inner for
        }//end outer for

        //check for collisions between falcon and floaters. Order of growth of O(n) where n is number of floaters
        Point pntFalCenter = CommandCenter.getInstance().getFalcon().getCenter();
        int radFalcon = CommandCenter.getInstance().getFalcon().getRadius();

        Point pntFloaterCenter;
        int radFloater;
        for (Movable movFloater : CommandCenter.getInstance().getMovFloaters()) {
            pntFloaterCenter = movFloater.getCenter();
            radFloater = movFloater.getRadius();

            //detect collision
            if (pntFalCenter.distance(pntFloaterCenter) < (radFalcon + radFloater)) {

                Class<? extends Movable> clazz = movFloater.getClass();
                switch (clazz.getSimpleName()) {
                    case "ShieldFloater":
                        Sound.playSound("shieldup.wav");
                        CommandCenter.getInstance().getFalcon().setShield(Falcon.MAX_SHIELD);
                        break;
                    case "FireFloater":
                        Sound.playSound("insect.wav");
                        CommandCenter.getInstance().getFalcon().setFireLevel(Falcon.MAX_FIRE_LEVEL);
                        break;
                    case "NukeFloater":
                        Sound.playSound("nuke-up.wav");
                        CommandCenter.getInstance().getFalcon().setNukeMeter(Falcon.MAX_NUKE);
                        break;
                }
                CommandCenter.getInstance().getOpsQueue().enqueue(movFloater, GameOp.Action.REMOVE);


            }//end if
        }//end for

        processGameOpsQueue();

    }//end meth


    //This method adds and removes movables to/from their respective linked-lists.
    //This method is being called by the animationThread. The entire method is locked on the intrinsic lock of this
    // Game object. The main (Swing) thread also has access to the GameOpsQueue via the
    // key event methods such as keyReleased. Therefore, to avoid mutating the GameOpsQueue while we are iterating
    // it, we also synchronize the critical sections of the keyReleased and keyPressed methods below on the same
    // intrinsic lock.
    private synchronized void processGameOpsQueue() {

        //deferred mutation: these operations are done AFTER we have completed our collision detection to avoid
        // mutating the movable linkedlists while iterating them above.
        while (!CommandCenter.getInstance().getOpsQueue().isEmpty()) {
            GameOp gameOp = CommandCenter.getInstance().getOpsQueue().dequeue();
            Movable mov = gameOp.getMovable();
            GameOp.Action action = gameOp.getAction();

            switch (mov.getTeam()) {
                case FOE:
                    if (action == GameOp.Action.ADD) {
                        CommandCenter.getInstance().getMovFoes().add(mov);
                    } else { //GameOp.Operation.REMOVE
                        CommandCenter.getInstance().getMovFoes().remove(mov);
                        if (mov instanceof BossEnemy) spawnBee(2);
                    }

                    break;
                case FRIEND:
                    if (action == GameOp.Action.ADD) {
                        CommandCenter.getInstance().getMovFriends().add(mov);
                    } else { //GameOp.Operation.REMOVE
                        if (mov instanceof Falcon) {
                            CommandCenter.getInstance().initFalconAndDecrementFalconNum();
                        } else {
                            CommandCenter.getInstance().getMovFriends().remove(mov);
                        }
                    }
                    break;

                case FLOATER:
                    if (action == GameOp.Action.ADD) {
                        CommandCenter.getInstance().getMovFloaters().add(mov);
                    } else { //GameOp.Operation.REMOVE
                        CommandCenter.getInstance().getMovFloaters().remove(mov);
                    }
                    break;

               

            }

        }
    }





    private void spawnFireFloater() {

        if (CommandCenter.getInstance().getFrame() % FireFloater.SPAWN_NEW_WALL_FLOATER == 0 ) {
            CommandCenter.getInstance().getOpsQueue().enqueue(new FireFloater(), GameOp.Action.ADD);
        }
    }

    private void spawnShieldFloater() {

        if (CommandCenter.getInstance().getFrame() % ShieldFloater.SPAWN_SHIELD_FLOATER == 0) {
            CommandCenter.getInstance().getOpsQueue().enqueue(new ShieldFloater(), GameOp.Action.ADD);
        }
    }

    private void spawnNukeFloater() {

        if (CommandCenter.getInstance().getFrame() % NukeFloater.SPAWN_NUKE_FLOATER == 0) {
            CommandCenter.getInstance().getOpsQueue().enqueue(new NukeFloater(), GameOp.Action.ADD);
        }
    }






    public void spawnBee(int level){
        int beesToSpawn = level * 2; //spawn 2 bees per level

        for (int i = 0; i < beesToSpawn; i++) {
            BeeEnemy beeEnemy = new BeeEnemy();

            // Set unique positions for each bee. Adjust this logic as needed.
            int xSpawnPosition = Game.R.nextInt(Game.DIM.width);
            int ySpawnPosition = Game.R.nextInt(2*Game.DIM.height/3 );
            beeEnemy.setCenter(new Point(xSpawnPosition, ySpawnPosition));

            // Set the bee to move towards a random destination on the screen
            int xDestination = Game.R.nextInt(Game.DIM.width);
            int yDestination = Game.R.nextInt(Game.DIM.height);
            beeEnemy.setDeltaX((xDestination - xSpawnPosition) / 60); // Adjust the divisor for speed control
            beeEnemy.setDeltaY((yDestination - ySpawnPosition) / 60); // Adjust the divisor for speed control


            // Set other qproperties
            beeEnemy.setOrientation(Game.R.nextInt(4)*90); // facing the top
            beeEnemy.setRadius(Falcon.MIN_RADIUS);

            // Add the enemy to the game
            CommandCenter.getInstance().getOpsQueue().enqueue(beeEnemy, GameOp.Action.ADD);
        }

    }


    public void spawnBoss(int level){

            if(level ==3){
                BossEnemy bossEnemy = new BossEnemy();

                // Set unique positions for each bee. Adjust this logic as needed.
                int xSpawnPosition = Game.R.nextInt(Game.DIM.width);
                int ySpawnPosition = Game.R.nextInt(2*Game.DIM.height/3 );
                bossEnemy.setCenter(new Point(xSpawnPosition, ySpawnPosition));

                // Set the bee to move towards a random destination on the screen
                int xDestination = Game.R.nextInt(Game.DIM.width);
                int yDestination = Game.R.nextInt(Game.DIM.height);
                bossEnemy.setDeltaX((xDestination - xSpawnPosition) / 60); // Adjust the divisor for speed control
                bossEnemy.setDeltaY((yDestination - ySpawnPosition) / 60); // Adjust the divisor for speed control


                // Set other qproperties
                bossEnemy.setOrientation(180); // facing the top
                bossEnemy.setRadius(BossEnemy.MIN_RADIUS);

                // Add the enemy to the game
                CommandCenter.getInstance().getOpsQueue().enqueue(bossEnemy, GameOp.Action.ADD);
            }

        }





    private boolean isLevelClear() {

        boolean beeFree = true;
        boolean bossFree = true;
        for (Movable movFoe : CommandCenter.getInstance().getMovFoes()) {
            if (movFoe instanceof BeeEnemy) {
                beeFree = false;
                break;
            }
        }

        for (Movable movFoe : CommandCenter.getInstance().getMovFoes()) {
            if (movFoe instanceof BossEnemy) {
                bossFree = false;
                break;
            }
        }

        return beeFree & bossFree;
    }

    private void checkNewLevel() {

        if (isLevelClear()) {
            //currentLevel will be zero at beginning of game
            int level = CommandCenter.getInstance().getLevel();
            //award some points for having cleared the previous level
            CommandCenter.getInstance().setScore(CommandCenter.getInstance().getScore() + (10_000L * level));
            //bump the level up
            level = level + 1;
            CommandCenter.getInstance().setLevel(level);
            spawnBee(level);
            spawnBoss(level);

            CommandCenter.getInstance().getFalcon().setShield(Falcon.INITIAL_SPAWN_TIME);
            //show "Level X" in middle of screen
            CommandCenter.getInstance().getFalcon().setShowLevel(Falcon.INITIAL_SPAWN_TIME);

        }
    }


    // Varargs for stopping looping-music-clips
    private static void stopLoopingSounds(Clip... clpClips) {
        Arrays.stream(clpClips).forEach(clip -> clip.stop());
    }

    // ===============================================
    // KEYLISTENER METHODS
    // ===============================================

    @Override
    public void keyPressed(KeyEvent e) {
        Falcon falcon = CommandCenter.getInstance().getFalcon();
        int keyCode = e.getKeyCode();

        if (keyCode == START && CommandCenter.getInstance().isGameOver()) {
            CommandCenter.getInstance().initGame();
            return;
        }


        switch (keyCode) {
            case PAUSE:
                CommandCenter.getInstance().setPaused(!CommandCenter.getInstance().isPaused());
                if (CommandCenter.getInstance().isPaused()) stopLoopingSounds(soundBackground, soundThrust);
                break;
            case QUIT:
                System.exit(0);
                break;
            case UP:
                falcon.setDeltaY(falcon.getDeltaY() - 15);// Move left
                break;
            case DOWN:
                falcon.setDeltaY(falcon.getDeltaX() +15); // Move right
                break;

            case LEFT:
                falcon.setDeltaX(falcon.getDeltaX() - 15); // Move left
                break;
            case RIGHT:
                falcon.setDeltaX(falcon.getDeltaX() + 15); // Move right
                break;


            // possible future use
            // case KILL:
            // case SHIELD:
            // case NUM_ENTER:

            default:
                break;
        }

    }

    //key events are triggered by the main (Swing) thread which is listening for keystrokes. Notice that some of the
    // cases below touch the GameOpsQueue such as fire bullet and nuke.
    //The animation-thread also has access to the GameOpsQueue via the processGameOpsQueue() method.
    // Therefore, to avoid mutating the GameOpsQueue on the main thread, while we are iterating it on the
    // animation-thread, we synchronize on the same intrinsic lock. processGameOpsQueue() is also synchronized.
    @Override
    public void keyReleased(KeyEvent e) {
        Falcon falcon = CommandCenter.getInstance().getFalcon();
        int keyCode = e.getKeyCode();
        //show the key-code in the console
        System.out.println(keyCode);

        switch (keyCode) {
            case FIRE:
                synchronized (this){
                    CommandCenter.getInstance().getOpsQueue().enqueue(new Bullet(falcon), GameOp.Action.ADD);
                }
                Sound.playSound("thump.wav");
                break;
            case NUKE:
                if (CommandCenter.getInstance().getFalcon().getNukeMeter() > 0){
                    synchronized (this) {
                        CommandCenter.getInstance().getOpsQueue().enqueue(new Nuke(falcon), GameOp.Action.ADD);
                    }
                    Sound.playSound("nuke.wav");
                    CommandCenter.getInstance().getFalcon().setNukeMeter(0);
                }
                break;
            //releasing either the LEFT or RIGHT arrow key will set the TurnState to IDLE
            case LEFT:
            case RIGHT:
                falcon.setDeltaX(0); // Stop horizontal movement
                break;
//                falcon.setTurnState(Falcon.TurnState.IDLE);
//                break;
            case UP:
            case DOWN:
                falcon.setDeltaY(0); // Stop horizontal movement
                break;

            case MUTE:
                CommandCenter.getInstance().setMuted(!CommandCenter.getInstance().isMuted());

                if (!CommandCenter.getInstance().isMuted()) {
                    stopLoopingSounds(soundBackground);
                } else {
                    soundBackground.loop(Clip.LOOP_CONTINUOUSLY);
                }
                break;

            default:
                break;
        }

    }

    @Override
    // does nothing, but we need it b/c of KeyListener contract
    public void keyTyped(KeyEvent e) {
    }

}


