package com.nettakrim.panorandom;

import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class PanorandomClient implements ClientModInitializer {
	public static final String MOD_ID = "panorandom";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final List<Identifier> PANORAMAS = new ArrayList<>();
	public static final List<Identifier> ENABLED = new ArrayList<>();
	public static Identifier activePanorama;

	public static int rerollMode = 0;
	public static final String[] modes = new String[] {"on_title_screen", "on_screen_change", "on_reload"};

	public static CubeMapRenderer cubeMapRenderer;

	@Override
	public void onInitializeClient() {
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new PanoramaResourceLoader());
	}

	public static void randomisePanorama() {
		if (ENABLED.isEmpty()) {
			setPanorama(null);
		} else {
			Identifier identifier;
			int size = ENABLED.size();

			int attempts = size == 1 ? 100 : 0;
			do {
				identifier = ENABLED.get(MathHelper.floor(Math.random() * size));
				if (identifier != activePanorama) {
					break;
				}
				attempts++;
			} while (attempts < 100);

			setPanorama(identifier);
		}
	}

	public static void setPanorama(Identifier identifier) {
		if (identifier == null) {
			cubeMapRenderer = null;
		} else {
			cubeMapRenderer = new CubeMapRenderer(identifier);
		}
		activePanorama = identifier;
	}

	public static String cycleRerollMode() {
		rerollMode = (rerollMode+1)%3;
		return modes[rerollMode];
	}
}