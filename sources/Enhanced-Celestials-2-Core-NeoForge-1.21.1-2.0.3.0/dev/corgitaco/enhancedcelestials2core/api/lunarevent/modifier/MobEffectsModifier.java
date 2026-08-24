package dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import corgitaco.corgilib.entity.condition.Condition;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.MobEffectInstanceBuilder;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public record MobEffectsModifier(List<Pair<Condition, MobEffectInstanceBuilder>> effects) implements LunarEventModifier {
   public static final MapCodec<MobEffectsModifier> CODEC = Codec.pair(
         Condition.CODEC.fieldOf("filter").codec(), MobEffectInstanceBuilder.CODEC.fieldOf("mob_effect").codec()
      )
      .listOf()
      .fieldOf("effects")
      .xmap(MobEffectsModifier::new, MobEffectsModifier::effects);

   @Override
   public LunarEventModifierType<?> type() {
      return LunarEventModifierTypes.MOB_EFFECTS;
   }

   @Override
   public Component description() {
      if (this.effects.isEmpty()) {
         return Component.translatable(
            "enhancedcelestials2core.lunar_event_modifier.mob_effects", new Object[]{Component.translatable("enhancedcelestials2core.description.none")}
         );
      } else {
         MutableComponent joined = null;

         for (Pair<Condition, MobEffectInstanceBuilder> pair : this.effects) {
            Component entryComponent = Component.translatable(
               "enhancedcelestials2core.lunar_event_modifier.mob_effects.entry",
               new Object[]{((MobEffectInstanceBuilder)pair.getSecond()).description(), ((Condition)pair.getFirst()).toString()}
            );
            joined = joined == null ? entryComponent.copy() : joined.append(", ").append(entryComponent);
         }

         return Component.translatable("enhancedcelestials2core.lunar_event_modifier.mob_effects", new Object[]{joined});
      }
   }
}
