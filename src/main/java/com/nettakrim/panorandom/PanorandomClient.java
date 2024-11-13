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

	public static CubeMapRenderer cubeMapRenderer;

	@Override
	public void onInitializeClient() {
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new PanoramaResourceLoader());
	}

	public static void randomisePanorama() {
		if (ENABLED.isEmpty()) {
			setPanorama(null);
		} else {
			setPanorama(ENABLED.get(MathHelper.floor(Math.random() * ENABLED.size())));
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
}