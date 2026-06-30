package com.syhpear.client.mixin;

import com.syhpear.client.SyhpearClient;
import com.syhpear.client.event.EventBus;
import com.syhpear.client.modules.visual.FullbrightModule;
import com.syhpear.client.modules.visual.ZoomModule;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Syhpear Client — GameRendererMixin
 * Hooks into rendering pipeline for zoom, FOV, and shader support.
 */
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    /**
     * Modify FOV for Zoom module.
     */
    @ModifyReturnValue(
        method = "getFov",
        at = @At("RETURN")
    )
    private double modifyFov(double originalFov) {
        ZoomModule zoom = SyhpearClient.getModuleManager().getModule(ZoomModule.class);
        if (zoom != null && zoom.isEnabled() && zoom.isZooming()) {
            return zoom.getCurrentFov();
        }
        return originalFov;
    }

    /**
     * Fire RenderEvent before and after each frame render.
     */
    @Inject(
        method = "renderWorld",
        at = @At("HEAD")
    )
    private void onRenderWorldPre(float tickDelta, long limitTime, MatrixStack matrices, CallbackInfo ci) {
        SyhpearClient.getEventBus().post(new EventBus.WorldRenderEvent(tickDelta));
        SyhpearClient.getEventBus().post(new EventBus.RenderEvent(tickDelta, false));
    }

    @Inject(
        method = "renderWorld",
        at = @At("RETURN")
    )
    private void onRenderWorldPost(float tickDelta, long limitTime, MatrixStack matrices, CallbackInfo ci) {
        SyhpearClient.getEventBus().post(new EventBus.RenderEvent(tickDelta, true));
    }
}
