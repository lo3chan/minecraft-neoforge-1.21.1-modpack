package cc.cosmetica.cosmetica.settings;

import cc.cosmetica.cosmetica.gui.widget.SliderWidget;
import cc.cosmetica.kupe.api.State;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.gui.Component;
import java.util.function.Function;

public class IntSetting extends Setting<Integer> {
   private final Text description;

   public IntSetting(String key, int defaultValue) {
      super(key, defaultValue);
      this.description = Text.translatable(key + ".description", new String[0]);
   }

   @Override
   public Component createController() {
      final State<Float> temp = new State((float)this.get().intValue());
      return (new SliderWidget(temp, 1.0F, i -> Text.literal(String.format("%d", i.intValue()))) {
         @Override
         public void mouseReleased(double x, double y, int button) {
            if (((Float)temp.peek()).intValue() == IntSetting.this.get()) {
               IntSetting.this.set(((Float)temp.peek()).intValue());
            }
         }
      }).setDisabled(this.getManagement() != Setting.Management.USER);
   }

   public Text createDescription(Integer value) {
      return this.description;
   }
}
