/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package mezz.jei.library.load.registration;

import mezz.jei.api.ingredients.IIngredientTypeWithSubtypes;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.common.util.ErrorUtil;
import mezz.jei.library.ingredients.subtypes.SubtypeInterpreters;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SubtypeRegistration
implements ISubtypeRegistration {
    private static final Logger LOGGER = LogManager.getLogger();
    private final SubtypeInterpreters interpreters = new SubtypeInterpreters();

    @Override
    public <B, I> void registerSubtypeInterpreter(IIngredientTypeWithSubtypes<B, I> type, B base, ISubtypeInterpreter<I> interpreter) {
        ErrorUtil.checkNotNull(type, "type");
        ErrorUtil.checkNotNull(base, "base");
        ErrorUtil.checkNotNull(interpreter, "interpreter");
        Class<B> ingredientBaseClass = type.getIngredientBaseClass();
        if (!ingredientBaseClass.isInstance(base)) {
            throw new IllegalArgumentException(String.format("base (%s) must be an instance of %s", base.getClass(), ingredientBaseClass));
        }
        if (!this.interpreters.addInterpreter(type, base, interpreter)) {
            LOGGER.error("An interpreter is already registered for this: {}", base, (Object)new IllegalArgumentException());
        }
    }

    @Override
    public <B, I> void registerSubtypeInterpreter(IIngredientTypeWithSubtypes<B, I> type, B base, IIngredientSubtypeInterpreter<I> interpreter) {
        ErrorUtil.checkNotNull(type, "type");
        ErrorUtil.checkNotNull(base, "base");
        ErrorUtil.checkNotNull(interpreter, "interpreter");
        Class<B> ingredientBaseClass = type.getIngredientBaseClass();
        if (!ingredientBaseClass.isInstance(base)) {
            throw new IllegalArgumentException(String.format("base (%s) must be an instance of %s", base.getClass(), ingredientBaseClass));
        }
        if (!this.interpreters.addInterpreter(type, base, interpreter)) {
            LOGGER.error("An interpreter is already registered for this: {}", base, (Object)new IllegalArgumentException());
        }
    }

    public SubtypeInterpreters getInterpreters() {
        return this.interpreters;
    }
}

