package com.aetherteam.aether.client.renderer.entity;

import com.aetherteam.aether.client.renderer.AetherModelLayers;
import com.aetherteam.aether.client.renderer.entity.model.MimicModel;
import com.aetherteam.aether.entity.monster.dungeon.Mimic;
import java.util.Calendar;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;
import noobanidus.mods.lootr.neoforge.config.ConfigManager;

public class MimicRenderer extends MobRenderer<Mimic, MimicModel> {
   private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/mimic/normal.png");
   private static final ResourceLocation XMAS_TEXTURE = ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/mimic/christmas.png");
   private static final ResourceLocation LOOTR_TEXTURE = ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/mimic/lootr.png");
   private static final ResourceLocation OLD_LOOTR_TEXTURE = ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/mimic/old_lootr.png");
   private boolean isChristmas;

   public MimicRenderer(Context renderer) {
      super(renderer, new MimicModel(renderer.bakeLayer(AetherModelLayers.MIMIC)), 1.0F);
      Calendar calendar = Calendar.getInstance();
      if (calendar.get(2) == 11 && calendar.get(5) >= 24 && calendar.get(5) <= 26) {
         this.isChristmas = true;
      }
   }

   public ResourceLocation getTextureLocation(Mimic Mimic) {
      if (!ModList.get().isLoaded("lootr") || ConfigManager.isVanillaTextures()) {
         return this.isChristmas ? XMAS_TEXTURE : TEXTURE;
      } else {
         return ConfigManager.isNewTextures() ? LOOTR_TEXTURE : OLD_LOOTR_TEXTURE;
      }
   }
}
