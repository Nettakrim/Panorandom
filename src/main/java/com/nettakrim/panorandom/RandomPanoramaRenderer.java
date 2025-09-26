package com.nettakrim.panorandom;

import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.RotatingCubeMapRenderer;

import java.util.Arrays;

public class RandomPanoramaRenderer extends RotatingCubeMapRenderer {
    private final CubeMapRenderer original;

    public RandomPanoramaRenderer(CubeMapRenderer cubeMap) {
        super(cubeMap);
        this.original = cubeMap;
    }

    @Override
    public void render(DrawContext context, int width, int height, boolean rotate) {
        this.cubeMap = PanorandomClient.cubeMapRenderer == null ? this.original : PanorandomClient.cubeMapRenderer;
        try {
            super.render(context, width, height, rotate);
        } catch (Exception err) {
            PanorandomClient.LOGGER.error("Error Rendering Panorama: {} {}", err.getMessage(), Arrays.toString(err.getStackTrace()));
        }
    }
}
