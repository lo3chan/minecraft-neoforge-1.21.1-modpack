package net.mehvahdjukaar.moonlight.core.client.config;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import net.mehvahdjukaar.moonlight.api.client.gui.ConfigScreenExtensions;
import net.mehvahdjukaar.moonlight.api.client.util.RenderUtil;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

public final class ConfigScreenIcons {
   private static final Map<ResourceLocation, ItemStack> CACHE = new HashMap<>();
   private static final int PERIOD = 36;

   @Deprecated(
      forRemoval = true
   )
   public static void registerOverride(ResourceLocation id, Supplier<ItemStack> stack) {
      ConfigScreenExtensions.registerIcon(id, stack);
      CACHE.remove(id);
   }

   public static ItemStack resolve(@Nullable ResourceLocation id) {
      if (id == null) {
         return ItemStack.EMPTY;
      } else {
         ItemStack cached = CACHE.get(id);
         if (cached != null) {
            return cached;
         } else {
            ItemStack resolved = compute(id);
            CACHE.put(id, resolved);
            return resolved;
         }
      }
   }

   private static ItemStack compute(ResourceLocation id) {
      Supplier<ItemStack> override = ConfigScreenExtensions.iconOverride(id);
      if (override != null) {
         ItemStack s = override.get();
         if (s != null && !s.isEmpty()) {
            return s;
         }
      }

      Optional<Item> item = BuiltInRegistries.ITEM.getOptional(id);
      if (item.isPresent() && item.get() != Items.AIR) {
         return item.get().getDefaultInstance();
      } else {
         Optional<Block> block = BuiltInRegistries.BLOCK.getOptional(id);
         if (block.isPresent() && block.get() != Blocks.AIR) {
            ItemStack s = new ItemStack((ItemLike)block.get());
            if (!s.isEmpty()) {
               return s;
            }
         }

         return ItemStack.EMPTY;
      }
   }

   public static boolean has(@Nullable ResourceLocation id) {
      return id != null && !resolve(id).isEmpty();
   }

   public static boolean render(GuiGraphics graphics, @Nullable ResourceLocation id, int x, int y) {
      ItemStack stack = resolve(id);
      if (stack.isEmpty()) {
         return false;
      } else {
         graphics.renderItem(stack, x, y);
         return true;
      }
   }

   public static boolean renderAnimated(GuiGraphics graphics, @Nullable ResourceLocation id, int x, int y, float phase, boolean lit) {
      ItemStack stack = resolve(id);
      if (stack.isEmpty()) {
         return false;
      } else {
         if (!lit) {
            RenderSystem.setShaderColor(0.35F, 0.35F, 0.35F, 1.0F);
         }

         RenderUtil.renderGuiItemRelative(
            graphics.pose(),
            stack,
            x,
            y,
            Minecraft.getInstance().getItemRenderer(),
            (pose, model) -> animate(pose, model, phase),
            15728880,
            OverlayTexture.NO_OVERLAY
         );
         if (!lit) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         }

         return true;
      }
   }

   private static void animate(PoseStack pose, BakedModel model, float phase) {
      if (!(phase <= 0.0F)) {
         if (model.usesBlockLight()) {
            pose.mulPose(Axis.YP.rotationDegrees(phase * 10.0F));
         } else {
            float scale = 1.0F + 0.1F * Mth.sin(phase * 0.017453292F * 20.0F);
            pose.scale(scale, scale, scale);
         }
      }
   }

   public static final class Anim {
      private float phase;
      private long lastMs = -1L;

      public void update(boolean hovered) {
         long now = Util.getMillis();
         float dt = this.lastMs < 0L ? 0.0F : Math.min((float)(now - this.lastMs) / 1000.0F, 0.1F);
         this.lastMs = now;
         this.phase += (hovered ? 20.0F : -40.0F) * dt;
         if (this.phase < 0.0F) {
            this.phase = 0.0F;
         } else if (this.phase > 36.0F) {
            this.phase -= 36.0F;
         }
      }

      public float phase() {
         return this.phase;
      }
   }
}
