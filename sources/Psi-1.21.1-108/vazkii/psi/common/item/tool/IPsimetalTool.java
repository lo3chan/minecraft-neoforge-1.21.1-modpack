package vazkii.psi.common.item.tool;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.Nullable;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.ISocketable;
import vazkii.psi.common.core.handler.PlayerDataHandler;
import vazkii.psi.common.item.ItemCAD;
import vazkii.psi.common.item.base.ModDataComponents;
import vazkii.psi.common.spell.trick.block.PieceTrickBreakBlock;

public interface IPsimetalTool {
   static BlockHitResult raytraceFromEntity(Level worldIn, Player player, Fluid fluidMode, double range) {
      float f = player.getXRot();
      float f1 = player.getYRot();
      Vec3 vec3d = player.getEyePosition(1.0F);
      float f2 = Mth.cos(-f1 * 0.017453292F - 3.1415927F);
      float f3 = Mth.sin(-f1 * 0.017453292F - 3.1415927F);
      float f4 = -Mth.cos(-f * 0.017453292F);
      float f5 = Mth.sin(-f * 0.017453292F);
      float f6 = f3 * f4;
      float f7 = f2 * f4;
      Vec3 vec3d1 = vec3d.add(f6 * range, f5 * range, f7 * range);
      return worldIn.clip(new ClipContext(vec3d, vec3d1, Block.OUTLINE, fluidMode, player));
   }

   static void regen(ItemStack stack, Entity entityIn) {
      if (isItemValidForRegen(stack, entityIn)) {
         Player player = (Player)entityIn;
         PlayerDataHandler.PlayerData data = PlayerDataHandler.get(player);
         int regenTime = (Integer)stack.getOrDefault(ModDataComponents.REGEN_TIME, 0);
         if (!data.overflowed && regenTime % 16 == 0 && (float)data.getAvailablePsi() / data.getTotalPsi() > 0.5F) {
            data.deductPsi(150, 0, true);
            stack.setDamageValue(stack.getDamageValue() - 1);
         }

         stack.set(ModDataComponents.REGEN_TIME, regenTime + 1);
      }
   }

   static boolean isItemValidForRegen(ItemStack stack, Entity entityIn) {
      return !(entityIn instanceof Player player) ? false : player.getOffhandItem() != stack && player.getMainHandItem() != stack && stack.getDamageValue() > 0;
   }

   static boolean isEnabled(ItemStack stack) {
      return stack.getDamageValue() < stack.getMaxDamage();
   }

   default void castOnBlockBreak(ItemStack itemstack, Player player) {
      if (isEnabled(itemstack) && !PieceTrickBreakBlock.doingHarvestCheck.get()) {
         PlayerDataHandler.PlayerData data = PlayerDataHandler.get(player);
         ItemStack playerCad = PsiAPI.getPlayerCAD(player);
         if (!playerCad.isEmpty()) {
            ISocketable sockets = ISocketable.socketable(itemstack);
            ItemStack bullet = sockets.getSelectedBullet();
            ItemCAD.cast(
               player.getCommandSenderWorld(),
               player,
               data,
               bullet,
               playerCad,
               5,
               10,
               0.05F,
               context -> {
                  context.tool = itemstack;
                  context.positionBroken = raytraceFromEntity(
                     player.getCommandSenderWorld(), player, Fluid.NONE, player.getAttributes().getValue(Attributes.BLOCK_INTERACTION_RANGE)
                  );
               }
            );
         }
      }
   }

   default ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
      return new ToolSocketable(stack, 3);
   }
}
