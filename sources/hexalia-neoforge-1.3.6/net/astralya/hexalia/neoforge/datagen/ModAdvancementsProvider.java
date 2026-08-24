package net.astralya.hexalia.neoforge.datagen;

import java.util.Optional;
import java.util.function.Consumer;
import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.Advancement.Builder;
import net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class ModAdvancementsProvider implements AdvancementSubProvider {
   private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath("hexalia", "textures/block/willow_log.png");

   public void generate(Provider registries, Consumer<AdvancementHolder> writer) {
      AdvancementHolder root = Builder.advancement()
         .display(display((ItemLike)ModItems.HEX_FOCUS.get(), "root", Optional.of(BACKGROUND), AdvancementType.TASK))
         .addCriterion("has_hex_focus", has((ItemLike)ModItems.HEX_FOCUS.get()))
         .save(writer, id("root"));
      AdvancementHolder saltOfTheCraft = advancement((ItemLike)ModItems.SALT.get(), "salt_of_the_craft", AdvancementType.TASK)
         .parent(root)
         .addCriterion("has_salt", has((ItemLike)ModItems.SALT.get()))
         .save(writer, id("salt_of_the_craft"));
      AdvancementHolder crushCourse = advancement((ItemLike)ModItems.MORTAR_AND_PESTLE.get(), "crush_course", AdvancementType.TASK)
         .parent(root)
         .addCriterion("has_mortar", has((ItemLike)ModItems.MORTAR_AND_PESTLE.get()))
         .save(writer, id("crush_course"));
      AdvancementHolder knifeToTreeYou = advancement((ItemLike)ModItems.ATHAME.get(), "knife_to_tree_you", AdvancementType.TASK)
         .parent(root)
         .addCriterion("has_athame", has((ItemLike)ModItems.ATHAME.get()))
         .save(writer, id("knife_to_tree_you"));
      AdvancementHolder tableManners = advancement((ItemLike)ModBlocks.RITUAL_TABLE.get(), "table_manners", AdvancementType.TASK)
         .parent(saltOfTheCraft)
         .addCriterion("has_ritual_table", has((ItemLike)ModBlocks.RITUAL_TABLE.get()))
         .save(writer, id("table_manners"));
      AdvancementHolder starPower = advancement((ItemLike)ModItems.CELESTIAL_CRYSTAL.get(), "star_power", AdvancementType.GOAL)
         .parent(tableManners)
         .addCriterion("has_celestial_crystal", has((ItemLike)ModItems.CELESTIAL_CRYSTAL.get()))
         .save(writer, id("star_power"));
      AdvancementHolder essenceCollector = advancement((ItemLike)ModItems.WATER_NODE.get(), "essence_collector", AdvancementType.TASK)
         .parent(tableManners)
         .addCriterion(
            "has_any_node",
            has((ItemLike)ModItems.WATER_NODE.get(), (ItemLike)ModItems.AIR_NODE.get(), (ItemLike)ModItems.EARTH_NODE.get(), (ItemLike)ModItems.FIRE_NODE.get())
         )
         .save(writer, id("essence_collector"));
      AdvancementHolder changeOfPlans = advancement((ItemLike)ModItems.MUTAVIS.get(), "change_of_plans", AdvancementType.TASK)
         .parent(crushCourse)
         .addCriterion("has_mutavis", has((ItemLike)ModItems.MUTAVIS.get()))
         .save(writer, id("change_of_plans"));
      AdvancementHolder ringOfChange = advancement((ItemLike)ModItems.MORPHORA.get(), "ring_of_change", AdvancementType.GOAL)
         .parent(changeOfPlans)
         .addCriterion("has_morphora", has((ItemLike)ModItems.MORPHORA.get()))
         .save(writer, id("ring_of_change"));
      AdvancementHolder smallBeginnings = advancement((ItemLike)ModItems.SMALL_CAULDRON.get(), "small_beginnings", AdvancementType.TASK)
         .parent(root)
         .addCriterion("has_small_cauldron", has((ItemLike)ModItems.SMALL_CAULDRON.get()))
         .save(writer, id("small_beginnings"));
      AdvancementHolder brewbieAward = advancement((ItemLike)ModItems.RUSTIC_BOTTLE.get(), "brewbie_award", AdvancementType.TASK)
         .parent(smallBeginnings)
         .addCriterion("has_rustic_bottle", has((ItemLike)ModItems.RUSTIC_BOTTLE.get()))
         .save(writer, id("brewbie_award"));
      advancement((ItemLike)ModItems.FOUL_SAC.get(), "powder_and_pouch", AdvancementType.TASK)
         .parent(brewbieAward)
         .addCriterion("has_foul_sac", has((ItemLike)ModItems.FOUL_SAC.get()))
         .save(writer, id("powder_and_pouch"));
      AdvancementHolder silkenBeginnings = advancement((ItemLike)ModItems.SILK_IDOL.get(), "silken_beginnings", AdvancementType.TASK)
         .parent(saltOfTheCraft)
         .addCriterion("has_silk_idol", has((ItemLike)ModItems.SILK_IDOL.get()))
         .save(writer, id("silken_beginnings"));
      advancement((ItemLike)ModItems.PURITY_IDOL.get(), "pure_intentions", AdvancementType.GOAL)
         .parent(silkenBeginnings)
         .addCriterion("use_purity_idol", used((Item)ModItems.PURITY_IDOL.get()))
         .save(writer, id("pure_intentions"));
      advancement((ItemLike)ModItems.KELPWEAVE_BLADE.get(), "kelp_yourself", AdvancementType.TASK)
         .parent(tableManners)
         .addCriterion("has_kelpweave_blade", has((ItemLike)ModItems.KELPWEAVE_BLADE.get()))
         .save(writer, id("kelp_yourself"));
      advancement((ItemLike)ModItems.SAGE_PENDANT.get(), "wise_investment", AdvancementType.TASK)
         .parent(tableManners)
         .addCriterion("has_sage_pendant", has((ItemLike)ModItems.SAGE_PENDANT.get()))
         .save(writer, id("wise_investment"));
      advancement((ItemLike)ModBlocks.SPIRIT_BLOOM.get(), "herb_nerd", AdvancementType.CHALLENGE)
         .parent(crushCourse)
         .addCriterion("dreamshroom", has((ItemLike)ModBlocks.DREAMSHROOM.get()))
         .addCriterion("spirit_bloom", has((ItemLike)ModBlocks.SPIRIT_BLOOM.get()))
         .addCriterion("ghost_fern", has((ItemLike)ModBlocks.GHOST_FERN.get()))
         .addCriterion("celestial_bloom", has((ItemLike)ModBlocks.CELESTIAL_BLOOM.get()))
         .addCriterion("siren_kelp", has((ItemLike)ModItems.SIREN_KELP.get()))
         .save(writer, id("herb_nerd"));
      advancement((ItemLike)ModItems.MANDRAKE.get(), "seasoned_farmer", AdvancementType.CHALLENGE)
         .parent(crushCourse)
         .addCriterion("mandrake", has((ItemLike)ModItems.MANDRAKE.get()))
         .addCriterion("sunfire_tomato", has((ItemLike)ModItems.SUNFIRE_TOMATO.get()))
         .addCriterion("galeberries", has((ItemLike)ModItems.GALEBERRIES.get()))
         .addCriterion("saltsprout", has((ItemLike)ModItems.SALTSPROUT.get()))
         .addCriterion("chillberries", has((ItemLike)ModItems.CHILLBERRIES.get()))
         .save(writer, id("seasoned_farmer"));
      advancement((ItemLike)ModItems.TEMPEST_IDOL.get(), "master_or_not", AdvancementType.GOAL)
         .parent(silkenBeginnings)
         .addCriterion("use_rain", used((Item)ModItems.RAINFALL_IDOL.get()))
         .addCriterion("use_clear", used((Item)ModItems.CLARITY_IDOL.get()))
         .addCriterion("use_storm", used((Item)ModItems.TEMPEST_IDOL.get()))
         .save(writer, id("master_or_not"));
      advancement((ItemLike)ModItems.BREW_OF_DAYBLOOM.get(), "brewed_awakening", AdvancementType.CHALLENGE)
         .parent(brewbieAward)
         .addCriterion("slimewalker", used((Item)ModItems.BREW_OF_SLIMEWALKER.get()))
         .addCriterion("bloodlust", used((Item)ModItems.BREW_OF_BLOODLUST.get()))
         .addCriterion("spikeskin", used((Item)ModItems.BREW_OF_SPIKESKIN.get()))
         .addCriterion("homestead", used((Item)ModItems.BREW_OF_HOMESTEAD.get()))
         .addCriterion("siphoning", used((Item)ModItems.BREW_OF_SIPHON.get()))
         .addCriterion("daybloom", used((Item)ModItems.BREW_OF_DAYBLOOM.get()))
         .addCriterion("arachnid_grace", used((Item)ModItems.BREW_OF_ARACHNID_GRACE.get()))
         .addCriterion("hollow_silence", used((Item)ModItems.BREW_OF_HOLLOW_SILENCE.get()))
         .save(writer, id("brewed_awakening"));
   }

   private static Builder advancement(ItemLike icon, String name, AdvancementType type) {
      return Builder.advancement().display(display(icon, name, Optional.empty(), type));
   }

   private static DisplayInfo display(ItemLike icon, String name, Optional<ResourceLocation> background, AdvancementType type) {
      return new DisplayInfo(
         new ItemStack(icon),
         Component.translatable("advancements.hexalia." + name + ".title"),
         Component.translatable("advancements.hexalia." + name + ".description"),
         background,
         type,
         true,
         true,
         false
      );
   }

   private static Criterion<TriggerInstance> has(ItemLike... items) {
      return TriggerInstance.hasItems(items);
   }

   private static Criterion<net.minecraft.advancements.critereon.ConsumeItemTrigger.TriggerInstance> used(Item item) {
      return net.minecraft.advancements.critereon.ConsumeItemTrigger.TriggerInstance.usedItem(item);
   }

   private static String id(String path) {
      return ResourceLocation.fromNamespaceAndPath("hexalia", path).toString();
   }
}
