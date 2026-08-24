package net.cibernet.alchemancy.properties.special;

import java.util.Set;
import java.util.UUID;
import net.cibernet.alchemancy.mixin.accessors.VaultServerDataAccessor;
import net.cibernet.alchemancy.properties.Property;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.vault.VaultBlockEntity;
import net.minecraft.world.level.block.entity.vault.VaultServerData;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;

public class VaultLockpickingProperty extends Property {
   @Override
   public void onRightClickBlock(UseItemOnBlockEvent event) {
      Player player = event.getPlayer();
      if (player != null && !event.getLevel().isClientSide()) {
         ItemStack stack = event.getItemStack();
         Level level = event.getLevel();
         BlockPos pos = event.getPos();
         UUID playerUuid = event.getPlayer().getUUID();
         if (level.getBlockEntity(pos) instanceof VaultBlockEntity vault) {
            VaultServerData vaultData = vault.getServerData();
            if (vaultData == null) {
               return;
            }

            Set<UUID> rewarded = ((VaultServerDataAccessor)vaultData).invokeGetRewardedPlayers();
            if (rewarded.contains(playerUuid)) {
               event.getLevel().levelEvent(2001, pos, Block.getId(level.getBlockState(pos)));
               rewarded.removeIf(uuid -> uuid.equals(playerUuid));
               this.damageOrConsumeItem(player, stack, event.getHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND, 50);
               event.setCancellationResult(ItemInteractionResult.SUCCESS);
               event.setCanceled(true);
            }
         }
      }
   }

   @Override
   public int getColor(ItemStack stack) {
      return 2176322;
   }
}
