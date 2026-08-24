package dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier;

import com.mojang.serialization.MapCodec;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.DropSettings;
import net.minecraft.network.chat.Component;

public record BlockItemDropModifier(DropSettings settings) implements LunarEventModifier {
   public static final MapCodec<BlockItemDropModifier> CODEC = DropSettings.CODEC
      .fieldOf("settings")
      .xmap(BlockItemDropModifier::new, BlockItemDropModifier::settings);

   @Override
   public LunarEventModifierType<?> type() {
      return LunarEventModifierTypes.BLOCK_ITEM_DROP;
   }

   @Override
   public Component description() {
      return Component.translatable("enhancedcelestials2core.lunar_event_modifier.block_item_drop", new Object[]{this.settings.description()});
   }
}
