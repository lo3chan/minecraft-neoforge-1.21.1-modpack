package vazkii.psi.data;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.Ingredient.ItemValue;
import net.minecraft.world.item.crafting.Ingredient.TagValue;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags.Items;
import net.neoforged.neoforge.registries.DeferredHolder;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.psi.api.recipe.TrickRecipeBuilder;
import vazkii.psi.common.Psi;
import vazkii.psi.common.block.base.ModBlocks;
import vazkii.psi.common.crafting.ModCraftingRecipes;
import vazkii.psi.common.crafting.recipe.AssemblyScavengeRecipe;
import vazkii.psi.common.crafting.recipe.BulletToDriveRecipe;
import vazkii.psi.common.crafting.recipe.BulletUpgradeRecipe;
import vazkii.psi.common.crafting.recipe.ColorizerChangeRecipe;
import vazkii.psi.common.crafting.recipe.DriveDuplicateRecipe;
import vazkii.psi.common.crafting.recipe.SensorAttachRecipe;
import vazkii.psi.common.crafting.recipe.SensorRemoveRecipe;
import vazkii.psi.common.item.base.ModItems;
import vazkii.psi.common.lib.LibResources;
import vazkii.psi.common.lib.ModTags;

public class PsiRecipeGenerator extends RecipeProvider {
   public PsiRecipeGenerator(PackOutput pOutput, CompletableFuture<Provider> pRegistries) {
      super(pOutput, pRegistries);
   }

