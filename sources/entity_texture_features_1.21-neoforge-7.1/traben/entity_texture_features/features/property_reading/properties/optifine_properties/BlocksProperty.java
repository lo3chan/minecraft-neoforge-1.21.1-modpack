package traben.entity_texture_features.features.property_reading.properties.optifine_properties;

import java.util.Arrays;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.StringArrayOrRegexProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFUtils2;

public class BlocksProperty extends StringArrayOrRegexProperty {
   private static final Function<Entry<Property<?>, Comparable<?>>, String> PROPERTY_MAP_PRINTER = new Function<Entry<Property<?>, Comparable<?>>, String>() {
      public String apply(@Nullable Entry<Property<?>, Comparable<?>> entry) {
         if (entry == null) {
            return "<NULL>";
         } else {
            Property<?> property = entry.getKey();
            String var10000 = property.getName();
            return var10000 + "=" + this.nameValue(property, entry.getValue());
         }
      }

      private <T extends Comparable<T>> String nameValue(Property<T> property, Comparable<?> value) {
         return property.getName(value);
      }
   };
   protected final Function<BlockState, Boolean> blockStateMatcher;
   protected final boolean botherWithDeepStateCheck;

   protected BlocksProperty(Properties properties, int propertyNum, String[] ids) throws RandomProperty.RandomPropertyException {
      super(readPropertiesOrThrow(properties, propertyNum, ids).replaceAll("(?<=(^| ))minecraft:", ""));
      if (this.usesRegex) {
         this.blockStateMatcher = blockState -> this.MATCHER.testString(this.getFromStateBlockNameOnly(blockState))
            ? true
            : this.MATCHER.testString(this.getFromStateBlockNameWithStateData(blockState));
         this.botherWithDeepStateCheck = false;
      } else {
         this.blockStateMatcher = this::testBlocks;
         boolean hasStateNeeds = false;

         for (String s : this.ARRAY) {
            if (s.contains(":")) {
               hasStateNeeds = true;
               break;
            }
         }

         this.botherWithDeepStateCheck = hasStateNeeds;
      }
   }

   public static BlocksProperty getPropertyOrNull(Properties properties, int propertyNum) {
      try {
         return new BlocksProperty(properties, propertyNum, new String[]{"blocks", "block"});
      } catch (RandomProperty.RandomPropertyException var3) {
         return null;
      }
   }

   protected String getFromStateBlockNameOnly(BlockState state) {
      String block = BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString().replaceFirst("minecraft:", "");
      if (this.doPrint) {
         ETFUtils2.logMessage("Blocks property print (no blockstate data): [" + block + "]");
      }

      return block;
   }

   private String getFromStateBlockNameWithStateData(BlockState state) {
      String block = this.getFromStateBlockNameOnly(state);
      if (!state.getValues().isEmpty()) {
         block = block + ":" + state.getValues().entrySet().stream().map(PROPERTY_MAP_PRINTER).collect(Collectors.joining(":"));
      }

      if (this.doPrint) {
         ETFUtils2.logMessage("Blocks property print (with blockstate data): [" + block + "]");
      }

      return block;
   }

   protected boolean testBlocks(BlockState blockState) {
      if (this.MATCHER.testString(this.getFromStateBlockNameOnly(blockState))) {
         return true;
      } else {
         if (this.botherWithDeepStateCheck) {
            String fullBlockState = this.getFromStateBlockNameWithStateData(blockState);

            for (String string : this.ARRAY) {
               if (string.contains(":")) {
                  boolean matchesAllStateDataNeeded = true;

                  for (String split : string.split(":")) {
                     if (!fullBlockState.contains(split)) {
                        matchesAllStateDataNeeded = false;
                        break;
                     }
                  }

                  if (matchesAllStateDataNeeded) {
                     return true;
                  }
               }
            }
         }

         return false;
      }
   }

   @Override
   protected boolean shouldForceLowerCaseCheck() {
      return true;
   }

   @Nullable
   protected BlockState[] getTestingBlocks(ETFEntityRenderState entity) {
      if (entity.uuid().getLeastSignificantBits() == 53021371281465L) {
         return new BlockState[]{Blocks.SPAWNER.defaultBlockState()};
      } else if (entity instanceof BlockEntity blockEntity) {
         return blockEntity.getLevel() == null
            ? new BlockState[]{blockEntity.getBlockState()}
            : new BlockState[]{blockEntity.getBlockState(), blockEntity.getLevel().getBlockState(blockEntity.getBlockPos().below())};
      } else if (entity.world() != null && entity.blockPos() != null) {
         Level world = entity.world();
         BlockPos pos = entity.blockPos();
         return new BlockState[]{world.getBlockState(pos), world.getBlockState(pos.below())};
      } else {
         return null;
      }
   }

   @Override
   public boolean testEntityInternal(ETFEntityRenderState entity) {
      BlockState[] entityBlocks = this.getTestingBlocks(entity);
      if (entityBlocks == null) {
         if (this.doPrint) {
            ETFUtils2.logMessage("Blocks property print result: [false], because null");
         }

         return false;
      } else {
         if (this.doPrint) {
            ETFUtils2.logMessage("Blocks property print, found blocks: [" + Arrays.toString((Object[])entityBlocks) + "]");
         }

         for (BlockState entityBlock : entityBlocks) {
            if (this.blockStateMatcher.apply(entityBlock)) {
               if (this.doPrint) {
                  ETFUtils2.logMessage("Blocks property print result: [true]");
               }

               return true;
            }
         }

         if (this.doPrint) {
            ETFUtils2.logMessage("Blocks property print result: [false]");
         }

         return false;
      }
   }

   @Nullable
   @Override
   public String getValueFromEntity(ETFEntityRenderState etfEntity) {
      return null;
   }

   @NotNull
   @Override
   public String[] getPropertyIds() {
      return new String[]{"blocks", "block"};
   }
}
