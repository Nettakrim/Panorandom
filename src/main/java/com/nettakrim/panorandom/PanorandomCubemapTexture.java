package com.nettakrim.panorandom;

import net.minecraft.client.resource.metadata.TextureResourceMetadata;
import net.minecraft.client.texture.CubemapTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.TextureContents;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class PanorandomCubemapTexture extends CubemapTexture {
    private final List<Resource> resources;

    public PanorandomCubemapTexture(Identifier textureId, List<Resource> resources) {
        super(textureId);
        this.resources = resources;
    }

    public TextureContents loadContents(ResourceManager resourceManager) throws IOException {
        // cubemap texture order: 1, 3, 5, 4, 0, 2
        try (InputStream stream = this.resources.get(1).getInputStream()) {
            NativeImage face = NativeImage.read(stream);
            int width = face.getWidth();
            int height = face.getHeight();
            NativeImage nativeImage = new NativeImage(width, height * 6, false);
            face.copyRect(nativeImage, 0, 0, 0, 0, width, height, false, true);

            copyFace(nativeImage, this.resources.get(3), 1, width, height);
            copyFace(nativeImage, this.resources.get(5), 2, width, height);
            copyFace(nativeImage, this.resources.get(4), 3, width, height);
            copyFace(nativeImage, this.resources.get(0), 4, width, height);
            copyFace(nativeImage, this.resources.get(2), 5, width, height);

            return new TextureContents(nativeImage, new TextureResourceMetadata(true, false));
        }
    }

    private void copyFace(NativeImage nativeImage, Resource resource, int index, int width, int height) throws IOException {
        try (InputStream stream = resource.getInputStream()) {
            NativeImage face = NativeImage.read(stream);
            if (face.getWidth() != width || face.getHeight() != height) throw new IOException("Image dimensions of cubemap '" + resource + "' sides do not match: part 0 is " + width + "x" + height + ", but part " + index + " is " + face.getWidth() + "x" + face.getHeight());
            face.copyRect(nativeImage, 0, 0, 0, index * height, width, height, false, true);
        }
    }
}
