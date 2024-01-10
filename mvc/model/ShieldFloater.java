package edu.uchicago.gerber._08final.mvc.model;

import edu.uchicago.gerber._08final.mvc.controller.Game;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShieldFloater extends Floater {
	//spawn every 25 seconds
	public static final int SPAWN_SHIELD_FLOATER = Game.FRAMES_PER_SECOND * 25;
	public ShieldFloater() {
		Map<Floater.ImageState, BufferedImage> rasterMap = new HashMap<>();
		rasterMap.put(ImageState.FLOATER, loadGraphic("/imgs/fal/ShieldLootBox.png")); // Update with actual path
		setRasterMap(rasterMap);
		setExpiry(260);
	}


}
