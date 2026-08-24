package dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.network.chat.Component;

public record MobSpawnEquipmentModifier(List<MobEquipment.EquipmentCombination> equipmentCombinations) implements LunarEventModifier {
   public static final MapCodec<MobSpawnEquipmentModifier> CODEC = RecordCodecBuilder.mapCodec(
      builder -> builder.group(
            MobEquipment.EquipmentCombination.CODEC.listOf().fieldOf("equipment_combinations").forGetter(MobSpawnEquipmentModifier::equipmentCombinations)
         )
         .apply(builder, MobSpawnEquipmentModifier::new)
   );

   @Override
   public LunarEventModifierType<?> type() {
      return LunarEventModifierTypes.MOB_SPAWN_EQUIPMENT;
   }

   @Override
   public Component description() {
      return MobEquipment.describe("enhancedcelestials2core.lunar_event_modifier.mob_spawn_equipment", this.equipmentCombinations);
   }
}
