package io.wispforest.owo.ui.base;

import io.wispforest.owo.Owo;
import io.wispforest.owo.ui.core.OwoUIAdapter;
import io.wispforest.owo.ui.core.ParentComponent;
import io.wispforest.owo.ui.parsing.ConfigureHotReloadScreen;
import io.wispforest.owo.ui.parsing.UIModel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseUIModelHandledScreen<R extends ParentComponent, S extends AbstractContainerMenu> extends BaseOwoHandledScreen<R, S> {
   protected final UIModel model;
   protected final Class<R> rootComponentClass;
   @Nullable
   protected final ResourceLocation modelId;

   protected BaseUIModelHandledScreen(S handler, Inventory inventory, Component title, Class<R> rootComponentClass, BaseUIModelScreen.DataSource source) {
      super(handler, inventory, title);
      UIModel providedModel = source.get();
      if (providedModel == null) {
         source.reportError();
         this.invalid = true;
      }

      this.rootComponentClass = rootComponentClass;
      this.model = providedModel;
      this.modelId = source instanceof BaseUIModelScreen.DataSource.AssetDataSource assetSource ? assetSource.assetPath() : null;
   }

   protected BaseUIModelHandledScreen(S handler, Inventory inventory, Component title, Class<R> rootComponentClass, ResourceLocation modelId) {
      this(handler, inventory, title, rootComponentClass, BaseUIModelScreen.DataSource.asset(modelId));
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
}
