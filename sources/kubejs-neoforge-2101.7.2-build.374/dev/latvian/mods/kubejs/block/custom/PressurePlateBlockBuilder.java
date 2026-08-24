package dev.latvian.mods.kubejs.block.custom;

import dev.latvian.mods.kubejs.client.VariantBlockStateGenerator;
import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.kubejs.util.TickDuration;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.BlockSetType;

@ReturnsSelf
public class PressurePlateBlockBuilder extends ShapedBlockBuilder implements ButtonOrPressurePlateBuilder {
   public static final ResourceLocation[] PRESSURE_PLATE_TAGS = new ResourceLocation[]{BlockTags.PRESSURE_PLATES.location()};
   private static final ResourceLocation MODEL = ResourceLocation.withDefaultNamespace("block/pressure_plate_up");
   private static final ResourceLocation PRESSED_MODEL = ResourceLocation.withDefaultNamespace("block/pressure_plate_down");
   public transient BlockSetType behaviour;
   public transient int ticksToStayPressed;

   public PressurePlateBlockBuilder(ResourceLocation i) {
      super(i, "_pressure_plate");
      this.noCollision();
      this.tagBoth(PRESSURE_PLATE_TAGS);
      this.behaviour = BlockSetType.OAK;
      this.ticksToStayPressed = 20;
   }

   public PressurePlateBlockBuilder behaviour(BlockSetType behaviour) {
      this.behaviour = behaviour;
      return this;
   }

   public PressurePlateBlockBuilder ticksToStayPressed(TickDuration ticks) {
      this.ticksToStayPressed = ticks.intTicks();
      return this;
   }

   public Block createObject() {
      return new PressurePlateBlockBuilder.KubePressurePlateBlock(this.behaviour, this.ticksToStayPressed, this.createProperties());
   }

   @Override
   protected void generateBlockState(VariantBlockStateGenerator bs) {
      bs.variant("powered=false", (Consumer<VariantBlockStateGenerator.Variant>)(v -> v.model(this.id.withPath(ID.BLOCK))));
      bs.variant("powered=true", (Consumer<VariantBlockStateGenerator.Variant>)(v -> v.model(this.newID("block/", "_down"))));
   }

   @Override
   protected void generateBlockModels(KubeAssetGenerator generator) {
      generator.blockModel(this.id, m -> {
         m.parent(MODEL);
         m.texture("texture", this.baseTexture);
      });
      generator.blockModel(this.newID("", "_down"), m -> {
         m.parent(PRESSED_MODEL);
         m.texture("texture", this.baseTexture);
      });
   }

   private static class KubePressurePlateBlock extends PressurePlateBlock {
      private final int pressedTime;

      public KubePressurePlateBlock(BlockSetType type, int pressedTime, Properties properties) {
         super(type, properties);
         this.pressedTime = pressedTime;
      }

      protected int getPressedTime() {
         return this.pressedTime;
      }
   }
}
