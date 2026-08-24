package fuzs.puzzleslib.neoforge.api.data.v2.client.model;

import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile.UncheckedModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

@Deprecated
public class ModItemModelProvider extends ItemModelProvider {
   private final BlockStateProvider provider;

   public ModItemModelProvider(PackOutput output, String modId, ExistingFileHelper fileHelper, BlockStateProvider provider) {
      super(output, modId, fileHelper);
      this.provider = provider;
   }

   public ResourceLocation key(Item item) {
      return BuiltInRegistries.ITEM.getKey(item);
   }

   public String name(Item item) {
      return this.key(item).getPath();
   }

   public ItemModelBuilder basicItem(Item item, ResourceLocation texture) {
      return this.basicItem(Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item)), texture);
   }

   public ItemModelBuilder basicItem(ResourceLocation item, Item texture) {
      return this.basicItem(item, Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(texture)));
   }

   public ItemModelBuilder basicItem(ResourceLocation item, ResourceLocation texture) {
      return (ItemModelBuilder)((ItemModelBuilder)((ItemModelBuilder)this.getBuilder(item.toString())).parent(new UncheckedModelFile("item/generated")))
         .texture("layer0", ResourceLocationHelper.fromNamespaceAndPath(texture.getNamespace(), "item/" + texture.getPath()));
   }

   public ItemModelBuilder handheldItem(Item item) {
      return this.handheldItem(Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item)));
   }

   public ItemModelBuilder handheldItem(ResourceLocation item) {
      return (ItemModelBuilder)((ItemModelBuilder)((ItemModelBuilder)this.getBuilder(item.toString())).parent(new UncheckedModelFile("item/handheld")))
         .texture("layer0", ResourceLocationHelper.fromNamespaceAndPath(item.getNamespace(), "item/" + item.getPath()));
   }

   public ItemModelBuilder spawnEgg(Item item) {
      return this.spawnEgg(Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item)));
   }

   public ItemModelBuilder spawnEgg(ResourceLocation item) {
      return (ItemModelBuilder)((ItemModelBuilder)this.getBuilder(item.toString())).parent(new UncheckedModelFile("minecraft:item/template_spawn_egg"));
   }

   public ItemModelBuilder builtInItem(Item item, Block texture) {
      return this.builtInItem(item, texture, this.mcLoc("builtin/entity"));
   }

   public ItemModelBuilder builtInItem(Item item, Block texture, ResourceLocation parent) {
      return (ItemModelBuilder)((ItemModelBuilder)((ItemModelBuilder)this.getBuilder(this.name(item))).parent(this.getExistingFile(parent)))
         .texture("particle", this.provider.blockTexture(texture));
   }

   protected final void registerModels() {
   }

   public final CompletableFuture<?> run(CachedOutput cache) {
      return CompletableFuture.completedFuture(null);
   }
}
