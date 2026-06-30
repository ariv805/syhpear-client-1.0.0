package com.syhpear.client.mixin;

import com.syhpear.client.SyhpearClient;
import com.syhpear.client.event.EventBus;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Syhpear Client — MinecraftClientMixin
 * Fires TickEvent on every game tick.
 */
@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    private long tickCount = 0;

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTickHead(CallbackInfo ci) {
        SyhpearClient.getEventBus().post(new EventBus.TickEvent(tickCount++));
    }
}
