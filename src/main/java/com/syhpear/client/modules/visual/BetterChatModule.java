package com.syhpear.client.modules.visual;

import com.syhpear.client.SyhpearClient;
import com.syhpear.client.event.EventBus;
import com.syhpear.client.module.Module;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class BetterChatModule extends Module {

    private boolean timestamps   = true;
    private boolean compactChat  = true;
    private int     chatOpacity  = 80;   // percent
    private boolean chatBlur     = true;
    private int     maxMessages  = 100;
    private boolean copyOnClick  = false;

    public BetterChatModule() {
        super("Better Chat", "Improved chat with timestamps, compact mode, and blur background.", Category.VISUAL);
    }

    @Override
    protected void subscribeEvents() {
        SyhpearClient.getEventBus().subscribe(EventBus.ChatReceiveEvent.class, this::onChatReceive);
    }

    @Override
    protected void unsubscribeEvents() {
        SyhpearClient.getEventBus().unsubscribe(EventBus.ChatReceiveEvent.class, this::onChatReceive);
    }

    private void onChatReceive(EventBus.ChatReceiveEvent event) {
        if (timestamps) {
            java.time.LocalTime now = java.time.LocalTime.now();
            String ts = String.format("[%02d:%02d] ", now.getHour(), now.getMinute());
            event.message = ts + event.message;
        }
    }

    public void setTimestamps(boolean t)   { this.timestamps = t; }
    public void setCompact(boolean c)      { this.compactChat = c; }
    public void setChatOpacity(int o)      { this.chatOpacity = o; }
    public void setChatBlur(boolean b)     { this.chatBlur = b; }
}
