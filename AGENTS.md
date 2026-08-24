# The Aether & Beyond — NeoForge 1.21.1 Modpack Development & Architecture Guide

Welcome to **The Aether & Beyond** development suite. This repository is structured as a unified monorepo for developing, compiling, patching, and maintaining the NeoForge 1.21.1 modpack.

---

## 1. Environment & Target Specifications

- **Minecraft Version**: 1.21.1
- **Modloader**: **NeoForge** 21.1.248
- **Java Version**: Java 21 (Temurin / OpenJDK 21)
- **Target Distribution Architecture**: Dedicated Server (itso, Linux x86_64 Docker container) + Client Instances (GDLauncher Carbon / Prism).
- **Mod Sync Protocol**: **AutoModpack** 4.0.6 server host (/data/automodpack) syncing to clients on connect.

---

## 2. Modpack Architecture & Directory Structure

`	ext
minecraft-neoforge-1.21.1-modpack/
├── client-manifest.json        # Authoritative manifest of all 120 verified mods & classifications
├── modpack.json                # Upstream repository metadata, versions, and build definitions
├── settings.gradle             # Composite build discovery for all mod sources in /mods
├── build.gradle                # Root multi-project build definitions
├── config/                     # Synchronized mod configuration files
│   ├── DistantHorizons.toml
│   ├── jade/
│   ├── physicsmod/
│   └── yacl.json5
├── resourcepacks/              # Custom resource packs
├── shaderpacks/                # Shader configurations (e.g. Eclipse Shader)
└── mods/                       # Mod source projects and binary distribution jars
`

---

## 3. Custom Patches, Fixes & Interventions Applied

When modifying or updating any mod in this pack, you **MUST** preserve these critical architectural interventions:

### 3.1. Additional Structures (AdditionalStructures-1.21-(v.6.3.2-NEO))
- **Issue**: Crashed on dedicated server initialization due to dual client-only event bus subscribers (@EventBusSubscriber(Dist.CLIENT)).
- **Fix**: Disarmed the client event bus annotations via bytecode/source patch (XventBusSubscriber), isolating client rendering logic from server startup.

### 3.2. Hexerei (hexerei-0.5.0.3)
- **Issue**: Pre-mature ClientEvents.clientTickEvent firing before NeoForge loaded config files, throwing IllegalStateException: Cannot get config value before config is loaded.
- **Fix**: Disarmed clientTickEvent listener registration (clientTickEvenX) in Hexerei.class and @XubscribeEvent in ClientEvents.class.

### 3.3. Origins (origins-0.3 + jupiter-2.3.7)
- **Architecture**: Upgraded from older Fabric ports to official native **Origins (NeoForge) v0.3** requiring **Jupiter v2.3.7**.
- **Rule**: Do NOT use Fabric/Quilt-based legacy Origin ports.

### 3.4. Grassier Grass (grassiergrass-1.4.5)
- **Distribution Scope**: **CLIENT-ONLY**.
- **Rule**: References 
et.minecraft.client.resources.model.BakedModel in its main mod class. Must never be placed in dedicated server distribution folders (/data/mods/). AutoModpack is configured to leave client-side visual mods untouched.

---

## 4. Jules Engineering Directives & Modifying Mod Sources

1. **Zero-Error Build Floor**: Every modified mod subproject must compile with 0 errors via ./gradlew build targeting NeoForge 21.1.248 and Java 21.
2. **No Destructive Simplification**: Never comment out, stub, or delete core game systems (worldgen features, structures, recipes, networking packets) to silence compiler warnings.
3. **Client vs Server Distribution Boundary**:
   - Client-only mods (Sodium, Iris, Entity Model Features, BetterF3, Grassier Grass) must only interact with client-side classes.
   - Common mods must properly guard physical client code with @OnlyIn(Dist.CLIENT) or FMLEnvironment.dist.isClient().
4. **AutoModpack Sync Integrity**: When updating server mods, verify that the jar files in /mods match the definitions in client-manifest.json and modpack.json.

---

## 5. Build & Verification Commands

`ash
# Build all discovered mod projects in the monorepo
./gradlew buildAllMods

# Compile individual mod subprojects
./gradlew :mods:<mod-id>:build
`
