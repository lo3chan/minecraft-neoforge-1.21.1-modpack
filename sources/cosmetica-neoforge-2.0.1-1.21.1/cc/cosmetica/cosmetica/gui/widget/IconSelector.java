package cc.cosmetica.cosmetica.gui.widget;

import cc.cosmetica.core.api.Icon;
import cc.cosmetica.core.api.ImageCosmetic;
import cc.cosmetica.core.impl.Logging;
import cc.cosmetica.cosmetica.Cosmetica;
import cc.cosmetica.cosmetica.settings.CosmeticaSettings;
import cc.cosmetica.cosmetica.util.CosmeticaLogCategory;
import cc.cosmetica.kupe.api.Canvas;
import cc.cosmetica.kupe.api.ResourceKey;
import cc.cosmetica.kupe.api.State;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.gui.Align;
import cc.cosmetica.kupe.api.gui.Border;
import cc.cosmetica.kupe.api.gui.Component;
import cc.cosmetica.kupe.api.gui.Div;
import cc.cosmetica.kupe.api.gui.Element;
import cc.cosmetica.kupe.api.gui.Grid;
import cc.cosmetica.kupe.api.gui.Image;
import cc.cosmetica.kupe.api.gui.Label;
import cc.cosmetica.kupe.api.gui.Tooltip;
import cc.cosmetica.kupe.api.gui.style.CommonProperties;
import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.gui.style.Stylesheet;
import cc.cosmetica.kupe.api.maths.Margins;
import cc.cosmetica.kupe.api.maths.Region;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class IconSelector extends Div {
   private final AtomicBoolean iconDirty;
   private final State<List<IconSelector.IconOption>> availableIcons;

   public IconSelector(AtomicBoolean iconDirty, State<List<IconSelector.IconOption>> availableIcons) {
      super(new Component[0]);
      this.iconDirty = iconDirty;
      this.availableIcons = availableIcons;
   }

   public List<Component> build() {
      List<IconSelector.IconOption> iconOptions = new ArrayList<>((Collection<? extends IconSelector.IconOption>)this.availableIcons.acquire(this));
      Optional<String> management = (Optional<String>)Cosmetica.SELECTED_ICON
         .extract(this, ic -> ic instanceof Icon ? ((Icon)ic).getModpackId() : Optional.empty());
      boolean iconsDisabled = !CosmeticaSettings.SHOW_ICONS.acquire(this);
      if (management.isPresent()) {
         ImageCosmetic icon = (ImageCosmetic)Cosmetica.SELECTED_ICON.peek();
         if (iconOptions.stream().noneMatch(option -> icon.getId().equals(option.cosmetic.getId()))) {
            iconOptions.add(0, new IconSelector.IconOption(icon, false));
         }
      }

      IconSelector.SelectableIcon[] icons = iconOptions.stream()
         .map(icon -> new IconSelector.SelectableIcon(icon, management.isPresent(), iconsDisabled))
         .toArray(IconSelector.SelectableIcon[]::new);
      Function<Component, IconSelector.SelectableIcon> selectedState = t -> (IconSelector.SelectableIcon)Cosmetica.SELECTED_ICON.extract(t, cosmetic -> {
         IconSelector.SelectableIcon selected = null;

         for (IconSelector.SelectableIcon icon : icons) {
            if (icon.cosmetic.getId().equals(cosmetic.getId())) {
               selected = icon;
               break;
            }
         }

         return selected;
      });
      return ImmutableList.of(
         new IconSelector.IconHeader(selectedState).tag(new String[]{"horizontal", "header"}),
         new EntryList.Grid(icons, selectedState).tag(new String[]{"flex-1", "icon-selector"})
      );
   }

   public Stylesheet getStylesheet() {
      return new Stylesheet()
         .self(Style.create().set(CommonProperties.MARGINS, CommonProperties.fixed(new Margins(30, 10, 12, 10))).set(ALIGN_ITEMS, Align.STRETCH_START))
         .tag("header", Style.create().set(CommonProperties.MARGINS, CommonProperties.fixed(new Margins(0, 0, 2, 0))))
         .tag(
            "icon-image",
            Style.create().set(CommonProperties.WIDTH, CommonProperties.fixedSize(20)).set(CommonProperties.HEIGHT, CommonProperties.fixedSize(20))
         )
         .tag(
            "icon-replacement",
            Style.create().set(CommonProperties.BACKGROUND_COLOUR, OptionalInt.of(0)).set(CommonProperties.BORDER, Border.create(1, 16777215))
         )
         .tag("icon-selector", Style.create().set(Grid.ROW_GAP, 2).set(Grid.COLUMN_GAP, 2))
         .component(
            IconSelector.SelectableIcon.class,
            Style.create()
               .set(CommonProperties.PADDING, CommonProperties.fixed(new Margins(1)))
               .set(CommonProperties.WIDTH, CommonProperties.fixedSize(30))
               .set(CommonProperties.HEIGHT, CommonProperties.fixedSize(30))
         );
   }

   private static class IconHeader extends Div {
      private final Function<Component, IconSelector.SelectableIcon> icon;

      public IconHeader(Function<Component, IconSelector.SelectableIcon> icon) {
         super(new Component[0]);
         this.icon = icon;
      }

      public List<Component> build() {
         IconSelector.SelectableIcon icon = this.icon.apply(this);
         boolean noIcon = icon == null;
         ResourceLocation location = noIcon ? null : icon.cosmetic.getImage().location;
         Text displayIcon = noIcon
            ? Text.translatable("label.icons.noIcon", new String[0])
            : Text.translatable("label.icons.icon", new String[]{icon.cosmetic.getName()});
         return ImmutableList.of(
            new Label(displayIcon).tag(new String[]{"flex-1"}),
            noIcon
               ? new Div(new Component[0]).tag(new String[]{"icon-image", "icon-replacement"})
               : new Image(new ResourceKey(location)).tag(new String[]{"icon-image"})
         );
      }
   }

   public static class IconOption {
      private final ImageCosmetic cosmetic;
      private final boolean unlocked;

      public IconOption(ImageCosmetic cosmetic, boolean unlocked) {
         this.cosmetic = cosmetic;
         this.unlocked = unlocked;
      }
   }

   private class SelectableIcon extends Image {
      private final ImageCosmetic cosmetic;
      private final boolean disabled;
      @Nullable
      private final Text tooltip;

      public SelectableIcon(IconSelector.IconOption option, boolean managed, boolean disabled) {
         super(new ResourceKey(option.cosmetic.getImage().location));
         this.cosmetic = option.cosmetic;
         this.disabled = !option.unlocked || managed || disabled;
         this.tooltip = managed
            ? Text.translatable("tooltip.cosmetica.icon.managed", new String[0])
            : (
               disabled
                  ? Text.translatable("tooltip.cosmetica.icon.disabled", new String[0])
                  : (!option.unlocked ? Text.translatable("tooltip.cosmetica.icon.notUnlocked", new String[0]) : null)
            );
         this.setTransparent(!disabled && option.unlocked ? 1.0F : 0.6F);
      }

      public void mouseClicked(Element target, double x, double y, int button) {
         if (button == 0 && !this.disabled && this.cosmetic != Cosmetica.SELECTED_ICON.peek()) {
            Logging.getInstance().debug(CosmeticaLogCategory.GUI, "Setting icon " + this.cosmetic, new Object[0]);
            IconSelector.this.iconDirty.set(true);
            Cosmetica.SELECTED_ICON.set(this.cosmetic);
         }
      }

      public Stylesheet getStylesheet() {
         return this.tooltip != null ? new Stylesheet().self(Style.create().set(CommonProperties.TOOLTIP, Optional.of(new Tooltip(this.tooltip)))) : null;
      }

      public void render(Canvas canvas, Region region, Margins padding, int mouseX, int mouseY) {
         if (region.contains(mouseX, mouseY) && !((Optional)this.getStyle().get(CommonProperties.BORDER)).isPresent() && !this.disabled) {
            canvas.drawRect(region, 7368816);
         }

         super.render(canvas, region, padding, mouseX, mouseY);
      }
   }
}
