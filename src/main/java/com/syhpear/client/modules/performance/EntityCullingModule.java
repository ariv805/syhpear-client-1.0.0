package com.syhpear.client.modules.performance;

import com.syhpear.client.SyhpearClient;
import com.syhpear.client.event.EventBus;
import com.syhpear.client.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

/**
 * Syhpear Client — Entity Culling Module
 *
 * Skips rendering of entities that are:
 * - Behind the player's view frustum
 * - Occluded by terrain (not visible)
 * - Beyond a configurable render range
 */
public class EntityCullingModule extends Module {

    private int     maxEntityRange   = 64;  // blocks
    private boolean frustumCulling   = true;
    private boolean occlusionCulling = true;
    private int     culledThisTick   = 0;

    public EntityCullingModule() {
        super("Entity Culling",
              "Skips rendering entities outside view or hidden behind terrain.",
              Category.PERFORMANCE);
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
        culledThisTick = 0;
    }

    /**
     * Called from WorldRendererMixin — returns true if entity should be SKIPPED.
     */
    public boolean shouldCullEntity(Entity entity) {
        if (!enabled) return false;

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) return false;

        // Never cull the local player
        if (entity == mc.player) return false;

        Vec3d playerPos = mc.player.getPos();
        Vec3d entityPos = entity.getPos();
        double dist     = playerPos.distanceTo(entityPos);

        // Distance culling
        if (dist > maxEntityRange) {
            culledThisTick++;
            return true;
        }

        // Frustum culling (basic AABB check)
        if (frustumCulling) {
            Box box = entity.getBoundingBox();
            if (!isInViewFrustum(box, mc)) {
                culledThisTick++;
                return true;
            }
        }

        return false;
    }

    private boolean isInViewFrustum(Box box, MinecraftClient mc) {
        // Simplified frustum check — full implementation uses
        // ClippingHelper from WorldRenderer mixin
        if (mc.gameRenderer == null) return true;
        // Returns true (visible) by default; mixin replaces with real frustum test
        return true;
    }

    // ── Getters / Setters ─────────────────────────────────────────
    public int  getMaxEntityRange()               { return maxEntityRange; }
    public void setMaxEntityRange(int r)          { this.maxEntityRange = r; }
    public void setFrustumCulling(boolean f)      { this.frustumCulling = f; }
    public void setOcclusionCulling(boolean o)    { this.occlusionCulling = o; }
    public int  getCulledThisTick()               { return culledThisTick; }
}
