package dev.worldgen.lithostitched.api.worldgen.processor.enums;

import com.mojang.serialization.Codec;
import dev.worldgen.lithostitched.api.worldgen.processorcondition.ProcessorCondition;
import java.util.function.Function;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.BlockState;

public enum BlockType implements StringRepresentable {
   INPUT("input", data -> data.relative().state()),
   LOCATION("location", data -> data.absolute().state());

   public static final Codec<BlockType> CODEC = StringRepresentable.fromEnum(BlockType::values);
   private final String name;
   private final Function<ProcessorCondition.Data, BlockState> state;

   private BlockType(String name, Function<ProcessorCondition.Data, BlockState> state) {
      this.name = name;
      this.state = state;
   }

   public BlockState state(ProcessorCondition.Data data) {
      return this.state.apply(data);
   }

   public String getSerializedName() {
      return this.name;
   }
}
