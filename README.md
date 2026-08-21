# Minecraft NeoForge 1.21.1 Master Modpack Workspace

Master multi-mod source repository, composite build orchestrator, and porting workspace for **Minecraft NeoForge 1.21.1** (Java 21).

🔗 **Repository**: `lo3chan/minecraft-neoforge-1.21.1-modpack`

---

## 📦 Project Structure

```text
├── modpack.json         # Master manifest listing all 39 mods, upstreams, branches & build targets
├── settings.gradle      # Composite build orchestrator automatically including all subprojects in /mods
├── build.gradle         # Root Gradle build script (Java 21 / NeoForge 1.21.1)
├── sync_mods.py         # Automation script to pull, submodule, or sync upstream repositories
├── config/              # Pre-tuned configurations (C2ME + Distant Horizons zero-deadlock presets)
├── shaderpacks/         # Client shaderpacks (Eclipse Shaders)
├── datapacks/           # Integrated datapacks (Explorify)
└── mods/                # Submodules / cloned source repositories for individual mods
    ├── psi/             # Porting target (1.20.1 Forge -> 1.21.1 NeoForge)
    ├── alchemancy/      # Porting target (1.20.1 Forge -> 1.21.1 NeoForge)
    ├── thegraveyard/    # Porting target (1.20.1 Forge -> 1.21.1 NeoForge)
    ├── rottencreatures/ # Porting target (1.20.1 Forge -> 1.21.1 NeoForge)
    ├── tanshugetrees/   # Porting target (1.20.1 Forge -> 1.21.1 NeoForge)
    ├── blossomblade/    # Porting target (1.20.1 Forge -> 1.21.1 NeoForge)
    ├── betterarcheology/# Porting target (1.20.1 Forge -> 1.21.1 NeoForge)
    ├── sodium/          # Native NeoForge 1.21.1
    ├── iris/            # Native NeoForge 1.21.1
    ├── c2me/            # Native NeoForge 1.21.1
    └── ...
```

---

## 🛠️ Porting Roadmap (1.20.1 / 1.20 -> NeoForge 1.21.1)

The following mods are ingested from their upstream repositories and staged for 1.21.1 NeoForge modernization:

| Mod | Upstream Origin | Porting Focus |
|---|---|---|
| **Psi** | 1.20.1 Forge | Modernize Registry events, Data Components, and NeoForge Payload networking. |
| **Alchemancy** | 1.20.1 Forge | Transmutation recipes and NeoForge item capability migration. |
| **The Graveyard** | 1.20.1 Forge | Biome modifiers, structure template pools, and entity registration. |
| **Rotten Creatures** | 1.20.1 Forge | Mob AI attributes and NeoForge 1.21.1 entity spawn placement rules. |
| **Tan's Huge Trees** | 1.20.1 Forge | Configured feature / Placed feature registry format updates. |
| **Blossom Blade** | 1.20.1 Forge | Weapon attack attributes and NeoForge combat events. |
| **Better Archaeology** | 1.20.1 Forge | Brush loot tables and Decorated Pot / Sniffer archeology data maps. |
| **Nyf's Spiders** | 1.19 / 1.20 | Spider climbing mesh collision updates for 1.21.1 block models. |
| **Origins** | 1.20 Fabric/Forge | Porting power types and NeoForge keybinding / GUI layer. |

---

## 🚀 Getting Started

### 1. Requirements
- **JDK 21** (Eclipse Temurin 21 or Microsoft OpenJDK 21 required for NeoForge 1.21.1)
- **Git**
- **Python 3.8+** (for `sync_mods.py`)

### 2. Synchronize Mod Sources
To clone / submodule all 39 mod sources configured in `modpack.json`:
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
