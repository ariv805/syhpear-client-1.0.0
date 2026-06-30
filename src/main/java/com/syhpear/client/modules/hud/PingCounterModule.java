package com.syhpear.client.modules.hud;

import com.syhpear.client.SyhpearClient;
import com.syhpear.client.event.EventBus;
import com.syhpear.client.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.ArrayDeque;
import java.util.Deque;

public class PingCounterModule extends Module {

    private int x = 2, y = 12;

    public PingCounterModule() {
        super("Ping Counter", "Shows your current server ping in ms.", Category.HUD);
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
        if (mc.getNetworkHandler() == null || mc.player == null) return;

        var entry = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
        if (entry == null) return;

        int ping = entry.getLatency();
        int color = ping < 50  ? 0xFF00FF88
                  : ping < 100 ? 0xFF00BFFF
                  : ping < 200 ? 0xFFFFAA00
                               : 0xFFFF4444;

        // mc.textRenderer.drawWithShadow(ctx.getMatrices(), ping + "ms", x, y, color);
    }

    public void setPosition(int x, int y) { this.x = x; this.y = y; }
}
