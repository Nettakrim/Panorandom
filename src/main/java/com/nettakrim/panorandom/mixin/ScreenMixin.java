package com.nettakrim.panorandom.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.nettakrim.panorandom.PanorandomClient;
import com.nettakrim.panorandom.RandomPanoramaRenderer;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class ScreenMixin {
    @Shadow @Final protected static CubeMapRenderer PANORAMA_RENDERER;

    @ModifyExpressionValue(method = "<clinit>", at = @At(value = "NEW", target = "(Lnet/minecraft/client/gui/CubeMapRenderer;)Lnet/minecraft/client/gui/RotatingCubeMapRenderer;"))
    private static RotatingCubeMapRenderer replaceRotatingCubemapRenderer(RotatingCubeMapRenderer original) {
        return new RandomPanoramaRenderer(PANORAMA_RENDERER);
    }

    @Inject(method = "init(Lnet/minecraft/client/MinecraftClient;II)V", at = @At("HEAD"))
    private void randomisePanorama(CallbackInfo ci) {
        PanorandomClient.randomisePanorama();
    }
}