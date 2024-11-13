package com.nettakrim.panorandom;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.io.InputStream;
import java.util.*;

public class PanoramaResourceLoader extends SinglePreparationResourceReloader<Set<Map.Entry<String,List<Resource>>>> implements IdentifiableResourceReloadListener {
    public static final String resourceLocation = "textures/gui/title/background";

    @Override
    protected Set<Map.Entry<String,List<Resource>>> prepare(ResourceManager manager, Profiler profiler) {
        PanorandomClient.PANORAMAS.clear();

        Map<String, List<Resource>> panoramaSets = new HashMap<>();

        ResourceFinder resourceFinder = new ResourceFinder(resourceLocation, ".png");
        for (Map.Entry<Identifier, List<Resource>> identifierResourceEntry : resourceFinder.findAllResources(manager).entrySet()) {
            for (Resource resource : identifierResourceEntry.getValue()) {
                String name = resourceFinder.toResourceId(identifierResourceEntry.getKey()).getPath();

                int length = name.length();
                if (length <= 2) continue;

                String id = name.substring(length - 2);
                if (id.charAt(0) != '_' || id.charAt(1) < '0' || id.charAt(1) > '5') continue;

                String panoramaSet = resource.getPackId() + "/" + name.substring(0, length - 2);

                List<Resource> resources = panoramaSets.computeIfAbsent(panoramaSet, k -> new ArrayList<>());
                resources.add(resource);
            }
        }
        panoramaSets.entrySet().removeIf(panoramaSet -> panoramaSet.getValue().size() != 6);

        return panoramaSets.entrySet();
    }

    @Override
    protected void apply(Set<Map.Entry<String,List<Resource>>> prepared, ResourceManager manager, Profiler profiler) {
        TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
        for (Map.Entry<String,List<Resource>> panoramaSet : prepared) {
            StringBuilder builder = new StringBuilder(panoramaSet.getKey().toLowerCase(Locale.ROOT));
            for (int i = 0; i < builder.length(); i++) {
                if (!Identifier.isCharValid(builder.charAt(i))) {
                    builder.setCharAt(i, '_');
                }
            }
            String id = builder.toString();
            PanorandomClient.PANORAMAS.add(Identifier.of(PanorandomClient.MOD_ID, id));

            id += "_";
            List<Resource> resources = panoramaSet.getValue();
            for (int i = 0; i < resources.size(); i++) {
                registerNativeBackedImage(textureManager, id+i+".png", resources.get(i));
            }
        }
    }

    private void registerNativeBackedImage(TextureManager textureManager, String id, Resource resource) {
        try {
            InputStream inputStream = resource.getInputStream();
            NativeImage nativeImage = NativeImage.read(inputStream);
            inputStream.close();
            NativeImageBackedTexture texture = new NativeImageBackedTexture(nativeImage);

            textureManager.registerTexture(Identifier.of(PanorandomClient.MOD_ID, id), texture);
        } catch (Throwable err) {
            PanorandomClient.LOGGER.info("ERROR CREATING PANORAMA IMAGE:\n"+err);
        }
    }

    @Override
    public Identifier getFabricId() {
        return Identifier.of(PanorandomClient.MOD_ID, resourceLocation);
    }
}
