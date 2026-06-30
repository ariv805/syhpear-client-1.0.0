package com.syhpear.client.modules.visual;

import com.syhpear.client.SyhpearClient;
import com.syhpear.client.event.EventBus;
import com.syhpear.client.module.Module;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class MotionBlurModule extends Module {

    private float blurStrength = 0.5f;  // 0.0 = none, 1.0 = max
    private boolean onlyMoving = true;  // Only blur when camera moves

    public MotionBlurModule() {
        super("Motion Blur", "Adds smooth motion blur effect to camera movement.", Category.VISUAL);
    }

    @Override
    public void onEnable() {
        // Loads a post-processing shader via GameRenderer
        // mc.gameRenderer.loadPostProcessor(POST_SHADER_ID);
    }

    @Override
    public void onDisable() {
        // mc.gameRenderer.disablePostProcessor();
    }

    public void setBlurStrength(float s) { this.blurStrength = Math.max(0f, Math.min(1f, s)); }
    public float getBlurStrength()       { return blurStrength; }
}
