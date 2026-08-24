package com.aetherteam.aether.data.generators;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.advancement.IncubationTrigger;
import com.aetherteam.aether.advancement.LoreTrigger;
import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.data.resources.registries.AetherDimensions;
import com.aetherteam.aether.data.resources.registries.AetherMoaTypes;
import com.aetherteam.aether.entity.AetherEntityTypes;
import com.aetherteam.aether.event.hooks.AbilityHooks;
import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.loot.AetherLoot;
import com.aetherteam.aether.mixin.mixins.common.accessor.HoeItemAccessor;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.AdvancementRequirements.Strategy;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.advancements.critereon.TagPredicate;
import net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger.TriggerInstance;
import net.minecraft.advancements.critereon.LocationPredicate.Builder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.predicates.LocationCheck;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.AdvancementProvider.AdvancementGenerator;

public class AetherAdvancementData extends AdvancementProvider {
   public AetherAdvancementData(PackOutput output, CompletableFuture<Provider> registries, ExistingFileHelper helper) {
      super(output, registries, helper, List.of(new AetherAdvancementData.AetherAdvancements()));
   }

   private static TriggerInstance itemUsedOnLocationCheckAbove(Builder location, Builder above, net.minecraft.advancements.critereon.ItemPredicate.Builder item) {
      ContextAwarePredicate contextawarepredicate = ContextAwarePredicate.create(
         new LootItemCondition[]{
            LocationCheck.checkLocation(location).build(),
            LocationCheck.checkLocation(above, BlockPos.ZERO.above()).build(),
            MatchTool.toolMatches(item).build()
         }
      );
      return new TriggerInstance(Optional.empty(), Optional.of(contextawarepredicate));
   }

   public static Criterion<TriggerInstance> itemUsedOnBlockCheckAbove(
      Builder location, Builder above, net.minecraft.advancements.critereon.ItemPredicate.Builder item
   ) {
      return CriteriaTriggers.ITEM_USED_ON_BLOCK.createCriterion(itemUsedOnLocationCheckAbove(location, above, item));
   }

