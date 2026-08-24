package net.mcreator.undeadrevamp.procedures;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.HitResult.Type;

public class WoodennestOnBlockRightClickedProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == Items.GLASS_BOTTLE
            && entity.level()
                  .clip(
                     new ClipContext(
                        entity.getEyePosition(1.0F),
                        entity.getEyePosition(1.0F).add(entity.getViewVector(1.0F).scale(5.0)),
                        Block.OUTLINE,
                        Fluid.SOURCE_ONLY,
                        entity
                     )
                  )
                  .getType()
               == Type.BLOCK) {
            if (entity instanceof LivingEntity _entity) {
               _entity.swing(InteractionHand.MAIN_HAND, true);
            }

            if (entity instanceof Player _player && !_player.level().isClientSide()) {
               _player.displayClientMessage(Component.literal("Inefficient amount of product to be harvested"), true);
            }
         }
      }
   }
}
