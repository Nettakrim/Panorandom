package com.nettakrim.panorandom;

import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.renderer.CubeMap;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.util.Mth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PanorandomClient implements ClientModInitializer {
	public static final String MOD_ID = "panorandom";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final List<Identifier> PANORAMAS = new ArrayList<>();
	public static final List<Identifier> ENABLED = new ArrayList<>();
	public static final Set<Identifier> DISABLED = new HashSet<>();
	public static Identifier activePanorama;

	public static int rerollMode = 0;
	public static final String[] modes = new String[] {"on_title_screen", "on_screen_change", "on_reload"};

	public static CubeMap cubeMapRenderer;

	public static Data DATA;

	@Override
	public void onInitializeClient() {
		ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new PanoramaResourceLoader());
		DATA = new Data();
	}

	public static void randomisePanorama() {
		if (ENABLED.isEmpty()) {
			setPanorama(null);
		} else {
			Identifier identifier;
			int size = ENABLED.size();

			int attempts = size == 1 ? 100 : 0;
			do {
				identifier = ENABLED.get(Mth.floor(Math.random() * size));
				if (identifier != activePanorama) {
					break;
				}
				attempts++;
			} while (attempts < 100);

			setPanorama(identifier);
		}
	}

	public static void setPanorama(Identifier identifier) {
        PanorandomClient.LOGGER.info("setting panorama to: {}", identifier);
		if (identifier == null) {
			cubeMapRenderer = null;
		} else {
			cubeMapRenderer = new CubeMap(identifier);
		}
		activePanorama = identifier;
	}

	public static String cycleRerollMode() {
		rerollMode = (rerollMode+1)%3;
		return modes[rerollMode];
	}
}