package edu.uchicago.gerber._08final.mvc.model;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Bullet extends Sprite {



    public Bullet(Falcon falcon) {

        setTeam(Team.FRIEND);
        setColor(Color.ORANGE);

        //a bullet expires after 20 frames.
        setExpiry(20);
        setRadius(15);


        //everything is relative to the falcon ship that fired the bullet
        setCenter(falcon.getCenter());

        //set the bullet orientation to the falcon (ship) orientation
        setOrientation(falcon.getOrientation());

        final double FIRE_POWER = 35.0;
        double vectorX =
                Math.cos(Math.toRadians(getOrientation())) * FIRE_POWER;
        double vectorY =
                Math.sin(Math.toRadians(getOrientation())) * FIRE_POWER;

        //fire force: falcon inertia + fire-vector
        setDeltaX(falcon.getDeltaX() + vectorX);
        setDeltaY(falcon.getDeltaY() + vectorY);

        //we have a reference to the falcon passed into the constructor. Let's create some kick-back.
        //fire kick-back on the falcon: inertia - fire-vector / some arbitrary divisor
//        final double KICK_BACK_DIVISOR = 36.0;
//        falcon.setDeltaX(falcon.getDeltaX() - vectorX / KICK_BACK_DIVISOR);
//        falcon.setDeltaY(falcon.getDeltaY() - vectorY / KICK_BACK_DIVISOR);


        //define the points on a cartesian grid
        List<Point> listPoints = new ArrayList<>();
        listPoints.add(new Point(0, 3)); //top point
        listPoints.add(new Point(1, -1)); //right bottom
        listPoints.add(new Point(0, 0));
        listPoints.add(new Point(-1, -1)); //left bottom

        setCartesians(listPoints.toArray(new Point[0]));




    }


    public Bullet(BeeEnemy beeEnemy) {

        setTeam(Team.FOE);
        setColor(Color.red);

        //a bullet expires after 20 frames.
        setExpiry(18);
        setRadius(15);


        //everything is relative to the falcon ship that fired the bullet
        setCenter(beeEnemy.getCenter());

        //set the bullet orientation to the falcon (ship) orientation
        setOrientation(beeEnemy.getOrientation());

        final double FIRE_POWER = 25;
        double vectorX =
                Math.cos(Math.toRadians(getOrientation())) * FIRE_POWER;
        double vectorY =
                Math.sin(Math.toRadians(getOrientation())) * FIRE_POWER;

        //fire force: falcon inertia + fire-vector
        setDeltaX(beeEnemy.getDeltaX() + vectorX);
        setDeltaY(beeEnemy.getDeltaY() + vectorY);

        //we have a reference to the falcon passed into the constructor. Let's create some kick-back.
        //fire kick-back on the falcon: inertia - fire-vector / some arbitrary divisor
//        final double KICK_BACK_DIVISOR = 36.0;
//        falcon.setDeltaX(falcon.getDeltaX() - vectorX / KICK_BACK_DIVISOR);
//        falcon.setDeltaY(falcon.getDeltaY() - vectorY / KICK_BACK_DIVISOR);


        //define the points on a cartesian grid
        List<Point> listPoints = new ArrayList<>();
        listPoints.add(new Point(0, 3)); //top point
        listPoints.add(new Point(1, -1)); //right bottom
        listPoints.add(new Point(0, 0));
        listPoints.add(new Point(-1, -1)); //left bottom

        setCartesians(listPoints.toArray(new Point[0]));




    }


    public Bullet(BossEnemy bossEnemy) {

        setTeam(Team.FOE);
        setColor(Color.red);

        //a bullet expires after 20 frames.
        setExpiry(20);
        setRadius(20);


        //everything is relative to the falcon ship that fired the bullet
        setCenter(bossEnemy.getCenter());

        //set the bullet orientation to the falcon (ship) orientation

        setOrientation(bossEnemy.getOrientation());

        final double FIRE_POWER = 25;
        double vectorX =
                Math.cos(Math.toRadians(getOrientation())) * FIRE_POWER;
        double vectorY =
                Math.sin(Math.toRadians(getOrientation())) * FIRE_POWER;

        //fire force: falcon inertia + fire-vector
        setDeltaX(bossEnemy.getDeltaX() + vectorX);
        setDeltaY(bossEnemy.getDeltaY() + vectorY);

        //we have a reference to the falcon passed into the constructor. Let's create some kick-back.
        //fire kick-back on the falcon: inertia - fire-vector / some arbitrary divisor
//        final double KICK_BACK_DIVISOR = 36.0;
//        falcon.setDeltaX(falcon.getDeltaX() - vectorX / KICK_BACK_DIVISOR);
//        falcon.setDeltaY(falcon.getDeltaY() - vectorY / KICK_BACK_DIVISOR);


        //define the points on a cartesian grid
        List<Point> listPoints = new ArrayList<>();
        listPoints.add(new Point(0, 3)); //top point
        listPoints.add(new Point(1, -1)); //right bottom
        listPoints.add(new Point(0, 0));
        listPoints.add(new Point(-1, -1)); //left bottom

        setCartesians(listPoints.toArray(new Point[0]));




    }


    @Override
    public void draw(Graphics g) {
           renderVector(g);
    }
}
