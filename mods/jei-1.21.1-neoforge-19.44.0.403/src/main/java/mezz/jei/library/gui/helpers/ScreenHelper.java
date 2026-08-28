/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
 *  net.minecraft.client.renderer.Rect2i
 *  net.minecraft.world.inventory.Slot
 *  net.minecraft.world.item.ItemStack
 */
package mezz.jei.library.gui.helpers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import mezz.jei.api.gui.builder.IClickableIngredientFactory;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.gui.handlers.IGlobalGuiHandler;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiProperties;
import mezz.jei.api.gui.handlers.IScreenHandler;
import mezz.jei.api.runtime.IClickableIngredient;
import mezz.jei.api.runtime.IScreenHelper;
import mezz.jei.common.collect.ListMultiMap;
import mezz.jei.common.ingredients.ITypedIngredientFactory;
import mezz.jei.common.input.ClickableIngredientFactory;
import mezz.jei.common.platform.IPlatformScreenHelper;
import mezz.jei.common.platform.Services;
import mezz.jei.library.gui.GuiContainerHandlers;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ScreenHelper
implements IScreenHelper {
    private final IClickableIngredientFactory clickableIngredientFactory;
    private final List<IGlobalGuiHandler> globalGuiHandlers;
    private final GuiContainerHandlers guiContainerHandlers;
    private final ListMultiMap<Class<?>, IGhostIngredientHandler<?>> ghostIngredientHandlers;
    private final ListMultiMap<Class<?>, IGhostIngredientHandler<?>> cachedGhostIngredientHandlers;
    private final Map<Class<?>, IScreenHandler<?>> guiScreenHandlers;

    public ScreenHelper(ITypedIngredientFactory typedIngredientFactory, List<IGlobalGuiHandler> globalGuiHandlers, GuiContainerHandlers guiContainerHandlers, ListMultiMap<Class<?>, IGhostIngredientHandler<?>> ghostIngredientHandlers, Map<Class<?>, IScreenHandler<?>> guiScreenHandlers) {
        this.clickableIngredientFactory = new ClickableIngredientFactory(typedIngredientFactory);
        this.globalGuiHandlers = globalGuiHandlers;
        this.guiContainerHandlers = guiContainerHandlers;
        this.ghostIngredientHandlers = ghostIngredientHandlers;
        this.guiScreenHandlers = guiScreenHandlers;
        this.cachedGhostIngredientHandlers = new ListMultiMap();
    }

    @Override
    public <T extends Screen> Optional<IGuiProperties> getGuiProperties(T screen) {
        return this.getActiveScreenHandlerStream(screen).map(handler -> handler.apply(screen)).filter(Objects::nonNull).findFirst();
    }

    @Override
    public Stream<Rect2i> getGuiExclusionAreas(Screen screen) {
        Stream<Rect2i> globalGuiHandlerExclusionAreas = this.globalGuiHandlers.stream().map(IGlobalGuiHandler::getGuiExtraAreas).flatMap(Collection::stream);
        if (screen instanceof AbstractContainerScreen) {
            AbstractContainerScreen guiContainer = (AbstractContainerScreen)screen;
            Stream<Rect2i> guiExtraAreas = this.guiContainerHandlers.getGuiExtraAreas(guiContainer);
            return Stream.concat(globalGuiHandlerExclusionAreas, guiExtraAreas);
        }
        return globalGuiHandlerExclusionAreas;
    }

    @Override
    public Stream<IClickableIngredient<?>> getClickableIngredientUnderMouse(Screen screen, double mouseX, double mouseY) {
        return Stream.concat(this.getPluginsIngredientUnderMouse(this.clickableIngredientFactory, screen, mouseX, mouseY), this.getSlotIngredientUnderMouse(this.clickableIngredientFactory, screen).stream());
    }

    private Optional<IClickableIngredient<?>> getSlotIngredientUnderMouse(IClickableIngredientFactory factory, Screen guiScreen) {
        if (!(guiScreen instanceof AbstractContainerScreen)) {
            return Optional.empty();
        }
        AbstractContainerScreen guiContainer = (AbstractContainerScreen)guiScreen;
        IPlatformScreenHelper screenHelper = Services.PLATFORM.getScreenHelper();
        return screenHelper.getSlotUnderMouse(guiContainer).flatMap(slot -> this.getClickedIngredient(factory, (Slot)slot, (AbstractContainerScreen<?>)guiContainer));
    }

    private Stream<IClickableIngredient<?>> getPluginsIngredientUnderMouse(IClickableIngredientFactory factory, Screen guiScreen, double mouseX, double mouseY) {
        Stream<IClickableIngredient<?>> screenIngredients = this.getScreenHandlerIngredients(factory, guiScreen, mouseX, mouseY);
        Stream globalIngredients = this.globalGuiHandlers.stream().map(a -> a.getClickableIngredientUnderMouse(factory, mouseX, mouseY)).flatMap(Optional::stream);
        if (guiScreen instanceof AbstractContainerScreen) {
            AbstractContainerScreen guiContainer = (AbstractContainerScreen)guiScreen;
            Stream<IClickableIngredient<?>> containerIngredients = this.getGuiContainerHandlerIngredients(factory, guiContainer, mouseX, mouseY);
            return Stream.concat(containerIngredients, Stream.concat(screenIngredients, globalIngredients));
        }
        return Stream.concat(screenIngredients, globalIngredients);
    }

    private Optional<IClickableIngredient<ItemStack>> getClickedIngredient(IClickableIngredientFactory factory, Slot slot, AbstractContainerScreen<?> guiContainer) {
        ItemStack stack = slot.getItem();
        IPlatformScreenHelper screenHelper = Services.PLATFORM.getScreenHelper();
        return factory.createBuilder(stack).buildWithArea(screenHelper.getGuiLeft(guiContainer) + slot.x, screenHelper.getGuiTop(guiContainer) + slot.y, 16, 16);
    }

    private <T extends AbstractContainerScreen<?>> Stream<IClickableIngredient<?>> getGuiContainerHandlerIngredients(IClickableIngredientFactory factory, T guiContainer, double mouseX, double mouseY) {
        return this.guiContainerHandlers.getActiveGuiHandlerStream(guiContainer).map(a -> a.getClickableIngredientUnderMouse(factory, guiContainer, mouseX, mouseY)).flatMap(Optional::stream);
    }

    private <T extends Screen> Stream<IClickableIngredient<?>> getScreenHandlerIngredients(IClickableIngredientFactory factory, T guiScreen, double mouseX, double mouseY) {
        return this.getActiveScreenHandlerStream(guiScreen).map(handler -> handler.getClickableIngredientUnderMouse(factory, guiScreen, mouseX, mouseY)).flatMap(Optional::stream);
    }

    private <T extends Screen> Stream<IScreenHandler<T>> getActiveScreenHandlerStream(T guiScreen) {
        Class<?> guiScreenClass = guiScreen.getClass();
        return this.guiScreenHandlers.entrySet().stream().filter(entry -> ((Class)entry.getKey()).isInstance(guiScreen)).sorted(Comparator.comparingInt(entry -> ScreenHelper.getClassDistance(guiScreenClass, (Class)entry.getKey()))).map(Map.Entry::getValue).map(ScreenHelper::castScreenHandler);
    }

    private static int getClassDistance(Class<?> childClass, Class<?> parentClass) {
        int distance = 0;
        for (Class<?> currentClass = childClass; currentClass != null; currentClass = currentClass.getSuperclass()) {
            if (currentClass == parentClass) {
                return distance;
            }
            ++distance;
        }
        return Integer.MAX_VALUE;
    }

    private static <T extends Screen> IScreenHandler<T> castScreenHandler(IScreenHandler<?> handler) {
        return handler;
    }

    @Override
    public <T extends Screen> List<IGhostIngredientHandler<T>> getGhostIngredientHandlers(T guiScreen) {
        Class<?> guiScreenClass = guiScreen.getClass();
        if (this.cachedGhostIngredientHandlers.containsKey(guiScreenClass)) {
            List cached = (List)this.cachedGhostIngredientHandlers.get((Object)guiScreenClass);
            return cached;
        }
        ArrayList results = new ArrayList();
        Collection handlers = this.ghostIngredientHandlers.get((Object)guiScreen.getClass());
        if (!handlers.isEmpty()) {
            results.addAll(handlers);
        }
        for (Map.Entry entry : this.ghostIngredientHandlers.entrySet()) {
            List handlers2;
            Class handledClass = (Class)entry.getKey();
            if (!handledClass.isInstance(guiScreen) || (handlers2 = (List)entry.getValue()).isEmpty()) continue;
            results.addAll(handlers2);
        }
        this.cachedGhostIngredientHandlers.putAll(guiScreen.getClass(), results);
        List castResults = results;
        return castResults;
    }

    @Override
    public Stream<IGuiClickableArea> getGuiClickableArea(AbstractContainerScreen<?> guiContainer, double guiMouseX, double guiMouseY) {
        return this.guiContainerHandlers.getGuiClickableArea(guiContainer, guiMouseX, guiMouseY);
    }
}

