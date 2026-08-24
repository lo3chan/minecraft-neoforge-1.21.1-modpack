package net.mehvahdjukaar.moonlight.api.client.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public final class ConfigScreenExtensions {
   private static final Map<String, List<ConfigScreenExtensions.Overlay>> OVERLAYS = new HashMap<>();
   private static final Map<String, ConfigScreenExtensions.Showcase> SHOWCASES = new HashMap<>();
   private static final Map<ResourceLocation, Supplier<ItemStack>> ICON_OVERRIDES = new HashMap<>();

   public static void registerOverlay(String modId, ConfigScreenExtensions.Overlay overlay) {
      OVERLAYS.computeIfAbsent(modId, k -> new ArrayList<>()).add(overlay);
   }

   @Internal
   public static List<ConfigScreenExtensions.Overlay> overlaysFor(String modId) {
      return OVERLAYS.getOrDefault(modId, List.of());
   }

   public static void registerShowcase(String modId, ConfigScreenExtensions.Showcase showcase) {
      SHOWCASES.put(modId, showcase);
   }

   @Internal
   @Nullable
   public static ConfigScreenExtensions.Showcase showcaseFor(String modId) {
      return SHOWCASES.get(modId);
   }

   public static void registerIcon(ResourceLocation id, Supplier<ItemStack> stack) {
      ICON_OVERRIDES.put(id, stack);
   }

   @Internal
   @Nullable
   public static Supplier<ItemStack> iconOverride(ResourceLocation id) {
      return ICON_OVERRIDES.get(id);
   }

   public interface Overlay {
      void render(GuiGraphics var1, ConfigScreenExtensions.Panel var2, int var3, int var4, float var5);

      default boolean mouseClicked(ConfigScreenExtensions.Panel panel, double mouseX, double mouseY, int button) {
         return false;
      }
   }

   public record Panel(Screen screen, int left, int top, int right, int bottom) {
   }

   @FunctionalInterface
   public interface Showcase {
      AbstractWidget create(String var1, int var2, int var3, int var4, int var5);

      default boolean replacesCarousel() {
         return true;
      }
   }
}
