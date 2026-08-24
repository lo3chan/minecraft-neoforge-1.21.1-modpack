package snownee.jade.api.callback;

import snownee.jade.api.Accessor;
import snownee.jade.api.ui.IBoxElement;

@FunctionalInterface
public interface JadeTooltipCollectedCallback {
   void onTooltipCollected(IBoxElement var1, Accessor<?> var2);
}
