package traben.entity_texture_features.features.property_reading.properties.etf_properties;

import java.util.Properties;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.optifine_properties.BlocksProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

public class BlockBelowSolidProperty extends BlocksProperty {
   protected BlockBelowSolidProperty(Properties properties, int propertyNum, String[] ids) throws RandomProperty.RandomPropertyException {
      super(properties, propertyNum, ids);
   }

   public static BlocksProperty getPropertyOrNull(Properties properties, int propertyNum) {
      try {
         return new BlockBelowSolidProperty(properties, propertyNum, new String[]{"blockBelowSolid"});
      } catch (RandomProperty.RandomPropertyException var3) {
         return null;
      }
   }

   @Nullable
   @Override
   protected BlockState[] getTestingBlocks(ETFEntityRenderState entity) {
      if (entity.uuid().getLeastSignificantBits() == 53021371281465L) {
         return new BlockState[]{Blocks.SPAWNER.defaultBlockState()};
      } else if (entity.world() != null && entity.blockPos() != null) {
         Level world = entity.world();
         MutableBlockPos mutablePos = new MutableBlockPos();
         mutablePos.set(entity.blockPos());
         int minBuildHeight = world.getMinBuildHeight();

         while (minBuildHeight <= mutablePos.getY() && !world.getBlockState(mutablePos).isSolidRender(world, mutablePos)) {
            mutablePos.move(0, -1, 0);
         }

         return minBuildHeight > mutablePos.getY() ? null : new BlockState[]{world.getBlockState(mutablePos)};
      } else {
         return null;
      }
   }

   @NotNull
   @Override
   public String[] getPropertyIds() {
      return new String[]{"blockBelowSolid"};
   }
}
