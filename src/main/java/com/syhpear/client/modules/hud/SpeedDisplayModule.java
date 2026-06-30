package com.syhpear.client.modules.hud;

import com.syhpear.client.SyhpearClient;
import com.syhpear.client.event.EventBus;
import com.syhpear.client.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.ArrayDeque;
import java.util.Deque;

public class SpeedDisplayModule extends Module {
    private int x = 2, y = 200;

    public SpeedDisplayModule() {
        super("Speed Display", "Shows player movement speed in blocks per second.", Category.HUD);
    }

    @Override
    protected void subscribeEvents() {
        SyhpearClient.getEventBus().subscribe(EventBus.HudRenderEvent.class, this::onHudRender);
    }

    @Override
    protected void unsubscribeEvents() {
        SyhpearClient.getEventBus().unsubscribe(EventBus.HudRenderEvent.class, this::onHudRender);
    }

    private double lastX, lastZ;
    private double bps = 0;

    private void onHudRender(EventBus.HudRenderEvent event) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        double dx = mc.player.getX() - lastX;
        double dz = mc.player.getZ() - lastZ;
        bps = Math.sqrt(dx * dx + dz * dz) * 20; // ticks to seconds

        lastX = mc.player.getX();
        lastZ = mc.player.getZ();

        String text = String.format("Speed: %.2f bps", bps);
        // mc.textRenderer.drawWithShadow(ctx.getMatrices(), text, x, y, 0xFF00BFFF);
    }
}
