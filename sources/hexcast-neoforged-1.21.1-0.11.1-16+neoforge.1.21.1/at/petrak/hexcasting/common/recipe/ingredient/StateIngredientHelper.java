package at.petrak.hexcasting.common.recipe.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class StateIngredientHelper {
   public static StateIngredient of(Block block) {
      return new StateIngredientBlock(block);
   }

   public static StateIngredient of(BlockState state) {
      return new StateIngredientBlockState(state);
   }

   public static StateIngredient of(TagKey<Block> tag) {
      return of(tag.location());
   }

   public static StateIngredient of(ResourceLocation id) {
      return new StateIngredientTag(id);
   }

   public static StateIngredient of(Collection<Block> blocks) {
      return new StateIngredientBlocks(blocks);
   }

   public static StateIngredient tagExcluding(TagKey<Block> tag, StateIngredient... excluded) {
      return new StateIngredientTagExcluding(tag.location(), List.of(excluded));
   }

   public static StateIngredient deserialize(JsonObject object) {
      String var1 = GsonHelper.getAsString(object, "type");
      switch (var1) {
         case "tag":
            return new StateIngredientTag(ResourceLocation.parse(GsonHelper.getAsString(object, "tag")));
         case "block":
            return new StateIngredientBlock((Block)BuiltInRegistries.BLOCK.get(ResourceLocation.parse(GsonHelper.getAsString(object, "block"))));
         case "state":
            return new StateIngredientBlockState(readBlockState(object));
         case "blocks":
            List<Block> blocks = new ArrayList<>();

            for (JsonElement element : GsonHelper.getAsJsonArray(object, "blocks")) {
               blocks.add((Block)BuiltInRegistries.BLOCK.get(ResourceLocation.parse(element.getAsString())));
            }

            return new StateIngredientBlocks(blocks);
         case "tag_excluding":
            ResourceLocation tag = ResourceLocation.parse(GsonHelper.getAsString(object, "tag"));
            List<StateIngredient> ingr = new ArrayList<>();

            for (JsonElement element : GsonHelper.getAsJsonArray(object, "exclude")) {
               ingr.add(deserialize(GsonHelper.convertToJsonObject(element, "exclude entry")));
            }

            return new StateIngredientTagExcluding(tag, ingr);
         default:
            throw new JsonParseException("Unknown type!");
      }
   }

   @Nullable
   public static StateIngredient tryDeserialize(JsonObject object) {
      StateIngredient ingr = deserialize(object);
      if (ingr instanceof StateIngredientTag sit) {
         return sit.resolve().findAny().isEmpty() ? null : ingr;
      } else {
         if (!(ingr instanceof StateIngredientBlock) && !(ingr instanceof StateIngredientBlockState)) {
            if (ingr instanceof StateIngredientBlocks sib) {
               Collection<Block> blocks = sib.blocks;
               List<Block> list = new ArrayList<>(blocks);
               if (list.removeIf(b -> b == Blocks.AIR)) {
                  if (list.size() == 0) {
                     return null;
                  }

                  return of(list);
               }
            }
         } else if (ingr.test(Blocks.AIR.defaultBlockState())) {
            return null;
         }

         return ingr;
      }
   }

   public static StateIngredient read(FriendlyByteBuf buffer) {
      switch (buffer.readVarInt()) {
         case 0:
            int count = buffer.readVarInt();
            Set<Block> set = new HashSet<>();

            for (int i = 0; i < count; i++) {
               int id = buffer.readVarInt();
               Block block = (Block)BuiltInRegistries.BLOCK.byId(id);
               set.add(block);
            }

            return new StateIngredientBlocks(set);
         case 1:
            return new StateIngredientBlock((Block)BuiltInRegistries.BLOCK.byId(buffer.readVarInt()));
         case 2:
            return new StateIngredientBlockState(Block.stateById(buffer.readVarInt()));
         default:
            throw new IllegalArgumentException("Unknown input discriminator!");
      }
   }

   public static JsonObject serializeBlockState(BlockState state) {
      CompoundTag nbt = NbtUtils.writeBlockState(state);
      renameTag(nbt, "Name", "name");
      renameTag(nbt, "Properties", "properties");
      Dynamic<Tag> dyn = new Dynamic(NbtOps.INSTANCE, nbt);
      return ((JsonElement)dyn.convert(JsonOps.INSTANCE).getValue()).getAsJsonObject();
   }

   public static BlockState readBlockState(JsonObject object) {
      CompoundTag nbt = (CompoundTag)new Dynamic(JsonOps.INSTANCE, object).convert(NbtOps.INSTANCE).getValue();
      renameTag(nbt, "name", "Name");
      renameTag(nbt, "properties", "Properties");
      String name = nbt.getString("Name");
      ResourceLocation id = ResourceLocation.tryParse(name);
      if (id != null && BuiltInRegistries.BLOCK.getOptional(id).isPresent()) {
         return NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), nbt);
      } else {
         throw new IllegalArgumentException("Invalid or unknown block ID: " + name);
      }
   }

   @Deprecated
   @Nonnull
   public static List<ItemStack> toStackList(StateIngredient input) {
      return input.getDisplayedStacks();
   }

   private static void renameTag(CompoundTag tag, String from, String to) {
      Tag t = tag.get(from);
      if (t != null) {
         tag.remove(from);
         tag.put(to, t);
      }
   }
}
