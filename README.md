# Syhpear Client
### Performance Meets Simplicity

A premium Minecraft Java Edition client mod built with **Fabric**, focused on high FPS, modern UI, and stability.

```
╔══════════════════════════════════════╗
║       Syhpear Client v1.0.0          ║
║   Performance Meets Simplicity       ║
╚══════════════════════════════════════╝
```

---

## Requirements

| Tool          | Version  |
|---------------|----------|
| Java JDK      | 21+      |
| Minecraft     | 1.21.1+  |
| Fabric Loader | 0.16.5+  |
| Gradle        | 8.8+     |

---

## Build Instructions

### 1. Clone the project
```bash
git clone https://github.com/yourname/syhpear-client
cd syhpear-client
```

### 2. Download Gradle wrapper
```bash
# Linux/macOS
./gradlew wrapper --gradle-version=8.8

# Windows
gradlew.bat wrapper --gradle-version=8.8
```

### 3. Build the mod
```bash
./gradlew build
```

Output JAR will be in:
```
build/libs/syhpear-client-1.0.0.jar
```

### 4. Install
Copy the JAR to your Fabric mods folder:
```
%AppData%\.minecraft\mods\          (Windows)
~/.minecraft/mods/                  (Linux/macOS)
```

### 5. Zalith Launcher (Android)
- Open Zalith Launcher → Manage Mods → Add Mod
- Select `syhpear-client-1.0.0.jar`
- Launch with Fabric 1.21.1

---

## Project Structure

```
syhpear-client/
├── build.gradle
├── gradle.properties
├── settings.gradle
└── src/main/
    ├── java/com/syhpear/client/
    │   ├── SyhpearClient.java          ← Main entrypoint
    │   ├── event/
    │   │   └── EventBus.java           ← Event system
    │   ├── module/
    │   │   ├── Module.java             ← Base module class
    │   │   └── ModuleManager.java      ← Module registry
    │   ├── modules/
    │   │   ├── performance/
    │   │   │   ├── FpsBoostModule.java
    │   │   │   ├── EntityCullingModule.java
    │   │   │   └── PerformanceModules.java  (SmartRendering, Chunks, Memory, DynamicFPS, etc.)
    │   │   ├── hud/
    │   │   │   ├── HudModules.java     (FPS, Ping, CPS, Keystrokes, Coords, Armor, etc.)
    │   │   │   └── CrosshairModule.java
    │   │   └── visual/
    │   │       ├── FullbrightModule.java
    │   │       └── VisualModules.java  (Zoom, MotionBlur, ItemPhysics, BetterChat, etc.)
    │   ├── mixin/
    │   │   ├── GameRendererMixin.java  ← FOV / zoom hook
    │   │   ├── InGameHudMixin.java     ← HUD render hook
    │   │   ├── MinecraftClientMixin.java ← Tick hook
    │   │   ├── WorldRendererMixin.java ← Chunk/entity culling
    │   │   ├── ChatScreenMixin.java    ← Chat intercept
    │   │   └── MouseMixin.java         ← CPS + scroll
    │   ├── config/
    │   │   └── SyhpearConfig.java      ← JSON config, profiles, import/export
    │   ├── account/
    │   │   └── AccountManager.java     ← Multi-account, offline/MSA
    │   └── util/
    │       └── Logger.java
    └── resources/
        ├── fabric.mod.json
        └── syhpear.mixins.json
```

---

## Feature List

### Performance
| Module | Description |
|--------|-------------|
| FPS Boost | GPU/CPU optimization, V-Sync toggle, FPS limiter |
| Smart Rendering | Skip unchanged chunk re-renders |
| Chunk Optimization | Limit chunk builds per frame |
| Entity Culling | Skip off-screen / out-of-range entities |
| Memory Optimizer | Periodic GC, cache clearing |
| Dynamic FPS | Lower FPS when window unfocused |
| Thread Optimization | Max priority for render threads |
| Lazy Chunk Updates | Defer light & biome updates |

### HUD
| Module | Description |
|--------|-------------|
| FPS Counter | Color-coded FPS display |
| Ping Counter | Server latency in ms |
| CPS Counter | Left + right clicks per second |
| Keystrokes | WASD + Space + LMB/RMB display |
| Coordinates | XYZ + direction + biome |
| Armor Status | Equipment durability |
| Potion Status | Active effects + duration |
| Speed Display | BPS movement speed |
| Clock | Real-time clock (12h/24h) |
| Server Info | Current server IP |
| Crosshair | Custom crosshair with 5 presets, outline, opacity, animation |

### Visual
| Module | Description |
|--------|-------------|
| Fullbright | Max gamma — no darkness |
| Zoom | Smooth zoom (C key), scroll-to-adjust |
| Motion Blur | Post-processing camera blur |
| Item Physics | Rotating / bouncing dropped items |
| Better Chat | Timestamps, blur background, compact |
| Better Inventory | Dark rounded modern inventory UI |

### Config
- JSON persistence in `.minecraft/config/syhpear/`
- Multiple profiles
- Import / Export
- Auto-save on change
- Backup on corruption

### Account Manager
- Offline account creation + in-game switch
- Microsoft account display (re-auth via launcher)
- Favorites, history, avatar display (Minotar)

---

## Adding a New Module

1. Create a class extending `Module`:
```java
public class MyModule extends Module {
    public MyModule() {
        super("My Module", "Does something cool.", Category.VISUAL);
    }

    @Override
    protected void subscribeEvents() {
        SyhpearClient.getEventBus().subscribe(EventBus.HudRenderEvent.class, this::onHudRender);
    }

    private void onHudRender(EventBus.HudRenderEvent event) {
        // Draw your HUD element here
    }
}
```

2. Register it in `ModuleManager.registerAll()`:
```java
register(new MyModule());
```

That's it! Config save/load is automatic.

---

## License

MIT License — Free to use, modify, and distribute.

---

## Credits

Built with Fabric Modding API.  
Syhpear Client — Performance Meets Simplicity.
