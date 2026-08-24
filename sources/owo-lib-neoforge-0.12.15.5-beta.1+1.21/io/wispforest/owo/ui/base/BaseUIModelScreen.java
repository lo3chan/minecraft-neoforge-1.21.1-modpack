package io.wispforest.owo.ui.base;

import io.wispforest.owo.Owo;
import io.wispforest.owo.ui.core.OwoUIAdapter;
import io.wispforest.owo.ui.core.ParentComponent;
import io.wispforest.owo.ui.parsing.ConfigureHotReloadScreen;
import io.wispforest.owo.ui.parsing.UIModel;
import io.wispforest.owo.ui.parsing.UIModelLoader;
import io.wispforest.owo.ui.util.UIErrorToast;
import java.nio.file.Path;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseUIModelScreen<R extends ParentComponent> extends BaseOwoScreen<R> {
   protected final UIModel model;
   protected final Class<R> rootComponentClass;
   @Nullable
   protected final ResourceLocation modelId;

   protected BaseUIModelScreen(Class<R> rootComponentClass, BaseUIModelScreen.DataSource source) {
      UIModel providedModel = source.get();
      if (providedModel == null) {
         source.reportError();
         this.invalid = true;
      }

      this.rootComponentClass = rootComponentClass;
      this.model = providedModel;
      this.modelId = source instanceof BaseUIModelScreen.DataSource.AssetDataSource assetSource ? assetSource.assetPath() : null;
   }

   protected BaseUIModelScreen(Class<R> rootComponentClass, ResourceLocation modelId) {
      this(rootComponentClass, BaseUIModelScreen.DataSource.asset(modelId));
   }

   @NotNull
   @Override
   protected OwoUIAdapter<R> createAdapter() {
      return this.model.createAdapter(this.rootComponentClass, this);
   }

   @Override
   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (Owo.DEBUG && this.modelId != null && keyCode == 294 && (modifiers & 2) != 0) {
         this.minecraft.setScreen(new ConfigureHotReloadScreen(this.modelId, this));
         return true;
      } else {
         return super.keyPressed(keyCode, scanCode, modifiers);
      }
   }

   public interface DataSource {
      @Nullable
      UIModel get();

      void reportError();

      @Deprecated
      static BaseUIModelScreen.DataSource file(String filePath) {
         return new BaseUIModelScreen.DataSource() {
            @Nullable
            @Override
            public UIModel get() {
               if (!Owo.DEBUG) {
                  throw new IllegalStateException("Debug UI data source must not be used in production");
               } else {
                  return UIModel.load(Path.of(filePath));
               }
            }

            @Override
            public void reportError() {
               UIErrorToast.report("Could not load UI model from file " + filePath);
            }
         };
      }

      static BaseUIModelScreen.DataSource asset(ResourceLocation assetPath) {
         return new BaseUIModelScreen.DataSource.AssetDataSource(assetPath);
      }

      public record AssetDataSource(ResourceLocation assetPath) implements BaseUIModelScreen.DataSource {
         @Nullable
         @Override
         public UIModel get() {
            return UIModelLoader.get(this.assetPath);
         }

         @Override
         public void reportError() {
            UIErrorToast.report("No UI model with id " + this.assetPath + " was found");
         }
      }
   }
}
