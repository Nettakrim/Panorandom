package com.nettakrim.panorandom.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.nettakrim.panorandom.RandomPanoramaRenderer;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Shadow @Final protected CubeMapRenderer panoramaRenderer;

    @ModifyExpressionValue(method = "<init>", at = @At(value = "NEW", target = "(Lnet/minecraft/client/gui/CubeMapRenderer;)Lnet/minecraft/client/gui/RotatingCubeMapRenderer;"))
    private RotatingCubeMapRenderer replaceRotatingCubemapRenderer(RotatingCubeMapRenderer original) {
        return new RandomPanoramaRenderer(panoramaRenderer);
    }
}