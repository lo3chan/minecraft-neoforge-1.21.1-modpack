package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.client.VariantBlockStateGenerator;
import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
import java.util.List;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;

public enum BlockRotationType implements StringRepresentable {
   NONE("none"),
   HORIZONTAL("horizontal", BlockStateProperties.HORIZONTAL_FACING),
   VERTICAL("vertical", BlockStateProperties.ATTACH_FACE),
   FACING("facing", BlockStateProperties.FACING),
   WALL_ATTACHED("wall_attached", BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.ATTACH_FACE);

   public final String name;
   public final List<Property<?>> properties;

   private BlockRotationType(String name, Property<?>... properties) {
      this.name = name;
      this.properties = List.of(properties);
   }

   public String getSerializedName() {
      return this.name;
   }

   public void generateBlockStateJson(VariantBlockStateGenerator bs, BlockBuilder block) {
   }

   public void generateBlockModelJsons(KubeAssetGenerator gen) {
   }
}
