package com.syhpear.client.modules.hud;

import com.syhpear.client.SyhpearClient;
import com.syhpear.client.event.EventBus;
import com.syhpear.client.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.ArrayDeque;
import java.util.Deque;

public class KeystrokesModule extends Module {

    private int x = 200, y = 100;
    private int keySize    = 20;
    private int keySpacing = 2;
    private int bgColor    = 0x80000000;
    private int pressColor = 0xCC00BFFF;
    private int textColor  = 0xFFFFFFFF;

    // Track key states (injected via MouseMixin / KeyboardMixin)
    public boolean keyW, keyA, keyS, keyD, keySpace, keyLMB, keyRMB;

    public KeystrokesModule() {
        super("Keystrokes", "Displays WASD, Space, LMB, RMB key presses.", Category.HUD);
    }

    @Override
    protected void subscribeEvents() {
        SyhpearClient.getEventBus().subscribe(EventBus.HudRenderEvent.class, this::onHudRender);
    }

    @Override
    protected void unsubscribeEvents() {
        SyhpearClient.getEventBus().unsubscribe(EventBus.HudRenderEvent.class, this::onHudRender);
    }

    private void onHudRender(EventBus.HudRenderEvent event) {
        // Layout:
        //      [W]
        //   [A][S][D]
        //   [  SPACE  ]
        //   [LMB][RMB]
        // Each key drawn as a rounded rectangle, filled with pressColor if held.
        // Implementation uses DrawContext.fill() + drawWithShadow()
    }

    public void setPosition(int x, int y)   { this.x = x; this.y = y; }
    public void setKeySize(int size)         { this.keySize = size; }
}
