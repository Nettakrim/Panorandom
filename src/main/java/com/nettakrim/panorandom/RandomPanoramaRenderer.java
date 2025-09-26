package com.nettakrim.panorandom;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.RotatingCubeMapRenderer;

public class RandomPanoramaRenderer extends RotatingCubeMapRenderer {
    public float pitch;
    private final MinecraftClient client;

    public RandomPanoramaRenderer(CubeMapRenderer cubeMap) {
        super(cubeMap);
        this.client = MinecraftClient.getInstance();
    }

    public void render(DrawContext context, int width, int height, boolean rotate) {
        if (PanorandomClient.cubeMapRenderer != null) {
            if (rotate) this.pitch = wrap(this.pitch + (float)((double)this.client.getRenderTickCounter().getFixedDeltaTicks() * this.client.options.getPanoramaSpeed().getValue()) * 0.1F, 360.0F);
            PanorandomClient.cubeMapRenderer.draw(this.client, 10.0F, -this.pitch);
            // TODO: use the active panorama's overlay, default to vanilla if not found.
            context.drawTexture(RenderPipelines.GUI_TEXTURED, OVERLAY_TEXTURE, 0, 0, 0.0F, 0.0F, width, height, 16, 128, 16, 128);
        } else super.render(context, width, height, rotate);
    }

    public static float wrap(float a, float b) {
        return a > b ? a - b : a;
    }
}
