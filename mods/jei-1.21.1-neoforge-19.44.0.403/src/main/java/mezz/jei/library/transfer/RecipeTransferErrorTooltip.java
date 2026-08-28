/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 */
package mezz.jei.library.transfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class RecipeTransferErrorTooltip
implements IRecipeTransferError {
    private final List<Component> message = new ArrayList<Component>();

    public RecipeTransferErrorTooltip(Component message) {
        this.message.add((Component)Component.translatable((String)"jei.tooltip.transfer"));
        MutableComponent messageTextComponent = message.copy();
        this.message.add((Component)messageTextComponent.withStyle(ChatFormatting.RED));
    }

    @Override
    public IRecipeTransferError.Type getType() {
        return IRecipeTransferError.Type.USER_FACING;
    }

    @Override
    public List<Component> getTooltip() {
        return Collections.unmodifiableList(this.message);
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip) {
        tooltip.addAll(this.message);
    }
}

