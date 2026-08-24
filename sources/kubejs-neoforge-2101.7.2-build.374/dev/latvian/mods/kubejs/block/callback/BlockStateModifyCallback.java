package dev.latvian.mods.kubejs.block.callback;

import dev.latvian.mods.kubejs.typings.Info;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class BlockStateModifyCallback {
   private BlockState state;

   public BlockStateModifyCallback(BlockState state) {
      this.state = state;
   }

   @Info("Cycles the property")
   public <T extends Comparable<T>> BlockStateModifyCallback cycle(Property<T> property) {
      this.state = (BlockState)this.state.cycle(property);
      return this;
   }

   @Info("Gets the state. If it has been modified, gets the new state")
   public BlockState getState() {
      return this.state;
   }

   @Override
   public String toString() {
      return this.state.toString();
   }

   @Info("Get the properties this block has that can be changed")
   public Collection<Property<?>> getProperties() {
      return this.state.getProperties();
   }

   @Info("Checks if this block has the specified property")
   public <T extends Comparable<T>> boolean hasProperty(Property<T> property) {
      return this.state.hasProperty(property);
   }

   @Info("Gets the value of the passed in property")
   public <T extends Comparable<T>> T getValue(Property<T> property) {
      return (T)this.state.getValue(property);
   }

   @Info("Gets the value of the pased in property")
   public <T extends Comparable<T>> T get(Property<T> property) {
      return (T)this.state.getValue(property);
   }

   @Info("Gets the value of the passed in property as an Optional. If the property does not exist in this block the Optional will be empty")
   public <T extends Comparable<T>> Optional<T> getOptionalValue(Property<T> property) {
      return this.state.getOptionalValue(property);
   }

   @Info("Sets the value of the specified property")
   public <T extends Comparable<T>, V extends T> BlockStateModifyCallback setValue(Property<T> property, V comparable) {
      this.state = (BlockState)this.state.setValue(property, comparable);
      return this;
   }

   @Info("Sets the value of the specified boolean property")
   public BlockStateModifyCallback set(BooleanProperty property, boolean value) {
      this.state = (BlockState)this.state.setValue(property, value);
      return this;
   }

   @Info("Sets the value of the specified integer property")
   public BlockStateModifyCallback set(IntegerProperty property, Integer value) {
      this.state = (BlockState)this.state.setValue(property, value);
      return this;
   }

   @Info("Sets the value of the specified enum property")
   public <T extends Enum<T> & StringRepresentable> BlockStateModifyCallback set(EnumProperty<T> property, String value) {
      this.state = (BlockState)this.state.setValue(property, (Enum)property.getValue(value).get());
      return this;
   }

   public BlockStateModifyCallback populateNeighbours(Map<Map<Property<?>, Comparable<?>>, BlockState> map) {
      this.state.populateNeighbours(map);
      return this;
   }

   @Info("Get a map of this blocks properties to it's value")
   public Map<Property<?>, Comparable<?>> getValues() {
      return this.state.getValues();
   }

   @Info("Rotate the block using the specified Rotation")
   public BlockStateModifyCallback rotate(Rotation rotation) {
      this.state = this.state.rotate(rotation);
      return this;
   }

   @Info("Mirror the block using the specified Mirror")
   public BlockStateModifyCallback mirror(Mirror mirror) {
      this.state = this.state.mirror(mirror);
      return this;
   }

   @Info("Updates the shape of this block. Mostly used in waterloggable blocks to update the water flow")
   public BlockStateModifyCallback updateShape(Direction direction, BlockState blockState, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos2) {
      this.state = this.state.updateShape(direction, blockState, levelAccessor, blockPos, blockPos2);
      return this;
   }
}
