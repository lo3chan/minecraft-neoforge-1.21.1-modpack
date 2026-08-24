package vazkii.patchouli.client.base;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.item.ItemModBook;

public class BookModel implements BakedModel {
   private final BakedModel original;
   private final ItemOverrides itemHandler;

   public BookModel(BakedModel original, final Function<ResourceLocation, BakedModel> modelGetter) {
      this.original = original;
      this.itemHandler = new ItemOverrides(BookModel.DummyModelBaker.INSTANCE, null, Collections.emptyList()) {
         public BakedModel resolve(@NotNull BakedModel original, @NotNull ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {
            Book book = ItemModBook.getBook(stack);
            return book != null ? modelGetter.apply(book.model) : original;
         }
      };
   }

   @NotNull
   public ItemOverrides getOverrides() {
      return this.itemHandler;
   }

   @NotNull
   public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand) {
      return this.original.getQuads(state, side, rand);
   }

   public boolean useAmbientOcclusion() {
      return this.original.useAmbientOcclusion();
   }

   public boolean isGui3d() {
      return this.original.isGui3d();
   }

   public boolean usesBlockLight() {
      return this.original.usesBlockLight();
   }

   public boolean isCustomRenderer() {
      return this.original.isCustomRenderer();
   }

   @NotNull
   public TextureAtlasSprite getParticleIcon() {
      return this.original.getParticleIcon();
   }

   public ItemTransforms getTransforms() {
      return this.original.getTransforms();
   }

   private static class DummyModelBaker implements ModelBaker {
      static ModelBaker INSTANCE = new BookModel.DummyModelBaker();

      public Function<Material, TextureAtlasSprite> getModelTextureGetter() {
         return null;
      }

      public BakedModel bake(ResourceLocation location, ModelState state, Function<Material, TextureAtlasSprite> sprites) {
         return null;
      }

      public BakedModel bakeUncached(UnbakedModel model, ModelState state, Function<Material, TextureAtlasSprite> sprites) {
         return null;
      }

      public UnbakedModel getTopLevelModel(ModelResourceLocation location) {
         return null;
      }

      public UnbakedModel getModel(ResourceLocation resourceLocation) {
         return null;
      }

      @Nullable
      public BakedModel bake(ResourceLocation resourceLocation, ModelState modelState) {
         return null;
      }
   }
}
