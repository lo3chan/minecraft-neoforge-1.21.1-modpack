package net.joefoxe.hexerei.item.custom;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.client.renderer.entity.model.BroomKeychainChainModel;
import net.joefoxe.hexerei.client.renderer.entity.model.BroomKeychainModel;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class KeychainItem extends BroomAttachmentItem {
   public Pair<ResourceLocation, Model> chain_resources = null;

   public KeychainItem(Properties properties) {
      super(properties);
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void bakeModels() {
      EntityModelSet context = Minecraft.getInstance().getEntityModels();
      this.model = new BroomKeychainModel(context.bakeLayer(BroomKeychainModel.LAYER_LOCATION));
      this.texture = HexereiUtil.getResource("textures/entity/broom_keychain.png");
      this.dye_texture = null;
      this.chain_resources = Pair.of(
         HexereiUtil.getResource("textures/entity/broom_keychain.png"), new BroomKeychainChainModel(context.bakeLayer(BroomKeychainChainModel.LAYER_LOCATION))
      );
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      RegistryAccess access = Hexerei.DynamicRegistries.get();
      CompoundTag inv = ((CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
      ListTag tagList = inv.getList("Items", 10);
      CompoundTag compoundtag = tagList.getCompound(0);
      CompoundTag itemTags = tagList.getCompound(0);
      MutableComponent itemText = Component.translatable(ItemStack.parseOptional(access, compoundtag).getDescriptionId())
         .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10061824)));
      if (Screen.hasShiftDown()) {
         tooltipComponents.add(
            Component.translatable(
                  "<%s>", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)))}
               )
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
         );
         if (!ItemStack.parseOptional(access, itemTags).isEmpty()) {
            tooltipComponents.add(Component.translatable("tooltip.hexerei.keychain_with_item").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
         } else {
            tooltipComponents.add(Component.translatable("tooltip.hexerei.keychain_without_item").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
         }
      } else {
         tooltipComponents.add(
            Component.translatable(
                  "[%s]", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11184640)))}
               )
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
         );
         tooltipComponents.add(Component.translatable("tooltip.hexerei.broom_attachments").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
      }

      if (!ItemStack.parseOptional(access, itemTags).isEmpty()) {
         tooltipComponents.add(
            Component.translatable("tooltip.hexerei.keychain_contains", new Object[]{itemText}).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
         );
      }

      super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
   }
}
