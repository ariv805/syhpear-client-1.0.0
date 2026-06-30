package com.syhpear.client.module;

import com.syhpear.client.SyhpearClient;
import com.syhpear.client.event.EventBus;

/**
 * Syhpear Client — Base Module
 * All client features extend this class.
 */
public abstract class Module {

    public enum Category {
        PERFORMANCE ("Performance"),
        HUD         ("HUD"),
        VISUAL      ("Visual"),
        MULTIPLAYER ("Multiplayer"),
        CONFIG      ("Config"),
        MISC        ("Misc");

        public final String displayName;
        Category(String displayName) { this.displayName = displayName; }
    }

    // ── Module metadata ───────────────────────────────────────────
    protected final String   name;
    protected final String   description;
    protected final Category category;
    protected boolean        enabled;
    protected int            keybind = -1; // GLFW key, -1 = none

    protected Module(String name, String description, Category category) {
        this.name        = name;
        this.description = description;
        this.category    = category;
        this.enabled     = false;
    }

    // ── Lifecycle ─────────────────────────────────────────────────

    /** Called once when the module is registered */
    public void onRegister() {}

    /** Called when the module is enabled */
    public void onEnable() {}

    /** Called when the module is disabled */
    public void onDisable() {}

    // ── Toggle ────────────────────────────────────────────────────

    public void toggle() {
        if (enabled) disable(); else enable();
    }

    public void enable() {
        if (enabled) return;
        enabled = true;
        subscribeEvents();
        onEnable();
        SyhpearClient.getEventBus().post(new ModuleToggleEvent(this, true));
    }

    public void disable() {
        if (!enabled) return;
        enabled = false;
        unsubscribeEvents();
        onDisable();
        SyhpearClient.getEventBus().post(new ModuleToggleEvent(this, false));
    }

    /** Override to subscribe to EventBus events */
    protected void subscribeEvents() {}

    /** Override to unsubscribe from EventBus events */
    protected void unsubscribeEvents() {}

    // ── Getters / Setters ─────────────────────────────────────────
    public String   getName()        { return name; }
    public String   getDescription() { return description; }
    public Category getCategory()    { return category; }
    public boolean  isEnabled()      { return enabled; }
    public int      getKeybind()     { return keybind; }
    public void     setKeybind(int k){ this.keybind = k; }
    public void     setEnabled(boolean e) { if (e) enable(); else disable(); }

    // ── Module toggle event ────────────────────────────────────────
    public static class ModuleToggleEvent extends EventBus.Event {
        public final Module module;
        public final boolean enabled;
        public ModuleToggleEvent(Module module, boolean enabled) {
            this.module  = module;
            this.enabled = enabled;
        }
    }
}
