package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.block.RandomTickKubeEvent;
import dev.latvian.mods.kubejs.plugin.builtin.event.BlockEvents;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.BlockWrapper;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.match.Replaceable;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.kubejs.web.RelativeURL;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase;
import net.minecraft.world.level.block.state.properties.Property;

@RemapPrefixForJS("kjs$")
public interface BlockStateKJS extends RegistryObjectKJS<Block>, Replaceable {
   @Override
   default ResourceKey<Registry<Block>> kjs$getRegistryId() {
      return Registries.BLOCK;
   }

   @Override
   default Registry<Block> kjs$getRegistry() {
      return BuiltInRegistries.BLOCK;
   }

   @Override
   default Holder<Block> kjs$asHolder() {
      return ((BlockStateBase)this).getBlock().kjs$asHolder();
   }

   @Override
   default ResourceKey<Block> kjs$getKey() {
      return ((BlockStateBase)this).getBlock().kjs$getKey();
   }

   @Override
   default String kjs$getId() {
      return ((BlockStateBase)this).getBlock().kjs$getId();
   }

   default void kjs$setDestroySpeed(float v) {
      throw new NoMixinException();
   }

   default void kjs$setRequiresTool(boolean v) {
      throw new NoMixinException();
   }

   default void kjs$setLightEmission(int v) {
      throw new NoMixinException();
   }

   default boolean kjs$randomTickOverride(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
      return BlockEvents.RANDOM_TICK.hasListeners(state.kjs$getKey())
         ? BlockEvents.RANDOM_TICK.post(ScriptType.SERVER, state.kjs$getKey(), new RandomTickKubeEvent(level, pos, state, random)).interruptFalse()
         : false;
   }

   @Override
   default Object replaceThisWith(RecipeScriptContext cx, Object with) {
      return with instanceof BlockState state
         ? state
         : (with instanceof Block block ? block.defaultBlockState() : cx.cx().jsToJava(with, BlockWrapper.STATE_TYPE_INFO));
   }

   default RelativeURL kjs$getWebIconURL(int size) {
      return new RelativeURL("/img/" + size + "/block/" + ID.url(this.kjs$getIdLocation()));
   }

   default String kjs$toString() {
      BlockState state = (BlockState)this;
      StringBuilder sb = new StringBuilder();
      sb.append(state.getBlock().builtInRegistryHolder().getKey().location());
      boolean first = true;

      for (Property<?> prop : state.getProperties()) {
         Comparable<?> value = state.getValue(prop);
         if (!value.equals(state.getBlock().defaultBlockState().getValue(prop))) {
            if (first) {
               sb.append('[');
               first = false;
            } else {
               sb.append(',');
            }

            sb.append(prop.getName());
            sb.append('=');
            sb.append(prop.getName(Cast.to(value)));
         }
      }

      if (!first) {
         sb.append(']');
      }

      return sb.toString();
   }
}
