package com.syhpear.client;

import com.syhpear.client.config.SyhpearConfig;
import com.syhpear.client.event.EventBus;
import com.syhpear.client.module.ModuleManager;
import com.syhpear.client.account.AccountManager;
import com.syhpear.client.util.Logger;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * ╔═══════════════════════════════════════════╗
 * ║         SYHPEAR CLIENT v1.0.0             ║
 * ║    Performance Meets Simplicity           ║
 * ║    Minecraft Java Edition 1.21.1+         ║
 * ╚═══════════════════════════════════════════╝
 */
@Environment(EnvType.CLIENT)
public class SyhpearClient implements ClientModInitializer {

    public static final String MOD_ID      = "syhpear";
    public static final String MOD_NAME    = "Syhpear Client";
    public static final String VERSION     = "1.0.0";
    public static final String MC_VERSION  = "1.21.1";

    // Singleton
    private static SyhpearClient INSTANCE;

    // Core systems
    private SyhpearConfig   config;
    private ModuleManager   moduleManager;
    private AccountManager  accountManager;
    private EventBus        eventBus;

    @Override
    public void onInitializeClient() {
        INSTANCE = this;

        Logger.info("╔══════════════════════════════════════╗");
        Logger.info("║       Syhpear Client v" + VERSION + "          ║");
        Logger.info("║   Performance Meets Simplicity       ║");
        Logger.info("╚══════════════════════════════════════╝");

        long startTime = System.currentTimeMillis();

        // Initialize core systems in order
        initEventBus();
        initConfig();
        initModules();
        initAccounts();

        long elapsed = System.currentTimeMillis() - startTime;
        Logger.info("Syhpear Client loaded in " + elapsed + "ms — Ready!");
    }

    private void initEventBus() {
        eventBus = new EventBus();
        Logger.info("[EventBus] Initialized");
    }

    private void initConfig() {
        config = new SyhpearConfig();
        config.load();
        Logger.info("[Config] Loaded profile: " + config.getCurrentProfile());
    }

    private void initModules() {
        moduleManager = new ModuleManager();
        moduleManager.registerAll();
        moduleManager.loadStates(config);
        Logger.info("[Modules] Registered " + moduleManager.getModuleCount() + " modules");
    }

    private void initAccounts() {
        accountManager = new AccountManager();
        accountManager.loadAccounts();
        Logger.info("[Accounts] Loaded " + accountManager.getAccountCount() + " accounts");
    }

    // ── Static accessors ──────────────────────────────────────────
    public static SyhpearClient getInstance()       { return INSTANCE; }
    public static SyhpearConfig getConfig()         { return INSTANCE.config; }
    public static ModuleManager getModuleManager()  { return INSTANCE.moduleManager; }
    public static AccountManager getAccountManager(){ return INSTANCE.accountManager; }
    public static EventBus getEventBus()            { return INSTANCE.eventBus; }
}
