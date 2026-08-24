package net.mehvahdjukaar.moonlight.api.util;

import com.mojang.serialization.Codec;
import java.util.Locale;
import net.mehvahdjukaar.moonlight.api.MoonlightRegistry;
import net.mehvahdjukaar.moonlight.api.fluids.SoftFluidStack;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.LingeringPotionItem;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.SplashPotionItem;
import org.jetbrains.annotations.Nullable;

public enum PotionBottleType implements StringRepresentable {
   REGULAR("item.minecraft.potion"),
   SPLASH("item.minecraft.splash_potion"),
   LINGERING("item.minecraft.lingering_potion");

   private final String name = this.name().toLowerCase(Locale.ROOT);
   private final Component translatedName;
   public static final Codec<PotionBottleType> CODEC = StringRepresentable.fromValues(PotionBottleType::values);

   private PotionBottleType(String translatedKey) {
      this.translatedName = Component.translatable(translatedKey);
   }

   public ItemStack getDefaultItem() {
      return (switch (this) {
         case REGULAR -> Items.POTION;
         case SPLASH -> Items.SPLASH_POTION;
         case LINGERING -> Items.LINGERING_POTION;
      }).getDefaultInstance();
   }

   public Component getTranslatedName() {
      return this.translatedName;
   }

   public String getSerializedName() {
      return this.name;
   }

   @Nullable
   public static PotionBottleType get(Item potionItem) {
      if (potionItem instanceof SplashPotionItem) {
         return SPLASH;
      } else if (potionItem instanceof LingeringPotionItem) {
         return LINGERING;
      } else {
         return potionItem instanceof PotionItem ? REGULAR : null;
      }
   }

   public static PotionBottleType getOrDefault(Item filledContainer) {
      PotionBottleType type = get(filledContainer);
      return type != null ? type : REGULAR;
   }

   public static PotionBottleType getOrDefault(SoftFluidStack stack) {
      return (PotionBottleType)stack.getOrDefault(MoonlightRegistry.BOTTLE_TYPE.get(), REGULAR);
   }

   public static String truncateString(String str, int maxLength) {
      return str.length() <= maxLength ? str : str.substring(0, maxLength);
   }

   public static Component truncateComponent(Component component, int maxLength) {
      String str = component.getString();
      return (Component)(str.length() <= maxLength ? component : Component.literal(truncateString(str, maxLength)).withStyle(component.getStyle()));
   }
}
