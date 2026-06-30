package com.syhpear.client.modules.hud;

import com.syhpear.client.SyhpearClient;
import com.syhpear.client.event.EventBus;
import com.syhpear.client.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.ArrayDeque;
import java.util.Deque;

public class ArmorStatusModule extends Module {

    private int x = 2, y = 100;
    private boolean showDurability = true;
    private boolean colorByDurability = true;

    public ArmorStatusModule() {
        super("Armor Status", "Shows equipped armor pieces and their durability.", Category.HUD);
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
        if (mc.player == null) return;
        var armor = mc.player.getInventory().armor;
        // Render each armor slot with item icon + durability bar
        // Uses DrawContext.drawItem() + DrawContext.drawItemInSlot()
    }
}
