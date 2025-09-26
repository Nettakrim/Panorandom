package com.nettakrim.panorandom;

import net.minecraft.client.resource.metadata.TextureResourceMetadata;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.ReloadableTexture;
import net.minecraft.client.texture.TextureContents;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public class PanorandomOverlayTexture extends NativeImageBackedTexture {
    public final Identifier id;
    private final NativeImage resource;

    public PanorandomOverlayTexture(Identifier textureId, NativeImage resource) {
        super(textureId::toString, resource);
        this.id = textureId;
        this.resource = resource;
    }

    public TextureContents loadContents(ResourceManager resourceManager) {
        return new TextureContents(this.resource, new TextureResourceMetadata(true, true));
    }
}
