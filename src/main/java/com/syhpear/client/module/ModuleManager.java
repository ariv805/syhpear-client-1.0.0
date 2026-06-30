package com.syhpear.client.module;

import com.syhpear.client.config.SyhpearConfig;
import com.syhpear.client.modules.performance.*;
import com.syhpear.client.modules.hud.*;
import com.syhpear.client.modules.visual.*;
import com.syhpear.client.util.Logger;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Syhpear Client — ModuleManager
 * Registers and manages all client modules.
 */
public class ModuleManager {

    private final Map<String, Module> modules = new LinkedHashMap<>();

    public void registerAll() {
        // ── Performance modules ───────────────────────────────────
        register(new FpsBoostModule());
        register(new SmartRenderingModule());
        register(new ChunkOptimizationModule());
        register(new EntityCullingModule());
        register(new MemoryOptimizerModule());
        register(new DynamicFpsModule());
        register(new ThreadOptimizationModule());
        register(new LazyChunkUpdatesModule());

        // ── HUD modules ───────────────────────────────────────────
        register(new FpsCounterModule());
        register(new PingCounterModule());
        register(new CpsCounterModule());
        register(new KeystrokesModule());
        register(new CoordinatesModule());
        register(new ArmorStatusModule());
        register(new PotionStatusModule());
        register(new SpeedDisplayModule());
        register(new ClockModule());
        register(new ServerInfoModule());
        register(new CrosshairModule());

        // ── Visual modules ────────────────────────────────────────
        register(new FullbrightModule());
        register(new ZoomModule());
        register(new MotionBlurModule());
        register(new ItemPhysicsModule());
        register(new BetterChatModule());
        register(new BetterInventoryModule());
    }

    private void register(Module module) {
        modules.put(module.getName().toLowerCase(), module);
        module.onRegister();
        Logger.debug("[ModuleManager] Registered: " + module.getName());
    }

    public void loadStates(SyhpearConfig config) {
        for (Module module : modules.values()) {
            boolean state = config.getModuleState(module.getName());
            if (state) module.enable();
        }
    }

    // ── Accessors ─────────────────────────────────────────────────

    public Module getModule(String name) {
        return modules.get(name.toLowerCase());
    }

    @SuppressWarnings("unchecked")
    public <T extends Module> T getModule(Class<T> clazz) {
        return (T) modules.values().stream()
                .filter(m -> m.getClass() == clazz)
                .findFirst().orElse(null);
    }

    public Collection<Module> getModules() {
        return Collections.unmodifiableCollection(modules.values());
    }

    public List<Module> getModulesByCategory(Module.Category category) {
        return modules.values().stream()
                .filter(m -> m.getCategory() == category)
                .collect(Collectors.toList());
    }

    public List<Module> search(String query) {
        String q = query.toLowerCase();
        return modules.values().stream()
                .filter(m -> m.getName().toLowerCase().contains(q)
                          || m.getDescription().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    public int getModuleCount() { return modules.size(); }
}
