package mezz.jei.library.plugins.vanilla.ingredients;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;

public final class ItemStackCodecs {
   private static final Codec<DataComponentPatch> NBT_PRESERVING_COMPONENT_PATCH_CODEC = new Codec<DataComponentPatch>() {
      public <T> DataResult<Pair<DataComponentPatch, T>> decode(DynamicOps<T> ops, T input) {
         Optional<String> snbt = ops.getStringValue(input).result();
         return snbt.isPresent()
            ? ItemStackCodecs.parseComponentPatch(ops, snbt.get()).map(componentPatch -> Pair.of(componentPatch, input))
            : DataComponentPatch.CODEC.decode(ops, input);
      }

      public <T> DataResult<T> encode(DataComponentPatch input, DynamicOps<T> ops, T prefix) {
         return DataComponentPatch.CODEC.encodeStart(ItemStackCodecs.createNbtOps(ops), input).flatMap(tag -> Codec.STRING.encode(tag.toString(), ops, prefix));
      }
   };

   private ItemStackCodecs() {
   }

   public static Codec<ItemStack> createStrictSingleItemCodec() {
      return RecordCodecBuilder.create(
            i -> i.group(
                  ItemStack.ITEM_NON_AIR_CODEC.fieldOf("id").forGetter(ItemStack::getItemHolder),
                  NBT_PRESERVING_COMPONENT_PATCH_CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(ItemStack::getComponentsPatch)
               )
               .apply(i, (item, components) -> new ItemStack(item, 1, components))
         )
         .validate(ItemStackCodecs::validateStrictSingleItem);
   }

   private static DataResult<ItemStack> validateStrictSingleItem(ItemStack itemStack) {
      DataResult<Unit> componentValidation = ItemStack.validateComponents(itemStack.getComponents());
      if (componentValidation.isError()) {
         return componentValidation.map(unit -> itemStack);
      } else {
         return itemStack.getCount() > itemStack.getMaxStackSize()
            ? DataResult.error(() -> "Item stack with stack size of " + itemStack.getCount() + " was larger than maximum: " + itemStack.getMaxStackSize())
            : DataResult.success(itemStack);
      }
   }

   private static DataResult<DataComponentPatch> parseComponentPatch(DynamicOps<?> ops, String snbt) {
      try {
         CompoundTag tag = TagParser.parseTag(snbt);
         return DataComponentPatch.CODEC.parse(createNbtOps(ops), tag);
      } catch (CommandSyntaxException var3) {
         return DataResult.error(() -> "Failed to parse item stack components: " + var3.getMessage());
      }
   }

   private static DynamicOps<Tag> createNbtOps(DynamicOps<?> ops) {
      return (DynamicOps<Tag>)(ops instanceof RegistryOps<?> registryOps ? registryOps.withParent(NbtOps.INSTANCE) : NbtOps.INSTANCE);
   }
}
