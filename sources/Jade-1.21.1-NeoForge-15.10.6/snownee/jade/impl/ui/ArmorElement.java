package snownee.jade.impl.ui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.JadeIds;
import snownee.jade.api.theme.IThemeHelper;
import snownee.jade.api.ui.Element;
import snownee.jade.api.ui.IDisplayHelper;
import snownee.jade.impl.config.PluginConfig;
import snownee.jade.overlay.DisplayHelper;

public class ArmorElement extends Element {
   public static final ResourceLocation ARMOR = ResourceLocation.withDefaultNamespace("hud/armor_full");
   public static final ResourceLocation HALF_ARMOR = ResourceLocation.withDefaultNamespace("hud/armor_half");
   public static final ResourceLocation EMPTY_ARMOR = ResourceLocation.withDefaultNamespace("hud/armor_empty");
   private final float armor;
   private String text;
   private int iconsPerLine = 1;
   private int lineCount = 1;
   private int iconCount = 1;

   public ArmorElement(float armor) {
      this.armor = armor;
      if (armor > PluginConfig.INSTANCE.getInt(JadeIds.MC_ENTITY_ARMOR_MAX_FOR_RENDER)) {
         if (!PluginConfig.INSTANCE.get(JadeIds.MC_ENTITY_HEALTH_SHOW_FRACTIONS)) {
            armor = Mth.ceil(armor);
         }

         this.text = DisplayHelper.dfCommas.format(armor);
      } else {
         armor *= 0.5F;
         int maxHeartsPerLine = PluginConfig.INSTANCE.getInt(JadeIds.MC_ENTITY_HEALTH_ICONS_PER_LINE);
         this.iconCount = Mth.ceil(armor);
         this.iconsPerLine = Math.min(maxHeartsPerLine, this.iconCount);
         this.lineCount = Mth.ceil(armor / maxHeartsPerLine);
      }
   }

   @Override
   public Vec2 getSize() {
      return this.showText() ? new Vec2(DisplayHelper.font().width(this.text) + 10, 9.0F) : new Vec2(8 * this.iconsPerLine + 1, 5 + 4 * this.lineCount);
   }

   @Override
   public void render(GuiGraphics guiGraphics, float x, float y, float maxX, float maxY) {
      IDisplayHelper helper = IDisplayHelper.get();
      int xOffset = (this.iconCount - 1) % this.iconsPerLine * 8;
      int yOffset = this.lineCount * 4 - 4;

      for (int i = this.iconCount; i > 0; i--) {
         helper.blitSprite(guiGraphics, EMPTY_ARMOR, (int)(x + xOffset), (int)(y + yOffset), 9, 9);
         if (i <= Mth.floor(this.armor)) {
            helper.blitSprite(guiGraphics, ARMOR, (int)(x + xOffset), (int)(y + yOffset), 9, 9);
         }

         if (i > this.armor && i < this.armor + 1.0F) {
            helper.blitSprite(guiGraphics, HALF_ARMOR, (int)(x + xOffset), (int)(y + yOffset), 9, 9);
         }

         xOffset -= 8;
         if (xOffset < 0) {
            xOffset = this.iconsPerLine * 8 - 8;
            yOffset -= 4;
         }
      }

      if (this.showText()) {
         helper.drawText(guiGraphics, this.text, x + 10.0F, y + 1.0F, IThemeHelper.get().getNormalColor());
      }
   }

   @Nullable
   @Override
   public String getMessage() {
      return I18n.get("narration.jade.armor", new Object[]{Mth.ceil(this.armor)});
   }

   public boolean showText() {
      return this.text != null;
   }
}
