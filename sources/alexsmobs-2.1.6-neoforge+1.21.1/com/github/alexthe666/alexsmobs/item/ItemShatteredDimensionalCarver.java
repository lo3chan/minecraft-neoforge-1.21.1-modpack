package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.EntityVoidPortal;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public class ItemShatteredDimensionalCarver extends ItemDimensionalCarver implements IClientExtensionItem {
   public ItemShatteredDimensionalCarver(Properties props) {
      super(props);
   }

   @Override
   public void initializeClient(Consumer<IClientItemExtensions> consumer) {
      consumer.accept((IClientItemExtensions)AlexsMobs.PROXY.getISTERProperties());
   }

   @Override
   public void onPortalOpen(Level worldIn, LivingEntity player, EntityVoidPortal portal, Direction dir) {
      portal.setAttachmentFacing(dir);
      portal.setShattered(true);
      portal.setLifespan(2000);
      portal.exitDimension = worldIn.dimension();
      BlockPos playerPos = player.blockPosition();
      if (dir == Direction.DOWN) {
         portal.setDestination(new BlockPos(playerPos.getX(), AMCompat.minBuildHeight(worldIn) + 1, playerPos.getZ()));
      } else if (dir == Direction.UP) {
         portal.setDestination(new BlockPos(playerPos.getX(), AMCompat.maxBuildHeight(worldIn) - 1, playerPos.getZ()));
      } else {
         double worldBorderDistance = worldIn.getWorldBorder().getDistanceToBorder(playerPos.getX(), playerPos.getZ()) - 5.0;
         BlockPos millionPos = playerPos.relative(dir.getOpposite(), (int)Math.min(worldBorderDistance, 1000000.0));
         portal.setDestination(millionPos);
      }
   }
}
