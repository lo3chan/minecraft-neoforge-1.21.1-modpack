package dev.latvian.mods.kubejs.text.action;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;

public record RemoveLineTextAction(int line) implements TextAction {
   public static final TooltipActionType<RemoveLineTextAction> TYPE = new TooltipActionType<>(
      3, ByteBufCodecs.VAR_INT.map(RemoveLineTextAction::new, RemoveLineTextAction::line)
   );

   @Override
   public TooltipActionType<?> type() {
      return TYPE;
   }

   @Override
   public void apply(List<Component> lines) {
      if (this.line >= 0 && this.line < lines.size()) {
         lines.remove(this.line);
      }
   }
}
