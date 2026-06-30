package com.syhpear.client.account;

import com.google.gson.*;
import com.syhpear.client.util.Logger;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.session.Session;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Syhpear Client — Account Manager
 *
 * Features:
 * - Multiple accounts
 * - Microsoft (online) + Offline login
 * - Favorite accounts
 * - Quick switch
 * - Login history
 * - Avatar URL caching (Steve fallback)
 */
public class AccountManager {

    public enum AccountType { MICROSOFT, OFFLINE }

    public static class Account {
        public String      uuid;
        public String      username;
        public AccountType type;
        public boolean     favorite;
        public long        lastUsed;
        public String      avatarUrl;

        public Account(String uuid, String username, AccountType type) {
            this.uuid     = uuid;
            this.username = username;
            this.type     = type;
            this.favorite = false;
            this.lastUsed = System.currentTimeMillis();
            this.avatarUrl = "https://minotar.net/avatar/" + username + "/32";
        }
    }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final Path accountsFile;
    private final List<Account> accounts = new ArrayList<>();

    public AccountManager() {
        accountsFile = FabricLoader.getInstance()
            .getConfigDir().resolve("syhpear").resolve("accounts.json");
    }

    // ── Load / Save ───────────────────────────────────────────────

    public void loadAccounts() {
        try {
            if (!Files.exists(accountsFile)) return;
            String json = Files.readString(accountsFile);
            JsonArray arr = GSON.fromJson(json, JsonArray.class);
            for (JsonElement el : arr) {
                JsonObject obj = el.getAsJsonObject();
                Account acc = new Account(
                    obj.get("uuid").getAsString(),
                    obj.get("username").getAsString(),
                    AccountType.valueOf(obj.get("type").getAsString())
                );
                acc.favorite  = obj.has("favorite") && obj.get("favorite").getAsBoolean();
                acc.lastUsed  = obj.has("lastUsed") ? obj.get("lastUsed").getAsLong() : 0;
                acc.avatarUrl = obj.has("avatarUrl") ? obj.get("avatarUrl").getAsString() : acc.avatarUrl;
                accounts.add(acc);
            }
            Logger.info("[Accounts] Loaded " + accounts.size() + " accounts");
        } catch (Exception e) {
            Logger.error("[Accounts] Load failed: " + e.getMessage());
        }
    }

    public void saveAccounts() {
        try {
            Files.createDirectories(accountsFile.getParent());
            JsonArray arr = new JsonArray();
            for (Account acc : accounts) {
                JsonObject obj = new JsonObject();
                obj.addProperty("uuid",      acc.uuid);
                obj.addProperty("username",  acc.username);
                obj.addProperty("type",      acc.type.name());
                obj.addProperty("favorite",  acc.favorite);
                obj.addProperty("lastUsed",  acc.lastUsed);
                obj.addProperty("avatarUrl", acc.avatarUrl);
                arr.add(obj);
            }
            Files.writeString(accountsFile, GSON.toJson(arr));
        } catch (IOException e) {
            Logger.error("[Accounts] Save failed: " + e.getMessage());
        }
    }

    // ── Account operations ────────────────────────────────────────

    public void addOfflineAccount(String username) {
        String uuid = "offline-" + username.toLowerCase().replace(" ", "_");
        Account acc = new Account(uuid, username, AccountType.OFFLINE);
        accounts.removeIf(a -> a.uuid.equals(uuid)); // Replace if exists
        accounts.add(acc);
        saveAccounts();
        Logger.info("[Accounts] Added offline account: " + username);
    }

    /**
     * Switch to an account by UUID.
     * For OFFLINE accounts, creates a new Session immediately.
     * For MICROSOFT accounts, triggers OAuth flow (handled by launcher).
     */
    public boolean switchTo(String uuid) {
        Account acc = accounts.stream()
            .filter(a -> a.uuid.equals(uuid))
            .findFirst().orElse(null);

        if (acc == null) {
            Logger.warn("[Accounts] Account not found: " + uuid);
            return false;
        }

        if (acc.type == AccountType.OFFLINE) {
            MinecraftClient mc = MinecraftClient.getInstance();
            // Create offline session via reflection (session field is final)
            try {
                var sessionField = MinecraftClient.class.getDeclaredField("session");
                sessionField.setAccessible(true);
                sessionField.set(mc, new Session(acc.username, acc.uuid, "invalid_token", Optional.empty(), Optional.empty(), Session.AccountType.LEGACY));
                acc.lastUsed = System.currentTimeMillis();
                saveAccounts();
                Logger.info("[Accounts] Switched to offline: " + acc.username);
                return true;
            } catch (Exception e) {
                Logger.error("[Accounts] Switch failed: " + e.getMessage());
                return false;
            }
        }

        // Microsoft accounts: prompt user to re-authenticate
        Logger.info("[Accounts] Microsoft re-auth required for: " + acc.username);
        return false;
    }

    public void removeAccount(String uuid) {
        accounts.removeIf(a -> a.uuid.equals(uuid));
        saveAccounts();
    }

    public void toggleFavorite(String uuid) {
        accounts.stream().filter(a -> a.uuid.equals(uuid))
            .findFirst().ifPresent(a -> { a.favorite = !a.favorite; saveAccounts(); });
    }

    // ── Getters ───────────────────────────────────────────────────

    public List<Account> getAccounts() {
        return Collections.unmodifiableList(accounts);
    }

    public List<Account> getFavorites() {
        return accounts.stream().filter(a -> a.favorite).toList();
    }

    public List<Account> getHistory() {
        return accounts.stream()
            .sorted(Comparator.comparingLong((Account a) -> a.lastUsed).reversed())
            .toList();
    }

    public int getAccountCount() { return accounts.size(); }
}
