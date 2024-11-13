package com.nettakrim.panorandom.mixin;

import com.nettakrim.panorandom.PanorandomClient;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
    @Inject(method = "<init>()V", at = @At("TAIL"))
    private void randomisePanorama(CallbackInfo ci) {
        if (PanorandomClient.rerollMode == 0) {
            PanorandomClient.randomisePanorama();
        }
    }
}
