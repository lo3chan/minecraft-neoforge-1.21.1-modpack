package net.cibernet.alchemancy.registries;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.cibernet.alchemancy.blocks.PhantomMembraneBlock;
import net.cibernet.alchemancy.item.ArmorInnatePropertyItem;
import net.cibernet.alchemancy.item.DreamsteelBowItem;
import net.cibernet.alchemancy.item.InfusionFlaskItem;
import net.cibernet.alchemancy.item.InnatePropertyItem;
import net.cibernet.alchemancy.item.components.InfusedPropertiesComponent;
import net.cibernet.alchemancy.item.components.PropertyDataComponent;
import net.cibernet.alchemancy.item.components.PropertyModifierComponent;
import net.cibernet.alchemancy.properties.FeralProperty;
import net.cibernet.alchemancy.properties.special.ClayMoldProperty;
import net.cibernet.alchemancy.properties.special.HomeRunProperty;
import net.cibernet.alchemancy.properties.special.WaywardWarpProperty;
import net.cibernet.alchemancy.properties.voidborn.BlockVacuumProperty;
import net.cibernet.alchemancy.util.InfusionPropertyDispenseBehavior;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Unit;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.FoodProperties.Builder;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.ArmorMaterial.Layer;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.SimpleTier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.Items;
import org.jetbrains.annotations.NotNull;

