package net.joefoxe.hexerei.item.custom;

import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.joefoxe.hexerei.util.ResourceOrTag;
import net.joefoxe.hexerei.util.message.DowsingRodUpdatePositionPacket;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.HitResult;

public class DowsingRodItem extends Item {
   public static final TagKey<Biome> BT_SWAMP = createBiomeTag("has_structure/dark_coven_biomes");
   public BlockPos nearestPos = null;
   public boolean swampMode = true;
   private static final int distBetweenChecks = 30;
   private static final int searchOffset = 10;
   private static final int numOfChecks = 120;
   private static final int maxRadiusI = 600;
   private int radiusI = 0;
   private int angleI = 0;
   private BlockPos playerPos;
   public static final DynamicCommandExceptionType ERROR_INVALID_BIOME = new DynamicCommandExceptionType(
      p_137850_ -> Component.translatable("commands.locatebiome.invalid", new Object[]{p_137850_})
   );

   public DowsingRodItem(Properties properties) {
      super(properties);
   }

   public static double angleDifference(double angle1, double angle2) {
      double diff = (angle2 - angle1 + 180.0) % 360.0 - 180.0;
      return diff < -180.0 ? diff + 360.0 : diff;
   }

   public void inventoryTick(ItemStack p_41404_, Level world, Entity entity, int p_41407_, boolean p_41408_) {
      super.inventoryTick(p_41404_, world, entity, p_41407_, p_41408_);
      if (entity instanceof Player
         && (this.nearestPos == null && ((Player)entity).getMainHandItem() == p_41404_ || ((Player)entity).getOffhandItem() == p_41404_)) {
         if (this.swampMode) {
            this.findSwamp(world, entity);
         } else {
            this.findJungle(world, entity);
         }
      }
   }

   public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
      ItemStack itemstack = playerIn.getItemInHand(handIn);
      HitResult raytraceresult = getPlayerPOVHitResult(worldIn, playerIn, Fluid.ANY);
      if (playerIn.isSecondaryUseActive()) {
         if (!worldIn.isClientSide) {
            playerIn.getCooldowns().addCooldown(this, 20);
            this.swampMode = !this.swampMode;
            String s = "display.hexerei.dowsing_rod_swamp";
            if (!this.swampMode) {
               s = "display.hexerei.dowsing_rod_jungle";
            }

            if (this.swampMode) {
               this.findSwamp(worldIn, playerIn);
            } else {
               this.findJungle(worldIn, playerIn);
            }

            playerIn.displayClientMessage(Component.translatable(s), true);
         }

         playerIn.swing(handIn);
      } else {
         if (this.swampMode) {
            this.findSwamp(worldIn, playerIn);
            playerIn.displayClientMessage(Component.translatable("display.hexerei.dowsing_rod_swamp_new"), true);
         } else {
            this.findJungle(worldIn, playerIn);
            playerIn.displayClientMessage(Component.translatable("display.hexerei.dowsing_rod_jungle_new"), true);
         }

         playerIn.swing(handIn);
      }

      if (playerIn instanceof ServerPlayer serverPlayer && this.nearestPos != null) {
         HexereiPacketHandler.sendToPlayerClient(new DowsingRodUpdatePositionPacket(itemstack, this.nearestPos, this.swampMode), serverPlayer);
      }

      return InteractionResultHolder.pass(itemstack);
   }

   public void findSwamp(Level worldIn, Entity entity) {
      if (worldIn instanceof ServerLevel serverLevel) {
         ResourceOrTag<Biome> key = ResourceOrTag.get("#c:is_swamp", Registries.BIOME);
         Pair<BlockPos, Holder<Biome>> pair = serverLevel.findClosestBiome3d(key.holderPredicate(), entity.blockPosition(), 6400, 32, 64);
         if (pair != null) {
            this.nearestPos = (BlockPos)pair.getFirst();
         }
      }
   }

   public void findJungle(Level worldIn, Entity entity) {
      if (worldIn instanceof ServerLevel serverLevel) {
         ResourceOrTag<Biome> key = ResourceOrTag.get("#minecraft:is_jungle", Registries.BIOME);
         Pair<BlockPos, Holder<Biome>> pair = serverLevel.findClosestBiome3d(key.holderPredicate(), entity.blockPosition(), 6400, 32, 64);
         if (pair != null) {
            this.nearestPos = (BlockPos)pair.getFirst();
         }
      }
   }

   private static TagKey<Biome> createBiomeTag(String name) {
      return TagKey.create(Registries.BIOME, HexereiUtil.getResource(name));
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      if (Screen.hasShiftDown()) {
         tooltipComponents.add(
            Component.translatable(
                  "<%s>", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)))}
               )
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
         );
         tooltipComponents.add(Component.translatable("tooltip.hexerei.dowsing_rod_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
         tooltipComponents.add(Component.translatable("tooltip.hexerei.dowsing_rod_3").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
         tooltipComponents.add(Component.translatable("tooltip.hexerei.dowsing_rod_4").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
         tooltipComponents.add(Component.translatable("tooltip.hexerei.dowsing_rod_5").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
      } else {
         tooltipComponents.add(
            Component.translatable(
                  "[%s]", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11184640)))}
               )
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
         );
         tooltipComponents.add(Component.translatable("tooltip.hexerei.dowsing_rod").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
      }

      super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
   }
}
