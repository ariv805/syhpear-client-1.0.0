package com.syhpear.client.mixin;

import com.syhpear.client.SyhpearClient;
import com.syhpear.client.event.EventBus;
import com.syhpear.client.modules.hud.CpsCounterModule;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Syhpear Client — MouseMixin
 * Tracks mouse clicks for CPS counter and fires scroll events.
 */
@Mixin(Mouse.class)
public abstract class MouseMixin {

    @Inject(
        method = "onMouseButton",
        at = @At("HEAD")
    )
    private void onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        if (action != 1) return; // Only PRESS

        // 0 = Left, 1 = Right
        var cps = SyhpearClient.getModuleManager().getModule(CpsCounterModule.class);
        if (cps == null || !cps.isEnabled()) return;

        if (button == 0) cps.registerLeftClick();
        if (button == 1) cps.registerRightClick();
    }

    @Inject(
        method = "onMouseScroll",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onMouseScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        EventBus.MouseScrollEvent event = new EventBus.MouseScrollEvent(vertical);
        SyhpearClient.getEventBus().post(event);
        if (event.isCancelled()) ci.cancel();
    }
}