public class AlchemancyItems {
   public static final Items REGISTRY = DeferredRegister.createItems("alchemancy");
   public static final DeferredItem<BlockItem> BLAZEBLOOM = REGISTRY.registerSimpleBlockItem("blazebloom", AlchemancyBlocks.BLAZEBLOOM);
   public static final DeferredItem<Item> ALCHEMICAL_EXTRACT = REGISTRY.registerItem("alchemical_extract", properties -> new Item(properties) {
      @NotNull
      public UseAnim getUseAnimation(@NotNull ItemStack stack) {
         return UseAnim.DRINK;
      }
   }, new Properties().food(new Builder().effect(() -> new MobEffectInstance(AlchemancyMobEffects.INFERNO, 200, 0), 1.0F).build()));
   public static final DeferredItem<BlockItem> INFUSION_PEDESTAL = REGISTRY.registerSimpleBlockItem("infusion_pedestal", AlchemancyBlocks.INFUSION_PEDESTAL);
   public static final DeferredItem<BlockItem> ALCHEMANCY_FORGE = REGISTRY.registerSimpleBlockItem("alchemancy_forge", AlchemancyBlocks.ALCHEMANCY_FORGE);
   public static final DeferredItem<BlockItem> ALCHEMANCY_CATALYST = REGISTRY.registerSimpleBlockItem(
      "alchemancy_catalyst", AlchemancyBlocks.ALCHEMANCY_CATALYST
   );
   public static final DeferredItem<InnatePropertyItem> INFUSION_CODEX = REGISTRY.register(
      "infusion_codex", () -> new InnatePropertyItem.Builder().withProperties(AlchemancyProperties.INFUSION_CODEX).stacksTo(1).build()
   );
   public static final DeferredItem<BlockItem> PHANTOM_MEMBRANE_BLOCK = REGISTRY.register("phantom_membrane_block", () -> {
      BlockItem blockItem = new BlockItem((Block)AlchemancyBlocks.PHANTOM_MEMBRANE_BLOCK.get(), new Properties());
      DispenserBlock.DISPENSER_REGISTRY.put(blockItem.asItem(), (blockSource, item) -> {
         Level level = blockSource.level();
         Direction direction = (Direction)blockSource.state().getValue(DispenserBlock.FACING);
         BlockPos pos = blockSource.pos().relative(direction);
         BlockState state = ((PhantomMembraneBlock)AlchemancyBlocks.PHANTOM_MEMBRANE_BLOCK.value()).defaultBlockState();
         if (level.getBlockState(pos).canBeReplaced() && state.canSurvive(level, pos)) {
            level.setBlockAndUpdate(pos, state);
            InfusionPropertyDispenseBehavior.playDefaultParticles(blockSource, direction);
            item.shrink(1);
            return item;
         } else {
            return item;
         }
      });
      return blockItem;
   });
   public static final DeferredItem<BlockItem> GUST_BASKET = REGISTRY.registerSimpleBlockItem("gust_basket", AlchemancyBlocks.GUST_BASKET);
   public static final DeferredItem<BlockItem> FLAT_HOPPER = REGISTRY.registerSimpleBlockItem("flat_hopper", AlchemancyBlocks.FLAT_HOPPER);
   public static final DeferredItem<BlockItem> CHROMACHINE = REGISTRY.registerSimpleBlockItem("chromachine", AlchemancyBlocks.CHROMACHINE);
   public static final DeferredItem<Item> LEAD_INGOT = REGISTRY.registerSimpleItem("lead_ingot");
   public static final DeferredItem<SwordItem> LEAD_SWORD = REGISTRY.register(
      "lead_sword",
      () -> new SwordItem(
         AlchemancyItems.Materials.LEAD_TOOLS, new Properties().attributes(SwordItem.createAttributes(AlchemancyItems.Materials.LEAD_TOOLS, 3, -2.4F))
      )
   );
   public static final DeferredItem<ShovelItem> LEAD_SHOVEL = REGISTRY.register(
      "lead_shovel",
      () -> new ShovelItem(
         AlchemancyItems.Materials.LEAD_TOOLS, new Properties().attributes(ShovelItem.createAttributes(AlchemancyItems.Materials.LEAD_TOOLS, 1.5F, -3.0F))
      )
   );
   public static final DeferredItem<PickaxeItem> LEAD_PICKAXE = REGISTRY.register(
      "lead_pickaxe",
      () -> new PickaxeItem(
         AlchemancyItems.Materials.LEAD_TOOLS, new Properties().attributes(PickaxeItem.createAttributes(AlchemancyItems.Materials.LEAD_TOOLS, 1.0F, -2.8F))
      )
   );
   public static final DeferredItem<AxeItem> LEAD_AXE = REGISTRY.register(
      "lead_axe",
      () -> new AxeItem(
         AlchemancyItems.Materials.LEAD_TOOLS, new Properties().attributes(AxeItem.createAttributes(AlchemancyItems.Materials.LEAD_TOOLS, 6.0F, -3.1F))
      )
   );
   public static final DeferredItem<HoeItem> LEAD_HOE = REGISTRY.register(
      "lead_hoe",
      () -> new HoeItem(
         AlchemancyItems.Materials.LEAD_TOOLS, new Properties().attributes(HoeItem.createAttributes(AlchemancyItems.Materials.LEAD_TOOLS, -2.0F, -1.0F))
      )
   );
   public static final DeferredItem<Item> DREAMSTEEL_INGOT = REGISTRY.registerSimpleItem("dreamsteel_ingot");
   public static final DeferredItem<Item> DREAMSTEEL_NUGGET = REGISTRY.registerSimpleItem("dreamsteel_nugget");
   public static final DeferredItem<SwordItem> DREAMSTEEL_SWORD = REGISTRY.register(
      "dreamsteel_sword",
      () -> new SwordItem(
         AlchemancyItems.Materials.DREAMSTEEL_TOOLS,
         new Properties()
            .component(AlchemancyItems.Components.INFUSION_SLOTS, 6)
            .attributes(PickaxeItem.createAttributes(AlchemancyItems.Materials.DREAMSTEEL_TOOLS, 3.0F, -2.4F))
      )
   );
   public static final DeferredItem<ShovelItem> DREAMSTEEL_SHOVEL = REGISTRY.register(
      "dreamsteel_shovel",
      () -> new ShovelItem(
         AlchemancyItems.Materials.DREAMSTEEL_TOOLS,
         new Properties()
            .component(AlchemancyItems.Components.INFUSION_SLOTS, 6)
            .attributes(PickaxeItem.createAttributes(AlchemancyItems.Materials.DREAMSTEEL_TOOLS, 1.5F, -3.0F))
      )
   );
   public static final DeferredItem<PickaxeItem> DREAMSTEEL_PICKAXE = REGISTRY.register(
      "dreamsteel_pickaxe",
      () -> new PickaxeItem(
         AlchemancyItems.Materials.DREAMSTEEL_TOOLS,
         new Properties()
            .component(AlchemancyItems.Components.INFUSION_SLOTS, 6)
            .attributes(PickaxeItem.createAttributes(AlchemancyItems.Materials.DREAMSTEEL_TOOLS, 1.0F, -2.8F))
      )
   );
   public static final DeferredItem<AxeItem> DREAMSTEEL_AXE = REGISTRY.register(
      "dreamsteel_axe",
      () -> new AxeItem(
         AlchemancyItems.Materials.DREAMSTEEL_TOOLS,
         new Properties()
            .component(AlchemancyItems.Components.INFUSION_SLOTS, 6)
            .attributes(AxeItem.createAttributes(AlchemancyItems.Materials.DREAMSTEEL_TOOLS, 6.0F, -3.1F))
      )
   );
   public static final DeferredItem<HoeItem> DREAMSTEEL_HOE = REGISTRY.register(
      "dreamsteel_hoe",
      () -> new HoeItem(
         AlchemancyItems.Materials.DREAMSTEEL_TOOLS,
         new Properties()
            .component(AlchemancyItems.Components.INFUSION_SLOTS, 6)
            .attributes(AxeItem.createAttributes(AlchemancyItems.Materials.DREAMSTEEL_TOOLS, -2.0F, -1.0F))
      )
   );
   public static final DeferredItem<DreamsteelBowItem> DREAMSTEEL_BOW = REGISTRY.register(
      "dreamsteel_bow", () -> new DreamsteelBowItem(new Properties().durability(576))
   );
   public static final DeferredItem<InfusionFlaskItem> INFUSION_FLASK = REGISTRY.register(
      "infusion_flask", () -> new InfusionFlaskItem(new Properties().stacksTo(1).component(AlchemancyItems.Components.INFUSION_SLOTS, 2))
   );
   public static final DeferredItem<ArmorItem> LEAD_HELMET = REGISTRY.register(
      "lead_helmet", () -> new ArmorItem(AlchemancyItems.Materials.LEAD_ARMOR, Type.HELMET, new Properties().durability(Type.HELMET.getDurability(60)))
   );
   public static final DeferredItem<ArmorItem> LEAD_CHESTPLATE = REGISTRY.register(
      "lead_chestplate",
      () -> new ArmorItem(AlchemancyItems.Materials.LEAD_ARMOR, Type.CHESTPLATE, new Properties().durability(Type.CHESTPLATE.getDurability(60)))
   );
   public static final DeferredItem<ArmorItem> LEAD_LEGGINGS = REGISTRY.register(
      "lead_leggings", () -> new ArmorItem(AlchemancyItems.Materials.LEAD_ARMOR, Type.LEGGINGS, new Properties().durability(Type.LEGGINGS.getDurability(60)))
   );
   public static final DeferredItem<ArmorItem> LEAD_BOOTS = REGISTRY.register(
      "lead_boots", () -> new ArmorItem(AlchemancyItems.Materials.LEAD_ARMOR, Type.BOOTS, new Properties().durability(Type.BOOTS.getDurability(60)))
   );
   public static final DeferredItem<Item> BLANK_PEARL = REGISTRY.registerSimpleItem("blank_pearl");
   public static final DeferredItem<Item> REVEALING_PEARL = REGISTRY.registerSimpleItem("revealing_pearl");
   public static final DeferredItem<Item> PARADOX_PEARL = REGISTRY.registerSimpleItem("paradox_pearl");
   public static final DeferredItem<Item> ENTANGLED_SINGULARITY = REGISTRY.registerSimpleItem("entangled_singularity");
   public static final DeferredItem<InnatePropertyItem> VOID_PEARL = REGISTRY.register(
      "void_pearl", () -> new InnatePropertyItem.Builder().withProperties(AlchemancyProperties.VOIDBORN).build()
   );
   public static final DeferredItem<InnatePropertyItem> CHROMA_LENS = REGISTRY.register(
      "chroma_lens", () -> new InnatePropertyItem.Builder().withProperties(AlchemancyProperties.CHROMATIZE).build()
   );
   public static final DeferredItem<Item> GLOWING_ORB = REGISTRY.register(
      "glowing_orb", () -> new BlockItem((Block)AlchemancyBlocks.GLOWING_ORB.get(), new Properties())
   );
   public static final DeferredItem<Item> MICROSCOPIC_LENS = REGISTRY.registerSimpleItem("microscopic_lens");
   public static final DeferredItem<Item> MACROSCOPIC_LENS = REGISTRY.registerSimpleItem("macroscopic_lens");
   public static final DeferredItem<InnatePropertyItem> IRON_RING = REGISTRY.register(
      "pearlescent_ring", () -> new InnatePropertyItem.Builder().auxiliary(false).stacksTo(1).infusionSlots(1).build()
   );
   public static final DeferredItem<InnatePropertyItem> ETERNAL_GLOW_RING = REGISTRY.register(
      "eternal_glow_ring",
      () -> new InnatePropertyItem.Builder().withProperties(AlchemancyProperties.ETERNAL_GLOW).auxiliary(true).toggleable(true).stacksTo(1).build()
   );
   public static final DeferredItem<InnatePropertyItem> PHASING_RING = REGISTRY.register(
      "phasing_ring",
      () -> new InnatePropertyItem.Builder().withProperties(AlchemancyProperties.PHASE_STEP).auxiliary(true).toggleable(true).stacksTo(1).build()
   );
   public static final DeferredItem<InnatePropertyItem> UNDYING_RING = REGISTRY.register(
      "undying_ring", () -> new InnatePropertyItem.Builder().withProperties(AlchemancyProperties.DEATH_WARD).auxiliary(true).stacksTo(1).build()
   );
   public static final DeferredItem<InnatePropertyItem> FRIENDSHIP_RING = REGISTRY.register(
      "friendship_ring",
      () -> new InnatePropertyItem.Builder().withProperties(AlchemancyProperties.FRIENDLY).auxiliary(true).toggleable(true).stacksTo(1).build()
   );
   public static final DeferredItem<InnatePropertyItem> ATTRACTION_RING = REGISTRY.register(
      "attraction_ring",
      () -> new InnatePropertyItem.Builder().withProperties(AlchemancyProperties.ITEM_PULL).auxiliary(true).toggleable(true).stacksTo(1).build()
   );
   public static final DeferredItem<InnatePropertyItem> VOIDLESS_RING = REGISTRY.register(
      "voidless_ring",
      () -> new InnatePropertyItem.Builder()
         .withProperties(AlchemancyProperties.VOIDBORN)
         .auxiliary(true)
         .stacksTo(1)
         .durability(1001, Ingredient.of(new ItemLike[]{VOID_PEARL}))
         .build()
   );
   public static final DeferredItem<InnatePropertyItem> SPARKLING_BAND = REGISTRY.register(
      "sparkling_band",
      () -> new InnatePropertyItem.Builder()
         .withProperties(AlchemancyProperties.SPARKLING)
         .auxiliary(true)
         .addModifier(AlchemancyProperties.SPARKLING, AlchemancyProperties.Modifiers.IGNORE_INFUSED, false)
         .infusionSlots(1)
         .stacksTo(1)
         .build()
   );
   public static final DeferredItem<InnatePropertyItem> PROPERTY_VISOR = REGISTRY.register(
      "property_visor",
      () -> new InnatePropertyItem.Builder()
         .withProperties(AlchemancyProperties.HEADWEAR, AlchemancyProperties.REVEALING)
         .addModifier(AlchemancyProperties.HEADWEAR, AlchemancyProperties.Modifiers.ON_RIGHT_CLICK, true)
         .stacksTo(1)
         .build()
   );
   public static final DeferredItem<InnatePropertyItem> TINTED_GLASSES = REGISTRY.register(
      "tinted_glasses",
      () -> new InnatePropertyItem.Builder()
         .withProperties(AlchemancyProperties.HEADWEAR, AlchemancyProperties.TINTED_LENS)
         .addModifier(AlchemancyProperties.HEADWEAR, AlchemancyProperties.Modifiers.ON_RIGHT_CLICK, true)
         .stacksTo(1)
         .build()
   );
   public static final DeferredItem<ArmorInnatePropertyItem> NIMBUS_BELT = REGISTRY.register(
      "nimbus_belt",
      () -> new ArmorInnatePropertyItem(AlchemancyItems.Materials.BELT, Type.LEGGINGS, new Properties().durability(85), AlchemancyProperties.CLOUD_DASH)
   );
   public static final DeferredItem<ArmorInnatePropertyItem> CRYSTAL_STORM_BELT = REGISTRY.register(
      "crystal_storm_belt",
      () -> new ArmorInnatePropertyItem(AlchemancyItems.Materials.BELT, Type.LEGGINGS, new Properties().durability(115), AlchemancyProperties.CRYSTAL_DASH)
   );
   public static final DeferredItem<ArmorInnatePropertyItem> SHIFTING_LIGHTNING_BELT = REGISTRY.register(
      "shifting_lightning_belt",
      () -> new ArmorInnatePropertyItem(
         AlchemancyItems.Materials.BELT, Type.LEGGINGS, new Properties().durability(125), AlchemancyProperties.BLINKING, AlchemancyProperties.KINETIC_RECHARGE
      )
   );
   public static final DeferredItem<ArmorInnatePropertyItem> FLAMEWAKERS = REGISTRY.register(
      "flamewakers",
      () -> new ArmorInnatePropertyItem(
         AlchemancyItems.Materials.FLAME_EMPEROR_ARMOR, Type.BOOTS, new Properties().durability(400), AlchemancyProperties.FLAME_STEP
      )
   );
   public static final DeferredItem<ArmorInnatePropertyItem> TIDEWALKER_TREADS = REGISTRY.register(
      "tidewalker_treads",
      () -> new ArmorInnatePropertyItem(
         AlchemancyItems.Materials.TIDEWALKER_TREADS, Type.BOOTS, new Properties().durability(85), AlchemancyProperties.WAVE_RIDER
      )
   );
   public static final DeferredItem<ArmorInnatePropertyItem> HARDLIGHT_STEPS = REGISTRY.register(
      "hardlight_steps",
      () -> new ArmorInnatePropertyItem(
         AlchemancyItems.Materials.HARDLIGHT_STEPS, Type.BOOTS, new Properties().durability(140), AlchemancyProperties.AIR_WALKER
      )
   );
   public static final DeferredItem<ArmorInnatePropertyItem> MECHANICAL_BOOTS = REGISTRY.register(
      "mechanical_boots",
      () -> new ArmorInnatePropertyItem(
         AlchemancyItems.Materials.MECHANICAL_BOOTS,
         Type.BOOTS,
         new Properties().durability(165),
         AlchemancyProperties.ATHLETIC,
         AlchemancyProperties.KINETIC_RECHARGE
      )
   );
   public static final DeferredItem<InnatePropertyItem> WAYWARD_MEDALLION = REGISTRY.register(
      "wayward_medallion",
      () -> new InnatePropertyItem.Builder()
         .withProperties(AlchemancyProperties.WAYWARD_WARP)
         .durability(160, Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.ENDER_PEARL}))
         .tooltip(WaywardWarpProperty.MEDALLION_TOOLTIP)
         .stacksTo(1)
         .build()
   );
   public static final DeferredItem<InnatePropertyItem> ROCKET_POWERED_HAMMER = REGISTRY.register(
      "rocket_powered_hammer",
      () -> new InnatePropertyItem.Builder()
         .withProperties(AlchemancyProperties.ROCKET_POWERED, AlchemancyProperties.DENSE)
         .durability(600)
         .stacksTo(1)
         .build(new Properties().rarity(Rarity.EPIC).attributes(MaceItem.createAttributes()).component(DataComponents.TOOL, MaceItem.createToolProperties()))
   );
   public static final DeferredItem<InnatePropertyItem> BARRELS_WARHAMMER = REGISTRY.register(
      "barrels_warhammer",
      () -> new InnatePropertyItem.Builder()
         .withProperties(AlchemancyProperties.ROCKET_POWERED, AlchemancyProperties.DENSE)
         .durability(600)
         .stacksTo(1)
         .build(new Properties().rarity(Rarity.EPIC).attributes(MaceItem.createAttributes()).component(DataComponents.TOOL, MaceItem.createToolProperties()))
   );
   public static final DeferredItem<InnatePropertyItem> HOME_RUN_BAT = REGISTRY.register(
      "home_run_bat",
      () -> new InnatePropertyItem.Builder()
         .withProperties(AlchemancyProperties.HOME_RUN)
         .durability(800)
         .stacksTo(1)
         .build(new Properties().rarity(Rarity.EPIC).attributes(HomeRunProperty.createAttributes()))
   );
   public static final DeferredItem<InnatePropertyItem> BADA_BAT = REGISTRY.register(
      "bada_bat",
      () -> new InnatePropertyItem.Builder()
         .withProperties(AlchemancyProperties.HOME_RUN, AlchemancyProperties.BADA_QUIP)
         .durability(800)
         .stacksTo(1)
         .build(new Properties().rarity(Rarity.EPIC).attributes(HomeRunProperty.createAttributes()))
   );
   public static final DeferredItem<InnatePropertyItem> TELEKINETIC_GLOVE = REGISTRY.register(
      "telekinetic_glove", () -> new InnatePropertyItem.Builder().withProperties(AlchemancyProperties.KINETIC_GRAB).stacksTo(1).build()
   );
   public static final DeferredItem<SwordItem> FERAL_BLADE = REGISTRY.register(
      "feral_blade",
      () -> new SwordItem(
         Tiers.IRON,
         new Properties()
            .attributes(
               SwordItem.createAttributes(Tiers.IRON, 2, -1.2F)
                  .withModifierAdded(Attributes.ATTACK_SPEED, FeralProperty.OFFHAND_BONUS, EquipmentSlotGroup.OFFHAND)
            )
      )
   );
   public static final DeferredItem<SwordItem> HOT_ROD = REGISTRY.register(
      "hot_rod",
      () -> new SwordItem(
         AlchemancyItems.Materials.FLAME_EMPEROR_TOOLS,
         new Properties()
            .attributes(SwordItem.createAttributes(AlchemancyItems.Materials.FLAME_EMPEROR_TOOLS, 3, -2.4F))
            .component(
               (DataComponentType)AlchemancyItems.Components.INNATE_PROPERTIES.get(),
               new InfusedPropertiesComponent(List.of(AlchemancyProperties.FLAME_EMPEROR))
            )
      )
   );
   public static final DeferredItem<PickaxeItem> MOLTEN_CORE_PERFORATOR = REGISTRY.register(
      "molten_core_perforator",
      () -> new PickaxeItem(
         AlchemancyItems.Materials.FLAME_EMPEROR_TOOLS,
         new Properties()
            .attributes(PickaxeItem.createAttributes(AlchemancyItems.Materials.FLAME_EMPEROR_TOOLS, 1.0F, -2.8F))
            .component(
               (DataComponentType)AlchemancyItems.Components.INNATE_PROPERTIES.get(),
               new InfusedPropertiesComponent(List.of(AlchemancyProperties.FLAME_EMPEROR))
            )
      )
   );
   public static final DeferredItem<Item> LEADEN_APPLE = REGISTRY.registerSimpleItem(
      "leaden_apple", new Properties().rarity(Rarity.RARE).food(AlchemancyItems.Foods.LEADEN_APPLE)
   );
   public static final DeferredItem<InnatePropertyItem> LEADEN_CLOTH = REGISTRY.register(
      "leaden_sponge", () -> new InnatePropertyItem.Builder().withProperties(AlchemancyProperties.INFUSION_CLEANSE).stacksTo(1).build()
   );
   public static final DeferredItem<InnatePropertyItem> DIVINE_CLOTH = REGISTRY.register(
      "divine_sponge", () -> new InnatePropertyItem.Builder().withProperties(AlchemancyProperties.DIVINE_CLEANSE).stacksTo(1).build()
   );
   public static final DeferredItem<InnatePropertyItem> VAULT_LOCKPICK = REGISTRY.register(
      "vaultpick", () -> new InnatePropertyItem.Builder().withProperties(AlchemancyProperties.VAULTPICKING).durability(200).stacksTo(1).build()
   );
   public static final DeferredItem<InnatePropertyItem> BINDING_KEY = REGISTRY.register(
      "binding_key", () -> new InnatePropertyItem.Builder().withProperties(AlchemancyProperties.BINDING).stacksTo(1).build()
   );
   public static final DeferredItem<InnatePropertyItem> SPINNER_SPANNER = REGISTRY.register(
      "spinner_spanner", () -> new InnatePropertyItem.Builder().withProperties(AlchemancyProperties.ROTATING).stacksTo(1).build()
   );
   public static final DeferredItem<InnatePropertyItem> QUANTUM_SPANNER = REGISTRY.register(
      "quantum_spanner", () -> new InnatePropertyItem.Builder().withProperties(AlchemancyProperties.QUANTUM_SHIFT).stacksTo(1).build()
   );
   public static final DeferredItem<InnatePropertyItem> BLACK_HOLE_PICKAXE = REGISTRY.register(
      "black_hole_pickaxe",
      () -> new InnatePropertyItem.Builder()
         .withProperties(AlchemancyProperties.WORLD_OBLITERATOR)
         .addData(AlchemancyProperties.WORLD_OBLITERATOR, new BlockVacuumProperty.Data(BlockTags.MINEABLE_WITH_PICKAXE).save())
         .tooltip(BlockVacuumProperty.TOOL_TOOLTIP)
         .durability(3000)
         .stacksTo(1)
         .build(new Properties().rarity(Rarity.EPIC))
   );
   public static final DeferredItem<InnatePropertyItem> BLACK_HOLE_AXE = REGISTRY.register(
      "black_hole_axe",
      () -> new InnatePropertyItem.Builder()
         .withProperties(AlchemancyProperties.WORLD_OBLITERATOR)
         .addData(AlchemancyProperties.WORLD_OBLITERATOR, new BlockVacuumProperty.Data(BlockTags.LOGS).save())
         .tooltip(BlockVacuumProperty.TOOL_TOOLTIP)
         .durability(3000)
         .stacksTo(1)
         .build(new Properties().rarity(Rarity.EPIC))
   );
   public static final DeferredItem<InnatePropertyItem> BLACK_HOLE_SHOVEL = REGISTRY.register(
      "black_hole_shovel",
      () -> new InnatePropertyItem.Builder()
         .withProperties(AlchemancyProperties.WORLD_OBLITERATOR)
         .addData(AlchemancyProperties.WORLD_OBLITERATOR, new BlockVacuumProperty.Data(BlockTags.MINEABLE_WITH_SHOVEL).save())
         .tooltip(BlockVacuumProperty.TOOL_TOOLTIP)
         .durability(3000)
         .stacksTo(1)
         .build(new Properties().rarity(Rarity.EPIC))
   );
   public static final DeferredItem<InnatePropertyItem> BLACK_HOLE_HOE = REGISTRY.register(
      "black_hole_hoe",
      () -> new InnatePropertyItem.Builder()
         .withProperties(AlchemancyProperties.WORLD_OBLITERATOR)
         .addData(AlchemancyProperties.WORLD_OBLITERATOR, new BlockVacuumProperty.Data(BlockTags.MINEABLE_WITH_HOE).save())
         .tooltip(BlockVacuumProperty.TOOL_TOOLTIP)
         .durability(3000)
         .stacksTo(1)
         .build(new Properties().rarity(Rarity.EPIC))
   );
   public static final DeferredItem<Item> POCKET_BLACK_HOLE = REGISTRY.register(
      "pocket_black_hole",
      () -> new InnatePropertyItem.Builder().withProperties(AlchemancyProperties.THROWABLE, AlchemancyProperties.VOIDTOUCH).stacksTo(8).build()
   );
   public static final DeferredItem<InnatePropertyItem> UNSHAPED_CLAY = REGISTRY.register(
      "unshaped_clay", () -> new InnatePropertyItem.Builder().withProperties(AlchemancyProperties.CLAY_MOLD).tooltip(ClayMoldProperty.ITEM_TOOLTIP).build()
   );
   public static final DeferredItem<Item> PROPERTY_CAPSULE = REGISTRY.registerItem("property_capsule", properties -> new Item(properties.rarity(Rarity.EPIC)));
   public static final List<Holder<Item>> SECRET_TRANSMUTATIONS = List.of(HOME_RUN_BAT, BADA_BAT);

   public static class Components {
      public static final net.neoforged.neoforge.registries.DeferredRegister.DataComponents REGISTRY = DeferredRegister.createDataComponents(
         Registries.DATA_COMPONENT_TYPE, "alchemancy"
      );
      public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> INFUSION_SLOTS = REGISTRY.register(
         "infusion_slots",
         () -> new net.minecraft.core.component.DataComponentType.Builder()
            .persistent(ExtraCodecs.NON_NEGATIVE_INT)
            .networkSynchronized(ByteBufCodecs.VAR_INT)
            .build()
      );
      public static final DeferredHolder<DataComponentType<?>, DataComponentType<InfusedPropertiesComponent>> INFUSED_PROPERTIES = REGISTRY.register(
         "infused_properties",
         () -> new net.minecraft.core.component.DataComponentType.Builder()
            .persistent(InfusedPropertiesComponent.CODEC)
            .networkSynchronized(InfusedPropertiesComponent.STREAM_CODEC)
            .build()
      );
      public static final DeferredHolder<DataComponentType<?>, DataComponentType<InfusedPropertiesComponent>> INNATE_PROPERTIES = REGISTRY.register(
         "innate_properties",
         () -> new net.minecraft.core.component.DataComponentType.Builder()
            .persistent(InfusedPropertiesComponent.CODEC)
            .networkSynchronized(InfusedPropertiesComponent.STREAM_CODEC)
            .build()
      );
      public static final DeferredHolder<DataComponentType<?>, DataComponentType<InfusedPropertiesComponent>> STORED_PROPERTIES = REGISTRY.register(
         "stored_properties",
         () -> new net.minecraft.core.component.DataComponentType.Builder()
            .persistent(InfusedPropertiesComponent.CODEC)
            .networkSynchronized(InfusedPropertiesComponent.STREAM_CODEC)
            .build()
      );
      public static final DeferredHolder<DataComponentType<?>, DataComponentType<PropertyDataComponent>> PROPERTY_DATA = REGISTRY.register(
         "property_data",
         () -> new net.minecraft.core.component.DataComponentType.Builder()
            .persistent(PropertyDataComponent.CODEC)
            .networkSynchronized(PropertyDataComponent.STREAM_CODEC)
            .build()
      );
      public static final DeferredHolder<DataComponentType<?>, DataComponentType<PropertyModifierComponent>> PROPERTY_MODIFIERS = REGISTRY.register(
         "property_modifiers",
         () -> new net.minecraft.core.component.DataComponentType.Builder()
            .persistent(PropertyModifierComponent.CODEC)
            .networkSynchronized(PropertyModifierComponent.STREAM_CODEC)
            .build()
      );
      public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> INGREDIENT_DISPLAY = REGISTRY.register(
         "ingredient_display",
         () -> new net.minecraft.core.component.DataComponentType.Builder().persistent(Unit.CODEC).networkSynchronized(StreamCodec.unit(Unit.INSTANCE)).build()
      );
      public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> FE_STORAGE = REGISTRY.register(
         "battery_storage",
         () -> new net.minecraft.core.component.DataComponentType.Builder()
            .persistent(ExtraCodecs.NON_NEGATIVE_INT)
            .networkSynchronized(ByteBufCodecs.INT)
            .build()
      );
      public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> RANDOMIZED = REGISTRY.register(
         "randomized", () -> new net.minecraft.core.component.DataComponentType.Builder().persistent(Unit.CODEC).build()
      );
      public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> RECURSION_PREVENTION = REGISTRY.register(
         "recursion_prevention", () -> new net.minecraft.core.component.DataComponentType.Builder().persistent(Unit.CODEC).build()
      );
   }

   public static class Foods {
      public static final FoodProperties LEADEN_APPLE = new Builder()
         .nutrition(4)
         .saturationModifier(1.2F)
         .effect(new MobEffectInstance(MobEffects.POISON, 200, 9), 1.0F)
         .alwaysEdible()
         .build();
   }

   public static class Materials {
      public static final Tier LEAD_TOOLS = new SimpleTier(
         AlchemancyTags.Blocks.INCORRECT_FOR_LEAD_TOOL, 1000, 6.0F, 1.5F, 10, () -> Ingredient.of(AlchemancyTags.Items.REPAIRS_LEAD)
      );
      public static final Tier DREAMSTEEL_TOOLS = new SimpleTier(
         AlchemancyTags.Blocks.INCORRECT_FOR_DREAMSTEEL_TOOL, 1561, 9.0F, 4.0F, 15, () -> Ingredient.of(AlchemancyTags.Items.REPAIRS_DREAMSTEEL)
      );
      public static final Tier FLAME_EMPEROR_TOOLS = new SimpleTier(
         AlchemancyTags.Blocks.INCORRECT_FOR_FLAME_EMPEROR_TOOL, 1561, 8.0F, 3.0F, 15, () -> Ingredient.of(AlchemancyTags.Items.REPAIRS_FLAME_EMPEROR_TOOL)
      );
      public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIAL_REGISTRY = DeferredRegister.create(Registries.ARMOR_MATERIAL, "alchemancy");
      public static final Holder<ArmorMaterial> LEAD_ARMOR = registerArmor(
         "lead",
         10,
         0.0F,
         0.1F,
         SoundEvents.ARMOR_EQUIP_IRON,
         () -> Ingredient.of(new ItemLike[]{AlchemancyItems.LEAD_INGOT}),
         (Map<Type, Integer>)Util.make(new EnumMap(Type.class), p_323378_ -> {
            p_323378_.put(Type.BOOTS, 2);
            p_323378_.put(Type.LEGGINGS, 5);
            p_323378_.put(Type.CHESTPLATE, 6);
            p_323378_.put(Type.HELMET, 2);
            p_323378_.put(Type.BODY, 5);
         })
      );
      public static final Holder<ArmorMaterial> BELT = registerArmor(
         "belt",
         16,
         0.0F,
         0.0F,
         SoundEvents.ARMOR_EQUIP_LEATHER,
         () -> Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.LEATHER}),
         (Map<Type, Integer>)Util.make(new EnumMap(Type.class), p_323378_ -> p_323378_.put(Type.LEGGINGS, 2))
      );
      public static final Holder<ArmorMaterial> FLAME_EMPEROR_ARMOR = registerArmor(
         "flame_emperor_armor",
         18,
         2.0F,
         0.0F,
         SoundEvents.ARMOR_EQUIP_GOLD,
         () -> Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.BLAZE_POWDER}),
         (Map<Type, Integer>)Util.make(new EnumMap(Type.class), p_323378_ -> p_323378_.put(Type.BOOTS, 3))
      );
      public static final Holder<ArmorMaterial> TIDEWALKER_TREADS = registerArmor(
         "tidewalker_treads",
         15,
         0.0F,
         0.0F,
         SoundEvents.ARMOR_EQUIP_LEATHER,
         () -> Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.LEATHER}),
         (Map<Type, Integer>)Util.make(new EnumMap(Type.class), p_323378_ -> p_323378_.put(Type.BOOTS, 1))
      );
      public static final Holder<ArmorMaterial> HARDLIGHT_STEPS = registerArmor(
         "hardlight_steps",
         15,
         0.0F,
         0.0F,
         SoundEvents.ARMOR_EQUIP_GOLD,
         () -> Ingredient.of(new ItemLike[]{AlchemancyItems.GLOWING_ORB}),
         (Map<Type, Integer>)Util.make(new EnumMap(Type.class), p_323378_ -> p_323378_.put(Type.BOOTS, 2))
      );
      public static final Holder<ArmorMaterial> MECHANICAL_BOOTS = registerArmor(
         "mechanical_boots",
         15,
         0.0F,
         0.0F,
         SoundEvents.ARMOR_EQUIP_IRON,
         () -> Ingredient.EMPTY,
         (Map<Type, Integer>)Util.make(new EnumMap(Type.class), p_323378_ -> p_323378_.put(Type.BOOTS, 2))
      );

      private static Holder<ArmorMaterial> registerArmor(
         String key,
         int enchantmentValue,
         float toughness,
         float knockbackResistance,
         Holder<SoundEvent> equipSound,
         Supplier<Ingredient> repairMaterial,
         Map<Type, Integer> defenseMap
      ) {
         return ARMOR_MATERIAL_REGISTRY.register(
            key,
            () -> new ArmorMaterial(
               defenseMap,
               enchantmentValue,
               equipSound,
               repairMaterial,
               List.of(new Layer(ResourceLocation.fromNamespaceAndPath("alchemancy", key))),
               toughness,
               knockbackResistance
            )
         );
      }
   }
}
