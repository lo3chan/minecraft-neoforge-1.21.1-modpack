package fuzs.puzzleslib.api.client.gui.v2.components.tooltip;

import fuzs.puzzleslib.impl.client.gui.TooltipBuilderImpl;
import java.time.Duration;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;

public interface TooltipBuilder {
   static TooltipBuilder create() {
      return new TooltipBuilderImpl();
   }

   static TooltipBuilder create(FormattedText... lines) {
      return new TooltipBuilderImpl(lines);
   }

   static TooltipBuilder create(List<? extends FormattedText> lines) {
      return new TooltipBuilderImpl(lines);
   }

   TooltipBuilder addLines(FormattedText... var1);

   TooltipBuilder addLines(List<? extends FormattedText> var1);

   TooltipBuilder setLines(Supplier<List<? extends FormattedText>> var1);

   TooltipBuilder setDelay(Duration var1);

   TooltipBuilder setTooltipPositionerFactory(BiFunction<ClientTooltipPositioner, AbstractWidget, ClientTooltipPositioner> var1);

   TooltipBuilder splitLines();

   TooltipBuilder splitLines(int var1);

   TooltipBuilder setTooltipLineProcessor(Function<List<? extends FormattedText>, List<FormattedCharSequence>> var1);

   void build(AbstractWidget var1);
}
