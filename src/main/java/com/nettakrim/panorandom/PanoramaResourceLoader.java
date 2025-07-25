package com.nettakrim.panorandom;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.util.*;

public class PanoramaResourceLoader extends SinglePreparationResourceReloader<Set<Map.Entry<String,List<Resource>>>> implements IdentifiableResourceReloadListener {
    public static final String resourceLocation = "textures/gui/title/background";

    @Override
    protected Set<Map.Entry<String,List<Resource>>> prepare(ResourceManager manager, Profiler profiler) {
        PanorandomClient.PANORAMAS.clear();
        PanorandomClient.ENABLED.clear();

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
        for (Map.Entry<String,List<Resource>> panoramaSet : prepared) {
            StringBuilder builder = new StringBuilder(panoramaSet.getKey().toLowerCase(Locale.ROOT));
            for (int i = 0; i < builder.length(); i++) {
                if (!Identifier.isCharValid(builder.charAt(i))) {
                    builder.setCharAt(i, '_');
                }
            }
            Identifier identifier = Identifier.of(PanorandomClient.MOD_ID, builder.toString());

            PanorandomClient.LOGGER.info("registering cubemap textures "+identifier);
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
}
