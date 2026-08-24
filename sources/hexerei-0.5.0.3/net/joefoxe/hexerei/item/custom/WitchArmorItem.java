package net.joefoxe.hexerei.item.custom;

import java.util.List;
import javax.annotation.Nullable;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.event.ClientEvents;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.ArmorMaterial.Layer;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.DyedItemColor;

public class WitchArmorItem extends ArmorItem {
   public WitchArmorItem(Holder<ArmorMaterial> materialIn, Type type, Properties builder) {
      super(materialIn, type, builder);
   }

   public int getColor(ItemStack stack) {
      String name = stack.getHoverName().getString();
      DyeColor col = HexereiUtil.getDyeColorNamed(name, 0);
      if (col != null) {
         float f3 = ClientEvents.getClientTicks() / 10.0F * 4.0F % 16.0F / 16.0F;
         DyeColor col2 = HexereiUtil.getDyeColorNamed(name, 1);
         float[] afloat1 = HexereiUtil.rgbIntToFloatArray(col.getTextureDiffuseColor());
         float[] afloat2 = HexereiUtil.rgbIntToFloatArray(col2.getTextureDiffuseColor());
         float f = afloat1[0] * (1.0F - f3) + afloat2[0] * f3;
         float f1 = afloat1[1] * (1.0F - f3) + afloat2[1] * f3;
         float f2 = afloat1[2] * (1.0F - f3) + afloat2[2] * f3;
         return HexereiUtil.getColorValueAlpha(f, f1, f2, 1.0F);
      } else {
         return ((DyedItemColor)stack.getOrDefault(DataComponents.DYED_COLOR, new DyedItemColor(-1, true))).rgb();
      }
   }

   @Nullable
   public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, Layer layer, boolean innerModel) {
      return HexereiUtil.getDyeColor(stack) != 4337438 && layer.dyeable()
         ? ResourceLocation.fromNamespaceAndPath("hexerei", "textures/models/armor/witch_armor_layer1_dyed.png")
         : ResourceLocation.fromNamespaceAndPath("hexerei", "textures/models/armor/witch_armor_layer1.png");
   }

   @org.jetbrains.annotations.Nullable
   public EquipmentSlot getEquipmentSlot(ItemStack stack) {
      return super.getEquipmentSlot(stack);
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      if (Screen.hasShiftDown()) {
         tooltipComponents.add(
            Component.translatable(
                  "<%s>", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)))}
               )
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
         );
         Player player = null;
         if (context.level() != null && context.level().isClientSide) {
            player = Hexerei.proxy.getPlayer();
         }

         int num = 0;
         boolean hat = player != null && player.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof WitchArmorItem;
         boolean robe = player != null && player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof WitchArmorItem;
         boolean boots = player != null && player.getItemBySlot(EquipmentSlot.FEET).getItem() instanceof WitchArmorItem;
         if (hat) {
            num++;
         }

         if (robe) {
            num++;
         }

         if (boots) {
            num++;
         }

         tooltipComponents.add(Component.translatable("tooltip.hexerei.witch_armor_shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
         tooltipComponents.add(
            Component.translatable("tooltip.hexerei.witch_armor_pieces").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(num < 2 ? 5184536 : 2396416)))
         );
         tooltipComponents.add(Component.translatable("").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
         tooltipComponents.add(
            Component.translatable(
                  " %s - %s", new Object[]{Component.translatable("item.hexerei.witch_helmet"), Component.translatable("item.hexerei.mushroom_witch_hat")}
               )
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(hat ? (num < 2 ? 1863748 : 3261557) : 3355443)))
         );
         tooltipComponents.add(
            Component.translatable(" %s", new Object[]{Component.translatable("item.hexerei.witch_chestplate")})
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(robe ? (num < 2 ? 1863748 : 3261557) : 3355443)))
         );
         tooltipComponents.add(
            Component.translatable(" %s", new Object[]{Component.translatable("item.hexerei.witch_boots")})
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(boots ? (num < 2 ? 1863748 : 3261557) : 3355443)))
         );
         tooltipComponents.add(
            Component.translatable("tooltip.hexerei.witch_armor_bonus", new Object[]{num, 2})
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(num < 2 ? 5184536 : 2396416)))
         );
         tooltipComponents.add(
            Component.translatable("tooltip.hexerei.witch_armor_bonus_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(num < 2 ? 3355443 : 3261557)))
         );
         tooltipComponents.add(
            Component.translatable("tooltip.hexerei.witch_armor_bonus_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(num < 2 ? 3355443 : 3261557)))
         );
         tooltipComponents.add(
            Component.translatable("tooltip.hexerei.witch_armor_bonus", new Object[]{num, 3})
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(num < 3 ? 5184536 : 2396416)))
         );
         tooltipComponents.add(
            Component.translatable("tooltip.hexerei.witch_armor_bonus_3").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(num < 3 ? 3355443 : 3261557)))
         );
         tooltipComponents.add(
            Component.translatable("tooltip.hexerei.witch_armor_bonus_4").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(num < 3 ? 3355443 : 3261557)))
         );
      } else {
         tooltipComponents.add(
            Component.translatable(
                  "[%s]", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11184640)))}
               )
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
         );
         Player playerx = null;
         if (context.level() != null && context.level().isClientSide) {
            playerx = Hexerei.proxy.getPlayer();
         }

         int numx = 0;
         boolean hatx = playerx != null && playerx.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof WitchArmorItem;
         boolean robex = playerx != null && playerx.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof WitchArmorItem;
         boolean bootsx = playerx != null && playerx.getItemBySlot(EquipmentSlot.FEET).getItem() instanceof WitchArmorItem;
         if (hatx) {
            numx++;
         }

         if (robex) {
            numx++;
         }

         if (bootsx) {
            numx++;
         }

         tooltipComponents.add(
            Component.translatable("tooltip.hexerei.witch_armor_bonus", new Object[]{numx, 2})
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(numx < 2 ? 5184536 : 2396416)))
         );
      }

      super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
   }
}
