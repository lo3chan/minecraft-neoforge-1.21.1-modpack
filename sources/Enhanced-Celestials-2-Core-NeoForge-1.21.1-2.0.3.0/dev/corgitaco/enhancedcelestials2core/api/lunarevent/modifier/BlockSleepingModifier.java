package dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier;

import com.mojang.serialization.MapCodec;
import corgitaco.corgilib.entity.condition.Condition;
import net.minecraft.network.chat.Component;

public record BlockSleepingModifier(Condition condition) implements LunarEventModifier {
   public static final MapCodec<BlockSleepingModifier> CODEC = Condition.CODEC
      .fieldOf("condition")
      .xmap(BlockSleepingModifier::new, BlockSleepingModifier::condition);

   @Override
   public LunarEventModifierType<?> type() {
      return LunarEventModifierTypes.BLOCK_SLEEPING;
   }

   @Override
   public Component description() {
      return Component.translatable("enhancedcelestials2core.lunar_event_modifier.block_sleeping", new Object[]{this.condition.toString()});
   }
}
