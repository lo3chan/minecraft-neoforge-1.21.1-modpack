# Task Specification: Deep Technical Analysis of Minecraft 1.21.1 NeoForge Recipe Book Craftable Filter Desync

## 1. Problem Statement
In Minecraft NeoForge 1.21.1 with modern modded setups (e.g. Better Recipe Book Extended / RecipeBookPlus / Visual Workbench / AutoModpack):
- When a player opens a 3x3 Crafting Table (CraftingScreen / VisualCraftingMenu), items that the player has 100% of the materials for (e.g. Wooden Shovel with Wood Planks and Sticks in inventory) are rendered in red / uncraftable state.
- When toggling the 'Show Craftable' filter button in the Recipe Book GUI, the recipe book page becomes completely empty (0 recipes shown).
- This issue persisted across both RecipeBookPlus and Better Recipe Book Extended mods.

## 2. Technical Context & Subsystems to Analyze
1. **NeoForge 1.21.1 Recipe System & Tag Synchronization**:
   - In 1.21+, wooden tool and armor recipes use item tag ingredients (#minecraft:planks, #c:wooden_rods / #minecraft:stick).
   - How ClientboundUpdateTagsPacket and ClientboundUpdateRecipesPacket populate the client-side TagNetworkSerialization and RecipeManager.
   - How StackedContents.accountSimpleStack() and StackedContents.canCraft() evaluate dynamic tags vs exact items.
2. **Container Screen Lifecycle & Slot Caching**:
   - When a Crafting Table block entity or container menu (CraftingMenu / VisualCraftingMenu) opens, how fillCraftSlotsStackedContents() populates this.stackedContents.
   - In RecipeBookComponent, updateStackedContents() and updateCollections() are called.
   - Mixin interactions in BetterRecipeBookExtended (com.alonie.brbe.mixins.incompletecrafting.RecipeBookComponentMixin, com.alonie.brbe.mixins.pipeline.RecipeBookComponentMixin, com.alonie.brbe.util.CollectionPipeline, com.alonie.brbe.util.PartialCraftingUtil).
   - Slot-hash caching: brbe in betterRecipeBook skipping vanilla updateStackedContents() when container slots update concurrently.
3. **Craftable Filtering Pipeline**:
   - RecipeCollection.canCraft(StackedContents, width, height, isCreative) and RecipeCollection.hasCraftable().
   - CollectionPipeline.applyFilterToggle(List<RecipeCollection>, boolean) filtering out collections where hasCraftable() == false.

## 3. Jules Objectives (READ ONLY / NO MODIFICATIONS)
- Perform a deep code and architectural audit of the Recipe Book craftable evaluation lifecycle in 1.21.1 NeoForge.
- Trace the exact sequence of events from when a player right clicks a crafting table, slots synchronize, StackedContents is filled, canCraft() is executed on RecipeCollection, to GUI button rendering.
- Identify every potential failure point that causes canCraft() / hasCraftable() to return false despite valid ingredients being in player inventory.
- Output a detailed, structured diagnostic report explaining the root causes and recommended architectural fixes.
- DO NOT MODIFY CODE OR SUBMIT COMMITS. ONLY PERFORM ANALYSIS.
