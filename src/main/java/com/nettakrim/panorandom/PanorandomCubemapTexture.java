package com.nettakrim.panorandom;

import com.mojang.blaze3d.platform.NativeImage;
import java.io.IOException;
import net.minecraft.client.renderer.texture.CubeMapTexture;
import net.minecraft.client.renderer.texture.MipmapStrategy;
import net.minecraft.client.renderer.texture.TextureContents;
import net.minecraft.client.resources.metadata.texture.TextureMetadataSection;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;

public class PanorandomCubemapTexture extends CubeMapTexture {
    private final Identifier id;
    private final CubemapImages resources;

    public PanorandomCubemapTexture(Identifier textureId, CubemapImages resources) {
        super(textureId);
        this.id = textureId;
        this.resources = resources;
    }

    public TextureContents loadContents(ResourceManager resourceManager) throws IOException {
        // cubemap texture order: 1, 3, 5, 4, 0, 2
        NativeImage face = this.resources.img1();
        int width = face.getWidth();
        int height = face.getHeight();
        NativeImage nativeImage = new NativeImage(width, height * 6, false);
        face.copyRect(nativeImage, 0, 0, 0, 0, width, height, false, true);

        copyFace(nativeImage, this.resources.img3(), 1, width, height);
        copyFace(nativeImage, this.resources.img5(), 2, width, height);
        copyFace(nativeImage, this.resources.img4(), 3, width, height);
        copyFace(nativeImage, this.resources.img0(), 4, width, height);
        copyFace(nativeImage, this.resources.img2(), 5, width, height);

        return new TextureContents(nativeImage, new TextureMetadataSection(true, false, MipmapStrategy.AUTO, 0f));
    }

    private void copyFace(NativeImage nativeImage, NativeImage face, int index, int width, int height) throws IOException {
        if (face.getWidth() != width || face.getHeight() != height) throw new IOException("Image dimensions of cubemap '" + this.id + "' sides do not match: part 0 is " + width + "x" + height + ", but part " + index + " is " + face.getWidth() + "x" + face.getHeight());
        face.copyRect(nativeImage, 0, 0, 0, index * height, width, height, false, true);
    }
    
    public record CubemapImages(NativeImage img0, NativeImage img1, NativeImage img2, NativeImage img3, NativeImage img4, NativeImage img5) {}
}
