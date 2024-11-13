package com.nettakrim.panorandom;

import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.RotatingCubeMapRenderer;

public class RandomPanoramaRenderer extends RotatingCubeMapRenderer {
    private final CubeMapRenderer original;

    public RandomPanoramaRenderer(CubeMapRenderer cubeMap) {
        super(cubeMap);
        this.original = cubeMap;
    }

    @Override
    public void render(DrawContext context, int width, int height, float alpha, float tickDelta) {
        cubeMap = PanorandomClient.cubeMapRenderer == null ? original : PanorandomClient.cubeMapRenderer;
        super.render(context, width, height, alpha, tickDelta);
    }
}