   public static class AetherAdvancements implements AdvancementGenerator {
      public void generate(Provider provider, Consumer<AdvancementHolder> consumer, ExistingFileHelper existingFileHelper) {
         AdvancementHolder theAether = net.minecraft.advancements.Advancement.Builder.advancement()
            .display(
               (ItemLike)AetherItems.AETHER_PORTAL_FRAME.get(),
               Component.translatable("advancement.aether.the_aether"),
               Component.translatable("advancement.aether.the_aether.desc"),
               ResourceLocation.fromNamespaceAndPath("aether", "textures/block/dungeon/carved_stone.png"),
               AdvancementType.TASK,
               false,
               false,
               false
            )
            .addCriterion(
               "the_aether", net.minecraft.advancements.critereon.ChangeDimensionTrigger.TriggerInstance.changedDimensionTo(AetherDimensions.AETHER_LEVEL)
            )
            .save(consumer, ResourceLocation.fromNamespaceAndPath("aether", "the_aether"), existingFileHelper);
         AdvancementHolder enterAether = net.minecraft.advancements.Advancement.Builder.advancement()
            .parent(theAether)
            .display(
               Blocks.GLOWSTONE,
               Component.translatable("advancement.aether.enter_aether"),
               Component.translatable("advancement.aether.enter_aether.desc"),
               null,
               AdvancementType.TASK,
               true,
               true,
               false
            )
            .addCriterion(
               "enter_aether", net.minecraft.advancements.critereon.ChangeDimensionTrigger.TriggerInstance.changedDimensionTo(AetherDimensions.AETHER_LEVEL)
            )
            .rewards(new AdvancementRewards(0, List.of(AetherLoot.ENTER_AETHER), List.of(), Optional.empty()))
            .save(consumer, ResourceLocation.fromNamespaceAndPath("aether", "enter_aether"), existingFileHelper);
         AdvancementHolder readLore = net.minecraft.advancements.Advancement.Builder.advancement()
            .parent(enterAether)
            .display(
               (ItemLike)AetherItems.BOOK_OF_LORE.get(),
               Component.translatable("advancement.aether.read_lore"),
               Component.translatable("advancement.aether.read_lore.desc"),
               null,
               AdvancementType.TASK,
               true,
               true,
               false
            )
            .addCriterion("lore_book_entry", LoreTrigger.Instance.forAny())
            .save(consumer, ResourceLocation.fromNamespaceAndPath("aether", "read_lore"), existingFileHelper);
         AdvancementHolder loreception = net.minecraft.advancements.Advancement.Builder.advancement()
            .parent(readLore)
            .display(
               (ItemLike)AetherItems.BOOK_OF_LORE.get(),
               Component.translatable("advancement.aether.loreception"),
               Component.translatable("advancement.aether.loreception.desc"),
               null,
               AdvancementType.GOAL,
               true,
               true,
               true
            )
            .addCriterion("lore_book_entry", LoreTrigger.Instance.forItem((ItemLike)AetherItems.BOOK_OF_LORE.get()))
            .save(consumer, ResourceLocation.fromNamespaceAndPath("aether", "loreception"), existingFileHelper);
         AdvancementHolder zanite = net.minecraft.advancements.Advancement.Builder.advancement()
            .parent(enterAether)
            .display(
               (ItemLike)AetherItems.ZANITE_GEMSTONE.get(),
               Component.translatable("advancement.aether.zanite"),
               Component.translatable("advancement.aether.zanite.desc"),
               null,
               AdvancementType.TASK,
               true,
               true,
               false
            )
            .addCriterion(
               "zanite",
               net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance.hasItems(new ItemLike[]{(ItemLike)AetherItems.ZANITE_GEMSTONE.get()})
            )
            .save(consumer, ResourceLocation.fromNamespaceAndPath("aether", "zanite"), existingFileHelper);
         AdvancementHolder craftAltar = net.minecraft.advancements.Advancement.Builder.advancement()
            .parent(zanite)
            .display(
               (ItemLike)AetherBlocks.ALTAR.get(),
               Component.translatable("advancement.aether.craft_altar"),
               Component.translatable("advancement.aether.craft_altar.desc"),
               null,
               AdvancementType.TASK,
               true,
               true,
               false
            )
            .addCriterion(
               "craft_altar",
               net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance.hasItems(new ItemLike[]{(ItemLike)AetherBlocks.ALTAR.get()})
            )
            .save(consumer, ResourceLocation.fromNamespaceAndPath("aether", "craft_altar"), existingFileHelper);
         AdvancementHolder icestone = net.minecraft.advancements.Advancement.Builder.advancement()
            .parent(craftAltar)
            .display(
               (ItemLike)AetherBlocks.ICESTONE.get(),
               Component.translatable("advancement.aether.icestone"),
               Component.translatable("advancement.aether.icestone.desc"),
               null,
               AdvancementType.TASK,
               true,
               true,
               false
            )
            .addCriterion(
               "icestone",
               net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance.hasItems(new ItemLike[]{(ItemLike)AetherBlocks.ICESTONE.get()})
            )
            .save(consumer, ResourceLocation.fromNamespaceAndPath("aether", "icestone"), existingFileHelper);
         AdvancementHolder iceAccessory = net.minecraft.advancements.Advancement.Builder.advancement()
            .parent(icestone)
            .display(
               (ItemLike)AetherItems.ICE_PENDANT.get(),
               Component.translatable("advancement.aether.ice_accessory"),
               Component.translatable("advancement.aether.ice_accessory.desc"),
               null,
               AdvancementType.TASK,
               true,
               true,
               false
            )
            .requirements(Strategy.OR)
            .addCriterion(
               "ice_pendant",
               net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance.hasItems(new ItemLike[]{(ItemLike)AetherItems.ICE_PENDANT.get()})
            )
            .addCriterion(
               "ice_ring",
               net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance.hasItems(new ItemLike[]{(ItemLike)AetherItems.ICE_RING.get()})
            )
            .save(consumer, ResourceLocation.fromNamespaceAndPath("aether", "ice_accessory"), existingFileHelper);
         AdvancementHolder blueAercloud = net.minecraft.advancements.Advancement.Builder.advancement()
            .parent(enterAether)
            .display(
               (ItemLike)AetherBlocks.BLUE_AERCLOUD.get(),
               Component.translatable("advancement.aether.blue_aercloud"),
               Component.translatable("advancement.aether.blue_aercloud.desc"),
               null,
               AdvancementType.TASK,
               true,
               true,
               false
            )
            .addCriterion(
               "blue_aercloud", net.minecraft.advancements.critereon.EnterBlockTrigger.TriggerInstance.entersBlock((Block)AetherBlocks.BLUE_AERCLOUD.get())
            )
            .save(consumer, ResourceLocation.fromNamespaceAndPath("aether", "blue_aercloud"), existingFileHelper);
         AdvancementHolder obtainEgg = net.minecraft.advancements.Advancement.Builder.advancement()
            .parent(blueAercloud)
            .display(
               (ItemLike)AetherItems.BLUE_MOA_EGG.get(),
               Component.translatable("advancement.aether.obtain_egg"),
               Component.translatable("advancement.aether.obtain_egg.desc"),
               null,
               AdvancementType.TASK,
               true,
               true,
               false
            )
            .requirements(Strategy.OR)
            .addCriterion(
               "moa_egg",
               net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance.hasItems(
                  new ItemPredicate[]{net.minecraft.advancements.critereon.ItemPredicate.Builder.item().of(AetherTags.Items.MOA_EGGS).build()}
               )
            )
            .save(consumer, ResourceLocation.fromNamespaceAndPath("aether", "obtain_egg"), existingFileHelper);
         AdvancementHolder obtainPetal = net.minecraft.advancements.Advancement.Builder.advancement()
            .parent(obtainEgg)
            .display(
               (ItemLike)AetherItems.AECHOR_PETAL.get(),
               Component.translatable("advancement.aether.obtain_petal"),
               Component.translatable("advancement.aether.obtain_petal.desc"),
               null,
               AdvancementType.TASK,
               true,
               true,
               false
            )
            .addCriterion(
               "aechor_petal",
               net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance.hasItems(new ItemLike[]{(ItemLike)AetherItems.AECHOR_PETAL.get()})
            )
            .save(consumer, ResourceLocation.fromNamespaceAndPath("aether", "obtain_petal"), existingFileHelper);
         AdvancementHolder preventAechorPlantSpawning = net.minecraft.advancements.Advancement.Builder.advancement()
            .parent(obtainPetal)
            .display(
               (ItemLike)AetherBlocks.PURPLE_FLOWER.get(),
               Component.translatable("advancement.aether.prevent_aechor_petal_spawning"),
               Component.translatable("advancement.aether.prevent_aechor_petal_spawning.desc"),
               null,
               AdvancementType.TASK,
               true,
               true,
               false
            )
            .requirements(Strategy.OR)
            .addCriterion(
               "place_flower",
               TriggerInstance.itemUsedOnBlock(
                  Builder.location().setBlock(net.minecraft.advancements.critereon.BlockPredicate.Builder.block().of(AetherTags.Blocks.ENCHANTED_GRASS)),
                  net.minecraft.advancements.critereon.ItemPredicate.Builder.item().of(AetherTags.Items.AECHOR_PLANT_SPAWNABLE_DETERRENT)
               )
            )
            .addCriterion(
               "enchant_grass",
               AetherAdvancementData.itemUsedOnBlockCheckAbove(
                  Builder.location().setBlock(net.minecraft.advancements.critereon.BlockPredicate.Builder.block().of(AetherTags.Blocks.ENCHANTED_GRASS)),
                  Builder.location()
                     .setBlock(net.minecraft.advancements.critereon.BlockPredicate.Builder.block().of(AetherTags.Blocks.AECHOR_PLANT_SPAWNABLE_DETERRENT)),
                  net.minecraft.advancements.critereon.ItemPredicate.Builder.item().of(new ItemLike[]{AetherItems.AMBROSIUM_SHARD})
               )
            )
            .save(consumer, ResourceLocation.fromNamespaceAndPath("aether", "prevent_aechor_petal_spawning"), existingFileHelper);
         AdvancementHolder preventSwetSpawning = net.minecraft.advancements.Advancement.Builder.advancement()
            .parent(preventAechorPlantSpawning)
            .display(
               AetherItems.createSwetBannerItemStack(provider.lookupOrThrow(Registries.BANNER_PATTERN)),
               Component.translatable("advancement.aether.prevent_swet_spawning"),
               Component.translatable("advancement.aether.prevent_swet_spawning.desc"),
               null,
               AdvancementType.TASK,
               true,
               true,
               false
            )
            .addCriterion(
               "place_banner",
               TriggerInstance.itemUsedOnBlock(
                  Builder.location(),
                  net.minecraft.advancements.critereon.ItemPredicate.Builder.item()
                     .of(new ItemLike[]{Items.BLACK_BANNER})
                     .hasComponents(
                        DataComponentPredicate.allOf(AetherItems.createSwetBannerItemStack(provider.lookupOrThrow(Registries.BANNER_PATTERN)).getComponents())
                     )
               )
            )
            .save(consumer, ResourceLocation.fromNamespaceAndPath("aether", "prevent_swet_spawning"), existingFileHelper);
         AdvancementHolder incubateMoa = net.minecraft.advancements.Advancement.Builder.advancement()
            .parent(obtainEgg)
            .display(
               (ItemLike)AetherBlocks.INCUBATOR.get(),
               Component.translatable("advancement.aether.incubate_moa"),
               Component.translatable("advancement.aether.incubate_moa.desc"),
               null,
               AdvancementType.TASK,
               true,
               true,
               false
            )
            .addCriterion(
               "incubate_moa",
               IncubationTrigger.Instance.forItem(net.minecraft.advancements.critereon.ItemPredicate.Builder.item().of(AetherTags.Items.MOA_EGGS).build())
            )
            .save(consumer, ResourceLocation.fromNamespaceAndPath("aether", "incubate_moa"), existingFileHelper);
         CompoundTag moaTag = new CompoundTag();
         moaTag.putString("MoaType", AetherMoaTypes.BLACK.location().toString());
         AdvancementHolder blackMoa = net.minecraft.advancements.Advancement.Builder.advancement()
            .parent(incubateMoa)
            .display(
               Items.FEATHER,
               Component.translatable("advancement.aether.black_moa"),
               Component.translatable("advancement.aether.black_moa.desc"),
               null,
               AdvancementType.GOAL,
               true,
               true,
               false
            )
            .addCriterion(
               "black_moa",
               net.minecraft.advancements.critereon.StartRidingTrigger.TriggerInstance.playerStartsRiding(
                  net.minecraft.advancements.critereon.EntityPredicate.Builder.entity()
                     .vehicle(
                        net.minecraft.advancements.critereon.EntityPredicate.Builder.entity()
                           .of((EntityType)AetherEntityTypes.MOA.get())
                           .nbt(new NbtPredicate(moaTag))
                     )
               )
            )
            .save(consumer, ResourceLocation.fromNamespaceAndPath("aether", "black_moa"), existingFileHelper);
         AdvancementHolder mountPhyg = net.minecraft.advancements.Advancement.Builder.advancement()
            .parent(blueAercloud)
            .display(
               Items.SADDLE,
               Component.translatable("advancement.aether.mount_phyg"),
               Component.translatable("advancement.aether.mount_phyg.desc"),
               null,
               AdvancementType.TASK,
               true,
               true,
               false
            )
            .addCriterion(
               "mount_phyg",
               net.minecraft.advancements.critereon.StartRidingTrigger.TriggerInstance.playerStartsRiding(
                  net.minecraft.advancements.critereon.EntityPredicate.Builder.entity()
                     .vehicle(net.minecraft.advancements.critereon.EntityPredicate.Builder.entity().of((EntityType)AetherEntityTypes.PHYG.get()))
               )
            )
            .save(consumer, ResourceLocation.fromNamespaceAndPath("aether", "mount_phyg"), existingFileHelper);
         AdvancementHolder enchantedGravitite = net.minecraft.advancements.Advancement.Builder.advancement()
            .parent(craftAltar)
            .display(
               (ItemLike)AetherBlocks.ENCHANTED_GRAVITITE.get(),
               Component.translatable("advancement.aether.enchanted_gravitite"),
               Component.translatable("advancement.aether.enchanted_gravitite.desc"),
               null,
               AdvancementType.TASK,
               true,
               true,
               false
            )
            .addCriterion(
               "enchanted_gravitite",
               net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance.hasItems(
                  new ItemLike[]{(ItemLike)AetherBlocks.ENCHANTED_GRAVITITE.get()}
               )
            )
            .save(consumer, ResourceLocation.fromNamespaceAndPath("aether", "enchanted_gravitite"), existingFileHelper);
         AdvancementHolder gravititeArmor = net.minecraft.advancements.Advancement.Builder.advancement()
            .parent(enchantedGravitite)
            .display(
               (ItemLike)AetherItems.GRAVITITE_CHESTPLATE.get(),
               Component.translatable("advancement.aether.gravitite_armor"),
               Component.translatable("advancement.aether.gravitite_armor.desc"),
               null,
               AdvancementType.GOAL,
               true,
               true,
               false
            )
            .addCriterion(
               "gravitite_helmet",
               net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance.hasItems(
                  new ItemLike[]{(ItemLike)AetherItems.GRAVITITE_HELMET.get()}
               )
            )
            .addCriterion(
               "gravitite_chestplate",
               net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance.hasItems(
                  new ItemLike[]{(ItemLike)AetherItems.GRAVITITE_CHESTPLATE.get()}
               )
            )
            .addCriterion(
               "gravitite_leggings",
               net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance.hasItems(
                  new ItemLike[]{(ItemLike)AetherItems.GRAVITITE_LEGGINGS.get()}
               )
            )
            .addCriterion(
               "gravitite_boots",
               net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance.hasItems(new ItemLike[]{(ItemLike)AetherItems.GRAVITITE_BOOTS.get()})
            )
            .addCriterion(
               "gravitite_gloves",
               net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance.hasItems(
                  new ItemLike[]{(ItemLike)AetherItems.GRAVITITE_GLOVES.get()}
               )
            )
            .save(consumer, ResourceLocation.fromNamespaceAndPath("aether", "gravitite_armor"), existingFileHelper);
         AdvancementHolder bronzeDungeon = net.minecraft.advancements.Advancement.Builder.advancement()
            .parent(enchantedGravitite)
            .display(
               (ItemLike)AetherItems.BRONZE_DUNGEON_KEY.get(),
               Component.translatable("advancement.aether.bronze_dungeon"),
               Component.translatable("advancement.aether.bronze_dungeon.desc"),
               null,
               AdvancementType.GOAL,
               true,
               true,
               false
            )
            .addCriterion(
               "kill_slider",
               net.minecraft.advancements.critereon.KilledTrigger.TriggerInstance.playerKilledEntity(
                  net.minecraft.advancements.critereon.EntityPredicate.Builder.entity().of((EntityType)AetherEntityTypes.SLIDER.get())
               )
            )
            .save(consumer, ResourceLocation.fromNamespaceAndPath("aether", "bronze_dungeon"), existingFileHelper);
         AdvancementHolder hammerLoot = net.minecraft.advancements.Advancement.Builder.advancement()
            .parent(bronzeDungeon)
            .display(
               (ItemLike)AetherItems.HAMMER_OF_KINGBDOGZ.get(),
               Component.translatable("advancement.aether.hammer_loot"),
               Component.translatable("advancement.aether.hammer_loot.desc"),
               null,
               AdvancementType.GOAL,
               true,
               true,
               false
            )
            .addCriterion(
               "hammer_loot",
               net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance.hasItems(
                  new ItemLike[]{(ItemLike)AetherItems.HAMMER_OF_KINGBDOGZ.get()}
               )
            )
            .save(consumer, ResourceLocation.fromNamespaceAndPath("aether", "hammer_loot"), existingFileHelper);
         AdvancementHolder zephyrHammer = net.minecraft.advancements.Advancement.Builder.advancement()
            .parent(hammerLoot)
            .display(
               Items.SNOWBALL,
               Component.translatable("advancement.aether.zephyr_hammer"),
               Component.translatable("advancement.aether.zephyr_hammer.desc"),
               null,
               AdvancementType.CHALLENGE,
               true,
               true,
               true
            )
            .addCriterion(
               "zephyr_hammer",
               net.minecraft.advancements.critereon.KilledTrigger.TriggerInstance.playerKilledEntity(
                  net.minecraft.advancements.critereon.EntityPredicate.Builder.entity().of((EntityType)AetherEntityTypes.ZEPHYR.get()),
                  net.minecraft.advancements.critereon.DamageSourcePredicate.Builder.damageType()
                     .tag(TagPredicate.is(DamageTypeTags.IS_PROJECTILE))
                     .direct(net.minecraft.advancements.critereon.EntityPredicate.Builder.entity().of((EntityType)AetherEntityTypes.HAMMER_PROJECTILE.get()))
               )
            )
            .save(consumer, ResourceLocation.fromNamespaceAndPath("aether", "zephyr_hammer"), existingFileHelper);
         AdvancementHolder lanceLoot = net.minecraft.advancements.Advancement.Builder.advancement()
            .parent(bronzeDungeon)
            .display(
               (ItemLike)AetherItems.VALKYRIE_LANCE.get(),
               Component.translatable("advancement.aether.lance_loot"),
               Component.translatable("advancement.aether.lance_loot.desc"),
               null,
               AdvancementType.TASK,
               true,
               true,
               false
            )
            .addCriterion(
               "lance_loot",
               net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance.hasItems(new ItemLike[]{(ItemLike)AetherItems.VALKYRIE_LANCE.get()})
            )
            .save(consumer, ResourceLocation.fromNamespaceAndPath("aether", "lance_loot"), existingFileHelper);
         AdvancementHolder silverDungeon = net.minecraft.advancements.Advancement.Builder.advancement()
            .parent(lanceLoot)
            .display(
               (ItemLike)AetherItems.SILVER_DUNGEON_KEY.get(),
               Component.translatable("advancement.aether.silver_dungeon"),
               Component.translatable("advancement.aether.silver_dungeon.desc"),
               null,
               AdvancementType.GOAL,
               true,
               true,
               false
            )
            .addCriterion(
               "kill_valkyrie_queen",
               net.minecraft.advancements.critereon.KilledTrigger.TriggerInstance.playerKilledEntity(
                  net.minecraft.advancements.critereon.EntityPredicate.Builder.entity().of((EntityType)AetherEntityTypes.VALKYRIE_QUEEN.get())
               )
            )
            .save(consumer, ResourceLocation.fromNamespaceAndPath("aether", "silver_dungeon"), existingFileHelper);
         AdvancementHolder valkyrieLoot = net.minecraft.advancements.Advancement.Builder.advancement()
            .parent(silverDungeon)
            .display(
               (ItemLike)AetherItems.VALKYRIE_HELMET.get(),
               Component.translatable("advancement.aether.valkyrie_loot"),
               Component.translatable("advancement.aether.valkyrie_loot.desc"),
               null,
               AdvancementType.GOAL,
               true,
               true,
               false
            )
            .requirements(Strategy.OR)
            .addCriterion(
               "valkyrie_pickaxe",
               net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance.hasItems(
                  new ItemLike[]{(ItemLike)AetherItems.VALKYRIE_PICKAXE.get()}
               )
            )
            .addCriterion(
               "valkyrie_hoe",
               net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance.hasItems(new ItemLike[]{(ItemLike)AetherItems.VALKYRIE_HOE.get()})
            )
            .addCriterion(
               "valkyrie_axe",
               net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance.hasItems(new ItemLike[]{(ItemLike)AetherItems.VALKYRIE_AXE.get()})
            )
            .addCriterion(
               "valkyrie_shovel",
               net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance.hasItems(new ItemLike[]{(ItemLike)AetherItems.VALKYRIE_SHOVEL.get()})
            )
            .addCriterion(
               "valkyrie_helmet",
               net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance.hasItems(new ItemLike[]{(ItemLike)AetherItems.VALKYRIE_HELMET.get()})
            )
            .addCriterion(
               "valkyrie_chestplate",
               net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance.hasItems(
                  new ItemLike[]{(ItemLike)AetherItems.VALKYRIE_CHESTPLATE.get()}
               )
            )
            .addCriterion(
               "valkyrie_leggings",
               net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance.hasItems(
                  new ItemLike[]{(ItemLike)AetherItems.VALKYRIE_LEGGINGS.get()}
               )
            )
            .addCriterion(
               "valkyrie_boots",
               net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance.hasItems(new ItemLike[]{(ItemLike)AetherItems.VALKYRIE_BOOTS.get()})
            )
            .addCriterion(
               "valkyrie_gloves",
               net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance.hasItems(new ItemLike[]{(ItemLike)AetherItems.VALKYRIE_GLOVES.get()})
            )
            .save(consumer, ResourceLocation.fromNamespaceAndPath("aether", "valkyrie_loot"), existingFileHelper);
         AdvancementHolder valkyrieHoe = net.minecraft.advancements.Advancement.Builder.advancement()
            .parent(valkyrieLoot)
            .display(
               (ItemLike)AetherBlocks.AETHER_FARMLAND.get(),
               Component.translatable("advancement.aether.valkyrie_hoe"),
               Component.translatable("advancement.aether.valkyrie_hoe.desc"),
               null,
               AdvancementType.CHALLENGE,
               true,
               true,
               true
            )
            .addCriterion(
               "valkyrie_hoe",
               TriggerInstance.itemUsedOnBlock(
                  Builder.location()
                     .setBlock(
                        net.minecraft.advancements.critereon.BlockPredicate.Builder.block()
                           .of(
                              Stream.concat(
                                    AbilityHooks.ToolHooks.TILLABLES.keySet().stream(),
                                    HoeItemAccessor.aether$getTillables().keySet().stream().sorted(Comparator.comparing(Block::getDescriptionId))
                                 )
                                 .toList()
                           )
                     ),
                  net.minecraft.advancements.critereon.ItemPredicate.Builder.item().of(new ItemLike[]{(ItemLike)AetherItems.VALKYRIE_HOE.get()})
               )
            )
            .save(consumer, ResourceLocation.fromNamespaceAndPath("aether", "valkyrie_hoe"), existingFileHelper);
         AdvancementHolder regenStone = net.minecraft.advancements.Advancement.Builder.advancement()
            .parent(silverDungeon)
            .display(
               (ItemLike)AetherItems.REGENERATION_STONE.get(),
               Component.translatable("advancement.aether.regen_stone"),
               Component.translatable("advancement.aether.regen_stone.desc"),
               null,
               AdvancementType.TASK,
               true,
               true,
               false
            )
            .addCriterion(
               "regen_stone",
               net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance.hasItems(
                  new ItemLike[]{(ItemLike)AetherItems.REGENERATION_STONE.get()}
               )
            )
            .save(consumer, ResourceLocation.fromNamespaceAndPath("aether", "regen_stone"), existingFileHelper);
         AdvancementHolder goldDungeon = net.minecraft.advancements.Advancement.Builder.advancement()
            .parent(regenStone)
            .display(
               (ItemLike)AetherItems.GOLD_DUNGEON_KEY.get(),
               Component.translatable("advancement.aether.gold_dungeon"),
               Component.translatable("advancement.aether.gold_dungeon.desc"),
               null,
               AdvancementType.GOAL,
               true,
               true,
               false
            )
            .addCriterion(
               "kill_sun_spirit",
               net.minecraft.advancements.critereon.KilledTrigger.TriggerInstance.playerKilledEntity(
                  net.minecraft.advancements.critereon.EntityPredicate.Builder.entity().of((EntityType)AetherEntityTypes.SUN_SPIRIT.get())
               )
            )
            .save(consumer, ResourceLocation.fromNamespaceAndPath("aether", "gold_dungeon"), existingFileHelper);
         AdvancementHolder phoenixArmor = net.minecraft.advancements.Advancement.Builder.advancement()
            .parent(goldDungeon)
            .display(
               (ItemLike)AetherItems.PHOENIX_HELMET.get(),
               Component.translatable("advancement.aether.phoenix_armor"),
               Component.translatable("advancement.aether.phoenix_armor.desc"),
               null,
               AdvancementType.GOAL,
               true,
               true,
               false
            )
            .requirements(Strategy.OR)
            .addCriterion(
               "phoenix_helmet",
               net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance.hasItems(new ItemLike[]{(ItemLike)AetherItems.PHOENIX_HELMET.get()})
            )
            .addCriterion(
               "phoenix_chestplate",
               net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance.hasItems(
                  new ItemLike[]{(ItemLike)AetherItems.PHOENIX_CHESTPLATE.get()}
               )
            )
            .addCriterion(
               "phoenix_leggings",
               net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance.hasItems(
                  new ItemLike[]{(ItemLike)AetherItems.PHOENIX_LEGGINGS.get()}
               )
            )
            .addCriterion(
               "phoenix_boots",
               net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance.hasItems(new ItemLike[]{(ItemLike)AetherItems.PHOENIX_BOOTS.get()})
            )
            .addCriterion(
               "phoenix_gloves",
               net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance.hasItems(new ItemLike[]{(ItemLike)AetherItems.PHOENIX_GLOVES.get()})
            )
            .save(consumer, ResourceLocation.fromNamespaceAndPath("aether", "phoenix_armor"), existingFileHelper);
         AdvancementHolder obsidianArmor = net.minecraft.advancements.Advancement.Builder.advancement()
            .parent(phoenixArmor)
            .display(
               (ItemLike)AetherItems.OBSIDIAN_CHESTPLATE.get(),
               Component.translatable("advancement.aether.obsidian_armor"),
               Component.translatable("advancement.aether.obsidian_armor.desc"),
               null,
               AdvancementType.CHALLENGE,
               true,
               true,
               true
            )
            .addCriterion(
               "obsidian_helmet",
               net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance.hasItems(new ItemLike[]{(ItemLike)AetherItems.OBSIDIAN_HELMET.get()})
            )
            .addCriterion(
               "obsidian_chestplate",
               net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance.hasItems(
                  new ItemLike[]{(ItemLike)AetherItems.OBSIDIAN_CHESTPLATE.get()}
               )
            )
            .addCriterion(
               "obsidian_leggings",
               net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance.hasItems(
                  new ItemLike[]{(ItemLike)AetherItems.OBSIDIAN_LEGGINGS.get()}
               )
            )
            .addCriterion(
               "obsidian_boots",
               net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance.hasItems(new ItemLike[]{(ItemLike)AetherItems.OBSIDIAN_BOOTS.get()})
            )
            .addCriterion(
               "obsidian_gloves",
               net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance.hasItems(new ItemLike[]{(ItemLike)AetherItems.OBSIDIAN_GLOVES.get()})
            )
            .save(consumer, ResourceLocation.fromNamespaceAndPath("aether", "obsidian_armor"), existingFileHelper);
         AdvancementHolder aetherSleep = net.minecraft.advancements.Advancement.Builder.advancement()
            .parent(goldDungeon)
            .display(
               (ItemLike)AetherBlocks.SKYROOT_BED.get(),
               Component.translatable("advancement.aether.aether_sleep"),
               Component.translatable("advancement.aether.aether_sleep.desc"),
               null,
               AdvancementType.CHALLENGE,
               true,
               true,
               true
            )
            .addCriterion(
               "aether_sleep",
               CriteriaTriggers.SLEPT_IN_BED
                  .createCriterion(
                     new net.minecraft.advancements.critereon.PlayerTrigger.TriggerInstance(
                        Optional.of(
                           EntityPredicate.wrap(
                              net.minecraft.advancements.critereon.EntityPredicate.Builder.entity().located(Builder.inDimension(AetherDimensions.AETHER_LEVEL))
                           )
                        )
                     )
                  )
            )
            .save(consumer, ResourceLocation.fromNamespaceAndPath("aether", "aether_sleep"), existingFileHelper);
      }
   }
}
