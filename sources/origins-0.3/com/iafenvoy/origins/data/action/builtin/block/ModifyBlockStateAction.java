package com.iafenvoy.origins.data.action.builtin.block;

import com.iafenvoy.origins.data.action.BlockAction;
import com.iafenvoy.origins.util.codec.MiscCodecs;
import com.iafenvoy.origins.util.math.ResourceOperation;
import com.iafenvoy.origins.util.wrapper.OptionalBoolean;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.runtime.SwitchBootstraps;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;

public record ModifyBlockStateAction(
   String property, ResourceOperation operation, OptionalInt change, OptionalBoolean value, Optional<String> enumValue, boolean cycle
) implements BlockAction {
   public static final MapCodec<ModifyBlockStateAction> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            Codec.STRING.fieldOf("property").forGetter(ModifyBlockStateAction::property),
            ResourceOperation.CODEC.optionalFieldOf("operation", ResourceOperation.ADD).forGetter(ModifyBlockStateAction::operation),
            MiscCodecs.integer("change").forGetter(ModifyBlockStateAction::change),
            MiscCodecs.bool("value").forGetter(ModifyBlockStateAction::value),
            Codec.STRING.optionalFieldOf("enum").forGetter(ModifyBlockStateAction::enumValue),
            Codec.BOOL.optionalFieldOf("change", false).forGetter(ModifyBlockStateAction::cycle)
         )
         .apply(instance, ModifyBlockStateAction::new)
   );

   @NotNull
   @Override
   public MapCodec<? extends BlockAction> codec() {
      return CODEC;
   }

   @Override
   public void execute(@NotNull Level level, @NotNull BlockPos pos, @NotNull Optional<Direction> direction) {
      BlockState state = level.getBlockState(pos);
      Collection<Property<?>> properties = state.getProperties();
      String desiredPropertyName = this.property();
      Property<?> property = null;

      for (Property<?> p : properties) {
         if (p.getName().equals(desiredPropertyName)) {
            property = p;
            break;
         }
      }

      if (property != null) {
         if (this.cycle()) {
            level.setBlockAndUpdate(pos, (BlockState)state.cycle(property));
         } else {
            Object value = state.getValue(property);
            Objects.requireNonNull(value);
            Object var19 = value;
            byte var10 = 0;

            while (true) {
               switch (SwitchBootstraps.typeSwitch<"typeSwitch",Enum,Boolean,Integer>(var19, var10)) {
                  case 0:
                     Enum<?> ignored = (Enum<?>)var19;
                     if (this.enumValue().isPresent()) {
                        modifyEnumState(level, pos, state, property, this.enumValue().get());
                        return;
                     }

                     var10 = 1;
                     break;
                  case 1:
                     Boolean ignored = (Boolean)var19;
                     if (this.value().isPresent()) {
                        level.setBlockAndUpdate(pos, (BlockState)state.setValue(property, this.value().getAsBoolean()));
                        return;
                     }

                     var10 = 2;
                     break;
                  case 2:
                     Integer ignored = (Integer)var19;
                     if (!this.change().isPresent()) {
                        var10 = 3;
                        break;
                     } else {
                        ResourceOperation op = this.operation();
                        int opValue = this.change().getAsInt();
                        int newValue = (Integer)value;
                        switch (op) {
                           case ADD:
                              newValue += opValue;
                              break;
                           case SET:
                              newValue = opValue;
                        }

                        if (property.getPossibleValues().contains(newValue)) {
                           level.setBlockAndUpdate(pos, (BlockState)state.setValue(property, newValue));
                        }

                        return;
                     }
                  default:
                     return;
               }
            }
         }
      }
   }

   private static <T extends Comparable<T>> void modifyEnumState(Level world, BlockPos pos, BlockState originalState, Property<T> property, String value) {
      Optional<T> enumValue = property.getValue(value);
      enumValue.ifPresent(v -> world.setBlockAndUpdate(pos, (BlockState)originalState.setValue(property, v)));
   }
}
