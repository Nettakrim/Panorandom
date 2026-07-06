package com.nettakrim.panorandom;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class ConfigScreen extends OptionsSubScreen {
    protected ConfigScreen(Screen parentScreen) {
        super(parentScreen, Minecraft.getInstance().options, translate("name"));
    }

    @Override
    protected void addOptions() {
        if (this.list != null) {
            List<AbstractWidget> widgets = new ArrayList<>();

            widgets.add(Button.builder(translate(PanorandomClient.modes[PanorandomClient.rerollMode]), button -> button.setMessage(translate(PanorandomClient.cycleRerollMode()))).build());
            widgets.add(Button.builder(translate("random"), button -> PanorandomClient.randomisePanorama()).build());

            for (Identifier identifier : PanorandomClient.PANORAMAS) {
                addPanoramaButton(widgets, identifier);
            }

            this.list.addSmall(widgets);
        }
    }

    protected void addPanoramaButton(List<AbstractWidget> widgets, Identifier identifier) {
        String name = identifier.toString().substring(PanorandomClient.MOD_ID.length()+1);
        boolean enabled = !PanorandomClient.DISABLED.contains(identifier);

        AbstractWidget panoramaButton = Button.builder(Component.literal(name), button -> PanorandomClient.setPanorama(identifier)).build();
        panoramaButton.active = enabled;
        widgets.add(panoramaButton);

        AbstractWidget toggleButton = Button.builder(translate(enabled ? "enabled" : "disabled"), button -> toggle(panoramaButton, button, identifier)).build();
        widgets.add(toggleButton);
    }

    protected void toggle(AbstractWidget panoramaButton, AbstractWidget toggleButton, Identifier identifier) {
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
            PanorandomClient.DISABLED.remove(identifier);
        } else {
            PanorandomClient.ENABLED.remove(identifier);
            if (PanorandomClient.activePanorama.equals(identifier)) {
                PanorandomClient.randomisePanorama();
            }
            PanorandomClient.DISABLED.add(identifier);
        }
    }

    protected static Component translate(String key) {
        return Component.translatable(PanorandomClient.MOD_ID+"."+key);
    }

    @Override
    public void onClose() {
        super.onClose();
        PanorandomClient.DATA.save();
    }
}
