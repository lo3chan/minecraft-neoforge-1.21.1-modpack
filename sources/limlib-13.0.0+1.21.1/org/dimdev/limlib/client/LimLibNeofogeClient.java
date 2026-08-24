package org.dimdev.limlib.client;

import java.lang.reflect.Field;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ModelEvent.RegisterAdditional;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import org.dimdev.limlib.client.specialmodels.SpecialModelLoadingPlugin;
import org.dimdev.limlib.impl.LimlibClient;
import org.dimdev.limlib.impl.client.LimLibClientSided;
import org.jetbrains.annotations.Nullable;

@Mod(
   value = "limlib",
   dist = {Dist.CLIENT}
)
public class LimLibNeofogeClient extends NeoForgeClientSided<LimLibNeofogeClient, LimlibClient> implements LimLibClientSided<LimLibNeofogeClient> {
   private static final Field BAKED_MODEL_WRAPPER_ORIGINAL_MODEL = findOriginalModelField();
   private boolean specialModelLoadingPluginRegistered;

   public LimLibNeofogeClient(IEventBus bus, ModContainer container) {
      super(bus, container, LimlibClient.INSTANCE);
      bus.addListener(this::registerAdditionalSpecialModels);
   }

   @Override
   public void registerSpecialModelLoadingPlugin() {
      this.specialModelLoadingPluginRegistered = true;
   }

   private void registerAdditionalSpecialModels(RegisterAdditional event) {
      if (this.specialModelLoadingPluginRegistered) {
         SpecialModelLoadingPlugin.prepareModelLoading();

         for (ResourceLocation specialModelId : SpecialModelLoadingPlugin.scanSpecialModelIds(Minecraft.getInstance().getResourceManager())) {
            event.register(ModelResourceLocation.standalone(specialModelId));
         }
      }
   }

   @Nullable
   @Override
   public BakedModel getWrappedBakedModel(BakedModel model) {
      if (!(model instanceof BakedModelWrapper)) {
         return null;
      } else {
         try {
            return BAKED_MODEL_WRAPPER_ORIGINAL_MODEL.get(model) instanceof BakedModel bakedModel ? bakedModel : null;
         } catch (IllegalAccessException var4) {
            throw new RuntimeException("Unable to read wrapped NeoForge baked model", var4);
         }
      }
   }

   private static Field findOriginalModelField() {
      try {
         Field field = BakedModelWrapper.class.getDeclaredField("originalModel");
         field.setAccessible(true);
         return field;
      } catch (NoSuchFieldException var1) {
         throw new ExceptionInInitializerError(var1);
      }
   }
}
