package com.nettakrim.panorandom;

import net.minecraft.client.resource.metadata.TextureResourceMetadata;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.ReloadableTexture;
import net.minecraft.client.texture.TextureContents;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class PanorandomCubemapTexture extends ReloadableTexture {
    List<Resource> resources;

    public PanorandomCubemapTexture(Identifier textureId, List<Resource> resources) {
        super(textureId);
        this.resources = resources;
    }

    @Override
    public TextureContents loadContents(ResourceManager resourceManager) throws IOException {
        // TODO:
        //for (int i = 0; i < resources.size(); i++) {
        //    InputStream inputStream = resources.get(i).getInputStream();
        //    NativeImage nativeImage = NativeImage.read(inputStream);
        //    inputStream.close();
        //}

        NativeImage nativeImage = new NativeImage(64, 64, false);
        for (int x = 0; x < 64; x++) {
            for (int y = 0; y < 64; y++) {
                nativeImage.setColor(x, y, x*4*256 + y*4);
            }
        }

        return new TextureContents(nativeImage, new TextureResourceMetadata(true, false));
    }
}
