package dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import corgitaco.corgilib.entity.condition.Condition;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.DropSettings;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public record EntityDropModifier(List<Pair<Condition, DropSettings>> drops) implements LunarEventModifier {
   public static final MapCodec<EntityDropModifier> CODEC = Codec.pair(
         Condition.CODEC.fieldOf("condition").codec(), DropSettings.CODEC.fieldOf("drop_settings").codec()
      )
      .listOf()
      .fieldOf("drops")
      .xmap(EntityDropModifier::new, EntityDropModifier::drops);

   @Override
   public LunarEventModifierType<?> type() {
      return LunarEventModifierTypes.ENTITY_DROP;
   }

   @Override
   public Component description() {
      if (this.drops.isEmpty()) {
         return Component.translatable(
            "enhancedcelestials2core.lunar_event_modifier.entity_drop", new Object[]{Component.translatable("enhancedcelestials2core.description.none")}
         );
      } else {
         MutableComponent joined = null;

         for (Pair<Condition, DropSettings> pair : this.drops) {
            Component entryComponent = Component.translatable(
               "enhancedcelestials2core.lunar_event_modifier.entity_drop.entry",
               new Object[]{((DropSettings)pair.getSecond()).description(), ((Condition)pair.getFirst()).toString()}
            );
            joined = joined == null ? entryComponent.copy() : joined.append(", ").append(entryComponent);
         }

         return Component.translatable("enhancedcelestials2core.lunar_event_modifier.entity_drop", new Object[]{joined});
      }
   }
}
