package edu.uchicago.gerber._08final.mvc.model;

import edu.uchicago.gerber._08final.mvc.controller.CommandCenter;
import edu.uchicago.gerber._08final.mvc.controller.Utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Floater extends Sprite {


    public enum ImageState {

        FLOATER,
    }

    public Floater() {

        setTeam(Team.FLOATER);

        //default values, all of which can be overridden in the extending concrete classes
        setExpiry(250);

        Map<Floater.ImageState, BufferedImage> rasterMap = new HashMap<>();
        rasterMap.put(ImageState.FLOATER, loadGraphic("/imgs/fal/FireLootBox.png")); // Path to enemy image

        setRasterMap(rasterMap);

//        setColor(Color.WHITE);
        setRadius(25);
        //set random DeltaX
        setDeltaX(somePosNegValue(10));
        //set random DeltaY
        setDeltaY(somePosNegValue(10));
        //set random spin
        setSpin(somePosNegValue(10));
    }

    @Override
    public void draw(Graphics g) {
        Floater.ImageState imageState= ImageState.FLOATER;

        renderRaster((Graphics2D) g, getRasterMap().get(imageState));
    }

}
