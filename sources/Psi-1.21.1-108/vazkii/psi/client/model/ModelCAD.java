package vazkii.psi.client.model;

import java.util.Collections;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.psi.api.cad.EnumCADComponent;
import vazkii.psi.api.cad.ICAD;
import vazkii.psi.api.cad.ICADAssembly;

@OnlyIn(Dist.CLIENT)
public class ModelCAD implements BakedModel {
   private final ItemOverrides itemHandler = new ItemOverrides() {
      @Nullable
      public BakedModel resolve(@NotNull BakedModel model, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int mode) {
         ICAD cad = (ICAD)stack.getItem();
         ItemStack assemblyStack = cad.getComponentInSlot(stack, EnumCADComponent.ASSEMBLY);
         if (assemblyStack.isEmpty()) {
            return Minecraft.getInstance().getModelManager().getMissingModel();
         } else {
            ResourceLocation cadModel = ((ICADAssembly)assemblyStack.getItem()).getCADModel(assemblyStack, stack);
            return Minecraft.getInstance().getModelManager().getModel(ModelResourceLocation.standalone(cadModel));
         }
      }
   };

   @Deprecated
   @NotNull
   public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource random) {
      return Collections.emptyList();
   }

   @NotNull
   public List<BakedQuad> getQuads(
      @Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData data, @Nullable RenderType renderType
   ) {
      return Collections.emptyList();
   }

   public boolean useAmbientOcclusion() {
      return true;
   }

   public boolean isGui3d() {
      return true;
   }

   public boolean usesBlockLight() {
      return true;
   }

   public boolean isCustomRenderer() {
      return false;
   }

   @Deprecated
   @NotNull
   public TextureAtlasSprite getParticleIcon() {
      return this.getParticleIcon(ModelData.EMPTY);
   }

   @NotNull
   public TextureAtlasSprite getParticleIcon(@NotNull ModelData data) {
      return (TextureAtlasSprite)Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(MissingTextureAtlasSprite.getLocation());
   }

   @NotNull
   public ItemOverrides getOverrides() {
      return this.itemHandler;
   }
}
