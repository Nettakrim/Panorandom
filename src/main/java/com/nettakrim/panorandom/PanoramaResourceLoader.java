package com.nettakrim.panorandom;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class PanoramaResourceLoader extends SinglePreparationResourceReloader<Set<Map.Entry<String,List<NativeImage>>>> implements IdentifiableResourceReloadListener {
    public static final String resourceLocation = "textures/gui/title/background";

    @Override
    protected Set<Map.Entry<String,List<NativeImage>>> prepare(ResourceManager manager, Profiler profiler) {
        PanorandomClient.PANORAMAS.clear();
        PanorandomClient.ENABLED.clear();

        Map<String, List<IndexedNativeImage>> unsortedSets = new HashMap<>();

        ResourceFinder resourceFinder = new ResourceFinder(resourceLocation, ".png");
        for (Map.Entry<Identifier, List<Resource>> identifierResourceEntry : resourceFinder.findAllResources(manager).entrySet()) {
            for (Resource resource : identifierResourceEntry.getValue()) {
                String name = resourceFinder.toResourceId(identifierResourceEntry.getKey()).getPath();

                int length = name.length();
                if (length <= 2) continue;

                String id = name.substring(length - 2);
                if (id.charAt(0) != '_' || id.charAt(1) < '0' || id.charAt(1) > '5') continue;

                int faceIndex = name.charAt(1) - '0';
                String panoramaSet = resource.getPackId() + "/" + name.substring(0, length - 2);

                List<IndexedNativeImage> resources = unsortedSets.computeIfAbsent(panoramaSet, k -> new ArrayList<>());
                try (InputStream stream = resource.getInputStream()) {
                    resources.add(new IndexedNativeImage(NativeImage.read(stream), faceIndex));
                } catch (IOException error) {
                    PanorandomClient.LOGGER.warn("Failed to prepare panorama with id '{}': {}", name, error);
                }
            }
        }
        unsortedSets.entrySet().removeIf(panoramaSet -> panoramaSet.getValue().size() != 6);

        Map<String, List<NativeImage>> sortedSets = new HashMap<>();

        for (Map.Entry<String, List<IndexedNativeImage>> entry : unsortedSets.entrySet()) {
            List<NativeImage> sortedResources = entry.getValue().stream().sorted(Comparator.comparingInt(IndexedNativeImage::faceIndex)).map(IndexedNativeImage::resource).toList();
            sortedSets.put(entry.getKey(), sortedResources);
        }

        return sortedSets.entrySet();
    }

    @Override
    protected void apply(Set<Map.Entry<String,List<NativeImage>>> prepared, ResourceManager manager, Profiler profiler) {
        for (Map.Entry<String,List<NativeImage>> panoramaSet : prepared) {
            StringBuilder builder = new StringBuilder(panoramaSet.getKey().toLowerCase(Locale.ROOT));
            for (int i = 0; i < builder.length(); i++) {
                if (!Identifier.isCharValid(builder.charAt(i))) {
                    builder.setCharAt(i, '_');
                }
            }
            Identifier identifier = Identifier.of(PanorandomClient.MOD_ID, builder.toString());

            PanorandomClient.LOGGER.info("registering cubemap textures {}", identifier);
            MinecraftClient.getInstance().getTextureManager().registerTexture(identifier, new PanorandomCubemapTexture(identifier, panoramaSet.getValue()));
            PanorandomClient.PANORAMAS.add(identifier);
        }

        Collections.sort(PanorandomClient.PANORAMAS);
        PanorandomClient.ENABLED.addAll(PanorandomClient.PANORAMAS);
        PanorandomClient.ENABLED.removeIf(PanorandomClient.DISABLED::contains);
        PanorandomClient.randomisePanorama();
    }

    @Override
    public Identifier getFabricId() {
        return Identifier.of(PanorandomClient.MOD_ID, resourceLocation);
    }

    private record IndexedNativeImage(NativeImage resource, int faceIndex) {}
}
