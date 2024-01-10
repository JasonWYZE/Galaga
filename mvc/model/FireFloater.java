package edu.uchicago.gerber._08final.mvc.model;

import edu.uchicago.gerber._08final.mvc.controller.Game;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FireFloater extends Floater {

	//spawn every 40 seconds
	public static final int SPAWN_NEW_WALL_FLOATER = Game.FRAMES_PER_SECOND * 40;
	public FireFloater() {
		Map<Floater.ImageState, BufferedImage> rasterMap = new HashMap<>();
		rasterMap.put(ImageState.FLOATER, loadGraphic("/imgs/fal/FireLootBox.png")); // Update with actual path
		setRasterMap(rasterMap);
		setExpiry(230);
	}





}
