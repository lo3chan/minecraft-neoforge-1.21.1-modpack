package at.petrak.hexcasting.interop.patchouli;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.level.Level;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

public class PatternProcessor implements IComponentProcessor {
   private String translationKey;

   public void setup(Level level, IVariableProvider vars) {
      if (vars.has("header")) {
         this.translationKey = vars.get("header", level.registryAccess()).asString();
      } else {
         IVariable key = vars.get("op_id", level.registryAccess());
         String opName = key.asString();
         String prefix = "hexcasting.action.";
         boolean hasOverride = I18n.exists(prefix + "book." + opName);
         this.translationKey = prefix + (hasOverride ? "book." : "") + opName;
      }
   }

   public IVariable process(Level level, String key) {
      return key.equals("translation_key") ? IVariable.wrap(this.translationKey) : null;
   }
}
