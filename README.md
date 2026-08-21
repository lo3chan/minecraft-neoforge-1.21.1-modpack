# Minecraft NeoForge 1.21.1 Master Modpack Workspace

Master multi-mod source repository and porting workspace for **Minecraft NeoForge 1.21.1**.

---

## 📦 Project Structure

```text
├── modpack.json         # Master manifest listing mods, upstream repos, branches & build targets
├── settings.gradle      # Composite build orchestrator automatically including all subprojects in /mods
├── build.gradle         # Root Gradle build script (Java 21 / NeoForge 1.21.1)
├── sync_mods.py         # Automation script to pull, submodule, or sync upstream repositories
├── shaderpacks/         # Client shaderpacks (Eclipse Shaders)
└── mods/                # Submodules / cloned source repositories for individual mods
    ├── sodium/
    ├── iris/
    ├── distant-horizons/
    ├── ferritecore/
    └── ...
```

---

## ⚡ Integrated Performance Suite (19 Mods)

| Category | Mods Included |
|---|---|
| **Rendering & GPU** | **Sodium**, **Iris Shaders**, **Distant Horizons**, **ImmediatelyFast**, **Entity Culling**, **More Culling** |
| **Memory & RAM** | **FerriteCore**, **ModernFix**, **BadOptimizations**, **Mods Optimizer** |
| **Tick & Server Performance** | **Radon (Lithium)**, **Alternate Current**, **FastSuite**, **Clumps** |
| **World Gen & Profiling** | **Noisium**, **Chunky**, **Spark** |
| **UI & Shader Extras** | **Reese's Sodium Options**, **Sodium Extra**, **Eclipse Shaders** (`shaderpacks/`) |

---

## 🚀 Getting Started

### 1. Requirements
- **JDK 21** (Temurin 21 or Microsoft OpenJDK 21 required for NeoForge 1.21.1)
- **Git**
- **Python 3.8+** (for `sync_mods.py`)

### 2. Synchronize Mod Sources
To clone / submodule all 19 mod sources configured in `modpack.json`:
```bash
python sync_mods.py
```

### 3. Build & Test
Open this repository in IntelliJ IDEA or Eclipse. The root `settings.gradle` will automatically detect each mod subproject in `/mods` as a composite build.

To build all mods:
```bash
./gradlew buildAllMods
```

Or build an individual mod:
```bash
./gradlew :mod-name:build
```
