package com.syhpear.client.modules.visual;

import com.syhpear.client.SyhpearClient;
import com.syhpear.client.event.EventBus;
import com.syhpear.client.module.Module;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class FullbrightModule extends Module {

    private float savedGamma = 1.0f;
    private float gammaValue = 16.0f;    // Very high gamma = Fullbright

    public FullbrightModule() {
        super("Fullbright", "Removes all darkness — see everything clearly without torches.", Category.VISUAL);
        this.keybind = GLFW.GLFW_KEY_F;  // Default: F key
    }

    @Override
    public void onEnable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        savedGamma = mc.options.getGamma().getValue().floatValue();
        mc.options.getGamma().setValue((double) gammaValue);
    }

    @Override
    public void onDisable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        mc.options.getGamma().setValue((double) savedGamma);
    }

    public void setGammaValue(float v) {
        this.gammaValue = v;
        if (enabled) MinecraftClient.getInstance().options.getGamma().setValue((double)v);
    }

    public float getGammaValue() { return gammaValue; }
}
