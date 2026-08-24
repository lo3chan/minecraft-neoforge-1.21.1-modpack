package fuzs.puzzleslib.neoforge.api.data.v2.client;

import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.neoforge.api.data.v2.client.model.ModItemModelProvider;
import fuzs.puzzleslib.neoforge.api.data.v2.core.NeoForgeDataProviderContext;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile.ExistingModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.apache.commons.lang3.ArrayUtils;

@Deprecated
public abstract class AbstractModelProvider extends BlockStateProvider {
   private final ModItemModelProvider itemModels;

   public AbstractModelProvider(NeoForgeDataProviderContext context) {
      this(context.getModId(), context.getPackOutput(), context.getFileHelper());
   }

   public AbstractModelProvider(String modId, PackOutput packOutput, ExistingFileHelper fileHelper) {
      super(packOutput, modId, fileHelper);
      this.itemModels = new ModItemModelProvider(packOutput, modId, fileHelper, this);
   }

   public final ModItemModelProvider itemModels() {
      return this.itemModels;
   }

   protected abstract void registerStatesAndModels();

   public void simpleExistingBlock(Block block) {
      this.simpleBlock(block, this.existingBlockModel(block));
   }

   public void simpleExistingBlockWithItem(Block block) {
      ExistingModelFile model = this.existingBlockModel(block);
      this.simpleBlock(block, model);
      this.simpleBlockItem(block, model);
   }

   public ExistingModelFile existingBlockModel(Block block) {
      return new ExistingModelFile(this.blockTexture(block), this.models().existingFileHelper);
   }

   public void builtInBlock(Block block, Block particleTexture) {
      this.builtInBlock(block, this.blockTexture(particleTexture));
   }

   public void builtInBlock(Block block, ResourceLocation particleTexture) {
      this.simpleBlock(block, ((BlockModelBuilder)this.models().getBuilder(this.name(block))).texture("particle", particleTexture));
   }

   public void cubeBottomTopBlock(Block block) {
      this.cubeBottomTopBlock(
         block, this.extend(this.blockTexture(block), "_side"), this.extend(this.blockTexture(block), "_bottom"), this.extend(this.blockTexture(block), "_top")
      );
      this.itemModels().withExistingParent(this.name(block), this.extendKey(block, "block"));
   }

   public void cubeBottomTopBlock(Block block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
      this.simpleBlock(block, this.models().cubeBottomTop(this.name(block), side, bottom, top));
   }

   public ResourceLocation key(Block block) {
      return BuiltInRegistries.BLOCK.getKey(block);
   }

   public String name(Block block) {
      return this.key(block).getPath();
   }

   public ResourceLocation extendKey(Block block, String... extensions) {
      ResourceLocation loc = this.key(block);
      extensions = (String[])ArrayUtils.add(extensions, loc.getPath());
      return ResourceLocationHelper.fromNamespaceAndPath(loc.getNamespace(), String.join("/", extensions));
   }

   public ResourceLocation extend(ResourceLocation rl, String suffix) {
      return ResourceLocationHelper.fromNamespaceAndPath(rl.getNamespace(), rl.getPath() + suffix);
   }
}
