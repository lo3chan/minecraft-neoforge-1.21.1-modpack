package net.mcreator.undeadrevamp.procedures;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class WitherchargemaskHelmetTickEventProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         if (entity.isShiftKeyDown()) {
            if (entity instanceof Player _plrCldCheck2
               && _plrCldCheck2.getCooldowns()
                  .isOnCooldown((entity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY).getItem())) {
               if (entity instanceof Player _player && !_player.level().isClientSide()) {
                  _player.displayClientMessage(Component.literal("Wither Charge Unready"), true);
               }
            } else if (entity instanceof Player _player && !_player.level().isClientSide()) {
               _player.displayClientMessage(Component.literal("Wither Charge Ready"), true);
            }
         }
      }
   }
}
