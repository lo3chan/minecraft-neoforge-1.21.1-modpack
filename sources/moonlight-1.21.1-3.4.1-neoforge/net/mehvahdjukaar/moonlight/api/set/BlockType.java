package net.mehvahdjukaar.moonlight.api.set;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Supplier;
import net.mehvahdjukaar.moonlight.api.util.INamedSupplier;
import net.mehvahdjukaar.moonlight.api.util.TextHelper;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.mehvahdjukaar.moonlight.core.set.BlockSetInternal;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BlockType {
   private final BiMap<String, Object> children = HashBiMap.create();
   public final ResourceLocation id;

   protected BlockType(ResourceLocation resourceLocation) {
      this.id = resourceLocation;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public String getTypeName() {
      String path = this.id.getPath();
      return path.contains("/") ? path.substring(path.lastIndexOf("/") + 1) : path;
   }

   public String getNamespace() {
      return this.id.getNamespace();
   }

   public String getAppendableId() {
      return this.getNamespace() + "/" + this.getTypeName();
   }

   public String getAppendableIdWith(String suffix) {
      return this.getAppendableIdWith("", suffix);
   }

   public String getAppendableIdWith(String prefix, String suffix) {
      String prefixed = prefix.isEmpty() ? "" : prefix + "_";
      return this.getNamespace() + "/" + prefixed + this.getTypeName() + "_" + suffix;
   }

   public String createPathWith(String shortenedId, String suffix) {
      return this.createFullIdWith("", "", shortenedId, "", suffix);
   }

   public String createPathWith(String shortenedId, String prefix, String suffix) {
      return this.createFullIdWith("", "", shortenedId, prefix, suffix);
   }

   public String createFullIdWith(String modIdOrEmpty, String folderOrEmpty, String shortenedIdOrEmpty, String prefixOrEmpty, String suffix) {
      String modIded = modIdOrEmpty.isEmpty() ? "" : modIdOrEmpty + ":";
      String foldered = folderOrEmpty.isEmpty() ? "" : folderOrEmpty + "/";
      String namespaced = modIdOrEmpty.equals(this.getNamespace()) ? "" : this.getNamespace() + "/";
      String shortenedId = shortenedIdOrEmpty.isEmpty() ? "" : shortenedIdOrEmpty + "/";
      String prefixed = "";
      if (prefixOrEmpty.contains("/")) {
         prefixed = prefixOrEmpty;
      } else if (!prefixOrEmpty.isEmpty()) {
         prefixed = prefixOrEmpty + "_";
      }

      String suffixed = "";
      if (suffix.matches("\\.(png|json)")) {
         suffixed = suffix;
      } else if (!suffix.isEmpty()) {
         suffixed = "_" + suffix;
      }

      return modIded + foldered + shortenedId + namespaced + prefixed + this.getTypeName() + suffixed;
   }

   @Override
   public String toString() {
      return this.id.toString();
   }

   public abstract String getTranslationKey();

   public String getVariantId(String baseName) {
      String namespace = this.isVanilla() ? "" : this.getNamespace() + "/";
      return baseName.contains("%s") ? namespace + String.format(baseName, this.getTypeName()) : namespace + baseName + "_" + this.getTypeName();
   }

   public String getVariantId(String baseName, boolean prefix) {
      return this.getVariantId(prefix ? baseName + "_%s" : "%s_" + baseName);
   }

   public String getVariantId(String postfix, String prefix) {
      return this.getVariantId(prefix + "_%s_" + postfix);
   }

   public String getReadableName() {
      return TextHelper.getReadableName(this.getTypeName());
   }

   public boolean isVanilla() {
      return this.getNamespace().equals("minecraft");
   }

   public <T extends BlockType> BlockTypeRegistry<T> getRegistry() {
      return BlockSetInternal.getRegistry((Class<T>)this.getClass());
   }

   @Nullable
   protected <V> V findRelatedEntry(String prefixOrInfix, Registry<V> reg) {
      return this.findRelatedEntry(prefixOrInfix, "", reg);
   }

   @Nullable
   protected <V> V findRelatedEntry(String prefixOrInfix, String suffix, Registry<V> reg) {
      String prefixed = prefixOrInfix.isEmpty() ? "" : prefixOrInfix + "_";
      String infixed = prefixOrInfix.isEmpty() ? "" : "_" + prefixOrInfix;
      String suffixed = suffix.isEmpty() ? "" : "_" + suffix;
      ResourceLocation[] targets = new ResourceLocation[]{
         this.id.withPath(this.id.getPath() + infixed + suffixed), this.id.withPath(prefixed + this.id.getPath() + suffixed)
      };
      return Utils.findFirstInRegistry(reg, targets);
   }

   public Set<Entry<String, Object>> getChildren() {
      return this.children.entrySet();
   }

   @Nullable
   public Item getItemOfThis(String key) {
      Item it = this.getChild(key) instanceof ItemLike i ? i.asItem() : null;
      return it == Items.AIR ? null : it;
   }

   @Nullable
   public Block getBlockOfThis(String key) {
      Object v = this.getChild(key);
      if (v instanceof BlockItem bi) {
         return bi.getBlock();
      } else {
         return v instanceof Block b ? b : null;
      }
   }

   @Nullable
   public Object getChild(String key) {
      return this.children.get(key);
   }

   public boolean hasChild(String key) {
      return this.children.containsKey(key);
   }

   public boolean hasChildren(String... keys) {
      for (String key : keys) {
         if (!this.hasChild(key)) {
            return false;
         }
      }

      return true;
   }

   public void addChild(String genericName, @Nullable Object obj) {
      if (obj != Items.AIR && obj != Blocks.AIR) {
         if (obj != null) {
            try {
               if (this.children.get(genericName) == obj) {
                  return;
               }

               this.children.put(genericName, obj);
               BlockTypeRegistry<?> registry = BlockSetInternal.getRegistry(this.getClass());
               if (registry != null) {
                  registry.mapObjectToType(obj, this);
               }
            } catch (Exception var4) {
               Moonlight.LOGGER.warn("Failed to add block type child. Key: {}, Object: {}, BlockType: {}. Ignoring", genericName, obj, this);
            }
         }
      } else {
         throw new IllegalStateException("Tried to add air block/item to Block Type. Key " + genericName + ". This is a Moonlight bug, please report me");
      }
   }

   protected abstract void initializeChildrenBlocks();

   protected abstract void initializeChildrenItems();

   public abstract ItemLike mainChild();

   @Nullable
   public String getChildKey(Object child) {
      BiMap<Object, String> inverse = this.children.inverse();
      String firs = (String)inverse.get(child);
      if (firs != null) {
         return firs;
      } else {
         return child instanceof BlockItem bi ? (String)inverse.get(bi.getBlock()) : null;
      }
   }

   @Nullable
   public static <T extends BlockType> Object changeType(Object current, @NotNull T originalMat, @NotNull T destinationMat) {
      if (destinationMat == originalMat) {
         return current;
      } else {
         String key = originalMat.getChildKey(current);
         return key != null ? destinationMat.getChild(key) : null;
      }
   }

   @Nullable
   public static <T extends BlockType> Item changeItemType(Item current, @NotNull T originalMat, @NotNull T destinationMat) {
      Object changed = changeType(current, originalMat, destinationMat);
      if (changed == null && current instanceof BlockItem bi && changeType(bi.getBlock(), originalMat, destinationMat) instanceof Block il) {
         Item i = il.asItem();
         if (i != Items.AIR) {
            changed = i;
         }
      }

      if (changed instanceof ItemLike ilx) {
         if (ilx.asItem() == current) {
            Moonlight.LOGGER
               .error("Somehow changed an item type into itself. How? Target mat {}, destination map {}, item {}", destinationMat, originalMat, ilx);
         }

         return ilx.asItem();
      } else {
         return null;
      }
   }

   @Nullable
   public static <T extends BlockType> Block changeBlockType(@NotNull Block current, T originalMat, T destinationMat) {
      Object changed = changeType(current, originalMat, destinationMat);
      if (changed == null && current.asItem() != Items.AIR && changeType(current.asItem(), originalMat, destinationMat) instanceof BlockItem bi) {
         Item i = bi.asItem();
         if (i != Items.AIR) {
            changed = i;
         }
      }

      return changed instanceof Block b ? b : null;
   }

   public SoundType getSound() {
      return this.mainChild() instanceof Block b ? b.defaultBlockState().getSoundType() : SoundType.STONE;
   }

   public void removeChild(String childKey) {
      this.children.remove(childKey);
   }

   @FunctionalInterface
   public interface SetFinder<T extends BlockType> extends Supplier<Optional<T>> {
      Optional<T> get();
   }

   public abstract static class SetFinderBuilder<T extends BlockType> implements BlockType.SetFinder<T> {
      protected final ResourceLocation id;
      protected final Map<String, Supplier<ItemLike>> childNames = new HashMap<>();
      private final BlockTypeRegistry<T> reg;

      public SetFinderBuilder(ResourceLocation id, BlockTypeRegistry<T> reg) {
         this.id = id;
         this.reg = reg;
      }

      public BlockType.SetFinderBuilder<T> child(String childType, Supplier<ItemLike> child) {
         this.childNames.put(childType, child);
         return this;
      }

      public BlockType.SetFinderBuilder<T> childItem(String childType, ResourceLocation childName) {
         return this.child(childType, () -> (ItemLike)BuiltInRegistries.ITEM.getOptional(childName).orElseThrow());
      }

      public BlockType.SetFinderBuilder<T> childItemAffix(String childType, String prefix, String suffix) {
         return this.childItem(childType, prefix + this.id.getPath() + suffix);
      }

      public BlockType.SetFinderBuilder<T> childItemSuffix(String childType, String suffix) {
         return this.childItem(childType, this.id.getPath() + suffix);
      }

      public BlockType.SetFinderBuilder<T> childItem(String childType, String childName) {
         return this.childItem(childType, Utils.idWithOptionalNamespace(childName, this.id.getNamespace()));
      }

      public BlockType.SetFinderBuilder<T> childBlock(String childType, ResourceLocation childName) {
         return this.child(childType, () -> (ItemLike)BuiltInRegistries.BLOCK.getOptional(childName).orElseThrow());
      }

      public BlockType.SetFinderBuilder<T> childBlockAffix(String childType, String prefix, String suffix) {
         return this.childBlock(childType, prefix + this.id.getPath() + suffix);
      }

      public BlockType.SetFinderBuilder<T> childBlockSuffix(String childType, String suffix) {
         return this.childBlock(childType, this.id.getPath() + suffix);
      }

      public BlockType.SetFinderBuilder<T> childBlock(String childType, String childName) {
         return this.childBlock(childType, Utils.idWithOptionalNamespace(childName, this.id.getNamespace()));
      }

      public INamedSupplier<T> build() {
         return this.reg.makeFutureHolder(this.id);
      }
   }
}
