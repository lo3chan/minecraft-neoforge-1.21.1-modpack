/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.common.transfer;

import java.util.ArrayList;
import java.util.List;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.common.transfer.TransferOperation;

public class RecipeTransferOperationsResult {
    public final List<TransferOperation> results = new ArrayList<TransferOperation>();
    public final List<IRecipeSlotView> missingItems = new ArrayList<IRecipeSlotView>();
}

