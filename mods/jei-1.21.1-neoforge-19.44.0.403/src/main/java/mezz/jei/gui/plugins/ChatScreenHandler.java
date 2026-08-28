/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.screens.ChatScreen
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.HoverEvent
 *  net.minecraft.network.chat.HoverEvent$Action
 *  net.minecraft.network.chat.HoverEvent$ItemStackInfo
 *  net.minecraft.network.chat.Style
 *  net.minecraft.world.item.ItemStack
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.gui.plugins;

import java.util.Optional;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IClickableIngredientFactory;
import mezz.jei.api.gui.handlers.IGuiProperties;
import mezz.jei.api.gui.handlers.IScreenHandler;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.runtime.IClickableIngredient;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.common.chat.JeiChatItemLinkHover;
import mezz.jei.common.chat.JeiChatItemLinks;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ChatScreenHandler
implements IScreenHandler<ChatScreen> {
    private final IIngredientManager ingredientManager;

    public ChatScreenHandler(IIngredientManager ingredientManager) {
        this.ingredientManager = ingredientManager;
    }

    @Override
    @Nullable
    public IGuiProperties apply(ChatScreen chatScreen) {
        return null;
    }

    @Override
    public Optional<? extends IClickableIngredient<?>> getClickableIngredientUnderMouse(IClickableIngredientFactory factory, ChatScreen chatScreen, double mouseX, double mouseY) {
        return JeiChatItemLinkHover.getHoveredText((Screen)chatScreen, mouseX, mouseY).flatMap(hoveredText -> this.getIngredient(hoveredText.style()).flatMap(typedIngredient -> factory.createBuilder(typedIngredient).buildWithArea(hoveredText.area())));
    }

    private Optional<ITypedIngredient<?>> getIngredient(Style style) {
        return this.getJeiChatLinkIngredient(style).or(() -> this.getVanillaChatItemIngredient(style));
    }

    private Optional<ITypedIngredient<?>> getJeiChatLinkIngredient(Style style) {
        return JeiChatItemLinkHover.getIngredientLink(style).flatMap(link -> JeiChatItemLinks.resolveTypedIngredient(link, this.ingredientManager));
    }

    private Optional<ITypedIngredient<ItemStack>> getVanillaChatItemIngredient(Style style) {
        HoverEvent hoverEvent = style.getHoverEvent();
        if (hoverEvent == null) {
            return Optional.empty();
        }
        HoverEvent.ItemStackInfo itemStackInfo = (HoverEvent.ItemStackInfo)hoverEvent.getValue(HoverEvent.Action.SHOW_ITEM);
        if (itemStackInfo == null) {
            return Optional.empty();
        }
        ItemStack itemStack = itemStackInfo.getItemStack();
        return this.ingredientManager.createTypedIngredient(VanillaTypes.ITEM_STACK, itemStack, false);
    }
}

