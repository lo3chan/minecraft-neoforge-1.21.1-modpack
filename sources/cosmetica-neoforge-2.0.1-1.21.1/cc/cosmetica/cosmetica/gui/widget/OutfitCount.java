package cc.cosmetica.cosmetica.gui.widget;

import cc.cosmetica.cosmetica.Cosmetica;
import cc.cosmetica.kupe.api.State;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.gui.Component;
import cc.cosmetica.kupe.api.gui.Div;
import cc.cosmetica.kupe.api.gui.Label;
import java.util.Collections;
import java.util.List;

public class OutfitCount extends Div {
   private final State<Integer> outfitLimit;

   public OutfitCount(State<Integer> outfitLimit) {
      super(new Component[0]);
      this.outfitLimit = outfitLimit;
   }

   public List<Component> build() {
      int limit = (Integer)this.outfitLimit.acquire(this);
      int count = (Integer)Cosmetica.OWN_OUTFITS.extract(this, List::size);
      return Collections.singletonList(
         new Label(
            count < 0
               ? Text.translatable("label.cosmetica.loading", new String[0])
               : Text.translatable("label.cosmetica.outfitCount", new String[]{String.valueOf(count), String.valueOf(limit)})
         )
      );
   }
}
