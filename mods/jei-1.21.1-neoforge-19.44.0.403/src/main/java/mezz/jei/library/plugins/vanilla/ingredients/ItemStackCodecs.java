/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.core.component.DataComponentMap
 *  net.minecraft.core.component.DataComponentPatch
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.NbtOps
 *  net.minecraft.nbt.Tag
 *  net.minecraft.nbt.TagParser
 *  net.minecraft.resources.RegistryOps
 *  net.minecraft.world.item.ItemStack
 */
package mezz.jei.library.plugins.vanilla.ingredients;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.item.ItemStack;

public final class ItemStackCodecs {
    private static final Codec<DataComponentPatch> NBT_PRESERVING_COMPONENT_PATCH_CODEC = new Codec<DataComponentPatch>(){

        public <T> DataResult<Pair<DataComponentPatch, T>> decode(DynamicOps<T> ops, T input) {
            Optional snbt = ops.getStringValue(input).result();
            if (snbt.isPresent()) {
                return ItemStackCodecs.parseComponentPatch(ops, (String)snbt.get()).map(componentPatch -> Pair.of((Object)componentPatch, (Object)input));
            }
            return DataComponentPatch.CODEC.decode(ops, input);
        }

        public <T> DataResult<T> encode(DataComponentPatch input, DynamicOps<T> ops, T prefix) {
            return DataComponentPatch.CODEC.encodeStart(ItemStackCodecs.createNbtOps(ops), (Object)input).flatMap(tag -> Codec.STRING.encode((Object)tag.toString(), ops, prefix));
        }
    };

    private ItemStackCodecs() {
    }

    public static Codec<ItemStack> createStrictSingleItemCodec() {
        return RecordCodecBuilder.create(i -> i.group((App)ItemStack.ITEM_NON_AIR_CODEC.fieldOf("id").forGetter(ItemStack::getItemHolder), (App)NBT_PRESERVING_COMPONENT_PATCH_CODEC.optionalFieldOf("components", (Object)DataComponentPatch.EMPTY).forGetter(ItemStack::getComponentsPatch)).apply((Applicative)i, (item, components) -> new ItemStack(item, 1, components))).validate(ItemStackCodecs::validateStrictSingleItem);
    }

    private static DataResult<ItemStack> validateStrictSingleItem(ItemStack itemStack) {
        DataResult componentValidation = ItemStack.validateComponents((DataComponentMap)itemStack.getComponents());
        if (componentValidation.isError()) {
            return componentValidation.map(unit -> itemStack);
        }
        if (itemStack.getCount() > itemStack.getMaxStackSize()) {
            return DataResult.error(() -> "Item stack with stack size of " + itemStack.getCount() + " was larger than maximum: " + itemStack.getMaxStackSize());
        }
        return DataResult.success((Object)itemStack);
    }

    private static DataResult<DataComponentPatch> parseComponentPatch(DynamicOps<?> ops, String snbt) {
        try {
            CompoundTag tag = TagParser.parseTag((String)snbt);
            return DataComponentPatch.CODEC.parse(ItemStackCodecs.createNbtOps(ops), (Object)tag);
        }
        catch (CommandSyntaxException e) {
            return DataResult.error(() -> "Failed to parse item stack components: " + e.getMessage());
        }
    }

    private static DynamicOps<Tag> createNbtOps(DynamicOps<?> ops) {
        if (ops instanceof RegistryOps) {
            RegistryOps registryOps = (RegistryOps)ops;
            return registryOps.withParent((DynamicOps)NbtOps.INSTANCE);
        }
        return NbtOps.INSTANCE;
    }
}

