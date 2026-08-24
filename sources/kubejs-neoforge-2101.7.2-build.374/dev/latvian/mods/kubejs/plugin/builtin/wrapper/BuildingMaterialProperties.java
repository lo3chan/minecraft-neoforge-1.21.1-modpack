package dev.latvian.mods.kubejs.plugin.builtin.wrapper;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.block.custom.ButtonOrPressurePlateBuilder;
import dev.latvian.mods.kubejs.registry.RegistryKubeEvent;
import dev.latvian.mods.kubejs.util.KubeResourceLocation;
import dev.latvian.mods.kubejs.util.TickDuration;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.HideFromJS;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockSetType;

public record BuildingMaterialProperties(
   BuildingMaterialProperties.Blocks blocks,
   Optional<Boolean> baseBlock,
   Optional<Boolean> baseBlockSuffix,
   Consumer<BlockBuilder> properties,
   Optional<BlockSetType> behaviour,
   Optional<TickDuration> ticksToStayPressed
) {
   public static final TypeInfo TYPE_INFO = TypeInfo.of(BuildingMaterialProperties.class);

   private boolean add(Function<BuildingMaterialProperties.Blocks, Optional<Boolean>> func) {
      return this.blocks == null || func.apply(this.blocks).orElse(true);
   }

   @HideFromJS
   public void register(Context cx, RegistryKubeEvent<Block> event, KubeResourceLocation id) {
      ArrayList<BlockBuilder> builder = new ArrayList<>();
      if (this.baseBlock.orElse(true)) {
         boolean _baseBlockSuffix = this.baseBlockSuffix.orElse(true);
         BlockBuilder baseBlock = (BlockBuilder)event.create(cx, _baseBlockSuffix ? id.withPath(px -> px + "_block") : id);
         builder.add(baseBlock);
         if (_baseBlockSuffix) {
            baseBlock.texture(id.wrapped().withPath(px -> "block/" + px).toString());
         }
      }

      if (this.add(BuildingMaterialProperties.Blocks::slab)) {
         builder.add((BlockBuilder)event.create(cx, id.withPath(px -> px + "_slab"), new KubeResourceLocation(KubeJS.id("slab"))));
      }

      if (this.add(BuildingMaterialProperties.Blocks::stairs)) {
         builder.add((BlockBuilder)event.create(cx, id.withPath(px -> px + "_stairs"), new KubeResourceLocation(KubeJS.id("stairs"))));
      }

      if (this.add(BuildingMaterialProperties.Blocks::fence)) {
         builder.add((BlockBuilder)event.create(cx, id.withPath(px -> px + "_fence"), new KubeResourceLocation(KubeJS.id("fence"))));
      }

      if (this.add(BuildingMaterialProperties.Blocks::fenceGate)) {
         builder.add((BlockBuilder)event.create(cx, id.withPath(px -> px + "_fence_gate"), new KubeResourceLocation(KubeJS.id("fence_gate"))));
      }

      if (this.add(BuildingMaterialProperties.Blocks::wall)) {
         builder.add((BlockBuilder)event.create(cx, id.withPath(px -> px + "_wall"), new KubeResourceLocation(KubeJS.id("wall"))));
      }

      if (this.add(BuildingMaterialProperties.Blocks::pressurePlate)) {
         builder.add((BlockBuilder)event.create(cx, id.withPath(px -> px + "_pressure_plate"), new KubeResourceLocation(KubeJS.id("pressure_plate"))));
      }

      if (this.add(BuildingMaterialProperties.Blocks::button)) {
         builder.add((BlockBuilder)event.create(cx, id.withPath(px -> px + "_button"), new KubeResourceLocation(KubeJS.id("button"))));
      }

      if (this.add(BuildingMaterialProperties.Blocks::trapdoor)) {
         builder.add((BlockBuilder)event.create(cx, id.withPath(px -> px + "_trapdoor"), new KubeResourceLocation(KubeJS.id("trapdoor"))));
      }

      if (this.add(BuildingMaterialProperties.Blocks::door)) {
         builder.add((BlockBuilder)event.create(cx, id.withPath(px -> px + "_door"), new KubeResourceLocation(KubeJS.id("door"))));
      }

      for (BlockBuilder b : builder) {
         if (this.properties != null) {
            this.properties.accept(b);
         }

         if (b instanceof ButtonOrPressurePlateBuilder p) {
            this.behaviour.ifPresent(p::behaviour);
            this.ticksToStayPressed.ifPresent(p::ticksToStayPressed);
         }
      }
   }

   public record Blocks(
      Optional<Boolean> slab,
      Optional<Boolean> stairs,
      Optional<Boolean> fence,
      Optional<Boolean> fenceGate,
      Optional<Boolean> wall,
      Optional<Boolean> pressurePlate,
      Optional<Boolean> button,
      Optional<Boolean> trapdoor,
      Optional<Boolean> door
   ) {
   }
}
