package com.syhpear.client.modules.hud;

import com.syhpear.client.SyhpearClient;
import com.syhpear.client.event.EventBus;
import com.syhpear.client.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.ArrayDeque;
import java.util.Deque;

public class ClockModule extends Module {
    private int x = 2, y = 210;
    private boolean use24h = true;

    public ClockModule() {
        super("Clock", "Displays real-time clock on the HUD.", Category.HUD);
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
        java.time.LocalTime now = java.time.LocalTime.now();
        String time = use24h
            ? String.format("%02d:%02d", now.getHour(), now.getMinute())
            : String.format("%d:%02d %s",
                now.getHour() % 12 == 0 ? 12 : now.getHour() % 12,
                now.getMinute(),
                now.getHour() < 12 ? "AM" : "PM");
        // mc.textRenderer.drawWithShadow(ctx.getMatrices(), time, x, y, 0xFFFFFFFF);
    }
}
