package com.syhpear.client.modules.visual;

import com.syhpear.client.SyhpearClient;
import com.syhpear.client.event.EventBus;
import com.syhpear.client.module.Module;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class ItemPhysicsModule extends Module {

    private boolean rotate    = true;
    private boolean bounce    = true;
    private float   gravity   = 0.04f;
    private boolean onlyOwn   = false;

    public ItemPhysicsModule() {
        super("Item Physics", "Makes dropped items rotate and bounce with physics.", Category.VISUAL);
    }

    @Override
    protected void subscribeEvents() {
        SyhpearClient.getEventBus().subscribe(EventBus.RenderEvent.class, this::onRender);
    }

    @Override
    protected void unsubscribeEvents() {
        SyhpearClient.getEventBus().unsubscribe(EventBus.RenderEvent.class, this::onRender);
    }

    private void onRender(EventBus.RenderEvent event) {
        // Override applied via EntityRendererMixin for ItemEntity
        // Applies rotation matrix based on entity age + bounce sine wave
    }

    public void setRotate(boolean r)  { this.rotate = r; }
    public void setBounce(boolean b)  { this.bounce = b; }
    public void setGravity(float g)   { this.gravity = g; }
}
