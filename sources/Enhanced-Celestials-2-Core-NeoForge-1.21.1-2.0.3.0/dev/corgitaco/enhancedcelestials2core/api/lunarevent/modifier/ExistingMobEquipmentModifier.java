package dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.network.chat.Component;

public record ExistingMobEquipmentModifier(List<MobEquipment.EquipmentCombination> equipmentCombinations) implements LunarEventModifier {
   public static final MapCodec<ExistingMobEquipmentModifier> CODEC = RecordCodecBuilder.mapCodec(
      builder -> builder.group(
            MobEquipment.EquipmentCombination.CODEC.listOf().fieldOf("equipment_combinations").forGetter(ExistingMobEquipmentModifier::equipmentCombinations)
         )
         .apply(builder, ExistingMobEquipmentModifier::new)
   );

   @Override
   public LunarEventModifierType<?> type() {
      return LunarEventModifierTypes.EXISTING_MOB_EQUIPMENT;
   }

   @Override
   public Component description() {
      return MobEquipment.describe("enhancedcelestials2core.lunar_event_modifier.existing_mob_equipment", this.equipmentCombinations);
   }
}
