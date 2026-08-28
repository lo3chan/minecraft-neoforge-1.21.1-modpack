/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.util.StringRepresentable
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.VariantHolder
 *  net.minecraft.world.entity.animal.CatVariant
 *  net.minecraft.world.entity.animal.FrogVariant
 *  net.minecraft.world.entity.npc.VillagerType
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.level.block.BedBlock
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.ShulkerBoxBlock
 *  net.minecraft.world.level.block.SignBlock
 *  net.minecraft.world.level.block.SkullBlock
 *  net.minecraft.world.level.block.entity.BedBlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.DecoratedPotBlockEntity
 *  net.minecraft.world.level.block.entity.PotDecorations
 *  net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity
 *  net.minecraft.world.level.block.entity.SignBlockEntity
 *  net.minecraft.world.level.block.entity.SkullBlockEntity
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package traben.entity_texture_features.features.property_reading.properties.etf_properties;

import java.util.Optional;
import java.util.Properties;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.animal.CatVariant;
import net.minecraft.world.entity.animal.FrogVariant;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.BedBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.block.entity.PotDecorations;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.StringArrayOrRegexProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFEntity;

public class VariantProperty
extends StringArrayOrRegexProperty {
    protected VariantProperty(String string) throws RandomProperty.RandomPropertyException {
        super(string);
    }

    public static VariantProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new VariantProperty(VariantProperty.readPropertiesOrThrow(properties, propertyNum, "variant", "variants"));
        }
        catch (RandomProperty.RandomPropertyException var3) {
            return null;
        }
    }

    @Override
    protected boolean shouldForceLowerCaseCheck() {
        return false;
    }

    @Override
    @Nullable
    public String getValueFromEntity(ETFEntityRenderState state) {
        if (state == null) {
            return null;
        }
        ETFEntity etfEntity = state.entity();
        if (etfEntity instanceof Entity) {
            if (etfEntity instanceof VariantHolder) {
                VariantHolder variableEntity = (VariantHolder)etfEntity;
                Object object = variableEntity.getVariant();
                if (object instanceof StringRepresentable) {
                    StringRepresentable stringIdentifiable = (StringRepresentable)object;
                    return stringIdentifiable.getSerializedName();
                }
                object = variableEntity.getVariant();
                if (object instanceof CatVariant) {
                    CatVariant catVariant = (CatVariant)object;
                    return BuiltInRegistries.CAT_VARIANT.getResourceKey((Object)catVariant).map(catVariantRegistryKey -> catVariantRegistryKey.location().getPath()).orElse(null);
                }
                object = variableEntity.getVariant();
                if (object instanceof FrogVariant) {
                    FrogVariant frogVariant = (FrogVariant)object;
                    return BuiltInRegistries.FROG_VARIANT.getResourceKey((Object)frogVariant).map(frogVariantRegistryKey -> frogVariantRegistryKey.location().getPath()).orElse(null);
                }
                object = variableEntity.getVariant();
                if (object instanceof Holder) {
                    Holder registryEntry = (Holder)object;
                    return registryEntry.unwrapKey().isPresent() ? ((ResourceKey)registryEntry.unwrapKey().get()).location().getPath() : null;
                }
                object = variableEntity.getVariant();
                if (object instanceof Optional) {
                    Object t;
                    Optional possibleStringIdentifiable = (Optional)object;
                    if (possibleStringIdentifiable.isPresent() && (t = possibleStringIdentifiable.get()) instanceof StringRepresentable) {
                        StringRepresentable stringIdentifiable = (StringRepresentable)t;
                        return stringIdentifiable.getSerializedName();
                    }
                    return null;
                }
                object = variableEntity.getVariant();
                if (object instanceof VillagerType) {
                    VillagerType villagerType = (VillagerType)object;
                    return villagerType.toString();
                }
                return variableEntity.getVariant().toString();
            }
            return BuiltInRegistries.ENTITY_TYPE.getResourceKey((Object)((Entity)etfEntity).getType()).map(key -> key.location().getPath()).orElse(null);
        }
        if (etfEntity instanceof BlockEntity) {
            BedBlockEntity bedBlockEntity;
            ShulkerBoxBlockEntity shulkerBoxBlockEntity;
            SignBlockEntity signBlockEntity;
            Block block;
            if (etfEntity instanceof SignBlockEntity && (block = (signBlockEntity = (SignBlockEntity)etfEntity).getBlockState().getBlock()) instanceof SignBlock) {
                SignBlock abstractSignBlock = (SignBlock)block;
                return abstractSignBlock.type().name();
            }
            if (etfEntity instanceof ShulkerBoxBlockEntity && (block = (shulkerBoxBlockEntity = (ShulkerBoxBlockEntity)etfEntity).getBlockState().getBlock()) instanceof ShulkerBoxBlock) {
                ShulkerBoxBlock shulkerBoxBlock = (ShulkerBoxBlock)block;
                return String.valueOf(shulkerBoxBlock.getColor());
            }
            if (etfEntity instanceof BedBlockEntity && (block = (bedBlockEntity = (BedBlockEntity)etfEntity).getBlockState().getBlock()) instanceof BedBlock) {
                BedBlock bedBlock = (BedBlock)block;
                return String.valueOf(bedBlock.getColor());
            }
            if (etfEntity instanceof DecoratedPotBlockEntity) {
                DecoratedPotBlockEntity pot = (DecoratedPotBlockEntity)etfEntity;
                PotDecorations sherds = pot.getDecorations();
                return (sherds.back().isPresent() ? ((Item)sherds.back().get()).getDescriptionId() : "none") + "," + (sherds.left().isPresent() ? ((Item)sherds.left().get()).getDescriptionId() : "none") + "," + (sherds.right().isPresent() ? ((Item)sherds.right().get()).getDescriptionId() : "none") + "," + (sherds.front().isPresent() ? ((Item)sherds.front().get()).getDescriptionId() : "none");
            }
            Object suffix = "";
            if (etfEntity instanceof SkullBlockEntity) {
                SkullBlockEntity skull = (SkullBlockEntity)etfEntity;
                suffix = "_direction_" + String.valueOf(skull.getBlockState().getValue((Property)SkullBlock.ROTATION));
            }
            return (String)BuiltInRegistries.BLOCK_ENTITY_TYPE.getResourceKey((Object)((BlockEntity)etfEntity).getType()).map(key -> key.location().getPath()).orElse(null) + (String)suffix;
        }
        return null;
    }

    @Override
    @NotNull
    public String[] getPropertyIds() {
        return new String[]{"variant", "variants"};
    }
}

