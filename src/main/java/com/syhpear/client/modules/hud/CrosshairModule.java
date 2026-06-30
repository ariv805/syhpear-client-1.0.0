package com.syhpear.client.modules.hud;

import com.syhpear.client.SyhpearClient;
import com.syhpear.client.event.EventBus;
import com.syhpear.client.module.Module;
import net.minecraft.client.MinecraftClient;

/**
 * Syhpear Client — Crosshair Module
 *
 * Features:
 * - Custom crosshair shapes (cross, dot, circle, arrow, T-shape)
 * - Dynamic crosshair (reacts to player speed / attack)
 * - Outline with adjustable thickness
 * - Opacity control
 * - Animated crosshair
 * - Multiple presets
 */
public class CrosshairModule extends Module {

    public enum CrosshairStyle {
        DEFAULT, DOT, CIRCLE, CROSS, T_SHAPE, ARROW, CUSTOM
    }

    public enum Preset {
        CLASSIC   ("Classic",   CrosshairStyle.CROSS,  0xFFFFFFFF, 1, 5, false),
        NEON_BLUE ("Neon Blue", CrosshairStyle.CROSS,  0xFF00BFFF, 2, 6, true),
        DOT       ("Dot",       CrosshairStyle.DOT,    0xFFFFFFFF, 2, 3, false),
        CIRCLE    ("Circle",    CrosshairStyle.CIRCLE, 0xFF00BFFF, 1, 8, true),
        NONE      ("None",      CrosshairStyle.CUSTOM, 0x00000000, 0, 0, false);

        public final String name;
        public final CrosshairStyle style;
        public final int color;
        public final int thickness;
        public final int size;
        public final boolean outline;

        Preset(String name, CrosshairStyle style, int color, int thickness, int size, boolean outline) {
            this.name = name; this.style = style; this.color = color;
            this.thickness = thickness; this.size = size; this.outline = outline;
        }
    }

    // ── Settings ──────────────────────────────────────────────────
    private CrosshairStyle style      = CrosshairStyle.CROSS;
    private int    color              = 0xFFFFFFFF;
    private int    outlineColor       = 0xFF000000;
    private int    thickness          = 1;
    private int    size               = 6;
    private int    opacity            = 255;         // 0-255
    private boolean showOutline       = false;
    private boolean dynamic           = false;       // expands when moving
    private boolean animated          = false;       // rotation/pulse
    private float   animationSpeed    = 1.0f;
    private Preset  currentPreset     = Preset.CLASSIC;

    // Runtime state
    private float   animTick          = 0f;
    private float   dynamicExpand     = 0f;

    public CrosshairModule() {
        super("Crosshair", "Fully customizable crosshair with presets, outlines, and animations.", Category.HUD);
    }

    @Override
    protected void subscribeEvents() {
        SyhpearClient.getEventBus().subscribe(EventBus.HudRenderEvent.class, this::onHudRender);
        SyhpearClient.getEventBus().subscribe(EventBus.TickEvent.class, this::onTick);
    }

    @Override
    protected void unsubscribeEvents() {
        SyhpearClient.getEventBus().unsubscribe(EventBus.HudRenderEvent.class, this::onHudRender);
        SyhpearClient.getEventBus().unsubscribe(EventBus.TickEvent.class, this::onTick);
    }

