package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.citadel.server.block.LecternBooks;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityCockroachEgg;
import com.github.alexthe666.alexsmobs.entity.EntityEmuEgg;
import com.github.alexthe666.alexsmobs.entity.EntityEnderiophageRocket;
import com.github.alexthe666.alexsmobs.entity.EntitySharkToothArrow;
import com.github.alexthe666.alexsmobs.entity.EntityTossedItem;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AbstractProjectileDispenseBehavior;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.food.FoodProperties.Builder;
import net.minecraft.world.item.BannerPatternItem;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AMItemRegistry {
   public static final AMArmorMaterial ROADRUNNER_ARMOR_MATERIAL = new AMArmorMaterial(
      "roadrunner", 18, new int[]{3, 3, 3, 3}, 20, SoundEvents.ARMOR_EQUIP_TURTLE, 0.0F
   );
   public static final AMArmorMaterial CROCODILE_ARMOR_MATERIAL = new AMArmorMaterial(
      "crocodile", 22, new int[]{2, 5, 7, 3}, 25, SoundEvents.ARMOR_EQUIP_TURTLE, 1.0F
   );
   public static final AMArmorMaterial CENTIPEDE_ARMOR_MATERIAL = new AMArmorMaterial(
      "centipede", 20, new int[]{6, 6, 6, 6}, 22, SoundEvents.ARMOR_EQUIP_TURTLE, 0.5F
   );
   public static final AMArmorMaterial MOOSE_ARMOR_MATERIAL = new AMArmorMaterial("moose", 19, new int[]{3, 3, 3, 3}, 21, SoundEvents.ARMOR_EQUIP_TURTLE, 0.5F);
   public static final AMArmorMaterial RACCOON_ARMOR_MATERIAL = new AMArmorMaterial(
      "raccoon", 17, new int[]{3, 3, 3, 3}, 21, SoundEvents.ARMOR_EQUIP_LEATHER, 2.5F
   );
   public static final AMArmorMaterial SOMBRERO_ARMOR_MATERIAL = new AMArmorMaterial(
      "sombrero", 14, new int[]{2, 2, 2, 2}, 30, SoundEvents.ARMOR_EQUIP_LEATHER, 0.5F
   );
   public static final AMArmorMaterial SPIKED_TURTLE_SHELL_ARMOR_MATERIAL = new AMArmorMaterial(
      "spiked_turtle_shell", 35, new int[]{3, 3, 3, 3}, 30, SoundEvents.ARMOR_EQUIP_TURTLE, 1.0F, 0.2F
   );
   public static final AMArmorMaterial FEDORA_ARMOR_MATERIAL = new AMArmorMaterial(
      "fedora", 10, new int[]{2, 2, 2, 2}, 30, SoundEvents.ARMOR_EQUIP_LEATHER, 0.5F
   );
   public static final AMArmorMaterial EMU_ARMOR_MATERIAL = new AMArmorMaterial("emu", 9, new int[]{4, 4, 4, 4}, 20, SoundEvents.ARMOR_EQUIP_LEATHER, 0.5F);
   public static final AMArmorMaterial TARANTULA_HAWK_ELYTRA_MATERIAL = new AMArmorMaterial(
      "tarantula_hawk_elytra", 9, new int[]{3, 3, 3, 3}, 5, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F
   );
   public static final AMArmorMaterial FROSTSTALKER_ARMOR_MATERIAL = new AMArmorMaterial(
      "froststalker", 9, new int[]{3, 3, 3, 3}, 15, SoundEvents.ARMOR_EQUIP_LEATHER, 0.5F
   );
   public static final AMArmorMaterial ROCKY_ARMOR_MATERIAL = new AMArmorMaterial(
      "rocky_roller", 20, new int[]{2, 5, 7, 3}, 10, SoundEvents.ARMOR_EQUIP_TURTLE, 0.5F
   );
   public static final AMArmorMaterial FLYING_FISH_MATERIAL = new AMArmorMaterial(
      "flying_fish", 9, new int[]{1, 1, 1, 1}, 8, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F
   );
   public static final AMArmorMaterial NOVELTY_HAT_MATERIAL = new AMArmorMaterial(
      "novelty_hat", 10, new int[]{2, 2, 2, 2}, 30, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F
   );
   public static final AMArmorMaterial KIMONO_MATERIAL = new AMArmorMaterial("kimono", 8, new int[]{3, 3, 3, 3}, 15, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F);
   public static final DeferredRegister<Item> DEF_REG = DeferredRegister.create(Registries.ITEM, "alexsmobs");
   public static final Supplier<Item> TAB_ICON = regItem("tab_icon", () -> new ItemTabIcon(new Properties()));
   public static final Supplier<Item> FANCY_ITEM = regItem("fancy_item", () -> new ItemCustomRender(new Properties().stacksTo(1)));
   public static final Supplier<Item> EFFECT_ITEM = regItem("effect_item", () -> new ItemCustomRender(new Properties().stacksTo(1)));
   public static final Supplier<Item> ANIMAL_DICTIONARY = regItem("animal_dictionary", () -> new ItemAnimalDictionary(new Properties().stacksTo(1)));
   public static final Supplier<Item> BEAR_FUR = regItem("bear_fur", () -> new Item(new Properties()));
   public static final Supplier<Item> BEAR_DUST = regItem("bear_dust", () -> new ItemBearDust(new Properties().rarity(Rarity.EPIC)));
   public static final Supplier<Item> ROADRUNNER_FEATHER = regItem("roadrunner_feather", () -> new Item(new Properties()));
   public static final Supplier<Item> ROADDRUNNER_BOOTS = regItem("roadrunner_boots", () -> new ItemModArmor(ROADRUNNER_ARMOR_MATERIAL, Type.BOOTS));
   public static final Supplier<Item> LAVA_BOTTLE = regItem("lava_bottle", () -> new Item(new Properties().stacksTo(1)));
   public static final Supplier<Item> BONE_SERPENT_TOOTH = regItem("bone_serpent_tooth", () -> new Item(new Properties().fireResistant()));
   public static final Supplier<Item> GAZELLE_HORN = regItem("gazelle_horn", () -> new Item(new Properties().fireResistant()));
   public static final Supplier<Item> CROCODILE_SCUTE = regItem("crocodile_scute", () -> new Item(new Properties()));
   public static final Supplier<Item> CROCODILE_CHESTPLATE = regItem("crocodile_chestplate", () -> new ItemModArmor(CROCODILE_ARMOR_MATERIAL, Type.CHESTPLATE));
   public static final Supplier<Item> MAGGOT = regItem(
      "maggot", () -> new Item(new Properties().food(new Builder().nutrition(1).saturationModifier(0.2F).build()))
   );
   public static final Supplier<Item> BANANA = regItem(
      "banana", () -> new Item(new Properties().food(new Builder().nutrition(4).saturationModifier(0.3F).build()))
   );
   public static final Supplier<Item> ANCIENT_DART = regItem("ancient_dart", () -> new Item(new Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));
   public static final Supplier<Item> HALO = regItem("halo", () -> new ItemInventoryOnly(new Properties()));
   public static final Supplier<Item> BLOOD_SAC = regItem("blood_sac", () -> new Item(new Properties()));
   public static final Supplier<Item> MOSQUITO_PROBOSCIS = regItem("mosquito_proboscis", () -> new Item(new Properties()));
   public static final Supplier<Item> BLOOD_SPRAYER = regItem("blood_sprayer", () -> new ItemBloodSprayer(new Properties().durability(100)));
   public static final Supplier<Item> RATTLESNAKE_RATTLE = regItem("rattlesnake_rattle", () -> new Item(new Properties()));
   public static final Supplier<Item> CHORUS_ON_A_STICK = regItem("chorus_on_a_stick", () -> new Item(new Properties().stacksTo(1)));
   public static final Supplier<Item> SHARK_TOOTH = regItem("shark_tooth", () -> new Item(new Properties()));
   public static final Supplier<Item> SHARK_TOOTH_ARROW = regItem("shark_tooth_arrow", () -> new ItemModArrow(new Properties()));
   public static final Supplier<Item> LOBSTER_TAIL = regItem(
      "lobster_tail", () -> new Item(new Properties().food(new Builder().nutrition(2).saturationModifier(0.4F).build()))
   );
   public static final Supplier<Item> COOKED_LOBSTER_TAIL = regItem(
      "cooked_lobster_tail", () -> new Item(new Properties().food(new Builder().nutrition(6).saturationModifier(0.65F).build()))
   );
   public static final Supplier<Item> LOBSTER_BUCKET = regItem(
      "lobster_bucket", () -> new ItemModFishBucket(AMEntityRegistry.LOBSTER, Fluids.WATER, new Properties())
   );
   public static final Supplier<Item> KOMODO_SPIT = regItem("komodo_spit", () -> new Item(new Properties()));
   public static final Supplier<Item> KOMODO_SPIT_BOTTLE = regItem("komodo_spit_bottle", () -> new Item(new Properties()));
   public static final Supplier<Item> POISON_BOTTLE = regItem("poison_bottle", () -> new Item(new Properties()));
   public static final Supplier<Item> SOPA_DE_MACACO = regItem(
      "sopa_de_macaco",
      () -> new Item(new Properties().food(new Builder().nutrition(5).saturationModifier(0.4F).usingConvertsTo(Items.BOWL).build()).stacksTo(1))
   );
   public static final Supplier<Item> CENTIPEDE_LEG = regItem("centipede_leg", () -> new Item(new Properties()));
   public static final Supplier<Item> CENTIPEDE_LEGGINGS = regItem("centipede_leggings", () -> new ItemModArmor(CENTIPEDE_ARMOR_MATERIAL, Type.LEGGINGS));
   public static final Supplier<Item> MOSQUITO_LARVA = regItem("mosquito_larva", () -> new Item(new Properties()));
   public static final Supplier<Item> MOOSE_ANTLER = regItem("moose_antler", () -> new Item(new Properties()));
   public static final Supplier<Item> MOOSE_HEADGEAR = regItem("moose_headgear", () -> new ItemModArmor(MOOSE_ARMOR_MATERIAL, Type.HELMET));
   public static final Supplier<Item> MOOSE_RIBS = regItem(
      "moose_ribs", () -> new Item(new Properties().food(new Builder().nutrition(3).saturationModifier(0.6F).build()))
   );
   public static final Supplier<Item> COOKED_MOOSE_RIBS = regItem(
      "cooked_moose_ribs", () -> new Item(new Properties().food(new Builder().nutrition(7).saturationModifier(0.85F).build()))
   );
   public static final Supplier<Item> MIMICREAM = regItem("mimicream", () -> new Item(new Properties()));
   public static final Supplier<Item> RACCOON_TAIL = regItem("raccoon_tail", () -> new Item(new Properties()));
   public static final Supplier<Item> FRONTIER_CAP = regItem("frontier_cap", () -> new ItemModArmor(RACCOON_ARMOR_MATERIAL, Type.HELMET));
   public static final Supplier<Item> BLOBFISH = regItem(
      "blobfish",
      () -> new Item(
         new Properties().food(new Builder().nutrition(3).saturationModifier(0.4F).effect(new MobEffectInstance(MobEffects.POISON, 120, 0), 1.0F).build())
      )
   );
   public static final Supplier<Item> BLOBFISH_BUCKET = regItem(
      "blobfish_bucket", () -> new ItemModFishBucket(AMEntityRegistry.BLOBFISH, Fluids.WATER, new Properties())
   );
   public static final Supplier<Item> FISH_OIL = regItem(
      "fish_oil", () -> new ItemFishOil(new Properties().craftRemainder(Items.GLASS_BOTTLE).food(new Builder().nutrition(0).saturationModifier(0.2F).build()))
   );
   public static final Supplier<Item> MARACA = regItem("maraca", () -> new ItemMaraca(new Properties()));
   public static final Supplier<Item> SOMBRERO = regItem("sombrero", () -> new ItemModArmor(SOMBRERO_ARMOR_MATERIAL, Type.HELMET));
   public static final Supplier<Item> COCKROACH_WING_FRAGMENT = regItem("cockroach_wing_fragment", () -> new Item(new Properties()));
   public static final Supplier<Item> COCKROACH_WING = regItem("cockroach_wing", () -> new Item(new Properties()));
   public static final Supplier<Item> COCKROACH_OOTHECA = regItem("cockroach_ootheca", () -> new ItemAnimalEgg(new Properties()));
   public static final Supplier<Item> ACACIA_BLOSSOM = regItem("acacia_blossom", () -> new Item(new Properties()));
   public static final Supplier<Item> SOUL_HEART = regItem("soul_heart", () -> new Item(new Properties()));
   public static final Supplier<Item> SPIKED_SCUTE = regItem("spiked_scute", () -> new Item(new Properties()));
   public static final Supplier<Item> SPIKED_TURTLE_SHELL = regItem(
      "spiked_turtle_shell", () -> new ItemModArmor(SPIKED_TURTLE_SHELL_ARMOR_MATERIAL, Type.HELMET)
   );
   public static final Supplier<Item> SHRIMP_FRIED_RICE = regItem(
      "shrimp_fried_rice", () -> new Item(new Properties().food(new Builder().nutrition(12).saturationModifier(1.0F).build()))
   );
   public static final Supplier<Item> GUSTER_EYE = regItem("guster_eye", () -> new Item(new Properties()));
   public static final Supplier<Item> POCKET_SAND = regItem("pocket_sand", () -> new ItemPocketSand(new Properties().durability(220)));
   public static final Supplier<Item> WARPED_MUSCLE = regItem("warped_muscle", () -> new Item(new Properties()));
   public static final Supplier<Item> HEMOLYMPH_SAC = regItem("hemolymph_sac", () -> new Item(new Properties()));
   public static final Supplier<Item> HEMOLYMPH_BLASTER = regItem("hemolymph_blaster", () -> new ItemHemolymphBlaster(new Properties().durability(150)));
   public static final Supplier<Item> WARPED_MIXTURE = regItem(
      "warped_mixture", () -> new Item(new Properties().rarity(Rarity.RARE).stacksTo(1).craftRemainder(Items.GLASS_BOTTLE))
   );
   public static final Supplier<Item> STRADDLITE = regItem("straddlite", () -> new Item(new Properties().fireResistant()));
   public static final Supplier<Item> STRADPOLE_BUCKET = regItem(
      "stradpole_bucket", () -> new ItemModFishBucket(AMEntityRegistry.STRADPOLE, Fluids.LAVA, new Properties())
   );
   public static final Supplier<Item> STRADDLEBOARD = regItem("straddleboard", () -> new ItemStraddleboard(new Properties().fireResistant().durability(220)));
   public static final Supplier<Item> EMU_EGG = regItem("emu_egg", () -> new ItemAnimalEgg(new Properties().stacksTo(8)));
   public static final Supplier<Item> BOILED_EMU_EGG = regItem(
      "boiled_emu_egg", () -> new Item(new Properties().food(new Builder().nutrition(4).saturationModifier(1.0F).build()))
   );
   public static final Supplier<Item> EMU_FEATHER = regItem("emu_feather", () -> new Item(new Properties().fireResistant()));
   public static final Supplier<Item> EMU_LEGGINGS = regItem("emu_leggings", () -> new ItemModArmor(EMU_ARMOR_MATERIAL, Type.LEGGINGS));
   public static final Supplier<Item> PLATYPUS_BUCKET = regItem(
      "platypus_bucket", () -> new ItemModFishBucket(AMEntityRegistry.PLATYPUS, Fluids.WATER, new Properties())
   );
   public static final Supplier<Item> FEDORA = regItem("fedora", () -> new ItemModArmor(FEDORA_ARMOR_MATERIAL, Type.HELMET));
   public static final Supplier<Item> DROPBEAR_CLAW = regItem("dropbear_claw", () -> new Item(new Properties()));
   public static final Supplier<Item> KANGAROO_MEAT = regItem(
      "kangaroo_meat", () -> new Item(new Properties().food(new Builder().nutrition(4).saturationModifier(0.6F).build()))
   );
   public static final Supplier<Item> COOKED_KANGAROO_MEAT = regItem(
      "cooked_kangaroo_meat", () -> new Item(new Properties().food(new Builder().nutrition(8).saturationModifier(0.85F).build()))
   );
   public static final Supplier<Item> KANGAROO_HIDE = regItem("kangaroo_hide", () -> new Item(new Properties()));
   public static final Supplier<Item> KANGAROO_BURGER = regItem(
      "kangaroo_burger", () -> new Item(new Properties().food(new Builder().nutrition(12).saturationModifier(1.0F).build()))
   );
   public static final Supplier<Item> AMBERGRIS = regItem("ambergris", () -> new ItemFuel(new Properties(), 12800));
   public static final Supplier<Item> CACHALOT_WHALE_TOOTH = regItem("cachalot_whale_tooth", () -> new Item(new Properties()));
   public static final Supplier<Item> ECHOLOCATOR = regItem(
      "echolocator", () -> new ItemEcholocator(new Properties().durability(100), ItemEcholocator.EchoType.ECHOLOCATION)
   );
   public static final Supplier<Item> ENDOLOCATOR = regItem(
      "endolocator", () -> new ItemEcholocator(new Properties().durability(25), ItemEcholocator.EchoType.ENDER)
   );
   public static final Supplier<Item> GONGYLIDIA = regItem(
      "gongylidia", () -> new Item(new Properties().food(new Builder().nutrition(3).saturationModifier(1.2F).build()))
   );
   public static final Supplier<Item> LEAFCUTTER_ANT_PUPA = regItem("leafcutter_ant_pupa", () -> new ItemLeafcutterPupa(new Properties()));
   public static final Supplier<Item> ENDERIOPHAGE_ROCKET = regItem("enderiophage_rocket", () -> new ItemEnderiophageRocket(new Properties()));
   public static final Supplier<Item> FALCONRY_GLOVE_INVENTORY = regItem("falconry_glove_inventory", () -> new ItemInventoryOnly(new Properties()));
   public static final Supplier<Item> FALCONRY_GLOVE_HAND = regItem("falconry_glove_hand", () -> new ItemInventoryOnly(new Properties()));
   public static final Supplier<Item> FALCONRY_GLOVE = regItem("falconry_glove", () -> new ItemFalconryGlove(new Properties().stacksTo(1)));
   public static final Supplier<Item> FALCONRY_HOOD = regItem("falconry_hood", () -> new Item(new Properties()));
   public static final Supplier<Item> TARANTULA_HAWK_WING_FRAGMENT = regItem("tarantula_hawk_wing_fragment", () -> new Item(new Properties()));
   public static final Supplier<Item> TARANTULA_HAWK_WING = regItem("tarantula_hawk_wing", () -> new Item(new Properties()));
   public static final Supplier<Item> TARANTULA_HAWK_ELYTRA = regItem(
      "tarantula_hawk_elytra",
      () -> new ItemTarantulaHawkElytra(AMCompat.glider(new Properties().durability(800).rarity(Rarity.UNCOMMON)), TARANTULA_HAWK_ELYTRA_MATERIAL)
   );
   public static final Supplier<Item> MYSTERIOUS_WORM = regItem("mysterious_worm", () -> new ItemMysteriousWorm(new Properties().rarity(Rarity.RARE)));
   public static final Supplier<Item> VOID_WORM_MANDIBLE = regItem("void_worm_mandible", () -> new Item(new Properties()));
   public static final Supplier<Item> VOID_WORM_EYE = regItem("void_worm_eye", () -> new Item(new Properties().rarity(Rarity.RARE)));
   public static final Supplier<Item> DIMENSIONAL_CARVER = regItem(
      "dimensional_carver", () -> new ItemDimensionalCarver(new Properties().durability(20).rarity(Rarity.EPIC))
   );
   public static final Supplier<Item> SHATTERED_DIMENSIONAL_CARVER = regItem(
      "shattered_dimensional_carver", () -> new ItemShatteredDimensionalCarver(new Properties().durability(4).rarity(Rarity.RARE))
   );
   public static final Supplier<Item> SERRATED_SHARK_TOOTH = regItem("serrated_shark_tooth", () -> new Item(new Properties()));
   public static final Supplier<Item> FRILLED_SHARK_BUCKET = regItem(
      "frilled_shark_bucket", () -> new ItemModFishBucket(AMEntityRegistry.FRILLED_SHARK, Fluids.WATER, new Properties())
   );
   public static final Supplier<Item> SHIELD_OF_THE_DEEP = regItem(
      "shield_of_the_deep",
      () -> new ItemShieldOfTheDeep(AMCompat.repairableWith(new Properties().durability(400).rarity(Rarity.UNCOMMON), "shield_of_the_deep"))
   );
   public static final Supplier<Item> MIMIC_OCTOPUS_BUCKET = regItem(
      "mimic_octopus_bucket", () -> new ItemModFishBucket(AMEntityRegistry.MIMIC_OCTOPUS, Fluids.WATER, new Properties())
   );
   public static final Supplier<Item> FROSTSTALKER_HORN = regItem("froststalker_horn", () -> new Item(new Properties()));
   public static final Supplier<Item> FROSTSTALKER_HELMET = regItem("froststalker_helmet", () -> new ItemModArmor(FROSTSTALKER_ARMOR_MATERIAL, Type.HELMET));
   public static final Supplier<Item> PIGSHOES = regItem("pigshoes", () -> new ItemPigshoes(new Properties().stacksTo(1)));
   public static final Supplier<Item> STRADDLE_HELMET = regItem("straddle_helmet", () -> new Item(new Properties().fireResistant()));
   public static final Supplier<Item> STRADDLE_SADDLE = regItem("straddle_saddle", () -> new Item(new Properties().fireResistant()));
   public static final Supplier<Item> COSMIC_COD = regItem(
      "cosmic_cod",
      () -> new Item(
         new Properties()
            .food(
               new Builder()
                  .nutrition(6)
                  .saturationModifier(0.3F)
                  .effect(new MobEffectInstance(AMCompat.effect(AMEffectRegistry.ENDER_FLU.get()), 12000), 0.15F)
                  .build()
            )
      )
   );
   public static final Supplier<Item> SHED_SNAKE_SKIN = regItem("shed_snake_skin", () -> new Item(new Properties()));
   public static final Supplier<Item> VINE_LASSO_INVENTORY = regItem("vine_lasso_inventory", () -> new ItemInventoryOnly(new Properties()));
   public static final Supplier<Item> VINE_LASSO_HAND = regItem("vine_lasso_hand", () -> new ItemInventoryOnly(new Properties()));
   public static final Supplier<Item> VINE_LASSO = regItem("vine_lasso", () -> new ItemVineLasso(new Properties().stacksTo(1)));
   public static final Supplier<Item> ROCKY_SHELL = regItem("rocky_shell", () -> new Item(new Properties()));
   public static final Supplier<Item> ROCKY_CHESTPLATE = regItem("rocky_chestplate", () -> new ItemModArmor(ROCKY_ARMOR_MATERIAL, Type.CHESTPLATE));
   public static final Supplier<Item> POTTED_FLUTTER = regItem("potted_flutter", () -> new ItemFlutterPot(new Properties()));
   public static final Supplier<Item> TERRAPIN_BUCKET = regItem(
      "terrapin_bucket", () -> new ItemModFishBucket(AMEntityRegistry.TERRAPIN, Fluids.WATER, new Properties())
   );
   public static final Supplier<Item> COMB_JELLY_BUCKET = regItem(
      "comb_jelly_bucket", () -> new ItemModFishBucket(AMEntityRegistry.COMB_JELLY, Fluids.WATER, new Properties())
   );
   public static final Supplier<Item> RAINBOW_JELLY = regItem(
      "rainbow_jelly", () -> new ItemRainbowJelly(new Properties().food(new Builder().nutrition(1).saturationModifier(0.2F).build()))
   );
   public static final Supplier<Item> COSMIC_COD_BUCKET = regItem("cosmic_cod_bucket", () -> new ItemCosmicCodBucket(new Properties()));
   public static final Supplier<Item> MUNGAL_SPORES = regItem("mungal_spores", () -> new Item(new Properties()));
   public static final Supplier<Item> BISON_FUR = regItem("bison_fur", () -> new Item(new Properties()));
   public static final Supplier<Item> LOST_TENTACLE = regItem("lost_tentacle", () -> new Item(new Properties()));
   public static final Supplier<Item> SQUID_GRAPPLE = regItem(
      "squid_grapple", () -> new ItemSquidGrapple(AMCompat.repairableWith(new Properties().durability(450), "squid_grapple"))
   );
   public static final Supplier<Item> DEVILS_HOLE_PUPFISH_BUCKET = regItem(
      "devils_hole_pupfish_bucket", () -> new ItemModFishBucket(AMEntityRegistry.DEVILS_HOLE_PUPFISH, Fluids.WATER, new Properties())
   );
   public static final Supplier<Item> PUPFISH_LOCATOR = regItem(
      "pupfish_locator", () -> new ItemEcholocator(new Properties().durability(200), ItemEcholocator.EchoType.PUPFISH)
   );
   public static final Supplier<Item> SMALL_CATFISH_BUCKET = regItem(
      "small_catfish_bucket", () -> new ItemModFishBucket(AMEntityRegistry.CATFISH, Fluids.WATER, new Properties())
   );
   public static final Supplier<Item> MEDIUM_CATFISH_BUCKET = regItem("medium_catfish_bucket", () -> new ItemCatfishBucket(Fluids.WATER, new Properties()));
   public static final Supplier<Item> LARGE_CATFISH_BUCKET = regItem("large_catfish_bucket", () -> new ItemCatfishBucket(Fluids.WATER, new Properties()));
   public static final Supplier<Item> RAW_CATFISH = regItem(
      "raw_catfish", () -> new Item(new Properties().food(new Builder().nutrition(2).saturationModifier(0.3F).build()))
   );
   public static final Supplier<Item> COOKED_CATFISH = regItem(
      "cooked_catfish", () -> new Item(new Properties().food(new Builder().nutrition(5).saturationModifier(0.5F).build()))
   );
   public static final Supplier<Item> FLYING_FISH = regItem(
      "flying_fish", () -> new Item(new Properties().food(new Builder().nutrition(3).saturationModifier(0.4F).build()))
   );
   public static final Supplier<Item> FLYING_FISH_BOOTS = regItem("flying_fish_boots", () -> new ItemModArmor(FLYING_FISH_MATERIAL, Type.BOOTS));
   public static final Supplier<Item> FLYING_FISH_BUCKET = regItem(
      "flying_fish_bucket", () -> new ItemModFishBucket(AMEntityRegistry.FLYING_FISH, Fluids.WATER, new Properties())
   );
   public static final Supplier<Item> FISH_BONES = regItem("fish_bones", () -> new Item(new Properties()));
   public static final Supplier<Item> SKELEWAG_SWORD_INVENTORY = regItem("skelewag_sword_inventory", () -> new ItemInventoryOnly(new Properties()));
   public static final Supplier<Item> SKELEWAG_SWORD_HAND = regItem("skelewag_sword_hand", () -> new ItemInventoryOnly(new Properties()));
   public static final Supplier<Item> SKELEWAG_SWORD = regItem("skelewag_sword", () -> new ItemSkelewagSword(new Properties().stacksTo(1).durability(430)));
   public static final Supplier<Item> NOVELTY_HAT = regItem("novelty_hat", () -> new ItemModArmor(NOVELTY_HAT_MATERIAL, Type.HELMET));
   public static final Supplier<Item> MUDSKIPPER_BUCKET = regItem(
      "mudskipper_bucket", () -> new ItemModFishBucket(AMEntityRegistry.MUDSKIPPER, Fluids.WATER, new Properties())
   );
   public static final Supplier<Item> FARSEER_ARM = regItem("farseer_arm", () -> new Item(new Properties().rarity(Rarity.RARE)));
   public static final Supplier<Item> SKREECHER_SOUL = regItem("skreecher_soul", () -> new Item(new Properties()));
   public static final Supplier<Item> GHOSTLY_PICKAXE = regItem("ghostly_pickaxe", () -> new ItemGhostlyPickaxe(new Properties()));
   public static final Supplier<Item> ELASTIC_TENDON = regItem("elastic_tendon", () -> new Item(new Properties()));
   public static final Supplier<Item> TENDON_WHIP = regItem("tendon_whip", () -> new ItemTendonWhip(new Properties()));
   public static final Supplier<Item> UNSETTLING_KIMONO = regItem("unsettling_kimono", () -> new ItemModArmor(KIMONO_MATERIAL, Type.CHESTPLATE));
   public static final Supplier<Item> STINK_BOTTLE = regItem(
      "stink_bottle", () -> new ItemStinkBottle(AMBlockRegistry.SKUNK_SPRAY, new Properties().stacksTo(16))
   );
   public static final Supplier<Item> STINK_RAY_HAND = regItem("stink_ray_hand", () -> new ItemInventoryOnly(new Properties()));
   public static final Supplier<Item> STINK_RAY_INVENTORY = regItem("stink_ray_inventory", () -> new ItemInventoryOnly(new Properties()));
   public static final Supplier<Item> STINK_RAY_EMPTY_HAND = regItem("stink_ray_empty_hand", () -> new ItemInventoryOnly(new Properties()));
   public static final Supplier<Item> STINK_RAY_EMPTY_INVENTORY = regItem("stink_ray_empty_inventory", () -> new ItemInventoryOnly(new Properties()));
   public static final Supplier<Item> STINK_RAY = regItem("stink_ray", () -> new ItemStinkRay(new Properties().durability(5)));
   public static final Supplier<Item> BANANA_SLUG_SLIME = regItem("banana_slug_slime", () -> new Item(new Properties()));
   public static final Supplier<Item> MOSQUITO_REPELLENT_STEW = regItem(
      "mosquito_repellent_stew",
      () -> new Item(
         new Properties()
            .food(
               new Builder()
                  .nutrition(4)
                  .alwaysEdible()
                  .saturationModifier(0.3F)
                  .usingConvertsTo(Items.BOWL)
                  .effect(new MobEffectInstance(AMCompat.effect(AMEffectRegistry.MOSQUITO_REPELLENT.get()), 24000), 1.0F)
                  .build()
            )
            .stacksTo(1)
      )
   );
   public static final Supplier<Item> TRIOPS_BUCKET = regItem(
      "triops_bucket", () -> new ItemModFishBucket(AMEntityRegistry.TRIOPS, Fluids.WATER, new Properties())
   );
   public static final Supplier<Item> MUSIC_DISC_THIME = regItem(
      "music_disc_thime", () -> new Item(new Properties().stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(jukeboxSong("music_disc_thime")))
   );
   public static final Supplier<Item> MUSIC_DISC_DAZE = regItem(
      "music_disc_daze", () -> new Item(new Properties().stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(jukeboxSong("music_disc_daze")))
   );

   public static <I extends Item> Supplier<I> regItem(String name, Supplier<I> sup) {
      return DEF_REG.register(name, sup);
   }

   private static ResourceKey<JukeboxSong> jukeboxSong(String name) {
      return ResourceKey.create(Registries.JUKEBOX_SONG, AMCompat.rl("alexsmobs", name));
   }

   public static void initSpawnEggs() {
      regItem("spawn_egg_grizzly_bear", () -> AMCompat.spawnEgg(AMEntityRegistry.GRIZZLY_BEAR, 6896172, 9920836, new Properties()));
      regItem("spawn_egg_roadrunner", () -> AMCompat.spawnEgg(AMEntityRegistry.ROADRUNNER, 3812902, 16509390, new Properties()));
      regItem("spawn_egg_bone_serpent", () -> AMCompat.spawnEgg(AMEntityRegistry.BONE_SERPENT, 15063492, 16736312, new Properties()));
      regItem("spawn_egg_gazelle", () -> AMCompat.spawnEgg(AMEntityRegistry.GAZELLE, 14526069, 2894117, new Properties()));
      regItem("spawn_egg_crocodile", () -> AMCompat.spawnEgg(AMEntityRegistry.CROCODILE, 7571776, 10920286, new Properties()));
      regItem("spawn_egg_fly", () -> AMCompat.spawnEgg(AMEntityRegistry.FLY, 4604481, 8990254, new Properties()));
      regItem("spawn_egg_hummingbird", () -> AMCompat.spawnEgg(AMEntityRegistry.HUMMINGBIRD, 3300991, 4499295, new Properties()));
      regItem("spawn_egg_orca", () -> AMCompat.spawnEgg(AMEntityRegistry.ORCA, 2894892, 14080228, new Properties()));
      regItem("spawn_egg_sunbird", () -> AMCompat.spawnEgg(AMEntityRegistry.SUNBIRD, 16148815, 16768416, new Properties()));
      regItem("spawn_egg_gorilla", () -> AMCompat.spawnEgg(AMEntityRegistry.GORILLA, 5856093, 1842209, new Properties()));
      regItem("spawn_egg_crimson_mosquito", () -> AMCompat.spawnEgg(AMEntityRegistry.CRIMSON_MOSQUITO, 5455935, 12655130, new Properties()));
      regItem("spawn_egg_rattlesnake", () -> AMCompat.spawnEgg(AMEntityRegistry.RATTLESNAKE, 13547924, 9665115, new Properties()));
      regItem("spawn_egg_endergrade", () -> AMCompat.spawnEgg(AMEntityRegistry.ENDERGRADE, 7889587, 8502763, new Properties()));
      regItem("spawn_egg_hammerhead_shark", () -> AMCompat.spawnEgg(AMEntityRegistry.HAMMERHEAD_SHARK, 9081525, 12173016, new Properties()));
      regItem("spawn_egg_lobster", () -> AMCompat.spawnEgg(AMEntityRegistry.LOBSTER, 12857635, 14507832, new Properties()));
      regItem("spawn_egg_komodo_dragon", () -> AMCompat.spawnEgg(AMEntityRegistry.KOMODO_DRAGON, 7629903, 5653041, new Properties()));
      regItem("spawn_egg_capuchin_monkey", () -> AMCompat.spawnEgg(AMEntityRegistry.CAPUCHIN_MONKEY, 2433311, 15850163, new Properties()));
      regItem("spawn_egg_centipede", () -> AMCompat.spawnEgg(AMEntityRegistry.CENTIPEDE_HEAD, 3418926, 7550025, new Properties()));
      regItem("spawn_egg_warped_toad", () -> AMCompat.spawnEgg(AMEntityRegistry.WARPED_TOAD, 2070158, 16690285, new Properties()));
      regItem("spawn_egg_moose", () -> AMCompat.spawnEgg(AMEntityRegistry.MOOSE, 3551274, 13939075, new Properties()));
      regItem("spawn_egg_mimicube", () -> AMCompat.spawnEgg(AMEntityRegistry.MIMICUBE, 9076929, 6180719, new Properties()));
      regItem("spawn_egg_raccoon", () -> AMCompat.spawnEgg(AMEntityRegistry.RACCOON, 8749694, 2762534, new Properties()));
      regItem("spawn_egg_blobfish", () -> AMCompat.spawnEgg(AMEntityRegistry.BLOBFISH, 14403261, 10386047, new Properties()));
      regItem("spawn_egg_seal", () -> AMCompat.spawnEgg(AMEntityRegistry.SEAL, 4734002, 6707532, new Properties()));
      regItem("spawn_egg_cockroach", () -> AMCompat.spawnEgg(AMEntityRegistry.COCKROACH, 854281, 4334622, new Properties()));
      regItem("spawn_egg_shoebill", () -> AMCompat.spawnEgg(AMEntityRegistry.SHOEBILL, 8553090, 14005386, new Properties()));
      regItem("spawn_egg_elephant", () -> AMCompat.spawnEgg(AMEntityRegistry.ELEPHANT, 9275783, 15590865, new Properties()));
      regItem("spawn_egg_soul_vulture", () -> AMCompat.spawnEgg(AMEntityRegistry.SOUL_VULTURE, 2303533, 5764351, new Properties()));
      regItem("spawn_egg_snow_leopard", () -> AMCompat.spawnEgg(AMEntityRegistry.SNOW_LEOPARD, 11313811, 2498589, new Properties()));
      regItem("spawn_egg_spectre", () -> AMCompat.spawnEgg(AMEntityRegistry.SPECTRE, 13160687, 8884719, new Properties()));
      regItem("spawn_egg_crow", () -> AMCompat.spawnEgg(AMEntityRegistry.CROW, 856348, 1843248, new Properties()));
      regItem("spawn_egg_alligator_snapping_turtle", () -> AMCompat.spawnEgg(AMEntityRegistry.ALLIGATOR_SNAPPING_TURTLE, 7101522, 4548902, new Properties()));
      regItem("spawn_egg_mungus", () -> AMCompat.spawnEgg(AMEntityRegistry.MUNGUS, 8612493, 4539724, new Properties()));
      regItem("spawn_egg_mantis_shrimp", () -> AMCompat.spawnEgg(AMEntityRegistry.MANTIS_SHRIMP, 14370904, 1415454, new Properties()));
      regItem("spawn_egg_guster", () -> AMCompat.spawnEgg(AMEntityRegistry.GUSTER, 16307354, 16740874, new Properties()));
      regItem("spawn_egg_warped_mosco", () -> AMCompat.spawnEgg(AMEntityRegistry.WARPED_MOSCO, 3288920, 5988081, new Properties()));
      regItem("spawn_egg_straddler", () -> AMCompat.spawnEgg(AMEntityRegistry.STRADDLER, 6119278, 13478022, new Properties()));
      regItem("spawn_egg_stradpole", () -> AMCompat.spawnEgg(AMEntityRegistry.STRADPOLE, 6119278, 5728907, new Properties()));
      regItem("spawn_egg_emu", () -> AMCompat.spawnEgg(AMEntityRegistry.EMU, 6705990, 3881272, new Properties()));
      regItem("spawn_egg_platypus", () -> AMCompat.spawnEgg(AMEntityRegistry.PLATYPUS, 8212542, 3554115, new Properties()));
      regItem("spawn_egg_dropbear", () -> AMCompat.spawnEgg(AMEntityRegistry.DROPBEAR, 9055541, 6333347, new Properties()));
      regItem("spawn_egg_tasmanian_devil", () -> AMCompat.spawnEgg(AMEntityRegistry.TASMANIAN_DEVIL, 2434086, 11056319, new Properties()));
      regItem("spawn_egg_kangaroo", () -> AMCompat.spawnEgg(AMEntityRegistry.KANGAROO, 13540709, 14597536, new Properties()));
      regItem("spawn_egg_cachalot_whale", () -> AMCompat.spawnEgg(AMEntityRegistry.CACHALOT_WHALE, 9738393, 6252142, new Properties()));
      regItem("spawn_egg_leafcutter_ant", () -> AMCompat.spawnEgg(AMEntityRegistry.LEAFCUTTER_ANT, 9846819, 10901808, new Properties()));
      regItem("spawn_egg_enderiophage", () -> AMCompat.spawnEgg(AMEntityRegistry.ENDERIOPHAGE, 8859011, 16179917, new Properties()));
      regItem("spawn_egg_bald_eagle", () -> AMCompat.spawnEgg(AMEntityRegistry.BALD_EAGLE, 3284760, 16053492, new Properties()));
      regItem("spawn_egg_tiger", () -> AMCompat.spawnEgg(AMEntityRegistry.TIGER, 13066542, 2765363, new Properties()));
      regItem("spawn_egg_tarantula_hawk", () -> AMCompat.spawnEgg(AMEntityRegistry.TARANTULA_HAWK, 2312035, 14908216, new Properties()));
      regItem("spawn_egg_void_worm", () -> AMCompat.spawnEgg(AMEntityRegistry.VOID_WORM, 987174, 1481131, new Properties()));
      regItem("spawn_egg_frilled_shark", () -> AMCompat.spawnEgg(AMEntityRegistry.FRILLED_SHARK, 7498603, 8863037, new Properties()));
      regItem("spawn_egg_mimic_octopus", () -> AMCompat.spawnEgg(AMEntityRegistry.MIMIC_OCTOPUS, 16772060, 1907743, new Properties()));
      regItem("spawn_egg_seagull", () -> AMCompat.spawnEgg(AMEntityRegistry.SEAGULL, 13226716, 16767056, new Properties()));
      regItem("spawn_egg_froststalker", () -> AMCompat.spawnEgg(AMEntityRegistry.FROSTSTALKER, 7899841, 10601471, new Properties()));
      regItem("spawn_egg_tusklin", () -> AMCompat.spawnEgg(AMEntityRegistry.TUSKLIN, 7559233, 15262421, new Properties()));
      regItem("spawn_egg_laviathan", () -> AMCompat.spawnEgg(AMEntityRegistry.LAVIATHAN, 14058326, 3946823, new Properties()));
      regItem("spawn_egg_cosmaw", () -> AMCompat.spawnEgg(AMEntityRegistry.COSMAW, 7630269, 14073827, new Properties()));
      regItem("spawn_egg_toucan", () -> AMCompat.spawnEgg(AMEntityRegistry.TOUCAN, 16092979, 1974579, new Properties()));
      regItem("spawn_egg_maned_wolf", () -> AMCompat.spawnEgg(AMEntityRegistry.MANED_WOLF, 12286535, 4204314, new Properties()));
      regItem("spawn_egg_anaconda", () -> AMCompat.spawnEgg(AMEntityRegistry.ANACONDA, 5659682, 13858367, new Properties()));
      regItem("spawn_egg_anteater", () -> AMCompat.spawnEgg(AMEntityRegistry.ANTEATER, 4996922, 13417652, new Properties()));
      regItem("spawn_egg_rocky_roller", () -> AMCompat.spawnEgg(AMEntityRegistry.ROCKY_ROLLER, 11568495, 10064260, new Properties()));
      regItem("spawn_egg_flutter", () -> AMCompat.spawnEgg(AMEntityRegistry.FLUTTER, 7377453, 13663203, new Properties()));
      regItem("spawn_egg_gelada_monkey", () -> AMCompat.spawnEgg(AMEntityRegistry.GELADA_MONKEY, 11570276, 16731987, new Properties()));
      regItem("spawn_egg_jerboa", () -> AMCompat.spawnEgg(AMEntityRegistry.JERBOA, 14599562, 14589328, new Properties()));
      regItem("spawn_egg_terrapin", () -> AMCompat.spawnEgg(AMEntityRegistry.TERRAPIN, 7237168, 9606727, new Properties()));
      regItem("spawn_egg_comb_jelly", () -> AMCompat.spawnEgg(AMEntityRegistry.COMB_JELLY, 13625854, 7274379, new Properties()));
      regItem("spawn_egg_cosmic_cod", () -> AMCompat.spawnEgg(AMEntityRegistry.COSMIC_COD, 6915527, 14864895, new Properties()));
      regItem("spawn_egg_bunfungus", () -> AMCompat.spawnEgg(AMEntityRegistry.BUNFUNGUS, 7302545, 13183785, new Properties()));
      regItem("spawn_egg_bison", () -> AMCompat.spawnEgg(AMEntityRegistry.BISON, 4995630, 8021318, new Properties()));
      regItem("spawn_egg_giant_squid", () -> AMCompat.spawnEgg(AMEntityRegistry.GIANT_SQUID, 11225933, 14056811, new Properties()));
      regItem("spawn_egg_devils_hole_pupfish", () -> AMCompat.spawnEgg(AMEntityRegistry.DEVILS_HOLE_PUPFISH, 5667780, 7095413, new Properties()));
      regItem("spawn_egg_catfish", () -> AMCompat.spawnEgg(AMEntityRegistry.CATFISH, 8419159, 9073766, new Properties()));
      regItem("spawn_egg_flying_fish", () -> AMCompat.spawnEgg(AMEntityRegistry.FLYING_FISH, 8109293, 6848947, new Properties()));
      regItem("spawn_egg_skelewag", () -> AMCompat.spawnEgg(AMEntityRegistry.SKELEWAG, 14286001, 3821360, new Properties()));
      regItem("spawn_egg_rain_frog", () -> AMCompat.spawnEgg(AMEntityRegistry.RAIN_FROG, 12629403, 8086863, new Properties()));
      regItem("spawn_egg_potoo", () -> AMCompat.spawnEgg(AMEntityRegistry.POTOO, 9205587, 16760898, new Properties()));
      regItem("spawn_egg_mudskipper", () -> AMCompat.spawnEgg(AMEntityRegistry.MUDSKIPPER, 6320202, 4817004, new Properties()));
      regItem("spawn_egg_rhinoceros", () -> AMCompat.spawnEgg(AMEntityRegistry.RHINOCEROS, 10589588, 8549492, new Properties()));
      regItem("spawn_egg_sugar_glider", () -> AMCompat.spawnEgg(AMEntityRegistry.SUGAR_GLIDER, 8814977, 15461344, new Properties()));
      regItem("spawn_egg_farseer", () -> AMCompat.spawnEgg(AMEntityRegistry.FARSEER, 3356495, 9568089, new Properties()));
      regItem("spawn_egg_skreecher", () -> AMCompat.spawnEgg(AMEntityRegistry.SKREECHER, 477271, 8386815, new Properties()));
      regItem("spawn_egg_underminer", () -> AMCompat.spawnEgg(AMEntityRegistry.UNDERMINER, 14082815, 7111876, new Properties()));
      regItem("spawn_egg_murmur", () -> AMCompat.spawnEgg(AMEntityRegistry.MURMUR, 8406088, 11906972, new Properties()));
      regItem("spawn_egg_skunk", () -> AMCompat.spawnEgg(AMEntityRegistry.SKUNK, 2239798, 15001074, new Properties()));
      regItem("spawn_egg_banana_slug", () -> AMCompat.spawnEgg(AMEntityRegistry.BANANA_SLUG, 16764997, 16773491, new Properties()));
      regItem("spawn_egg_blue_jay", () -> AMCompat.spawnEgg(AMEntityRegistry.BLUE_JAY, 6273022, 2702146, new Properties()));
      regItem("spawn_egg_caiman", () -> AMCompat.spawnEgg(AMEntityRegistry.CAIMAN, 6051377, 12305500, new Properties()));
      regItem("spawn_egg_triops", () -> AMCompat.spawnEgg(AMEntityRegistry.TRIOPS, 9861460, 13267280, new Properties()));
      registerPatternItem("bear");
      registerPatternItem("australia_0");
      registerPatternItem("australia_1");
      registerPatternItem("new_mexico");
      registerPatternItem("brazil");

      for (int i = 0; i <= 10; i++) {
         regItem("dimensional_carver_shard_" + i, () -> new ItemInventoryOnly(new Properties()));
      }
   }

   private static void registerPatternItem(String name) {
      TagKey<BannerPattern> bannerPatternTagKey = TagKey.create(Registries.BANNER_PATTERN, AMCompat.rl("alexsmobs", "pattern_for_" + name));
      regItem("banner_pattern_" + name, () -> new BannerPatternItem(bannerPatternTagKey, new Properties().stacksTo(1)));
   }

   public static void init() {
      CROCODILE_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(new ItemLike[]{(ItemLike)CROCODILE_SCUTE.get()}));
      ROADRUNNER_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(new ItemLike[]{(ItemLike)ROADRUNNER_FEATHER.get()}));
      CENTIPEDE_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(new ItemLike[]{(ItemLike)CENTIPEDE_LEG.get()}));
      MOOSE_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(new ItemLike[]{(ItemLike)MOOSE_ANTLER.get()}));
      RACCOON_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(new ItemLike[]{(ItemLike)RACCOON_TAIL.get()}));
      SOMBRERO_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(new ItemLike[]{Items.HAY_BLOCK}));
      SPIKED_TURTLE_SHELL_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(new ItemLike[]{(ItemLike)SPIKED_SCUTE.get()}));
      FEDORA_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(new ItemLike[]{Items.LEATHER}));
      EMU_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(new ItemLike[]{(ItemLike)EMU_FEATHER.get()}));
      ROCKY_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(new ItemLike[]{(ItemLike)ROCKY_SHELL.get()}));
      FLYING_FISH_MATERIAL.setRepairMaterial(Ingredient.of(new ItemLike[]{(ItemLike)FLYING_FISH.get()}));
      NOVELTY_HAT_MATERIAL.setRepairMaterial(Ingredient.of(new ItemLike[]{Items.BONE}));
      KIMONO_MATERIAL.setRepairMaterial((Supplier<Ingredient>)(() -> AMCompat.ingredientOf(ItemTags.WOOL)));
      LecternBooks.BOOKS.put(AMCompat.rl("alexsmobs", "animal_dictionary"), new LecternBooks.BookData(6318886, 16644333));
   }

   public static void initDispenser() {
      DispenserBlock.registerBehavior(
         (ItemLike)SHARK_TOOTH_ARROW.get(),
         new AbstractProjectileDispenseBehavior() {
            @Override
            protected Projectile getProjectile(Level worldIn, Position position, ItemStack stackIn) {
               EntitySharkToothArrow entityarrow = new EntitySharkToothArrow(
                  AMEntityRegistry.SHARK_TOOTH_ARROW.get(), position.x(), position.y(), position.z(), worldIn
               );
               entityarrow.pickup = Pickup.ALLOWED;
               return entityarrow;
            }
         }
      );
      DispenserBlock.registerBehavior((ItemLike)ANCIENT_DART.get(), new AbstractProjectileDispenseBehavior() {
         @Override
         protected Projectile getProjectile(Level worldIn, Position position, ItemStack stackIn) {
            EntityTossedItem tossedItem = new EntityTossedItem(worldIn, position.x(), position.y(), position.z());
            tossedItem.setDart(true);
            return tossedItem;
         }
      });
      DispenserBlock.registerBehavior((ItemLike)COCKROACH_OOTHECA.get(), new AbstractProjectileDispenseBehavior() {
         @Override
         protected Projectile getProjectile(Level worldIn, Position position, ItemStack stackIn) {
            return new EntityCockroachEgg(worldIn, position.x(), position.y(), position.z());
         }
      });
      DispenserBlock.registerBehavior((ItemLike)EMU_EGG.get(), new AbstractProjectileDispenseBehavior() {
         @Override
         protected Projectile getProjectile(Level worldIn, Position position, ItemStack stackIn) {
            return new EntityEmuEgg(worldIn, position.x(), position.y(), position.z());
         }
      });
      DispenserBlock.registerBehavior((ItemLike)ENDERIOPHAGE_ROCKET.get(), new AbstractProjectileDispenseBehavior() {
         @Override
         protected Projectile getProjectile(Level worldIn, Position position, ItemStack stackIn) {
            return new EntityEnderiophageRocket(worldIn, position.x(), position.y(), position.z(), stackIn);
         }
      });
      DispenseItemBehavior bucketDispenseBehavior = new DefaultDispenseItemBehavior() {
         private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

         public ItemStack execute(BlockSource blockSource, ItemStack stack) {
            DispensibleContainerItem dispensiblecontaineritem = (DispensibleContainerItem)stack.getItem();
            BlockPos blockpos = blockSource.pos().relative((Direction)blockSource.state().getValue(DispenserBlock.FACING));
            Level level = blockSource.level();
            if (dispensiblecontaineritem.emptyContents((Player)null, level, blockpos, (BlockHitResult)null)) {
               dispensiblecontaineritem.checkExtraContent((Player)null, level, stack, blockpos);
               return new ItemStack(Items.BUCKET);
            } else {
               return this.defaultDispenseItemBehavior.dispense(blockSource, stack);
            }
         }
      };
      DispenserBlock.registerBehavior((ItemLike)LOBSTER_BUCKET.get(), bucketDispenseBehavior);
      DispenserBlock.registerBehavior((ItemLike)BLOBFISH_BUCKET.get(), bucketDispenseBehavior);
      DispenserBlock.registerBehavior((ItemLike)STRADPOLE_BUCKET.get(), bucketDispenseBehavior);
      DispenserBlock.registerBehavior((ItemLike)PLATYPUS_BUCKET.get(), bucketDispenseBehavior);
      DispenserBlock.registerBehavior((ItemLike)FRILLED_SHARK_BUCKET.get(), bucketDispenseBehavior);
      DispenserBlock.registerBehavior((ItemLike)MIMIC_OCTOPUS_BUCKET.get(), bucketDispenseBehavior);
      DispenserBlock.registerBehavior((ItemLike)TERRAPIN_BUCKET.get(), bucketDispenseBehavior);
      DispenserBlock.registerBehavior((ItemLike)COMB_JELLY_BUCKET.get(), bucketDispenseBehavior);
      DispenserBlock.registerBehavior((ItemLike)COSMIC_COD_BUCKET.get(), bucketDispenseBehavior);
      DispenserBlock.registerBehavior((ItemLike)DEVILS_HOLE_PUPFISH_BUCKET.get(), bucketDispenseBehavior);
      DispenserBlock.registerBehavior((ItemLike)SMALL_CATFISH_BUCKET.get(), bucketDispenseBehavior);
      DispenserBlock.registerBehavior((ItemLike)MEDIUM_CATFISH_BUCKET.get(), bucketDispenseBehavior);
      DispenserBlock.registerBehavior((ItemLike)LARGE_CATFISH_BUCKET.get(), bucketDispenseBehavior);
      DispenserBlock.registerBehavior((ItemLike)FLYING_FISH_BUCKET.get(), bucketDispenseBehavior);
      DispenserBlock.registerBehavior((ItemLike)MUDSKIPPER_BUCKET.get(), bucketDispenseBehavior);
      ComposterBlock.COMPOSTABLES.put((ItemLike)BANANA.get(), 0.65F);
      ComposterBlock.COMPOSTABLES.put(AMBlockRegistry.BANANA_PEEL.get().asItem(), 1.0F);
      ComposterBlock.COMPOSTABLES.put((ItemLike)ACACIA_BLOSSOM.get(), 0.65F);
      ComposterBlock.COMPOSTABLES.put((ItemLike)GONGYLIDIA.get(), 0.9F);
   }

   static {
      initSpawnEggs();
   }
}
