package cc.cosmetica.cosmetica.gui.widget;

import cc.cosmetica.cosmetica.gui.BrowseScreen;
import cc.cosmetica.kupe.api.Screens;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.gui.Align;
import cc.cosmetica.kupe.api.gui.Button;
import cc.cosmetica.kupe.api.gui.Component;
import cc.cosmetica.kupe.api.gui.Div;
import cc.cosmetica.kupe.api.gui.Tooltip;
import cc.cosmetica.kupe.api.gui.AbstractScrollContainer.ScrollbarPosition;
import cc.cosmetica.kupe.api.gui.style.CommonProperties;
import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.gui.style.Stylesheet;
import cc.cosmetica.kupe.api.maths.Axis2D;
import cc.cosmetica.kupe.api.maths.Dimensions;
import cc.cosmetica.kupe.api.maths.Margins;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;

public class CosmeticsList extends Div {
   protected final CosmeticEntry[] entries;
   private final CosmeticsList.ListType listType;

   public CosmeticsList(Collection<CosmeticEntry> entries, CosmeticsList.ListType listType) {
      super(new Component[0]);
      this.entries = entries.toArray(new CosmeticEntry[0]);
      this.listType = listType;
   }

   public List<Component> build() {
      return this.listType != CosmeticsList.ListType.LIST_ONLY
         ? ImmutableList.of(
            new EntryList.Div(this.entries).tag(new String[]{"width-45vw", "contents-wrapper"}),
            new Div(
                  new Component[]{
                     new Button(Text.literal("+"), () -> Screens.setScreen(BrowseScreen.ID))
                        .setDisabled(this.listType == CosmeticsList.ListType.DISABLED || this.listType == CosmeticsList.ListType.OFFLINE)
                        .tag(this.listType.buttonTags)
                  }
               )
               .tag(new String[]{"width-45vw"})
               .withStyle(Style.create().set(FLOW_DIRECTION, Axis2D.POSITIVE_Y))
         )
         : ImmutableList.of(new EntryList.Div(this.entries).tag(new String[]{"width-45vw", "contents-wrapper"}));
   }

   @Nullable
   public Stylesheet getStylesheet() {
      return new Stylesheet()
         .self(Style.create().set(CommonProperties.PADDING, CommonProperties.fixed(new Margins(30, 10, 12, 10))).set(Div.ALIGN_ITEMS, Align.START))
         .tag("contents-wrapper", Style.create().set(CommonProperties.FLEX, 1).set(SCROLLBAR_POSITION, ScrollbarPosition.OUTSIDE))
         .tag(
            "width-45vw",
            Style.create()
               .set(CommonProperties.WIDTH, CommonProperties.screen(45.0F, 0.0F))
               .set(CommonProperties.MAXIMUM_SIZE, CommonProperties.fixed(new Dimensions(396, 2147483647)))
         )
         .tag(
            "no-outfit-disabled",
            Style.create().set(CommonProperties.TOOLTIP, Optional.of(new Tooltip(Text.translatable("tooltip.cosmetica.noOutfitDisabled", new String[0]))))
         )
         .tag(
            "no-outfit-offline",
            Style.create().set(CommonProperties.TOOLTIP, Optional.of(new Tooltip(Text.translatable("tooltip.cosmetica.offline", new String[0]))))
         );
   }

   public static enum ListType {
      EDITABLE("width-45vw"),
      LIST_ONLY("width-45vw"),
      DISABLED("width-45vw", "no-outfit-disabled"),
      OFFLINE("width-45vw", "no-outfit-offline");

      private final String[] buttonTags;

      private ListType(String... buttonTags) {
         this.buttonTags = buttonTags;
      }
   }
}
