package mezz.jei.gui.overlay.ingredients;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Stream;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.ingredients.rendering.BatchRenderElement;
import mezz.jei.api.runtime.IEditModeConfig;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.common.Internal;
import mezz.jei.common.collect.ListMultiMap;
import mezz.jei.common.gui.elements.OffsetDrawable;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.common.util.SafeIngredientUtil;
import mezz.jei.gui.overlay.elements.IElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;

public class IngredientListRenderer {
   private static final int BLACKLIST_COLOR = -570490880;
   private static final int WILDCARD_BLACKLIST_COLOR = -570448640;
   private final List<IngredientListSlot> slots = new ArrayList<>();
   private final ListMultiMap<IIngredientType<?>, BatchRenderElement<?>> renderElementsByType = new ListMultiMap<>();
   private final List<IDrawable> renderOverlays = new ArrayList<>();
   private final IIngredientManager ingredientManager;
   private final boolean searchable;
   private int blocked = 0;

   public IngredientListRenderer(IIngredientManager ingredientManager, boolean searchable) {
      this.ingredientManager = ingredientManager;
      this.searchable = searchable;
   }

   public void clear() {
      this.slots.clear();
      this.renderElementsByType.clear();
      this.renderOverlays.clear();
      this.blocked = 0;
   }

   public int size() {
      return this.slots.size() - this.blocked;
   }

   public void add(IngredientListSlot ingredientListSlot) {
      this.slots.add(ingredientListSlot);
      this.addRenderElement(ingredientListSlot);
   }

   private void addRenderElement(IngredientListSlot ingredientListSlot) {
      ingredientListSlot.getOptionalElement().ifPresent(element -> {
         ITypedIngredient<?> typedIngredient = element.getTypedIngredient();
         IIngredientType<?> ingredientType = typedIngredient.getType();
         ImmutableRect2i renderArea = ingredientListSlot.getRenderArea();
         BatchRenderElement<?> batchRenderElement = new BatchRenderElement<>(typedIngredient.getIngredient(), renderArea.x(), renderArea.y());
         this.renderElementsByType.put(ingredientType, batchRenderElement);
         IDrawable renderOverlay = element.createRenderOverlay();
         if (renderOverlay != null) {
            this.renderOverlays.add(OffsetDrawable.create(renderOverlay, renderArea.x(), renderArea.y()));
         }
      });
   }

   public Stream<IngredientListSlot> getSlots() {
      return this.slots.stream().filter(s -> !s.isBlocked());
   }

   public void set(int startIndex, List<IElement<?>> ingredientList) {
      this.blocked = 0;
      this.renderElementsByType.clear();
      this.renderOverlays.clear();
      Iterator<IElement<?>> elementIterator = ingredientList.listIterator(startIndex);

      for (IngredientListSlot ingredientListSlot : this.slots) {
         if (ingredientListSlot.isBlocked()) {
            ingredientListSlot.clear();
            this.blocked++;
         } else if (!elementIterator.hasNext()) {
            ingredientListSlot.clear();
         } else {
            IElement<?> element = elementIterator.next();

            while (!element.isVisible() && elementIterator.hasNext()) {
               element = elementIterator.next();
            }

            if (element.isVisible()) {
               ingredientListSlot.setElement(element);
               this.addRenderElement(ingredientListSlot);
            } else {
               ingredientListSlot.clear();
            }
         }
      }
   }

   public void render(GuiGraphics guiGraphics) {
      if (this.searchable && Internal.getClientToggleState().isEditModeEnabled()) {
         this.renderEditMode(guiGraphics);
      }

      for (Entry<IIngredientType<?>, List<BatchRenderElement<?>>> entry : this.renderElementsByType.entrySet()) {
         this.renderBatch(guiGraphics, entry);
      }

      for (IDrawable overlay : this.renderOverlays) {
         overlay.draw(guiGraphics);
      }
   }

   private <T> void renderBatch(GuiGraphics guiGraphics, Entry<IIngredientType<?>, List<BatchRenderElement<?>>> entry) {
      IIngredientType<T> type = (IIngredientType<T>)entry.getKey();
      IIngredientRenderer<T> ingredientRenderer = this.ingredientManager.getIngredientRenderer(type);
      List<BatchRenderElement<T>> elements = (List<BatchRenderElement<T>>)entry.getValue();
      SafeIngredientUtil.renderBatch(guiGraphics, type, ingredientRenderer, elements);
   }

   private void renderEditMode(GuiGraphics guiGraphics) {
      IEditModeConfig editModeConfig = Internal.getJeiRuntime().getEditModeConfig();

      for (IngredientListSlot slot : this.slots) {
         slot.getOptionalElement()
            .ifPresent(element -> renderEditMode(guiGraphics, slot.getArea(), slot.getPadding(), element.getTypedIngredient(), editModeConfig));
      }

      RenderSystem.enableBlend();
   }

   private static <T> void renderEditMode(
      GuiGraphics guiGraphics, ImmutableRect2i area, int padding, ITypedIngredient<T> typedIngredient, IEditModeConfig config
   ) {
      Set<IEditModeConfig.HideMode> hideModes = config.getIngredientHiddenUsingConfigFile(typedIngredient);
      if (!hideModes.isEmpty()) {
         boolean wildcard = hideModes.contains(IEditModeConfig.HideMode.WILDCARD);
         boolean single = hideModes.contains(IEditModeConfig.HideMode.SINGLE);
         if (wildcard && single) {
            guiGraphics.fill(
               RenderType.guiOverlay(), area.getX() + padding, area.getY() + padding, area.getX() + 16 + padding, area.getY() + 8 + padding, -570448640
            );
            guiGraphics.fill(
               RenderType.guiOverlay(), area.getX() + padding, area.getY() + 8 + padding, area.getX() + 16 + padding, area.getY() + 16 + padding, -570490880
            );
         } else if (wildcard) {
            guiGraphics.fill(
               RenderType.guiOverlay(), area.getX() + padding, area.getY() + padding, area.getX() + 16 + padding, area.getY() + 16 + padding, -570448640
            );
         } else if (single) {
            guiGraphics.fill(
               RenderType.guiOverlay(), area.getX() + padding, area.getY() + padding, area.getX() + 16 + padding, area.getY() + 16 + padding, -570490880
            );
         }

         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      }
   }
}
