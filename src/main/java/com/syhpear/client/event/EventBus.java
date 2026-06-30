package com.syhpear.client.event;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Syhpear Client — EventBus
 * Central event dispatcher for all client modules.
 */
public class EventBus {

    private final Map<Class<?>, List<EventListener<?>>> listeners = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public <T extends Event> void subscribe(Class<T> eventClass, EventListener<T> listener) {
        listeners.computeIfAbsent(eventClass, k -> new ArrayList<>()).add(listener);
    }

    public <T extends Event> void unsubscribe(Class<T> eventClass, EventListener<T> listener) {
        List<EventListener<?>> list = listeners.get(eventClass);
        if (list != null) list.remove(listener);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T extends Event> T post(T event) {
        List<EventListener<?>> list = listeners.get(event.getClass());
        if (list != null) {
            for (EventListener listener : list) {
                if (event instanceof CancellableEvent ce && ce.isCancelled()) break;
                listener.onEvent(event);
            }
        }
        return event;
    }

    @FunctionalInterface
    public interface EventListener<T extends Event> {
        void onEvent(T event);
    }

    // ── Base event types ─────────────────────────────────────────

    public static abstract class Event {}

    public static abstract class CancellableEvent extends Event {
        private boolean cancelled = false;
        public void cancel()               { this.cancelled = true; }
        public boolean isCancelled()       { return cancelled; }
    }

    // ── Game events ──────────────────────────────────────────────

    /** Fired every client tick */
    public static class TickEvent extends Event {
        public final long tickCount;
        public TickEvent(long tickCount) { this.tickCount = tickCount; }
    }

    /** Fired before/after rendering a frame */
    public static class RenderEvent extends Event {
        public final float tickDelta;
        public final boolean post;
        public RenderEvent(float tickDelta, boolean post) {
            this.tickDelta = tickDelta;
            this.post = post;
        }
    }

    /** Fired when HUD is being rendered */
    public static class HudRenderEvent extends Event {
        public final float tickDelta;
        public HudRenderEvent(float tickDelta) { this.tickDelta = tickDelta; }
    }

    /** Fired when player sends a chat message */
    public static class ChatSendEvent extends CancellableEvent {
        public String message;
        public ChatSendEvent(String message) { this.message = message; }
    }

    /** Fired when player receives a chat message */
    public static class ChatReceiveEvent extends CancellableEvent {
        public String message;
        public ChatReceiveEvent(String message) { this.message = message; }
    }

    /** Fired when mouse scroll is used */
    public static class MouseScrollEvent extends CancellableEvent {
        public final double scrollDelta;
        public MouseScrollEvent(double scrollDelta) { this.scrollDelta = scrollDelta; }
    }

    /** Fired when a key is pressed */
    public static class KeyPressEvent extends CancellableEvent {
        public final int keyCode;
        public final int action; // GLFW action: PRESS, RELEASE, REPEAT
        public KeyPressEvent(int keyCode, int action) {
            this.keyCode = keyCode;
            this.action = action;
        }
    }

    /** Fired when world render begins */
    public static class WorldRenderEvent extends Event {
        public final float tickDelta;
        public WorldRenderEvent(float tickDelta) { this.tickDelta = tickDelta; }
    }
}
