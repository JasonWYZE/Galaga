package edu.uchicago.gerber._08final.mvc.model;

import edu.uchicago.gerber._08final.mvc.controller.Game;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class NukeFloater extends Floater {

	public static final int SPAWN_NUKE_FLOATER = Game.FRAMES_PER_SECOND * 5;
	public NukeFloater() {
		Map<Floater.ImageState, BufferedImage> rasterMap = new HashMap<>();
		rasterMap.put(ImageState.FLOATER, loadGraphic("/imgs/fal/SpeedLootBox.png")); // Update with actual path
		setRasterMap(rasterMap);
		setExpiry(120);


	}



}
