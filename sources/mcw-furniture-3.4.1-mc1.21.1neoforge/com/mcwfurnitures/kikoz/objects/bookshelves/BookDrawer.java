package com.mcwfurnitures.kikoz.objects.bookshelves;

import com.mcwfurnitures.kikoz.objects.WideFurniture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class BookDrawer extends WideFurniture {
   public BookDrawer(Properties properties) {
      super(properties);
   }

   public float getEnchantPowerBonus(BlockState state, LevelReader world, BlockPos pos) {
      return 1.0F;
   }
}
