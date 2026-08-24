# The Council — NeoForge 1.21.1 Modpack Development & Architecture Guide

Welcome to **The Council** development suite. This repository is structured as a unified monorepo for developing, compiling, patching, and maintaining the NeoForge 1.21.1 modpack.

---

## 1. Environment & Target Specifications

- **Minecraft Version**: 1.21.1
- **Modloader**: **NeoForge** 21.1.248
- **Java Version**: Java 21 (Temurin / OpenJDK 21)
- **Target Distribution Architecture**: Dedicated Server (itso, Linux x86_64 Docker container) + Client Instances (GDLauncher Carbon / Prism).
- **Mod Sync Protocol**: **AutoModpack** 4.0.6 server host (/data/automodpack) syncing to clients on connect.

---

## 2. Monorepo Directory Layout: mods/ vs sources/

To avoid any ambiguity when navigating codebases, this repository distinguishes between active deployed mod subprojects and pristine upstream Git repositories:

`	ext
the-council-neoforge-1.21.1-modpack/
├── mods/                       # [ACTIVE WORKSPACE] Deployed mod projects with full uncompiled Java & resources
│   ├── farmersdelight/         # Deployed mod project tree (src/main/java, src/main/resources, build.gradle)
│   ├── origins-0.3/            # Native Origins NeoForge codebase
│   ├── modernfix-.../          # ModernFix NeoForge codebase
│   └── ... (120+ mods)
│
├── sources/                    # [UPSTREAM REFERENCE] Untouched official upstream Git repositories
│   ├── farmersdelight/         # Pristine upstream Git clone with full history, author docs, and original commits
│   ├── balm/                   # Pristine upstream Git clone
│   └── ...
│
├── config/                     # Synchronized mod configuration files (1,194 active configs)
│   ├── DistantHorizons.toml
│   ├── jade/
│   └── yacl.json5
│
├── client-manifest.json        # Authoritative inventory of all 120 verified deployed mods
├── modpack.json                # Master manifest with upstream URLs, branches, and versions
├── settings.gradle             # Composite build discovery for all mod subprojects in /mods
├── build.gradle                # Root multi-project build definitions
└── shaderpacks/                # Synchronized shaderpacks (Eclipse Shader, etc.)
`

### Key Differences:
- **mods/<mod-id>/ (Active Edit & Build Target)**: This is where you make code modifications, add features, or adjust assets. Every folder in mods/ is included in Gradle composite builds (settings.gradle) and compiles directly into the active modpack distribution.
- **sources/<mod-id>/ (Upstream Reference & Documentation)**: These are the pristine upstream clones. Use sources/ as reference documentation for upstream architecture, API patterns, JavaDocs, and Git commit diffs.

---

## 3. Custom Patches & Interventions (MUST PRESERVE)

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

1. **Zero-Error Build Floor**: Every modified mod subproject in /mods/ must compile with 0 errors via ./gradlew build targeting NeoForge 21.1.248 and Java 21.
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

# Compile individual mod subprojects in /mods
./gradlew :mods:<mod-id>:build
`
