package com.syhpear.client.modules.hud;

import com.syhpear.client.SyhpearClient;
import com.syhpear.client.event.EventBus;
import com.syhpear.client.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.ArrayDeque;
import java.util.Deque;

public class CpsCounterModule extends Module {

    private static final int WINDOW_MS = 1000;
    private final Deque<Long> leftClicks  = new ArrayDeque<>();
    private final Deque<Long> rightClicks = new ArrayDeque<>();
    private int x = 2, y = 22;

    public CpsCounterModule() {
        super("CPS Counter", "Counts left and right clicks per second.", Category.HUD);
    }

    public void registerLeftClick()  { leftClicks.add(System.currentTimeMillis()); }
    public void registerRightClick() { rightClicks.add(System.currentTimeMillis()); }

    public int getLeftCps() {
        long now = System.currentTimeMillis();
        leftClicks.removeIf(t -> now - t > WINDOW_MS);
        return leftClicks.size();
    }

    public int getRightCps() {
        long now = System.currentTimeMillis();
        rightClicks.removeIf(t -> now - t > WINDOW_MS);
        return rightClicks.size();
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
        String text = getLeftCps() + " CPS | R: " + getRightCps();
        // mc.textRenderer.drawWithShadow(ctx.getMatrices(), text, x, y, 0xFF00BFFF);
    }
}
