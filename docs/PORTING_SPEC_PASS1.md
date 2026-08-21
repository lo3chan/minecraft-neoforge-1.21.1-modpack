# NeoForge 1.21.1 Mod Modernization & Porting Specification (Pass 1)

This specification defines the migration rules and technical implementation standards for modernizing 1.20.1 Forge mod source trees to **Minecraft NeoForge 1.21.1 (Java 21)**.

---

## 1. Technical Baseline

- **Minecraft**: `1.21.1`
- **ModLoader**: `NeoForge 21.1.72` (or latest `21.1.x`)
- **Java**: `JDK 21` (`options.release.set(21)`)
- **Gradle Plugin**: `net.neoforged.moddev` (ModDevGradle)
- **Mappings**: ParchmentMC 1.21.1 + Official Mojang Mappings

---

## 2. Universal 1.21.1 Migration Patterns

### A. Data Components (Replacing NBT `ItemStack.getTag()`)
In Minecraft 1.20.5+ / 1.21.1, arbitrary NBT tags on `ItemStack` are replaced with typed `DataComponentType`:
- Old: `stack.getTag().getInt("custom_key")`
- New: `stack.get(MyDataComponents.CUSTOM_KEY)` / `stack.set(MyDataComponents.CUSTOM_KEY, value)`
- Registration: Register components via `DeferredRegister.DataComponents`.

### B. Data Attachments (Replacing Forge Capabilities)
Old capability providers (`ICapabilityProvider`) are replaced by NeoForge's Data Attachment system:
- Registration: `AttachmentType.builder(() -> new MyData()).serialize(Codec).build()` registered to `NeoForgeRegistries.ATTACHMENT_TYPES`.
- Access: `entity.getData(MyAttachmentTypes.DATA)` / `entity.setData(MyAttachmentTypes.DATA, value)`.

### C. Decoupled Registries
- Replace static `IEventBus` calls with NeoForge decoupled `RegisterEvent` or `DeferredRegister.Blocks`, `DeferredRegister.Items`, `DeferredRegister.Entities`.

### D. Item Attributes
- Old: `Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack)`
- New: `ItemAttributeModifiers` configured via item properties or components: `Item.Properties.attributes(ItemAttributeModifiers)`.

### E. Networking (Payloads)
- Old: `SimpleChannel` with `PacketDistributor.PLAYER.with(...)`
- New: Implement `CustomPacketPayload` with `Type<T>` and `StreamCodec<RegistryFriendlyByteBuf, T>`.
- Register via `PayloadRegistrar` in `RegisterPayloadHandlersEvent`.

---

## 3. Targeted Mods for Pass 1

### 1. Tan's Huge Trees (`mods/tanshugetrees`)
- **Objective**: Modernize tree feature generation.
- Update `PlacedFeature` and `ConfiguredFeature` JSONs to 1.21.1 data pack registry format.
- Update custom `FoliagePlacerType` and `TrunkPlacerType` registrations to NeoForge 1.21.1.

### 2. Blossom Blade (`mods/blossomblade`)
- **Objective**: Modernize weapons and combat integration.
- Migrate item tool properties to `ItemAttributeModifiers` and 1.21 `ToolMaterial`.
- Wire custom swing animations to NeoForge 1.21.1 attack events.

### 3. Rotten Creatures (`mods/rottencreatures`)
- **Objective**: Modernize undead entity variations.
- Register entity attributes via `EntityAttributeCreationEvent`.
- Update spawn placements via `RegisterSpawnPlacementsEvent` with 1.21.1 `SpawnPlacementType`.

### 4. Alchemancy (`mods/alchemancy`)
- **Objective**: Modernize transmutation and alchemy mechanics.
- Replace Forge capability handlers with NeoForge `AttachmentType`.
- Convert custom recipe serializers to `MapCodec<T>`.

---

## 4. Verification & Zero-Error Build Quality Floor
- All code must compile cleanly with `./gradlew build` / `./gradlew compileJava` (Exit Code 0).
- No features, entities, or transmutation systems may be deleted or commented out.
