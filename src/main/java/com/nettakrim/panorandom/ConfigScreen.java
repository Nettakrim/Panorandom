package com.nettakrim.panorandom;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class ConfigScreen extends GameOptionsScreen {
    protected ConfigScreen(Screen parentScreen) {
        super(parentScreen, MinecraftClient.getInstance().options, translate("name"));
    }

    @Override
    protected void addOptions() {
        if (this.body != null) {
            List<ClickableWidget> widgets = new ArrayList<>();

            widgets.add(ButtonWidget.builder(Text.literal("..."), button -> {}).build());
            widgets.add(ButtonWidget.builder(translate("random"), button -> PanorandomClient.randomisePanorama()).build());

            for (Identifier identifier : PanorandomClient.PANORAMAS) {
                addPanoramaButton(widgets, identifier);
            }

            this.body.addAll(widgets);
        }
    }

    protected void addPanoramaButton(List<ClickableWidget> widgets, Identifier identifier) {
        String name = identifier.toString().substring(PanorandomClient.MOD_ID.length()+1);
        boolean enabled = PanorandomClient.ENABLED.contains(identifier);

        ClickableWidget panoramaButton = ButtonWidget.builder(Text.literal(name), button -> PanorandomClient.setPanorama(identifier)).build();
        panoramaButton.active = enabled;
        widgets.add(panoramaButton);

        ClickableWidget toggleButton = ButtonWidget.builder(translate(enabled ? "enabled" : "disabled"), button -> toggle(panoramaButton, button, identifier)).build();
        widgets.add(toggleButton);
    }

    protected void toggle(ClickableWidget panoramaButton, ClickableWidget toggleButton, Identifier identifier) {
        boolean enabled = !panoramaButton.active;
        panoramaButton.active = enabled;
        toggleButton.setMessage(translate(enabled ? "enabled" : "disabled"));

        if (enabled) {
            if (!PanorandomClient.ENABLED.contains(identifier)) {
                PanorandomClient.ENABLED.add(identifier);
                if (PanorandomClient.ENABLED.size() == 1) {
                    PanorandomClient.setPanorama(identifier);
                }
            }
        } else {
            PanorandomClient.ENABLED.remove(identifier);
            if (PanorandomClient.activePanorama.equals(identifier)) {
                PanorandomClient.randomisePanorama();
            }
        }
    }

    protected static Text translate(String key) {
        return Text.translatable(PanorandomClient.MOD_ID+"."+key);
    }
}
