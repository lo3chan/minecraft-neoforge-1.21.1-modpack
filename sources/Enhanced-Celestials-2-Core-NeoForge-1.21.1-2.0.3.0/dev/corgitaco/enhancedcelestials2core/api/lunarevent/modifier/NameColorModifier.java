package dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier;

import com.mojang.serialization.MapCodec;
import dev.corgitaco.enhancedcelestials2core.util.ColorUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;

public record NameColorModifier(TextColor color) implements LunarEventModifier {
   public static final MapCodec<NameColorModifier> CODEC = ColorUtil.COLOR_CODEC
      .xmap(color -> new NameColorModifier(TextColor.fromRgb(color)), modifier -> modifier.color().getValue());

   public NameColorModifier(ChatFormatting chatFormatting) {
      this(TextColor.fromLegacyFormat(chatFormatting));
   }

   @Override
   public LunarEventModifierType<?> type() {
      return LunarEventModifierTypes.NAME_COLOR;
   }

   @Override
   public Component description() {
      return Component.translatable("enhancedcelestials2core.lunar_event_modifier.name_color", new Object[]{this.color.serialize()});
   }
}
