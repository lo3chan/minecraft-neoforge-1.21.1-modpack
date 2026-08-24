package fuzs.puzzleslib.api.client.event.v1;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResultHolder;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;

public final class ModelEvents {
   public static final EventInvoker<ModelEvents.ModifyUnbakedModel> MODIFY_UNBAKED_MODEL = EventInvoker.lookup(ModelEvents.ModifyUnbakedModel.class);
   public static final EventInvoker<ModelEvents.ModifyBakedModel> MODIFY_BAKED_MODEL = EventInvoker.lookup(ModelEvents.ModifyBakedModel.class);
   public static final EventInvoker<ModelEvents.AddAdditionalBakedModel> ADD_ADDITIONAL_BAKED_MODEL = EventInvoker.lookup(
      ModelEvents.AddAdditionalBakedModel.class
   );
   public static final EventInvoker<ModelEvents.CompleteModelLoading> COMPLETE_MODEL_LOADING = EventInvoker.lookup(ModelEvents.CompleteModelLoading.class);

   private ModelEvents() {
   }

   @FunctionalInterface
   public interface AddAdditionalBakedModel {
      void onAddAdditionalBakedModel(
         BiConsumer<ModelResourceLocation, BakedModel> var1, Function<ModelResourceLocation, BakedModel> var2, Supplier<ModelBaker> var3
      );
   }

   @FunctionalInterface
   public interface CompleteModelLoading {
      void onCompleteModelLoading(Supplier<ModelManager> var1, Supplier<ModelBakery> var2);
   }

   @FunctionalInterface
   public interface ModifyBakedModel {
      EventResultHolder<BakedModel> onModifyBakedModel(
         ModelResourceLocation var1,
         Supplier<BakedModel> var2,
         Supplier<ModelBaker> var3,
         Function<ModelResourceLocation, BakedModel> var4,
         BiConsumer<ModelResourceLocation, BakedModel> var5
      );
   }

   @FunctionalInterface
   public interface ModifyUnbakedModel {
      EventResultHolder<UnbakedModel> onModifyUnbakedModel(
         ModelResourceLocation var1,
         Supplier<UnbakedModel> var2,
         Function<ModelResourceLocation, UnbakedModel> var3,
         BiConsumer<ResourceLocation, UnbakedModel> var4
      );
   }
}
