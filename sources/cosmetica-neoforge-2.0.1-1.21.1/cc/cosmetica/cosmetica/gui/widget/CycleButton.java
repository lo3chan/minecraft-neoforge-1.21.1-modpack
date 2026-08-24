package cc.cosmetica.cosmetica.gui.widget;

import cc.cosmetica.cosmetica.settings.Setting;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.gui.Button;
import cc.cosmetica.kupe.api.gui.Component;
import cc.cosmetica.kupe.api.gui.Div;
import cc.cosmetica.kupe.api.gui.Tooltip;
import cc.cosmetica.kupe.api.gui.style.CommonProperties;
import cc.cosmetica.kupe.api.gui.style.Style;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nullable;

public class CycleButton<T> extends Div {
   private final Setting<T> setting;
   private final Supplier<T> cycle;
   @Nullable
   private final String translationKeyBase;

   public CycleButton(Setting<T> setting, Supplier<T> cycle, @Nullable String translationKeyBase) {
      super(new Component[0]);
      this.setting = setting;
      this.cycle = cycle;
      this.translationKeyBase = translationKeyBase;
   }

   public List<Component> build() {
      T value = this.setting.acquire(this);
      Text text;
      if (this.translationKeyBase == null) {
         if (value instanceof Boolean) {
            text = (Boolean)value ? Text.GUI_YES : Text.GUI_NO;
         } else {
            text = Text.literal(value.toString());
         }
      } else {
         text = Text.translatable(this.translationKeyBase + "." + value, new String[0]);
      }

      return Collections.singletonList(
         new Button(text, () -> this.setting.set(this.cycle.get()))
            .setDisabled(this.setting.getManagement() != Setting.Management.USER)
            .withStyle(
               Style.create()
                  .set(
                     CommonProperties.TOOLTIP,
                     this.setting.getManagement() == Setting.Management.MODPACK
                        ? Optional.of(new Tooltip(Text.translatable("tooltip.cosmetica.modpack_managed", new String[0])))
                        : Optional.empty()
                  )
            )
      );
   }
}
