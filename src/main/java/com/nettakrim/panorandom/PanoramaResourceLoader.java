package com.nettakrim.panorandom;

import com.nettakrim.panorandom.mixin.TextureManagerInvoker;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.List;

public class PanoramaResourceLoader extends SinglePreparationResourceReloader<Set<Map.Entry<String,List<PanoramaResourceLoader.Image>>>> implements IdentifiableResourceReloadListener {
    public static final String resourceLocation = "textures/gui/title/background";

    @Override
    protected Set<Map.Entry<String,List<Image>>> prepare(ResourceManager manager, Profiler profiler) {
        try {
            Map<String, List<IndexedImage>> unsortedSets = new HashMap<>();
            Map<String, Image> overlaySets = new HashMap<>();

            ResourceFinder resourceFinder = new ResourceFinder(resourceLocation, ".png");
            for (Map.Entry<Identifier, List<Resource>> identifierResourceEntry : resourceFinder.findAllResources(manager).entrySet()) {
                for (Resource resource : identifierResourceEntry.getValue()) {
                    String name = resourceFinder.toResourceId(identifierResourceEntry.getKey()).getPath();

                    int length = name.length();
                    if (length <= 2) continue;

                    if (name.endsWith("_overlay")) {
                        try (InputStream stream = resource.getInputStream()) {
                            overlaySets.put(resource.getPackId() + "/" + name.substring(0, length - "_overlay".length()), new Image(stream.readAllBytes()));
                        } catch (IOException error) {
                            PanorandomClient.LOGGER.error("Failed to prepare panorama overlay with id '{}': {}", name, error);
                        }
                        continue;
                    }

                    String id = name.substring(length - 2);
                    if (id.charAt(0) != '_' || id.charAt(1) < '0' || id.charAt(1) > '5') continue;

                    int faceIndex = name.charAt(1) - '0';
                    String panoramaSet = resource.getPackId() + "/" + name.substring(0, length - 2);

                    List<IndexedImage> resources = unsortedSets.computeIfAbsent(panoramaSet, k -> new ArrayList<>());
                    try (InputStream stream = resource.getInputStream()) {
                        resources.add(new IndexedImage(new Image(stream.readAllBytes()), faceIndex));
                    } catch (IOException error) {
                        PanorandomClient.LOGGER.error("Failed to prepare panorama with id '{}': {}", name, error);
                    }
                }
            }
            unsortedSets.entrySet().removeIf(panoramaSet -> panoramaSet.getValue().size() != 6);

            Map<String, List<Image>> sortedSets = new HashMap<>();

            for (Map.Entry<String, List<IndexedImage>> entry : unsortedSets.entrySet()) {
                List<Image> sortedResources = entry.getValue().stream().sorted(Comparator.comparingInt(IndexedImage::faceIndex)).map(IndexedImage::resource).toList();

                Image overlay = overlaySets.get(entry.getKey());
                if (overlay != null) {
                    List<Image> extended = new ArrayList<>(sortedResources);
                    extended.add(overlay);
                    sortedResources = extended;
                }

                sortedSets.put(entry.getKey(), sortedResources);
            }

            return sortedSets.entrySet();
        } catch (Exception error) {
            PanorandomClient.LOGGER.error("Failed to prepare panorandom: ", error);
        }
        return null;
    }

    private void clear() {
        for (Identifier id : PanorandomClient.PANORAMAS) {
            TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
            if (((TextureManagerInvoker)textureManager).getTextures().containsKey(id)) textureManager.destroyTexture(id);
            if (((TextureManagerInvoker)textureManager).getTextures().containsKey(id.withSuffixedPath("_overlay"))) textureManager.destroyTexture(id.withSuffixedPath("_overlay"));
        }
        PanorandomClient.PANORAMAS.clear();
        PanorandomClient.ENABLED.clear();
    }

    @Override
    protected void apply(Set<Map.Entry<String,List<Image>>> prepared, ResourceManager manager, Profiler profiler) {
        try {
            if (prepared == null) return;
            clear();
            for (Map.Entry<String,List<Image>> panoramaSet : prepared) {
                StringBuilder builder = new StringBuilder(panoramaSet.getKey().toLowerCase(Locale.ROOT));
                for (int i = 0; i < builder.length(); i++) {
                    if (!Identifier.isCharValid(builder.charAt(i))) builder.setCharAt(i, '_');
                }
                Identifier identifier = Identifier.of(PanorandomClient.MOD_ID, builder.toString());

                PanorandomClient.LOGGER.info("registering cubemap texture {}", identifier);
                List<Image> resources = panoramaSet.getValue();
                MinecraftClient.getInstance().getTextureManager().registerTexture(identifier, new PanorandomCubemapTexture(identifier, new PanorandomCubemapTexture.CubemapImages(resources.get(0).toNativeImage(), resources.get(1).toNativeImage(), resources.get(2).toNativeImage(), resources.get(3).toNativeImage(), resources.get(4).toNativeImage(), resources.get(5).toNativeImage())));
                Identifier overlayId = identifier.withSuffixedPath("_overlay");
                if (resources.size() == 7) {
                    PanorandomClient.LOGGER.info("registering overlay texture {}", overlayId);
                    MinecraftClient.getInstance().getTextureManager().registerTexture(overlayId, new NativeImageBackedTexture(overlayId::toString, resources.get(6).toNativeImage()));
                }
                PanorandomClient.PANORAMAS.add(identifier);
            }

            Collections.sort(PanorandomClient.PANORAMAS);
            PanorandomClient.ENABLED.addAll(PanorandomClient.PANORAMAS);
            PanorandomClient.ENABLED.removeIf(PanorandomClient.DISABLED::contains);
            PanorandomClient.randomisePanorama();
        } catch (Exception error) {
            PanorandomClient.LOGGER.error("Failed to apply panorandom: ", error);
        }
    }

    @Override
    public Identifier getFabricId() {
        return Identifier.of(PanorandomClient.MOD_ID, resourceLocation);
    }

    private record IndexedImage(Image resource, int faceIndex) {}

    public record Image(byte[] data) {
        public NativeImage toNativeImage() throws IOException {
            try (ByteArrayInputStream in = new ByteArrayInputStream(data)) {
                return NativeImage.read(in);
            }
        }
    }
}
