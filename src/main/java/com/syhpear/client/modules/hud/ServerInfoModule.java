package com.syhpear.client.modules.hud;

import com.syhpear.client.SyhpearClient;
import com.syhpear.client.event.EventBus;
import com.syhpear.client.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.ArrayDeque;
import java.util.Deque;

public class ServerInfoModule extends Module {
    private int x = 2, y = 220;

    public ServerInfoModule() {
        super("Server Info", "Shows the current server IP and port.", Category.HUD);
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
        if (mc.getCurrentServerEntry() == null) return;
        String ip = mc.getCurrentServerEntry().address;
        // mc.textRenderer.drawWithShadow(ctx.getMatrices(), "Server: " + ip, x, y, 0xFF00BFFF);
    }
}
