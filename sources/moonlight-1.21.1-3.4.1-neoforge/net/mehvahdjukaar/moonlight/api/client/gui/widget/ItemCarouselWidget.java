package net.mehvahdjukaar.moonlight.api.client.gui.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.mehvahdjukaar.moonlight.api.client.gui.GuiHelper;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ItemCarouselWidget extends AbstractWidget {
   private static final int ICON = 16;
   private static final int GAP = 6;
   private static final int CELL = 22;
   private static final float SPEED = 14.0F;
   private static final float SCROLL_IMPULSE = 220.0F;
   private static final float MAX_FLING = 900.0F;
   private static final float FLING_DECAY = 4.0F;
   private static final int FADE = 24;
   private static final int MAX_ITEMS = 256;
   private static final Map<String, List<ItemStack>> MOD_ITEMS = new HashMap<>();
   private final List<ItemStack> items;
   private final double span;
   private int background = -15000800;
   @Nullable
   private Integer outline = null;
   private double offset;
   private float speed = 14.0F;
   private float fling;
   private long lastMs = -1L;
   private int hoveredIndex = -1;

   public ItemCarouselWidget(int x, int y, int width, int height, List<ItemStack> items) {
      super(x, y, width, height, Component.empty());
      this.items = items;
      this.span = items.size() * 22.0;
      this.active = false;
   }

   @Nullable
   public static ItemCarouselWidget forMod(String modId, int x, int y, int width, int height) {
      List<ItemStack> items = itemsOf(modId);
      return items.isEmpty() ? null : new ItemCarouselWidget(x, y, width, height, items);
   }

   public static List<ItemStack> itemsOf(String modId) {
      return MOD_ITEMS.computeIfAbsent(modId, id -> {
         Level level = Minecraft.getInstance().level;
         FeatureFlagSet features = level == null ? FeatureFlags.DEFAULT_FLAGS : level.enabledFeatures();
         List<ItemStack> found = new ArrayList<>();

         for (Entry<ResourceKey<Item>, Item> e : BuiltInRegistries.ITEM.entrySet()) {
            if (e.getKey().location().getNamespace().equals(id)) {
               Item item = e.getValue();
               if (item.isEnabled(features) && I18n.exists(item.getDescriptionId())) {
                  ItemStack stack = item.getDefaultInstance();
                  if (!stack.isEmpty() && !hasNoModel(stack)) {
                     found.add(stack);
                     if (found.size() >= 256) {
                        break;
                     }
                  }
               }
            }
         }

         return List.copyOf(found);
      });
   }

   private static boolean hasNoModel(ItemStack stack) {
      Minecraft mc = Minecraft.getInstance();
      return mc.getItemRenderer().getModel(stack, null, null, 0) == mc.getModelManager().getMissingModel();
   }

   public ItemCarouselWidget background(int argb) {
      this.background = argb;
      return this;
   }

   public ItemCarouselWidget withOutline(int argb) {
      this.outline = argb;
      return this;
   }

   protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      int right = this.getX() + this.width;
      int bottom = this.getY() + this.height;
      graphics.fill(this.getX(), this.getY(), right, bottom, this.background);
      if (this.outline != null) {
         graphics.renderOutline(this.getX(), this.getY(), this.width, this.height, this.outline);
      }

      if (!this.items.isEmpty()) {
         this.advance(this.isHovered);
         int fade = Math.min(24, this.width / 3);
         int firstCell = Mth.floor(this.offset / 22.0);
         double shift = this.offset - firstCell * 22.0;
         int wholeShift = (int)shift;
         float subShift = (float)(shift - wholeShift);
         int iconY = this.getY() + (this.height - 16) / 2;
         int hovered = -1;
         graphics.enableScissor(this.getX(), this.getY(), right, bottom);
         graphics.pose().pushPose();
         graphics.pose().translate(-subShift, 0.0F, 0.0F);
         int i = 0;

         for (int cells = this.width / 22 + 2; i <= cells; i++) {
            int x = this.getX() + i * 22 + 3 - wholeShift;
            int index = Math.floorMod(firstCell + i, this.items.size());
            graphics.renderFakeItem(this.items.get(index), x, iconY);
            float drawnX = x - subShift;
            if (this.isHovered && mouseX >= drawnX && mouseX < drawnX + 16.0F && mouseX >= this.getX() + fade && mouseX < right - fade) {
               hovered = index;
            }
         }

         graphics.pose().popPose();
         graphics.disableScissor();
         i = ARGB32.color(0, this.background);
         RenderType overItems = RenderType.guiOverlay();
         GuiHelper.fillGradientHorizontal(graphics, overItems, this.getX(), this.getY(), this.getX() + fade, bottom, this.background, i);
         GuiHelper.fillGradientHorizontal(graphics, overItems, right - fade, this.getY(), right, bottom, i, this.background);
         if (hovered != this.hoveredIndex) {
            this.hoveredIndex = hovered;
            this.setTooltip(hovered < 0 ? null : Tooltip.create(this.items.get(hovered).getHoverName()));
         }
      }
   }

   private void advance(boolean hovered) {
      long now = Util.getMillis();
      float dt = this.lastMs < 0L ? 0.0F : Math.min((float)(now - this.lastMs) / 1000.0F, 0.1F);
      this.lastMs = now;
      this.speed = Mth.lerp(Math.min(1.0F, dt * 6.0F), this.speed, hovered ? 0.0F : 14.0F);
      this.fling = Mth.lerp(Math.min(1.0F, dt * 4.0F), this.fling, 0.0F);
      double moved = (this.offset + (this.speed + this.fling) * dt) % this.span;
      this.offset = moved < 0.0 ? moved + this.span : moved;
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
      if (this.items.isEmpty()) {
         return false;
      } else {
         this.fling = Mth.clamp(this.fling - (float)scrollY * 220.0F, -900.0F, 900.0F);
         return true;
      }
   }

   public boolean isMouseOver(double mouseX, double mouseY) {
      return this.visible && mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
   }

   protected boolean isValidClickButton(int button) {
      return false;
   }

   protected void updateWidgetNarration(NarrationElementOutput output) {
   }
}
