package cc.cosmetica.cosmetica.gui;

import cc.cosmetica.core.api.CosmeticaAPI;
import cc.cosmetica.core.impl.Logging;
import cc.cosmetica.cosmetica.Cosmetica;
import cc.cosmetica.kupe.api.ResourceKey;
import cc.cosmetica.kupe.api.Screen;
import cc.cosmetica.kupe.api.Screens;
import cc.cosmetica.kupe.api.State;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.gui.Button;
import cc.cosmetica.kupe.api.gui.Component;
import cc.cosmetica.kupe.api.gui.Div;
import cc.cosmetica.kupe.api.gui.TextBox;
import cc.cosmetica.kupe.api.gui.Tooltip;
import cc.cosmetica.kupe.api.gui.style.CommonProperties;
import cc.cosmetica.kupe.api.gui.style.Style;
import gg.cloaks.javaclient.model.CreateOutfitDto;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.Minecraft;

public class CreateNewOutfitScreen extends Screen {
   private final State<String> outfitName = new State("");
   private final State<Boolean> outfitPublic = new State(true);
   private final State<Boolean> disabled = new State(false);
   public static final ResourceKey ID = new ResourceKey("cosmetica", "create_new_outfit");

   public CreateNewOutfitScreen() {
      super(ID);
   }

   protected Component[] buildScreen() {
      boolean outfitPublic = (Boolean)this.outfitPublic.acquire(this);
      boolean disabled = (Boolean)this.disabled.acquire(this);
      return new Component[]{
         new TextBox(Text.translatable("label.create_new_outfit.outfitName", new String[0]), this.outfitName, true, 64).setDisabled(disabled),
         new Button(
               Text.translatable(
                  "label.create_new_outfit.searchable", new String[]{outfitPublic ? Text.GUI_YES.getDisplayString() : Text.GUI_NO.getDisplayString()}
               ),
               () -> this.outfitPublic.set(!outfitPublic)
            )
            .setDisabled(disabled),
         this.createSubmissionGroup(outfitPublic, disabled)
      };
   }

   public static String strip(String input) {
      return input == null ? null : input.replaceAll("^[\\p{Space}]+|[\\p{Space}]+$", "");
   }

   private Component createSubmissionGroup(boolean outfitPublic, boolean disabled) {
      int nameMinChars = 3;
      return new Div(new Component[0]) {
         public List<Component> build() {
            String outfitName = CreateNewOutfitScreen.strip(((String)CreateNewOutfitScreen.this.outfitName.acquire(this)).trim());
            boolean legalName = outfitName.length() >= 3;
            return Arrays.asList(
               new Button(Text.translatable("label.cosmetica.create", new String[0]), () -> {
                  CreateNewOutfitScreen.this.disabled.set(true);
                  CreateOutfitDto dto = new CreateOutfitDto().name(outfitName)._public(outfitPublic).accessories(Collections.emptyList());
                  CosmeticaAPI.outfits().requestAsync(dapi -> dapi.create(dto)).thenAcceptAsync(outfit -> {
                     if (!CosmeticaAPI.isWebsocketConnected()) {
                        ((List)Cosmetica.OWN_OUTFITS.peek()).add(new OutfitWheelScreen.OutfitOption(outfit));
                        Cosmetica.OWN_OUTFITS.set((List)Cosmetica.OWN_OUTFITS.peek());
                     }

                     Screens.closeCurrentScreen();
                  }, Minecraft.getInstance()).exceptionally(Cosmetica.mainThreadExcept(err -> {
                     Logging.getInstance().error("Error creating new outfit", err);
                     CreateNewOutfitScreen.this.disabled.set(false);
                  }));
               }).setDisabled(disabled || !legalName),
               new Button(Text.GUI_CANCEL, Screens::closeCurrentScreen)
                  .setDisabled(disabled)
                  .withStyle(
                     Style.create()
                        .set(
                           CommonProperties.TOOLTIP,
                           !legalName && !outfitName.isEmpty()
                              ? Optional.of(new Tooltip(Text.translatable("cosmetica.tooltip.notLongEnough", new String[0])))
                              : Optional.empty()
                        )
                  )
            );
         }
      };
   }
}
