package cn.foggyhillside.ends_delight.registry;

import cn.foggyhillside.ends_delight.event.loot.DragonLegAdditionModifier;
import cn.foggyhillside.ends_delight.event.loot.DragonMeatAdditionModifier;
import cn.foggyhillside.ends_delight.event.loot.DragonToothAdditionModifier;
import cn.foggyhillside.ends_delight.event.loot.EndermanGristleAdditionModifier;
import cn.foggyhillside.ends_delight.event.loot.EndermiteMeatAdditionModifier;
import cn.foggyhillside.ends_delight.event.loot.ShulkerMeatAdditionModifier;
import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries.Keys;

public class ModLootModifiers {
   public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = DeferredRegister.create(
      Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, "ends_delight"
   );
   public static final Supplier<MapCodec<? extends IGlobalLootModifier>> DragonLegModifierSerializers = LOOT_MODIFIERS.register(
      "dragon_leg", DragonLegAdditionModifier.CODEC
   );
   public static final Supplier<MapCodec<? extends IGlobalLootModifier>> DragonMeatModifierSerializers = LOOT_MODIFIERS.register(
      "dragon_meat", DragonMeatAdditionModifier.CODEC
   );
   public static final Supplier<MapCodec<? extends IGlobalLootModifier>> ShulkerMeatModifierSerializers = LOOT_MODIFIERS.register(
      "shulker_meat", ShulkerMeatAdditionModifier.CODEC
   );
   public static final Supplier<MapCodec<? extends IGlobalLootModifier>> DragonToothModifierSerializers = LOOT_MODIFIERS.register(
      "dragon_tooth", DragonToothAdditionModifier.CODEC
   );
   public static final Supplier<MapCodec<? extends IGlobalLootModifier>> EndermiteMeatModifierSerializers = LOOT_MODIFIERS.register(
      "endermite_meat", EndermiteMeatAdditionModifier.CODEC
   );
   public static final Supplier<MapCodec<? extends IGlobalLootModifier>> EndermanGristleModifierSerializers = LOOT_MODIFIERS.register(
      "enderman_gristle", EndermanGristleAdditionModifier.CODEC
   );
}
