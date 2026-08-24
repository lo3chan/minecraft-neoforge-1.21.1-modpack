package net.blay09.mods.balm.world.level.block.internal;

import com.mojang.datafixers.util.Either;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.blay09.mods.balm.world.level.block.DeferredBlock;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.Holder.Kind;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public record DeferredBlockImpl(Holder<Block> holder) implements DeferredBlock {
   public Item asItem() {
      return ((Block)this.holder.value()).asItem();
   }

   @Override
   public BlockState defaultBlockState() {
      return ((Block)this.holder.value()).defaultBlockState();
   }

   public Block value() {
      return (Block)this.holder.value();
   }

   public boolean isBound() {
      return this.holder.isBound();
   }

   public boolean is(ResourceLocation identifier) {
      return this.holder.is(identifier);
   }

   public boolean is(ResourceKey<Block> resourceKey) {
      return this.holder.is(resourceKey);
   }

   public boolean is(Predicate<ResourceKey<Block>> predicate) {
      return this.holder.is(predicate);
   }

   public boolean is(TagKey<Block> tagKey) {
      return this.holder.is(tagKey);
   }

   public boolean is(Holder<Block> holder) {
      return holder.is(holder);
   }

   public Stream<TagKey<Block>> tags() {
      return this.holder.tags();
   }

   public Either<ResourceKey<Block>, Block> unwrap() {
      return this.holder.unwrap();
   }

   public Optional<ResourceKey<Block>> unwrapKey() {
      return this.holder.unwrapKey();
   }

   public Kind kind() {
      return this.holder.kind();
   }

   public boolean canSerializeIn(HolderOwner<Block> holderOwner) {
      return this.holder.canSerializeIn(holderOwner);
   }

   @Override
   public ItemStack createStack(int count) {
      ItemStack itemStack = this.asItem().getDefaultInstance();
      itemStack.setCount(count);
      return itemStack;
   }

   @Override
   public Block asBlock() {
      return (Block)this.holder.value();
   }

   @Override
   public Holder<Block> asHolder() {
      return this.holder;
   }
}
