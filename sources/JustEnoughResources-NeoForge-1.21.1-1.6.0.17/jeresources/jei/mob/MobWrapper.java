package jeresources.jei.mob;

import java.util.Collections;
import java.util.List;
import jeresources.config.Settings;
import jeresources.entry.MobEntry;
import jeresources.util.CollectionHelper;
import jeresources.util.Font;
import jeresources.util.RenderHelper;
import jeresources.util.TranslationHelper;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryExtension;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.WanderingTrader;
import org.jetbrains.annotations.NotNull;

public class MobWrapper implements IRecipeCategoryExtension<MobEntry> {
   public void drawInfo(MobEntry recipe, int recipeWidth, int recipeHeight, @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
      LivingEntity livingEntity = recipe.getEntity();
      float scale = this.getScale(livingEntity);
      int offsetY = this.getOffsetY(livingEntity);
      RenderHelper.renderEntity(guiGraphics, 37, 105 - offsetY, scale, 38.0 - mouseX, 70 - offsetY - mouseY, livingEntity);
      String mobName = recipe.getMobName();
      if (Settings.showDevData) {
         String entityString = livingEntity.getStringUUID();
         if (entityString != null) {
            mobName = mobName + " (" + entityString + ")";
         }
      }

      Font.normal.print(guiGraphics, mobName, 7, 2);
      String biomesLine;
      if (recipe.hasMultipleBiomes()) {
         biomesLine = TranslationHelper.translateAndFormat("jer.mob.biome");
      } else {
         biomesLine = recipe.getTranslatedBiomes()
            .findFirst()
            .map(firstBiome -> TranslationHelper.translateAndFormat("jer.mob.spawn") + " " + firstBiome)
            .orElse("");
      }

      Font.normal.print(guiGraphics, biomesLine, 7, 12);
      Font.normal.print(guiGraphics, recipe.getLightLevel().toString(), 7, 22);
      Font.normal.print(guiGraphics, TranslationHelper.translateAndFormat("jer.mob.exp") + ": " + recipe.getExp(), 7, 32);
   }

   @NotNull
   public List<Component> getTooltipStrings(MobEntry recipe, double mouseX, double mouseY) {
      return recipe.hasMultipleBiomes() && this.isOnBiome(mouseX, mouseY)
         ? CollectionHelper.create(Component::literal, recipe.getTranslatedBiomes())
         : Collections.emptyList();
   }

   private boolean isOnBiome(double mouseX, double mouseY) {
      return 2.0 <= mouseX && mouseX < 165.0 && 12.0 <= mouseY && mouseY < 22.0;
   }

   private float getScale(LivingEntity livingEntity) {
      float width = livingEntity.getBbWidth();
      float height = livingEntity.getBbHeight();
      if (width <= height) {
         if (height < 0.9) {
            return 50.0F;
         } else if (height < 1.0F) {
            return 35.0F;
         } else if (height < 1.8) {
            return 33.0F;
         } else if (height < 2.0F) {
            return 32.0F;
         } else if (height < 3.0F) {
            return 24.0F;
         } else {
            return height < 4.0F ? 20.0F : 10.0F;
         }
      } else if (width < 1.0F) {
         return 38.0F;
      } else if (width < 2.0F) {
         return 27.0F;
      } else {
         return width < 3.0F ? 13.0F : 9.0F;
      }
   }

   private int getOffsetY(LivingEntity livingEntity) {
      int offsetY = 0;
      if (livingEntity instanceof Squid) {
         offsetY = 20;
      } else if (livingEntity instanceof Turtle) {
         offsetY = 10;
      } else if (livingEntity instanceof Witch) {
         offsetY = -5;
      } else if (livingEntity instanceof Ghast) {
         offsetY = 15;
      } else if (livingEntity instanceof WitherBoss) {
         offsetY = -15;
      } else if (livingEntity instanceof EnderDragon) {
         offsetY = 15;
      } else if (livingEntity instanceof EnderMan) {
         offsetY = -10;
      } else if (livingEntity instanceof AbstractGolem) {
         offsetY = -10;
      } else if (livingEntity instanceof Animal) {
         offsetY = -20;
      } else if (livingEntity instanceof Villager) {
         offsetY = -15;
      } else if (livingEntity instanceof Husk) {
         offsetY = -15;
      } else if (livingEntity instanceof AbstractIllager) {
         offsetY = -15;
      } else if (livingEntity instanceof WanderingTrader) {
         offsetY = -15;
      } else if (livingEntity instanceof Blaze) {
         offsetY = -10;
      } else if (livingEntity instanceof Creeper) {
         offsetY = -15;
      } else if (livingEntity instanceof AbstractPiglin) {
         offsetY = -15;
      }

      return offsetY;
   }
}
