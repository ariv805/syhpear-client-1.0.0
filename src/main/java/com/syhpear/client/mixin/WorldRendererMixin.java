package com.syhpear.client.mixin;

import com.syhpear.client.SyhpearClient;
import com.syhpear.client.modules.performance.EntityCullingModule;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Syhpear Client — WorldRendererMixin
 * Hooks world rendering for Smart Rendering and Chunk Optimization.
 */
@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {

    @Inject(
        method = "setupTerrain",
        at = @At("HEAD")
    )
    private void onSetupTerrain(Camera camera, Object frustum, boolean hasForcedFrustum, boolean spectator, CallbackInfo ci) {
        // ChunkOptimizationModule hooks here to limit rebuild count
    }
}

/**
 * Syhpear Client — EntityRendererMixin
 * Skips rendering entities flagged by EntityCullingModule.
 */
@Mixin(EntityRenderer.class)
abstract class EntityRendererMixin<T extends Entity> {

    @Inject(
        method = "render",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onEntityRender(T entity, float yaw, float tickDelta, Object matrices, Object vertexConsumers, int light, CallbackInfo ci) {
        EntityCullingModule culling = SyhpearClient.getModuleManager().getModule(EntityCullingModule.class);
        if (culling != null && culling.shouldCullEntity(entity)) {
            ci.cancel(); // Skip rendering this entity
        }
    }
}
