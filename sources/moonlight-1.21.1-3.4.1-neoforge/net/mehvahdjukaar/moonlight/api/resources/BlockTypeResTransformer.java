package net.mehvahdjukaar.moonlight.api.resources;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.mehvahdjukaar.moonlight.api.misc.TriFunction;
import net.mehvahdjukaar.moonlight.api.set.BlockType;
import net.mehvahdjukaar.moonlight.api.set.leaves.LeavesType;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public class BlockTypeResTransformer<T extends BlockType> {
   private final ResourceManager manager;
   private final String modId;
   private final List<BlockTypeResTransformer.TextModification<T>> textModifiers = new ArrayList<>();
   private BlockTypeResTransformer.TextModification<T> idModifiers = (s, id, w) -> s;

   private BlockTypeResTransformer(String modId, ResourceManager manager) {
      this.manager = manager;
      this.modId = modId;
   }

   public static <T extends BlockType> BlockTypeResTransformer<T> create(String modId, ResourceManager manager) {
      return new BlockTypeResTransformer<>(modId, manager);
   }

   public static BlockTypeResTransformer<WoodType> wood(String modId, ResourceManager manager) {
      return new BlockTypeResTransformer<>(modId, manager);
   }

   public static BlockTypeResTransformer<LeavesType> leaves(String modId, ResourceManager manager) {
      return new BlockTypeResTransformer<>(modId, manager);
   }

   public BlockTypeResTransformer<T> andThen(BlockTypeResTransformer<T> other) {
      this.textModifiers.addAll(other.textModifiers);
      this.idModifiers = other.idModifiers;
      return this;
   }

   public BlockTypeResTransformer<T> setIDModifier(BlockTypeResTransformer.TextModification<T> modifier) {
      this.idModifiers = modifier;
      return this;
   }

   public BlockTypeResTransformer<T> IDReplaceType(String oldTypeName) {
      return this.setIDModifier((s, id, w) -> replaceTypeNoNamespace(s, w, id, oldTypeName));
   }

   public BlockTypeResTransformer<T> IDReplaceBlock(String blockName) {
      return this.setIDModifier((s, id, w) -> s.replace(blockName, id.getPath()));
   }

   public BlockTypeResTransformer<T> addModifier(BlockTypeResTransformer.TextModification<T> modifier) {
      this.textModifiers.add(modifier);
      return this;
   }

   public BlockTypeResTransformer<T> replaceSimpleType(String oldTypeName) {
      return this.addModifier((s, id, w) -> replaceType(s, w, id, oldTypeName, this.modId));
   }

   public BlockTypeResTransformer<T> replaceGenericType(String oldTypeName, String entryClass) {
      this.addModifier((s, id, w) -> replaceFullGenericType(s, w, id, oldTypeName, this.modId, entryClass));
      return this;
   }

   public BlockTypeResTransformer<T> replaceBlockType(String oldTypeName) {
      this.addModifier((s, id, w) -> replaceFullGenericType(s, w, id, oldTypeName, this.modId, "block"));
      return this;
   }

   public BlockTypeResTransformer<T> replaceItemType(String oldTypeName) {
      this.addModifier((s, id, w) -> replaceFullGenericType(s, w, id, oldTypeName, this.modId, "item"));
      return this;
   }

   public BlockTypeResTransformer<T> replaceString(String from, String to) {
      return this.addModifier((s, id, w) -> s.replace(from, to));
   }

   @Nullable
   private ItemLike wfl(T t, String s) {
      if (t instanceof LeavesType l && l.getAssociatedWoodType() != null) {
         return l.getAssociatedWoodType().getChild(s) instanceof ItemLike il ? il : null;
      } else {
         return null;
      }
   }

   public BlockTypeResTransformer<T> replaceWithTextureFromChild(String target, String textureFromChild) {
      return this.replaceWithTextureFromChild(target, textureFromChild, s -> true);
   }

   public BlockTypeResTransformer<T> replaceWithTextureFromChild(String target, String textureFromChild, Predicate<String> texturePredicate) {
      return this.replaceWithTextureFromChild(target, w -> (ItemLike)w.getChild(textureFromChild), texturePredicate);
   }

   public BlockTypeResTransformer<T> replaceWithTextureFromChild(String target, Function<T, ItemLike> childProvider, Predicate<String> texturePredicate) {
      return this.addModifier((s, id, w) -> {
         String r = s;
         if (!s.matches("\\{\\s*\"parent\":\\s*\".*\"\\s*\\}")) {
            try {
               ItemLike woodObject = childProvider.apply(w);
               ResourceLocation newTexture = null;
               if (woodObject instanceof Block b) {
                  newTexture = RPUtils.findFirstBlockTextureLocation(this.manager, b, texturePredicate);
               } else if (woodObject instanceof Item i) {
                  newTexture = RPUtils.findFirstItemTextureLocation(this.manager, i);
               }

               if (newTexture != null) {
                  r = r.replace("\"" + target + "\"", "\"" + newTexture + "\"");
                  r = s.replace("\"block/", "\"minecraft:block/");
                  r = r.replace("\"" + target + "\"", "\"" + newTexture + "\"");
               }
            } catch (FileNotFoundException var12) {
            }
         }

         return r;
      });
   }

   public StaticResource transform(StaticResource resource, ResourceLocation blockId, T type) {
      String newText = resource.asString();

      for (BlockTypeResTransformer.TextModification<T> m : this.textModifiers) {
         newText = m.apply(newText, blockId, type);
      }

      ResourceLocation oldPath = resource.location;
      String id = this.idModifiers.apply(oldPath.getPath(), blockId, type);
      ResourceLocation newLocation = blockId.withPath(id);
      return StaticResource.create(newText.getBytes(), newLocation);
   }

   public static String replaceTypeNoNamespace(String text, BlockType blockType, ResourceLocation blockId, String oldTypeName) {
      return replaceFullGenericType(text, blockType, blockId, oldTypeName, null, 1);
   }

   public static String replaceType(String text, BlockType blockType, ResourceLocation blockId, String oldTypeName, String oldNamespace) {
      return replaceFullGenericType(text, blockType, blockId, oldTypeName, oldNamespace, 1);
   }

   public static String replaceFullGenericType(
      String text, BlockType newBlockType, ResourceLocation newBlockId, String oldTypeName, @Nullable String oldTypeNamespace, int folderDepth
   ) {
      StringBuilder sb = new StringBuilder();

      for (int i = 0; i < folderDepth; i++) {
         if (i != 0) {
            sb.append("\\/");
         }

         sb.append(".*?");
      }

      return replaceFullGenericType(text, newBlockType, newBlockId, oldTypeName, oldTypeNamespace, sb.toString());
   }

   public static String replaceFullGenericType(
      String text, BlockType blockType, ResourceLocation blockId, String oldTypeName, @Nullable String oldNamespace, String folderName
   ) {
      Pattern blockPathSubPathPattern = Pattern.compile("([^,]*(?=/))");
      Matcher blockPathSubPathMather = blockPathSubPathPattern.matcher(blockId.getPath());
      String blockFolderPrefix = blockPathSubPathMather.find() ? blockPathSubPathMather.group(1) : "";
      String blockTypeName = blockType.getTypeName();
      String newNamespace = oldNamespace == null ? "" : blockId.getNamespace() + ":";
      oldNamespace = oldNamespace == null ? "" : oldNamespace + ":";
      String folderRegEx = "(" + folderName + ")/";
      String extraFolderRegex = "(/?(?:\\w+/)*\\w*?)";
      String typeNameRegex = "(?<![a-zA-Z])" + oldTypeName + "(?![a-zA-Z])";
      Pattern subFolderPattern = Pattern.compile(oldNamespace + folderRegEx + extraFolderRegex + typeNameRegex);
      Matcher subFolderMatcher = subFolderPattern.matcher(text);
      return subFolderMatcher.replaceAll(m -> {
         String group2 = m.group(2).contains(oldTypeName) ? m.group(2).replaceAll(oldTypeName, blockTypeName) : m.group(2);
         return newNamespace + joinWithSeparator(m.group(1), blockFolderPrefix, group2 + blockTypeName);
      });
   }

   private static String joinWithSeparator(String... strings) {
      StringBuilder sb = new StringBuilder();

      for (String s : strings) {
         if (!s.isEmpty()) {
            if (!sb.isEmpty()) {
               sb.append("/");
            }

            sb.append(s);
         }
      }

      return sb.toString();
   }

   @FunctionalInterface
   public interface TextModification<T extends BlockType> extends TriFunction<String, ResourceLocation, T, String> {
      String apply(String var1, ResourceLocation var2, T var3);
   }
}