   protected <R extends Recipe<?>> void specialRecipe(
      RecipeOutput recipeOutput, DeferredHolder<RecipeType<?>, RecipeType<R>> type, Function<CraftingBookCategory, Recipe<?>> factory
   ) {
      Recipe<?> recipe = factory.apply(CraftingBookCategory.MISC);
      ResourceLocation id = type.getId();
      recipeOutput.accept(ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "dynamic/" + id.getPath()), recipe, null);
   }

   protected void buildRecipes(RecipeOutput consumer) {
      this.specialRecipe(consumer, ModCraftingRecipes.SCAVENGE_TYPE, AssemblyScavengeRecipe::new);
      this.specialRecipe(consumer, ModCraftingRecipes.BULLET_TO_DRIVE_TYPE, BulletToDriveRecipe::new);
      this.specialRecipe(consumer, ModCraftingRecipes.COLORIZER_CHANGE_TYPE, ColorizerChangeRecipe::new);
      this.specialRecipe(consumer, ModCraftingRecipes.DRIVE_DUPLICATE_TYPE, DriveDuplicateRecipe::new);
      this.specialRecipe(consumer, ModCraftingRecipes.SENSOR_ATTACH_TYPE, SensorAttachRecipe::new);
      this.specialRecipe(consumer, ModCraftingRecipes.SENSOR_REMOVE_TYPE, SensorRemoveRecipe::new);
      Criterion<TriggerInstance> hasIron = has(Items.INGOTS_IRON);
      Criterion<TriggerInstance> hasPsimetal = has(ModTags.INGOT_PSIMETAL);
      Criterion<TriggerInstance> hasEbonyPsimetal = has(ModTags.INGOT_EBONY_PSIMETAL);
      Criterion<TriggerInstance> hasIvoryPsimetal = has(ModTags.INGOT_IVORY_PSIMETAL);
      Criterion<TriggerInstance> hasPsidust = has(ModTags.PSIDUST);
      ShapedRecipeBuilder.shaped(RecipeCategory.MISC, (ItemLike)ModBlocks.cadAssembler.get())
         .define('I', Items.INGOTS_IRON)
         .define('P', net.minecraft.world.item.Items.PISTON)
         .pattern("IPI")
         .pattern("I I")
         .pattern(" I ")
         .unlockedBy("has_iron", hasIron)
         .save(consumer, Psi.location("assembler"));
      ShapedRecipeBuilder.shaped(RecipeCategory.MISC, (ItemLike)ModBlocks.programmer.get())
         .define('I', Items.INGOTS_IRON)
         .define('D', ModTags.PSIDUST)
         .pattern("IDI")
         .pattern("I I")
         .pattern(" I ")
         .unlockedBy("has_psidust", hasPsidust)
         .save(consumer, Psi.location("programmer"));
      ShapedRecipeBuilder.shaped(RecipeCategory.MISC, (ItemLike)ModItems.ebonyPsimetal.get())
         .define('S', ModTags.EBONY_SUBSTANCE)
         .define('I', ModTags.INGOT_PSIMETAL)
         .pattern("SSS")
         .pattern("SIS")
         .pattern("SSS")
         .unlockedBy("has_ebony_substance", has((ItemLike)ModItems.ebonySubstance.get()))
         .save(consumer, Psi.location("ebony_psimetal"));
      ShapedRecipeBuilder.shaped(RecipeCategory.MISC, (ItemLike)ModItems.ivoryPsimetal.get())
         .define('S', ModTags.IVORY_SUBSTANCE)
         .define('I', ModTags.INGOT_PSIMETAL)
         .pattern("SSS")
         .pattern("SIS")
         .pattern("SSS")
         .unlockedBy("has_ivory_substance", has((ItemLike)ModItems.ivorySubstance.get()))
         .save(consumer, Psi.location("ivory_psimetal"));
      ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, (ItemLike)ModItems.cadAssemblyIron.get())
         .define('I', Items.INGOTS_IRON)
         .pattern("III")
         .pattern("I  ")
         .unlockedBy("has_iron", hasIron)
         .save(consumer, Psi.location("cad_assembly_iron"));
      ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, (ItemLike)ModItems.cadAssemblyGold.get())
         .define('I', Items.INGOTS_GOLD)
         .pattern("III")
         .pattern("I  ")
         .unlockedBy("has_gold", has(Items.INGOTS_GOLD))
         .save(consumer, Psi.location("cad_assembly_gold"));
      ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, (ItemLike)ModItems.cadAssemblyPsimetal.get())
         .define('I', ModTags.INGOT_PSIMETAL)
         .pattern("III")
         .pattern("I  ")
         .unlockedBy("has_psimetal", hasPsimetal)
         .save(consumer, Psi.location("cad_assembly_psimetal"));
      ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, (ItemLike)ModItems.cadAssemblyEbony.get())
         .define('I', ModTags.INGOT_EBONY_PSIMETAL)
         .pattern("III")
         .pattern("I  ")
         .unlockedBy("has_ebony_psimetal", hasEbonyPsimetal)
         .save(consumer, Psi.location("cad_assembly_ebony"));
      ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, (ItemLike)ModItems.cadAssemblyIvory.get())
         .define('I', ModTags.INGOT_IVORY_PSIMETAL)
         .pattern("III")
         .pattern("I  ")
         .unlockedBy("has_ivory_psimetal", hasIvoryPsimetal)
         .save(consumer, Psi.location("cad_assembly_ivory"));
      ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, (ItemLike)ModItems.cadCoreBasic.get())
         .define('I', Items.INGOTS_IRON)
         .define('D', ModTags.PSIDUST)
         .pattern(" I ")
         .pattern("IDI")
         .pattern(" I ")
         .unlockedBy("has_psidust", hasPsidust)
         .save(consumer, Psi.location("cad_core_basic"));
      ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, (ItemLike)ModItems.cadCoreOverclocked.get())
         .define('I', ModTags.INGOT_PSIMETAL)
         .define('D', Items.DUSTS_REDSTONE)
         .pattern(" I ")
         .pattern("IDI")
         .pattern(" I ")
         .unlockedBy("has_psimetal", hasPsimetal)
         .save(consumer, Psi.location("cad_core_overclocked"));
      ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, (ItemLike)ModItems.cadCoreConductive.get())
         .define('I', ModTags.INGOT_PSIMETAL)
         .define('D', Items.DUSTS_GLOWSTONE)
         .pattern(" I ")
         .pattern("IDI")
         .pattern(" I ")
         .unlockedBy("has_psimetal", hasPsimetal)
         .save(consumer, Psi.location("cad_core_conductive"));
      ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, (ItemLike)ModItems.cadCoreHyperClocked.get())
         .define('I', ModTags.INGOT_PSIMETAL)
         .define('D', Items.DUSTS_REDSTONE)
         .define('G', ModTags.GEM_PSIGEM)
         .pattern(" G ")
         .pattern("IDI")
         .pattern(" G ")
         .unlockedBy("has_psimetal", hasPsimetal)
         .save(consumer, Psi.location("cad_core_hyperclocked"));
      ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, (ItemLike)ModItems.cadCoreRadiative.get())
         .define('I', ModTags.INGOT_PSIMETAL)
         .define('D', Items.DUSTS_GLOWSTONE)
         .define('G', ModTags.GEM_PSIGEM)
         .pattern(" G ")
         .pattern("IDI")
         .pattern(" G ")
         .unlockedBy("has_psimetal", hasPsimetal)
         .save(consumer, Psi.location("cad_core_radiative"));
      ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, (ItemLike)ModItems.cadSocketBasic.get())
         .define('I', Items.INGOTS_IRON)
         .define('D', ModTags.PSIDUST)
         .pattern("DI ")
         .pattern("I  ")
         .unlockedBy("has_psidust", hasPsidust)
         .save(consumer, Psi.location("cad_socket_basic"));
      ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, (ItemLike)ModItems.cadSocketSignaling.get())
         .define('I', ModTags.INGOT_PSIMETAL)
         .define('D', Items.DUSTS_REDSTONE)
         .pattern("DI ")
         .pattern("I  ")
         .unlockedBy("has_psimetal", hasPsimetal)
         .save(consumer, Psi.location("cad_socket_signaling"));
      ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, (ItemLike)ModItems.cadSocketLarge.get())
         .define('I', ModTags.INGOT_PSIMETAL)
         .define('D', Items.DUSTS_GLOWSTONE)
         .pattern("DI ")
         .pattern("I  ")
         .unlockedBy("has_psimetal", hasPsimetal)
         .save(consumer, Psi.location("cad_socket_large"));
      ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, (ItemLike)ModItems.cadSocketTransmissive.get())
         .define('I', ModTags.INGOT_PSIMETAL)
         .define('D', Items.DUSTS_REDSTONE)
         .define('G', ModTags.GEM_PSIGEM)
         .pattern("DI ")
         .pattern("IG ")
         .unlockedBy("has_psimetal", hasPsimetal)
         .save(consumer, Psi.location("cad_socket_transmissive"));
      ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, (ItemLike)ModItems.cadSocketHuge.get())
         .define('I', ModTags.INGOT_PSIMETAL)
         .define('D', Items.DUSTS_GLOWSTONE)
         .define('G', ModTags.GEM_PSIGEM)
         .pattern("DI ")
         .pattern("IG ")
         .unlockedBy("has_psimetal", hasPsimetal)
         .save(consumer, Psi.location("cad_socket_huge"));
      ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, (ItemLike)ModItems.cadBatteryBasic.get())
         .define('I', Items.INGOTS_IRON)
         .define('D', ModTags.PSIDUST)
         .define('G', Items.INGOTS_GOLD)
         .pattern("I")
         .pattern("D")
         .pattern("G")
         .unlockedBy("has_psidust", hasPsidust)
         .save(consumer, Psi.location("cad_battery_basic"));
      ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, (ItemLike)ModItems.cadBatteryExtended.get())
         .define('I', Items.INGOTS_IRON)
         .define('D', ModTags.INGOT_PSIMETAL)
         .define('G', Items.INGOTS_GOLD)
         .pattern("I")
         .pattern("D")
         .pattern("G")
         .unlockedBy("has_psimetal", hasPsimetal)
         .save(consumer, Psi.location("cad_battery_extended"));
      ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, (ItemLike)ModItems.cadBatteryUltradense.get())
         .define('I', Items.INGOTS_IRON)
         .define('D', ModTags.GEM_PSIGEM)
         .define('G', Items.INGOTS_GOLD)
         .pattern("I")
         .pattern("D")
         .pattern("G")
         .unlockedBy("has_psimetal", hasPsimetal)
         .save(consumer, Psi.location("cad_battery_ultradense"));

      for (DyeColor color : DyeColor.values()) {
         ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, (ItemLike)BuiltInRegistries.ITEM.get(Psi.location("cad_colorizer_" + color.getSerializedName())))
            .group("psi:colorizer")
            .define('D', ModTags.PSIDUST)
            .define('I', Items.INGOTS_IRON)
            .define('G', Items.GLASS_BLOCKS)
            .define('C', color.getTag())
            .pattern(" D ")
            .pattern("GCG")
            .pattern(" I ")
            .unlockedBy("has_psidust", hasPsidust)
            .save(consumer, Psi.location("cad_colorizer_" + color.getSerializedName()));
      }

      ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, (ItemLike)ModItems.cadColorizerRainbow.get())
         .group("psi:colorizer")
         .define('D', ModTags.PSIDUST)
         .define('I', Items.INGOTS_IRON)
         .define('G', Items.GLASS_BLOCKS)
         .define('C', Items.GEMS_PRISMARINE)
         .pattern(" D ")
         .pattern("GCG")
         .pattern(" I ")
         .unlockedBy("has_psidust", hasPsidust)
         .save(consumer, Psi.location("cad_colorizer_rainbow"));
      ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, (ItemLike)ModItems.cadColorizerPsi.get())
         .group("psi:colorizer")
         .define('D', ModTags.PSIDUST)
         .define('I', Items.INGOTS_IRON)
         .define('G', Items.GLASS_BLOCKS)
         .define('C', ModTags.PSIDUST)
         .pattern(" D ")
         .pattern("GCG")
         .pattern(" I ")
         .unlockedBy("has_psidust", hasPsidust)
         .save(consumer, Psi.location("cad_colorizer_psi"));
      ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, (ItemLike)ModItems.spellBullet.get())
         .define('I', Items.INGOTS_IRON)
         .define('D', ModTags.PSIDUST)
         .pattern("ID")
         .unlockedBy("has_psidust", hasPsidust)
         .save(consumer, Psi.location("spell_bullet_basic"));
      ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, (ItemLike)ModItems.projectileSpellBullet.get())
         .define('I', Items.INGOTS_IRON)
         .define('D', ModTags.PSIDUST)
         .define('A', ItemTags.ARROWS)
         .pattern("AID")
         .unlockedBy("has_psidust", hasPsidust)
         .save(consumer, Psi.location("spell_bullet_projectile"));
      new BulletUpgradeRecipe.Builder((Item)ModItems.projectileSpellBullet.get())
         .requires((ItemLike)ModItems.spellBullet.get())
         .requires(Ingredient.of(ItemTags.ARROWS))
         .unlockedBy("has_psidust", has((ItemLike)ModItems.psidust.get()))
         .save(consumer, Psi.location("spell_bullet_projectile_upgrade"));
      ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, (ItemLike)ModItems.loopSpellBullet.get())
         .define('I', Items.INGOTS_IRON)
         .define('D', ModTags.PSIDUST)
         .define('A', Items.STRINGS)
         .pattern("AID")
         .unlockedBy("has_psidust", hasPsidust)
         .save(consumer, Psi.location("spell_bullet_loopcast"));
      new BulletUpgradeRecipe.Builder((Item)ModItems.loopSpellBullet.get())
         .requires((ItemLike)ModItems.spellBullet.get())
         .requires(Ingredient.of(Items.STRINGS))
         .unlockedBy("has_psidust", has((ItemLike)ModItems.psidust.get()))
         .save(consumer, Psi.location("spell_bullet_loopcast_upgrade"));
      ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, (ItemLike)ModItems.circleSpellBullet.get())
         .define('I', Items.INGOTS_IRON)
         .define('D', ModTags.PSIDUST)
         .define('A', Ingredient.fromValues(Stream.of(new TagValue(Items.SLIME_BALLS), new ItemValue(new ItemStack(net.minecraft.world.item.Items.SNOWBALL)))))
         .pattern("AID")
         .unlockedBy("has_psidust", hasPsidust)
         .save(consumer, Psi.location("spell_bullet_circle"));
      new BulletUpgradeRecipe.Builder((Item)ModItems.circleSpellBullet.get())
         .requires((ItemLike)ModItems.spellBullet.get())
         .requires(Ingredient.fromValues(Stream.of(new TagValue(Items.SLIME_BALLS), new ItemValue(new ItemStack(net.minecraft.world.item.Items.SNOWBALL)))))
         .unlockedBy("has_psidust", has((ItemLike)ModItems.psidust.get()))
         .save(consumer, Psi.location("spell_bullet_circle_upgrade"));
      ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, (ItemLike)ModItems.grenadeSpellBullet.get())
         .define('I', Items.INGOTS_IRON)
         .define('D', ModTags.PSIDUST)
         .define('A', Items.GUNPOWDERS)
         .pattern("AID")
         .unlockedBy("has_psidust", hasPsidust)
         .save(consumer, Psi.location("spell_bullet_grenade"));
      new BulletUpgradeRecipe.Builder((Item)ModItems.grenadeSpellBullet.get())
         .requires((ItemLike)ModItems.spellBullet.get())
         .requires(Ingredient.of(Items.GUNPOWDERS))
         .unlockedBy("has_psidust", has((ItemLike)ModItems.psidust.get()))
         .save(consumer, Psi.location("spell_bullet_grenade_upgrade"));
      ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, (ItemLike)ModItems.chargeSpellBullet.get())
         .define('I', Items.INGOTS_IRON)
         .define('D', ModTags.PSIDUST)
         .define('A', Items.DUSTS_REDSTONE)
         .pattern("AID")
         .unlockedBy("has_psidust", hasPsidust)
         .save(consumer, Psi.location("spell_bullet_charge"));
      new BulletUpgradeRecipe.Builder((Item)ModItems.chargeSpellBullet.get())
         .requires((ItemLike)ModItems.spellBullet.get())
         .requires(Ingredient.of(Items.DUSTS_REDSTONE))
         .unlockedBy("has_psidust", has((ItemLike)ModItems.psidust.get()))
         .save(consumer, Psi.location("spell_bullet_charge_upgrade"));
      ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, (ItemLike)ModItems.mineSpellBullet.get())
         .define('I', Items.INGOTS_IRON)
         .define('D', ModTags.PSIDUST)
         .define('A', ItemTags.BUTTONS)
         .pattern("AID")
         .unlockedBy("has_psidust", hasPsidust)
         .save(consumer, Psi.location("spell_bullet_mine"));
      new BulletUpgradeRecipe.Builder((Item)ModItems.mineSpellBullet.get())
         .requires((ItemLike)ModItems.spellBullet.get())
         .requires(Ingredient.of(ItemTags.BUTTONS))
         .unlockedBy("has_psidust", has((ItemLike)ModItems.psidust.get()))
         .save(consumer, Psi.location("spell_bullet_mine_upgrade"));
      ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, (ItemLike)ModItems.spellDrive.get())
         .define('I', ModTags.INGOT_PSIMETAL)
         .define('R', Items.DUSTS_REDSTONE)
         .pattern("I")
         .pattern("R")
         .pattern("I")
         .unlockedBy("has_psimetal", hasPsimetal)
         .save(consumer, Psi.location("spell_drive"));
      ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, (ItemLike)ModItems.psimetalShovel.get())
         .define('P', ModTags.INGOT_PSIMETAL)
         .define('G', ModTags.GEM_PSIGEM)
         .define('I', Items.INGOTS_IRON)
         .pattern("GP")
         .pattern(" I")
         .pattern(" I")
         .unlockedBy("has_psimetal", hasPsimetal)
         .save(consumer, Psi.location("psimetal_shovel"));
      ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, (ItemLike)ModItems.psimetalPickaxe.get())
         .define('P', ModTags.INGOT_PSIMETAL)
         .define('G', ModTags.GEM_PSIGEM)
         .define('I', Items.INGOTS_IRON)
         .pattern("PGP")
         .pattern(" I ")
         .pattern(" I ")
         .unlockedBy("has_psimetal", hasPsimetal)
         .save(consumer, Psi.location("psimetal_pickaxe"));
      ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, (ItemLike)ModItems.psimetalAxe.get())
         .define('P', ModTags.INGOT_PSIMETAL)
         .define('G', ModTags.GEM_PSIGEM)
         .define('I', Items.INGOTS_IRON)
         .pattern("GP")
         .pattern("PI")
         .pattern(" I")
         .unlockedBy("has_psimetal", hasPsimetal)
         .save(consumer, Psi.location("psimetal_axe"));
      ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, (ItemLike)ModItems.psimetalSword.get())
         .define('P', ModTags.INGOT_PSIMETAL)
         .define('G', ModTags.GEM_PSIGEM)
         .define('I', Items.INGOTS_IRON)
         .pattern("P")
         .pattern("G")
         .pattern("I")
         .unlockedBy("has_psimetal", hasPsimetal)
         .save(consumer, Psi.location("psimetal_sword"));
      ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, (ItemLike)ModItems.psimetalExosuitHelmet.get())
         .define('P', ModTags.INGOT_PSIMETAL)
         .define('G', ModTags.GEM_PSIGEM)
         .pattern("GPG")
         .pattern("P P")
         .unlockedBy("has_psimetal", hasPsimetal)
         .save(consumer, Psi.location("psimetal_exosuit_helmet"));
      ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, (ItemLike)ModItems.psimetalExosuitChestplate.get())
         .define('P', ModTags.INGOT_PSIMETAL)
         .define('G', ModTags.GEM_PSIGEM)
         .pattern("P P")
         .pattern("GPG")
         .pattern("PPP")
         .unlockedBy("has_psimetal", hasPsimetal)
         .save(consumer, Psi.location("psimetal_exosuit_chestplate"));
      ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, (ItemLike)ModItems.psimetalExosuitLeggings.get())
         .define('P', ModTags.INGOT_PSIMETAL)
         .define('G', ModTags.GEM_PSIGEM)
         .pattern("GPG")
         .pattern("P P")
         .pattern("P P")
         .unlockedBy("has_psimetal", hasPsimetal)
         .save(consumer, Psi.location("psimetal_exosuit_leggings"));
      ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, (ItemLike)ModItems.psimetalExosuitBoots.get())
         .define('P', ModTags.INGOT_PSIMETAL)
         .define('G', ModTags.GEM_PSIGEM)
         .pattern("G G")
         .pattern("P P")
         .unlockedBy("has_psimetal", hasPsimetal)
         .save(consumer, Psi.location("psimetal_exosuit_boots"));
      ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, (ItemLike)ModItems.flashRing.get())
         .define('E', ModTags.INGOT_EBONY_PSIMETAL)
         .define('G', Items.DUSTS_GLOWSTONE)
         .define('P', ModTags.GEM_PSIGEM)
         .pattern(" E ")
         .pattern("EGE")
         .pattern(" P ")
         .unlockedBy("has_ebony_psimetal", hasEbonyPsimetal)
         .save(consumer, Psi.location("flash_ring"));
      ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, (ItemLike)ModItems.detonator.get())
         .define('P', ModTags.PSIDUST)
         .define('B', ItemTags.BUTTONS)
         .define('I', Items.INGOTS_IRON)
         .pattern(" B ")
         .pattern("IPI")
         .unlockedBy("has_psimetal", hasPsimetal)
         .save(consumer, Psi.location("detonator"));
      ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, (ItemLike)ModItems.exosuitController.get())
         .define('R', Items.DUSTS_REDSTONE)
         .define('G', Items.GLASS_BLOCKS)
         .define('I', ModTags.INGOT_PSIMETAL)
         .pattern("R")
         .pattern("G")
         .pattern("I")
         .unlockedBy("has_psimetal", hasPsimetal)
         .save(consumer, Psi.location("exosuit_controller"));
      ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, (ItemLike)ModItems.vectorRuler.get())
         .define('D', ModTags.PSIDUST)
         .define('I', Items.INGOTS_IRON)
         .pattern("D")
         .pattern("I")
         .pattern("I")
         .unlockedBy("has_psidust", hasPsidust)
         .save(consumer, Psi.location("vector_ruler"));
      ShapedRecipeBuilder.shaped(RecipeCategory.MISC, (ItemLike)ModItems.exosuitSensorLight.get())
         .define('M', Items.DUSTS_GLOWSTONE)
         .define('R', Items.INGOTS_IRON)
         .define('I', ModTags.INGOT_PSIMETAL)
         .pattern(" I ")
         .pattern("IMR")
         .pattern(" R ")
         .unlockedBy("has_psimetal", hasPsimetal)
         .save(consumer, Psi.location("exosuit_sensor_light"));
      ShapedRecipeBuilder.shaped(RecipeCategory.MISC, (ItemLike)ModItems.exosuitSensorWater.get())
         .define('M', Items.GEMS_PRISMARINE)
         .define('R', Items.INGOTS_IRON)
         .define('I', ModTags.INGOT_PSIMETAL)
         .pattern(" I ")
         .pattern("IMR")
         .pattern(" R ")
         .unlockedBy("has_psimetal", hasPsimetal)
         .save(consumer, Psi.location("exosuit_sensor_water"));
      ShapedRecipeBuilder.shaped(RecipeCategory.MISC, (ItemLike)ModItems.exosuitSensorHeat.get())
         .define('M', net.minecraft.world.item.Items.FIRE_CHARGE)
         .define('R', Items.INGOTS_IRON)
         .define('I', ModTags.INGOT_PSIMETAL)
         .pattern(" I ")
         .pattern("IMR")
         .pattern(" R ")
         .unlockedBy("has_psimetal", hasPsimetal)
         .save(consumer, Psi.location("exosuit_sensor_heat"));
      ShapedRecipeBuilder.shaped(RecipeCategory.MISC, (ItemLike)ModItems.exosuitSensorStress.get())
         .define('M', net.minecraft.world.item.Items.GLISTERING_MELON_SLICE)
         .define('R', Items.INGOTS_IRON)
         .define('I', ModTags.INGOT_PSIMETAL)
         .pattern(" I ")
         .pattern("IMR")
         .pattern(" R ")
         .unlockedBy("has_psimetal", hasPsimetal)
         .save(consumer, Psi.location("exosuit_sensor_stress"));
      ShapedRecipeBuilder.shaped(RecipeCategory.MISC, (ItemLike)ModItems.exosuitSensorTrigger.get())
         .define('M', net.minecraft.world.item.Items.GUNPOWDER)
         .define('R', Items.INGOTS_IRON)
         .define('I', ModTags.INGOT_PSIMETAL)
         .pattern(" I ")
         .pattern("IMR")
         .pattern(" R ")
         .unlockedBy("has_psimetal", hasPsimetal)
         .save(consumer, Psi.location("exosuit_sensor_trigger"));
      ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, (ItemLike)ModItems.cadColorizerEmpty.get())
         .define('D', ModTags.PSIDUST)
         .define('G', Items.GLASS_BLOCKS)
         .define('I', Items.INGOTS_IRON)
         .pattern(" D ")
         .pattern("G G")
         .pattern(" I ")
         .unlockedBy("has_psidust", hasPsidust)
         .save(consumer, Psi.location("cad_colorizer_empty"));
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ((Block)ModBlocks.psidustBlock.get()).asItem())
         .define('I', (ItemLike)ModItems.psidust.get())
         .pattern("III")
         .pattern("III")
         .pattern("III")
         .unlockedBy("has_psidust", hasPsidust)
         .save(consumer, Psi.location("psidust_block"));
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ((Block)ModBlocks.psimetalBlock.get()).asItem())
         .define('I', (ItemLike)ModItems.psimetal.get())
         .pattern("III")
         .pattern("III")
         .pattern("III")
         .unlockedBy("has_psimetal", hasPsimetal)
         .save(consumer, Psi.location("psimetal_block"));
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ((Block)ModBlocks.psigemBlock.get()).asItem())
         .define('I', (ItemLike)ModItems.psigem.get())
         .pattern("III")
         .pattern("III")
         .pattern("III")
         .unlockedBy("has_psigem", has((ItemLike)ModItems.psigem.get()))
         .save(consumer, Psi.location("psigem_block"));
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ((Block)ModBlocks.psimetalEbony.get()).asItem())
         .define('I', (ItemLike)ModItems.ebonyPsimetal.get())
         .pattern("III")
         .pattern("III")
         .pattern("III")
         .unlockedBy("has_ebony_psimetal", hasEbonyPsimetal)
         .save(consumer, Psi.location("ebony_block"));
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ((Block)ModBlocks.psimetalIvory.get()).asItem())
         .define('I', (ItemLike)ModItems.ivoryPsimetal.get())
         .pattern("III")
         .pattern("III")
         .pattern("III")
         .unlockedBy("has_ivory_psimetal", hasIvoryPsimetal)
         .save(consumer, Psi.location("ivory_block"));
      ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, (ItemLike)ModItems.psidust.get(), 9)
         .requires(((Block)ModBlocks.psidustBlock.get()).asItem())
         .unlockedBy("has_psidust", hasPsidust)
         .save(consumer, Psi.location("psidust_shapeless"));
      ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, (ItemLike)ModItems.psimetal.get(), 9)
         .requires(((Block)ModBlocks.psimetalBlock.get()).asItem())
         .unlockedBy("has_psimetal", hasPsimetal)
         .save(consumer, Psi.location("psimetal_shapeless"));
      ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, (ItemLike)ModItems.psigem.get(), 9)
         .requires(((Block)ModBlocks.psigemBlock.get()).asItem())
         .unlockedBy("has_psigem", has((ItemLike)ModItems.psigem.get()))
         .save(consumer, Psi.location("psigem_shapeless"));
      ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, (ItemLike)ModItems.ebonyPsimetal.get(), 9)
         .requires(((Block)ModBlocks.psimetalEbony.get()).asItem())
         .unlockedBy("has_ebony_psimetal", hasEbonyPsimetal)
         .save(consumer, Psi.location("ebony_ingot_shapeless"));
      ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, (ItemLike)ModItems.ivoryPsimetal.get(), 9)
         .requires(((Block)ModBlocks.psimetalIvory.get()).asItem())
         .unlockedBy("has_ivory_psimetal", hasIvoryPsimetal)
         .save(consumer, Psi.location("ivory_ingot_shapeless"));
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ((Block)ModBlocks.psimetalPlateBlack.get()).asItem())
         .define('C', ItemTags.COALS)
         .define('I', ModTags.INGOT_PSIMETAL)
         .pattern(" C ")
         .pattern("CIC")
         .pattern(" C ")
         .unlockedBy("has_psimetal", hasPsimetal)
         .save(consumer, Psi.location("psimetal_plate_black"));
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ((Block)ModBlocks.psimetalPlateWhite.get()).asItem())
         .define('C', Items.GEMS_QUARTZ)
         .define('I', ModTags.INGOT_PSIMETAL)
         .pattern(" C ")
         .pattern("CIC")
         .pattern(" C ")
         .unlockedBy("has_psimetal", hasPsimetal)
         .save(consumer, Psi.location("psimetal_plate_white"));
      ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, ((Block)ModBlocks.psimetalPlateBlackLight.get()).asItem())
         .requires(Items.DUSTS_GLOWSTONE)
         .requires(((Block)ModBlocks.psimetalPlateBlack.get()).asItem())
         .unlockedBy("has_psimetal", hasPsimetal)
         .save(consumer, Psi.location("psimetal_plate_black_light"));
      ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, ((Block)ModBlocks.psimetalPlateWhiteLight.get()).asItem())
         .requires(Items.DUSTS_GLOWSTONE)
         .requires(((Block)ModBlocks.psimetalPlateWhite.get()).asItem())
         .unlockedBy("has_psimetal", hasPsimetal)
         .save(consumer, Psi.location("psimetal_plate_white_light"));
      this.buildTrickRecipes(consumer);
   }

   protected void buildTrickRecipes(RecipeOutput consumer) {
      TrickRecipeBuilder.of((ItemLike)ModItems.psidust.get()).input(Items.DUSTS_REDSTONE).cad((ItemLike)ModItems.cadAssemblyIron.get()).build(consumer);
      TrickRecipeBuilder.of(PatchouliAPI.get().getBookStack(LibResources.PATCHOULI_BOOK))
         .input(net.minecraft.world.item.Items.BOOK)
         .cad((ItemLike)ModItems.cadAssemblyIron.get())
         .build(consumer);
      TrickRecipeBuilder.of((ItemLike)ModItems.cadAssemblyPsimetal.get())
         .input((ItemLike)ModItems.cadAssemblyGold.get())
         .trick(Psi.location("trick_infusion"))
         .cad((ItemLike)ModItems.cadAssemblyIron.get())
         .unlockedBy(getHasName((ItemLike)ModItems.cadAssemblyGold.get()), has((ItemLike)ModItems.cadAssemblyGold.get()))
         .build(consumer, Psi.location("gold_to_psimetal_assembly_upgrade"));
      TrickRecipeBuilder.of((ItemLike)ModItems.psimetal.get())
         .input(Items.INGOTS_GOLD)
         .trick(Psi.location("trick_infusion"))
         .cad((ItemLike)ModItems.cadAssemblyIron.get())
         .unlockedBy("has_gold_ingot", has(Items.INGOTS_GOLD))
         .build(consumer);
      TrickRecipeBuilder.of((ItemLike)ModItems.psigem.get())
         .input(Items.GEMS_DIAMOND)
         .trick(Psi.location("trick_greater_infusion"))
         .cad((ItemLike)ModItems.cadAssemblyPsimetal.get())
         .unlockedBy("has_diamond", has(Items.GEMS_DIAMOND))
         .build(consumer);
      TrickRecipeBuilder.of((ItemLike)ModItems.ebonySubstance.get())
         .input(ItemTags.COALS)
         .trick(Psi.location("trick_ebony_ivory"))
         .cad((ItemLike)ModItems.cadAssemblyPsimetal.get())
         .dimension(Level.END)
         .unlockedBy("has_coal", has(ItemTags.COALS))
         .build(consumer);
      TrickRecipeBuilder.of((ItemLike)ModItems.ivorySubstance.get())
         .input(Items.GEMS_QUARTZ)
         .trick(Psi.location("trick_ebony_ivory"))
         .cad((ItemLike)ModItems.cadAssemblyPsimetal.get())
         .dimension(Level.END)
         .unlockedBy("has_quartz", has(Items.GEMS_QUARTZ))
         .build(consumer);
   }
}
