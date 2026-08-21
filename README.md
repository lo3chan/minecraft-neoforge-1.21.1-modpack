# Minecraft Forge 1.20.1 Master Modpack Workspace

Master multi-mod source repository and porting workspace for Minecraft Forge 1.20.1.

---

## 📦 Project Structure

```text
├── modpack.json         # Master manifest listing mods, upstream repos, branches & build targets
├── settings.gradle      # Composite build orchestrator automatically including all subprojects in /mods
├── build.gradle         # Root Gradle build script
├── sync_mods.py         # Automation script to pull, submodule, or sync upstream repositories
└── mods/                # Submodules / cloned source repositories for individual mods
    ├── mod-a/
    └── mod-b/
```

---

## 🚀 Getting Started

### 1. Requirements
- **JDK 17** (Temurin or Adoptium recommended)
- **Git**
- **Python 3.8+** (for `sync_mods.py`)

### 2. Synchronize Mod Sources
To fetch all mod sources configured in `modpack.json`:
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
