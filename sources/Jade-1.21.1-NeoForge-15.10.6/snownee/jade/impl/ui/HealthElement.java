package snownee.jade.impl.ui;

import net.minecraft.client.Minecraft;
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
import snownee.jade.overlay.WailaTickHandler;
import snownee.jade.track.HealthTrackInfo;

public class HealthElement extends Element {
   public static final ResourceLocation HEART = ResourceLocation.withDefaultNamespace("hud/heart/full");
   public static final ResourceLocation HEART_BLINKING = ResourceLocation.withDefaultNamespace("hud/heart/full_blinking");
   public static final ResourceLocation HALF_HEART = ResourceLocation.withDefaultNamespace("hud/heart/half");
   public static final ResourceLocation HALF_HEART_BLINKING = ResourceLocation.withDefaultNamespace("hud/heart/half_blinking");
   public static final ResourceLocation EMPTY_HEART = ResourceLocation.withDefaultNamespace("hud/heart/container");
   public static final ResourceLocation EMPTY_HEART_BLINKING = ResourceLocation.withDefaultNamespace("hud/heart/container_blinking");
   private final float health;
   private String text;
   private int iconsPerLine = 1;
   private int lineCount = 1;
   private int iconCount = 1;
   private HealthTrackInfo track;

   public HealthElement(float maxHealth, float health) {
      this.health = health;
      if (maxHealth > PluginConfig.INSTANCE.getInt(JadeIds.MC_ENTITY_HEALTH_MAX_FOR_RENDER)) {
         if (!PluginConfig.INSTANCE.get(JadeIds.MC_ENTITY_HEALTH_SHOW_FRACTIONS)) {
            maxHealth = Mth.ceil(maxHealth);
            health = Mth.ceil(health);
         }

         this.text = String.format("%s/%s", DisplayHelper.dfCommas.format(health), DisplayHelper.dfCommas.format(maxHealth));
      } else {
         maxHealth *= 0.5F;
         int maxHeartsPerLine = PluginConfig.INSTANCE.getInt(JadeIds.MC_ENTITY_HEALTH_ICONS_PER_LINE);
         this.iconCount = Mth.ceil(maxHealth);
         this.iconsPerLine = Math.min(maxHeartsPerLine, this.iconCount);
         this.lineCount = Mth.ceil(maxHealth / maxHeartsPerLine);
      }
   }

   @Override
   public Vec2 getSize() {
      return this.showText() ? new Vec2(DisplayHelper.font().width(this.text) + 10, 9.0F) : new Vec2(8 * this.iconsPerLine + 1, 5 + 4 * this.lineCount);
   }

   @Override
   public void render(GuiGraphics guiGraphics, float x, float y, float maxX, float maxY) {
      float health = this.health * 0.5F;
      float lastHealth = health;
      boolean blink = false;
      if (this.track == null && this.getTag() != null) {
         this.track = WailaTickHandler.instance().progressTracker.getOrCreate(this.getTag(), HealthTrackInfo.class, () -> new HealthTrackInfo(this.health));
      }

      if (this.track != null) {
         this.track.setHealth(this.health);
         this.track.update(Minecraft.getInstance().getTimer().getRealtimeDeltaTicks());
         lastHealth = this.track.getLastHealth() * 0.5F;
         blink = this.track.isBlinking();
      }

      IDisplayHelper helper = IDisplayHelper.get();
      int xOffset = (this.iconCount - 1) % this.iconsPerLine * 8;
      int yOffset = this.lineCount * 4 - 4;

      for (int i = this.iconCount; i > 0; i--) {
         int xPos = (int)(x + xOffset);
         int yPos = (int)(y + yOffset);
         helper.blitSprite(guiGraphics, blink ? EMPTY_HEART_BLINKING : EMPTY_HEART, xPos, yPos, 9, 9);
         if (i <= Mth.floor(health)) {
            helper.blitSprite(guiGraphics, HEART, xPos, yPos, 9, 9);
         }

         if (i > health) {
            if (i <= Mth.floor(lastHealth)) {
               helper.blitSprite(guiGraphics, HEART_BLINKING, xPos, yPos, 9, 9);
            } else if (i > lastHealth && i < lastHealth + 1.0F) {
               helper.blitSprite(guiGraphics, HALF_HEART_BLINKING, xPos, yPos, 9, 9);
            }

            if (i < health + 1.0F) {
               helper.blitSprite(guiGraphics, HALF_HEART, xPos, yPos, 9, 9);
            }
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
      return I18n.get("narration.jade.health", new Object[]{Mth.ceil(this.health)});
   }

   public boolean showText() {
      return this.text != null;
   }
}
