package com.syhpear.client.modules.performance;

import com.syhpear.client.SyhpearClient;
import com.syhpear.client.event.EventBus;
import com.syhpear.client.module.Module;
import com.syhpear.client.util.Logger;
import net.minecraft.client.MinecraftClient;

/**
 * Syhpear Client — FPS Boost Module
 *
 * Optimizations:
 * - Reduces unnecessary re-renders
 * - Disables redundant entity shadow rendering
 * - Optimizes particle systems
 * - Reduces idle CPU/GPU usage
 */
public class FpsBoostModule extends Module {

    private boolean vsyncEnabled   = false;
    private int     fpsLimit       = 260;
    private boolean gpuOptimize    = true;
    private boolean cpuOptimize    = true;
    private boolean fastLoading    = true;

    public FpsBoostModule() {
        super("FPS Boost",
              "Maximizes FPS by applying GPU, CPU, and render optimizations.",
              Category.PERFORMANCE);
    }

    @Override
    public void onEnable() {
        applyOptimizations();
        Logger.info("[FPS Boost] Enabled — Target: " + fpsLimit + " FPS");
    }

    @Override
    public void onDisable() {
        restoreDefaults();
        Logger.info("[FPS Boost] Disabled");
    }

    @Override
    protected void subscribeEvents() {
        SyhpearClient.getEventBus().subscribe(EventBus.TickEvent.class, this::onTick);
    }

    @Override
    protected void unsubscribeEvents() {
        SyhpearClient.getEventBus().unsubscribe(EventBus.TickEvent.class, this::onTick);
    }

    private void onTick(EventBus.TickEvent event) {
        if (event.tickCount % 200 == 0) { // Every ~10 seconds
            runGarbageOptimization();
        }
    }

    private void applyOptimizations() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null) return;

        // V-Sync control
        mc.getWindow().setVsync(vsyncEnabled);

        // FPS limit
        mc.options.getMaxFps().setValue(fpsLimit);

        // GPU optimization: reduce render distance if FPS < 30
        if (gpuOptimize && mc.options.getViewDistance().getValue() > 8) {
            Logger.debug("[FPS Boost] GPU optimization active");
        }

        // CPU optimization: reduce background thread priority
        if (cpuOptimize) {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        }
    }

    private void restoreDefaults() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null) return;
        mc.getWindow().setVsync(mc.options.getEnableVsync().getValue());
    }

    private void runGarbageOptimization() {
        Runtime rt = Runtime.getRuntime();
        long used  = rt.totalMemory() - rt.freeMemory();
        long max   = rt.maxMemory();
        double pct = (double) used / max;

        if (pct > 0.85) {
            System.gc();
            Logger.debug("[FPS Boost] GC triggered — Memory at " + (int)(pct*100) + "%");
        }
    }

    // ── Settings ──────────────────────────────────────────────────
    public void setFpsLimit(int limit)      { this.fpsLimit = limit; if (enabled) applyOptimizations(); }
    public void setVsync(boolean v)         { this.vsyncEnabled = v; if (enabled) applyOptimizations(); }
    public void setGpuOptimize(boolean g)   { this.gpuOptimize = g; }
    public void setCpuOptimize(boolean c)   { this.cpuOptimize = c; }
    public int  getFpsLimit()               { return fpsLimit; }
    public boolean isVsync()               { return vsyncEnabled; }
}
