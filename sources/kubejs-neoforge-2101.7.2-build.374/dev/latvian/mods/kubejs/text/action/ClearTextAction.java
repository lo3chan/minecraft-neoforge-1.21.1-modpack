package dev.latvian.mods.kubejs.text.action;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;

public record ClearTextAction() implements TextAction {
   public static final ClearTextAction INSTANCE = new ClearTextAction();
   public static final TooltipActionType<ClearTextAction> TYPE = new TooltipActionType<>(6, StreamCodec.unit(INSTANCE));

   @Override
   public TooltipActionType<?> type() {
      return TYPE;
   }

   @Override
   public void apply(List<Component> lines) {
      lines.clear();
   }
}
