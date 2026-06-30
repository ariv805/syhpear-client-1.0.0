package com.syhpear.client.mixin;

import com.syhpear.client.SyhpearClient;
import com.syhpear.client.event.EventBus;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Syhpear Client — ChatScreenMixin
 * Intercepts chat messages for BetterChatModule.
 */
@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin {

    @Inject(
        method = "sendMessage",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onChatSend(String chatText, boolean addToHistory, CallbackInfo ci) {
        EventBus.ChatSendEvent event = new EventBus.ChatSendEvent(chatText);
        SyhpearClient.getEventBus().post(event);
        if (event.isCancelled()) ci.cancel();
    }
}
