package com.syhpear.client.modules.hud;

import com.syhpear.client.SyhpearClient;
import com.syhpear.client.event.EventBus;
import com.syhpear.client.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.ArrayDeque;
import java.util.Deque;

public class CoordinatesModule extends Module {

    private int x = 2, y = 32;
    private boolean showBiome     = true;
    private boolean showDirection = true;
    private boolean showChunk     = false;

    public CoordinatesModule() {
        super("Coordinates", "Shows XYZ position, direction, and biome.", Category.HUD);
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
        ClientPlayerEntity player = mc.player;
        if (player == null) return;

        int px = (int) player.getX();
        int py = (int) player.getY();
        int pz = (int) player.getZ();

        String coordText = "XYZ: " + px + " / " + py + " / " + pz;
        // mc.textRenderer.drawWithShadow(ctx.getMatrices(), coordText, x, y, 0xFF00BFFF);

        if (showDirection) {
            float yaw    = player.getYaw() % 360;
            if (yaw < 0) yaw += 360;
            String dir = getDirection(yaw);
            // mc.textRenderer.drawWithShadow(ctx.getMatrices(), "Facing: " + dir, x, y+10, 0xFFFFFFFF);
        }
    }

    private String getDirection(float yaw) {
        if (yaw < 22.5 || yaw >= 337.5) return "South";
        if (yaw < 67.5)  return "South-West";
        if (yaw < 112.5) return "West";
        if (yaw < 157.5) return "North-West";
        if (yaw < 202.5) return "North";
        if (yaw < 247.5) return "North-East";
        if (yaw < 292.5) return "East";
        return "South-East";
    }
}
