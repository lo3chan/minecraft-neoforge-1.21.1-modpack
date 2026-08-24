package fuzs.puzzleslib.impl.init;

import fuzs.puzzleslib.api.init.v3.family.BlockSetFamily;
import fuzs.puzzleslib.api.init.v3.family.BlockSetVariant;
import fuzs.puzzleslib.api.init.v3.registry.RegistryManager;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import net.minecraft.core.Holder.Reference;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.BlockFamily.Builder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

public final class BlockSetFamilyRegistrar implements BlockSetFamily, BlockSetFamily.Writable, BlockSetFamily.Context {
   private final Map<BlockSetVariant, Reference<Block>> blockVariants = new LinkedHashMap<>();
   private final Map<BlockSetVariant, Reference<Item>> itemVariants = new LinkedHashMap<>();
   private final Map<BlockSetVariant, Reference<EntityType<?>>> entityVariants = new LinkedHashMap<>();
   private final RegistryManager registries;
   private final Reference<Block> baseBlock;
   private final String basePath;
   private final BlockSetType blockSetType;
   private final WoodType woodType;
   private Consumer<Builder> blockFamilyConsumer = Function.identity()::apply;

   public BlockSetFamilyRegistrar(RegistryManager registries, Reference<Block> baseBlock, String basePath, BlockSetType blockSetType, WoodType woodType) {
      this.registries = registries;
      this.baseBlock = baseBlock;
      this.basePath = basePath;
      this.blockSetType = blockSetType;
      this.woodType = woodType;
   }

   @Override
   public Reference<Block> getBaseBlock() {
      return this.baseBlock;
   }

   @Override
   public BlockSetType getBlockSetType() {
      return this.blockSetType;
   }

   @Override
   public WoodType getWoodType() {
      return this.woodType;
   }

   @Override
   public BlockFamily getBlockFamily() {
      Builder blockFamily = new Builder((Block)this.getBaseBlock().value());
      this.getBlockVariants().forEach((variant, holder) -> {
         if (variant instanceof VanillaBlockSetVariant vanillaVariant) {
            vanillaVariant.variantBuilder.accept(blockFamily, (Block)holder.value());
         }
      });
      if (this.getBlockVariants().containsKey(BlockSetVariant.SIGN) && this.getBlockVariants().containsKey(BlockSetVariant.WALL_SIGN)) {
         blockFamily.sign((Block)this.getBlock(BlockSetVariant.SIGN).value(), (Block)this.getBlock(BlockSetVariant.WALL_SIGN).value());
      }

      this.blockFamilyConsumer.accept(blockFamily);
      return blockFamily.getFamily();
   }

   @Override
   public Map<BlockSetVariant, Reference<Block>> getBlockVariants() {
      return Collections.unmodifiableMap(this.blockVariants);
   }

   @Override
   public Map<BlockSetVariant, Reference<Item>> getItemVariants() {
      return Collections.unmodifiableMap(this.itemVariants);
   }

   @Override
   public Map<BlockSetVariant, Reference<EntityType<?>>> getEntityVariants() {
      return Collections.unmodifiableMap(this.entityVariants);
   }

   @Override
   public String getName(UnaryOperator<String> name) {
      return name.apply(this.basePath);
   }

   @Override
   public RegistryManager getRegistries() {
      return this.registries;
   }

   @Override
   public void registerBlock(BlockSetVariant variant, Reference<Block> holder) {
      Objects.requireNonNull(holder, "holder is null");
      if (this.blockVariants.put(variant, holder) != null) {
         throw new IllegalStateException(variant + " already present");
      }
   }

   @Override
   public void registerItem(BlockSetVariant variant, Reference<Item> holder) {
      Objects.requireNonNull(holder, "holder is null");
      if (this.itemVariants.put(variant, holder) != null) {
         throw new IllegalStateException(variant + " already present");
      }
   }

   @Override
   public void registerEntityType(BlockSetVariant variant, Reference<EntityType<?>> holder) {
      Objects.requireNonNull(holder, "holder is null");
      if (this.entityVariants.put(variant, holder) != null) {
         throw new IllegalStateException(variant + " already present");
      }
   }

   @Override
   public BlockSetFamily.Writable generateFor(BlockSetVariant variant) {
      variant.generateFor(this);
      return this;
   }

   @Override
   public BlockSetFamily.Writable configureBlockFamily(Consumer<Builder> blockFamilyConsumer) {
      Objects.requireNonNull(blockFamilyConsumer, "consumer is null");
      this.blockFamilyConsumer = blockFamilyConsumer;
      return this;
   }
}