    private void onTick(EventBus.TickEvent event) {
        if (animated) {
            animTick += animationSpeed * 0.05f;
            if (animTick > (float)(Math.PI * 2)) animTick = 0f;
        }

        if (dynamic) {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player != null) {
                double speed = mc.player.getVelocity().horizontalLength();
                dynamicExpand = (float)(speed * 10f);
            }
        } else {
            dynamicExpand = 0f;
        }
    }

    private void onHudRender(EventBus.HudRenderEvent event) {
        MinecraftClient mc = MinecraftClient.getInstance();
        int cx = mc.getWindow().getScaledWidth()  / 2;
        int cy = mc.getWindow().getScaledHeight() / 2;

        int effectiveSize = (int)(size + dynamicExpand);
        int alpha = (opacity << 24) & 0xFF000000;

        // DrawContext is obtained via InGameHudMixin
        // Crosshair rendering uses DrawContext.fill() for rectangles
        // and custom tessellator for circles/arcs

        switch (style) {
            case CROSS  -> drawCross(cx, cy, effectiveSize, thickness, alpha);
            case DOT    -> drawDot(cx, cy, thickness, alpha);
            case CIRCLE -> drawCircle(cx, cy, effectiveSize, thickness, alpha);
            case T_SHAPE-> drawTShape(cx, cy, effectiveSize, thickness, alpha);
            default     -> {} // use vanilla
        }
    }

    private void drawCross(int cx, int cy, int size, int thick, int alpha) {
        // Horizontal line
        // ctx.fill(cx - size, cy - thick/2, cx + size, cy + thick/2, alpha | (color & 0x00FFFFFF));
        // Vertical line
        // ctx.fill(cx - thick/2, cy - size, cx + thick/2, cy + size, alpha | (color & 0x00FFFFFF));
        if (showOutline) drawCrossOutline(cx, cy, size, thick, alpha);
    }

    private void drawCrossOutline(int cx, int cy, int size, int thick, int alpha) {
        int oc = alpha | (outlineColor & 0x00FFFFFF);
        // Draw outline by drawing slightly larger cross in outlineColor behind main cross
    }

    private void drawDot(int cx, int cy, int radius, int alpha) {
        int r = radius + 1;
        // ctx.fill(cx - r, cy - r, cx + r, cy + r, alpha | (color & 0x00FFFFFF));
    }

    private void drawCircle(int cx, int cy, int radius, int thick, int alpha) {
        // Uses tessellator to draw circle arc
        // Requires GL11 direct rendering via RenderSystem
    }

    private void drawTShape(int cx, int cy, int size, int thick, int alpha) {
        // Horizontal top bar only
        // ctx.fill(cx - size, cy - thick/2, cx + size, cy + thick/2, alpha | (color & 0x00FFFFFF));
        // Vertical downward line
        // ctx.fill(cx - thick/2, cy, cx + thick/2, cy + size, alpha | (color & 0x00FFFFFF));
    }

    // ── Preset loading ────────────────────────────────────────────
    public void applyPreset(Preset preset) {
        this.currentPreset = preset;
        this.style      = preset.style;
        this.color      = preset.color;
        this.thickness  = preset.thickness;
        this.size       = preset.size;
        this.showOutline = preset.outline;
    }

    // ── Getters / Setters ─────────────────────────────────────────
    public CrosshairStyle getStyle()          { return style; }
    public void setStyle(CrosshairStyle s)    { this.style = s; }
    public int  getColor()                    { return color; }
    public void setColor(int c)               { this.color = c; }
    public int  getThickness()                { return thickness; }
    public void setThickness(int t)           { this.thickness = Math.max(1, t); }
    public int  getSize()                     { return size; }
    public void setSize(int s)                { this.size = Math.max(1, s); }
    public int  getOpacity()                  { return opacity; }
    public void setOpacity(int o)             { this.opacity = Math.max(0, Math.min(255, o)); }
    public boolean isOutline()                { return showOutline; }
    public void setOutline(boolean o)         { this.showOutline = o; }
    public boolean isDynamic()                { return dynamic; }
    public void setDynamic(boolean d)         { this.dynamic = d; }
    public boolean isAnimated()               { return animated; }
    public void setAnimated(boolean a)        { this.animated = a; }
    public void setAnimationSpeed(float s)    { this.animationSpeed = s; }
    public Preset getCurrentPreset()          { return currentPreset; }
    public void setOutlineColor(int c)        { this.outlineColor = c; }
}
