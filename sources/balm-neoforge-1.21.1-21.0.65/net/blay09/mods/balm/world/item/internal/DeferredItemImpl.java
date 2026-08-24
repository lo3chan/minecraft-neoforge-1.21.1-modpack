package net.blay09.mods.balm.world.item.internal;

import com.mojang.datafixers.util.Either;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.blay09.mods.balm.world.item.DeferredItem;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.Holder.Kind;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public record DeferredItemImpl(Holder<Item> holder) implements DeferredItem {
   public Item asItem() {
      return (Item)this.holder.value();
   }

   public Item value() {
      return (Item)this.holder.value();
   }

   public boolean isBound() {
      return this.holder.isBound();
   }

   public boolean is(ResourceLocation identifier) {
      return this.holder.is(identifier);
   }

   public boolean is(ResourceKey<Item> resourceKey) {
      return this.holder.is(resourceKey);
   }

   public boolean is(Predicate<ResourceKey<Item>> predicate) {
      return this.holder.is(predicate);
   }

   public boolean is(TagKey<Item> tagKey) {
      return this.holder.is(tagKey);
   }

   public boolean is(Holder<Item> holder) {
      return holder.is(holder);
   }

   public Stream<TagKey<Item>> tags() {
      return this.holder.tags();
   }

   public Either<ResourceKey<Item>, Item> unwrap() {
      return this.holder.unwrap();
   }

   public Optional<ResourceKey<Item>> unwrapKey() {
      return this.holder.unwrapKey();
   }

   public Kind kind() {
      return this.holder.kind();
   }

   public boolean canSerializeIn(HolderOwner<Item> holderOwner) {
      return this.holder.canSerializeIn(holderOwner);
   }

   @Override
   public ItemStack createStack(int count) {
      ItemStack itemStack = this.asItem().getDefaultInstance();
      itemStack.setCount(count);
      return itemStack;
   }
}
