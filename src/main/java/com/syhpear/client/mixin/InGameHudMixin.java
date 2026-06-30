package com.syhpear.client.mixin;

import com.syhpear.client.SyhpearClient;
import com.syhpear.client.event.EventBus;
import com.syhpear.client.modules.hud.CrosshairModule;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Syhpear Client — InGameHudMixin
 * Hooks into HUD rendering to draw all Syhpear HUD elements.
 */
@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    /**
     * Fire HudRenderEvent after vanilla HUD renders.
     * All HUD modules draw here.
     */
    @Inject(
        method = "render",
        at = @At("RETURN")
    )
    private void onHudRender(DrawContext context, float tickDelta, CallbackInfo ci) {
        SyhpearClient.getEventBus().post(new EventBus.HudRenderEvent(tickDelta));
    }

    /**
     * Cancel vanilla crosshair rendering if Syhpear's crosshair module is enabled.
     */
    @Inject(
        method = "renderCrosshair",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onRenderCrosshair(DrawContext context, float tickDelta, CallbackInfo ci) {
        CrosshairModule crosshair = SyhpearClient.getModuleManager().getModule(CrosshairModule.class);
        if (crosshair != null && crosshair.isEnabled()) {
            // Cancel vanilla crosshair — our module draws its own
            ci.cancel();
        }
    }
}
