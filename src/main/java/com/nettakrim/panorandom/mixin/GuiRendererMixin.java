package com.nettakrim.panorandom.mixin;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.nettakrim.panorandom.PanorandomClient;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.renderer.CubeMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GuiRenderer.class)
public class GuiRendererMixin {
    @ModifyReceiver(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/CubeMap;render(FF)V"))
    private CubeMap replaceCubemap(CubeMap instance, float rotXInDegrees, float rotYInDegrees) {
        return PanorandomClient.cubeMapRenderer == null ? instance : PanorandomClient.cubeMapRenderer;
    }
}