package com.syhpear.client.modules.visual;

import com.syhpear.client.SyhpearClient;
import com.syhpear.client.event.EventBus;
import com.syhpear.client.module.Module;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class ZoomModule extends Module {

    private float  zoomFov         = 10.0f;   // FOV while zoomed
    private float  smoothness      = 0.1f;    // Lerp speed
    private boolean scrollToZoom   = true;    // Use scroll wheel to adjust zoom
    private boolean cinematicZoom  = false;   // Cinematic camera while zoomed

    private float  savedFov        = 70.0f;
    private float  currentFov      = 70.0f;
    private boolean isZooming      = false;
    private int     keybindZoom    = GLFW.GLFW_KEY_C;

    public ZoomModule() {
        super("Zoom", "Zoom in like a spyglass with smooth transitions.", Category.VISUAL);
        this.keybind = GLFW.GLFW_KEY_C;
    }

    @Override
    protected void subscribeEvents() {
        SyhpearClient.getEventBus().subscribe(EventBus.KeyPressEvent.class, this::onKey);
        SyhpearClient.getEventBus().subscribe(EventBus.RenderEvent.class, this::onRender);
        SyhpearClient.getEventBus().subscribe(EventBus.MouseScrollEvent.class, this::onScroll);
    }

    @Override
    protected void unsubscribeEvents() {
        SyhpearClient.getEventBus().unsubscribe(EventBus.KeyPressEvent.class, this::onKey);
        SyhpearClient.getEventBus().unsubscribe(EventBus.RenderEvent.class, this::onRender);
        SyhpearClient.getEventBus().unsubscribe(EventBus.MouseScrollEvent.class, this::onScroll);
    }

    private void onKey(EventBus.KeyPressEvent event) {
        if (event.keyCode != keybindZoom) return;
        if (event.action == GLFW.GLFW_PRESS) {
            isZooming = true;
            MinecraftClient mc = MinecraftClient.getInstance();
            savedFov = mc.options.getFov().getValue().floatValue();
        } else if (event.action == GLFW.GLFW_RELEASE) {
            isZooming = false;
        }
    }

    private void onScroll(EventBus.MouseScrollEvent event) {
        if (!isZooming || !scrollToZoom) return;
        zoomFov = Math.max(1f, Math.min(70f, zoomFov - (float)(event.scrollDelta * 2)));
        event.cancel();
    }

    private void onRender(EventBus.RenderEvent event) {
        MinecraftClient mc = MinecraftClient.getInstance();
        float target = isZooming ? zoomFov : savedFov;
        currentFov = lerp(currentFov, target, smoothness);
        // Inject currentFov via GameRendererMixin getFov() override
    }

    private float lerp(float a, float b, float t) { return a + (b - a) * t; }

    public void setZoomFov(float f)            { this.zoomFov = f; }
    public void setSmoothness(float s)         { this.smoothness = s; }
    public void setCinematic(boolean c)        { this.cinematicZoom = c; }
    public float getCurrentFov()              { return currentFov; }
    public boolean isZooming()               { return isZooming; }
}
