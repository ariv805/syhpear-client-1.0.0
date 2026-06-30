package com.syhpear.client.modules.hud;

import com.syhpear.client.SyhpearClient;
import com.syhpear.client.event.EventBus;
import com.syhpear.client.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.ArrayDeque;
import java.util.Deque;

public class FpsCounterModule extends Module {

    private int   x = 2, y = 2;
    private int   color      = 0xFF00BFFF; // Neon Blue
    private int   bgColor    = 0x80000000; // Semi-transparent black
    private float scale      = 1.0f;
    private boolean showBg   = true;

    public FpsCounterModule() {
        super("FPS Counter", "Displays current FPS on screen.", Category.HUD);
    }

    @Override
    protected void subscribeEvents() {
        SyhpearClient.getEventBus().subscribe(EventBus.HudRenderEvent.class, this::onHudRender);
    }

    @Override
    protected void unsubscribeEvents() {
        SyhpearClient.getEventBus().unsubscribe(EventBus.HudRenderEvent.class, this::onHudRender);
    }

    private void onHudRender(EventBus.HudRenderEvent event) {
        MinecraftClient mc = MinecraftClient.getInstance();
        int fps = mc.getCurrentFps();

        // Color-code by FPS
        int displayColor = fps >= 120 ? 0xFF00FF88   // Green  — high FPS
                         : fps >= 60  ? 0xFF00BFFF   // Blue   — ok
                         : fps >= 30  ? 0xFFFFAA00   // Yellow — low
                                      : 0xFFFF4444;  // Red    — very low

        // Render: DrawContext injected via mixin
        // mc.textRenderer.drawWithShadow(ctx.getMatrices(), fps + " FPS", x, y, displayColor);
    }

    public void setPosition(int x, int y) { this.x = x; this.y = y; }
    public void setColor(int color)        { this.color = color; }
}
