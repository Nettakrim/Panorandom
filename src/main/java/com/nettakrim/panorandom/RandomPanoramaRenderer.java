package com.nettakrim.panorandom;

import com.nettakrim.panorandom.mixin.TextureManagerInvoker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.util.Identifier;

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
            context.drawTexture(RenderPipelines.GUI_TEXTURED, getOverlayTexture(), 0, 0, 0.0F, 0.0F, width, height, 16, 128, 16, 128);
        } else super.render(context, width, height, rotate);
    }

    public static float wrap(float a, float b) {
        return a > b ? a - b : a;
    }

    public Identifier getOverlayTexture() {
        Identifier activeOverlay = getActiveOverlayTexture();
        return (((TextureManagerInvoker)this.client.getTextureManager()).getTextures().containsKey(activeOverlay)) ? activeOverlay : OVERLAY_TEXTURE;
    }

    private Identifier getActiveOverlayTexture() {
        return PanorandomClient.activePanorama.withSuffixedPath("_overlay");
    }
}
