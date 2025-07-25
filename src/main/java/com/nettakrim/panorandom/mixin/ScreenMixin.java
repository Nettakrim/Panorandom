package com.nettakrim.panorandom.mixin;

import com.nettakrim.panorandom.PanorandomClient;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class ScreenMixin {
    @Inject(method = "init(Lnet/minecraft/client/MinecraftClient;II)V", at = @At("TAIL"))
    private void randomisePanorama(CallbackInfo ci) {
        if (PanorandomClient.rerollMode == 1) {
            PanorandomClient.randomisePanorama();
        }
    }
}