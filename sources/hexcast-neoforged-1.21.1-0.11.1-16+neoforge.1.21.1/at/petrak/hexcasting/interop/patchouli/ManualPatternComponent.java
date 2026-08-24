package at.petrak.hexcasting.interop.patchouli;

import at.petrak.hexcasting.api.casting.math.HexCoord;
import at.petrak.hexcasting.api.casting.math.HexDir;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;
import net.minecraft.core.HolderLookup.Provider;
import vazkii.patchouli.api.IVariable;

public class ManualPatternComponent extends AbstractPatternComponent {
   @SerializedName("patterns")
   public String patternsRaw;
   @SerializedName("stroke_order")
   public String strokeOrderRaw;
   protected transient boolean strokeOrder;
   protected transient Provider registries;

   @Override
   public List<Pair<HexPattern, HexCoord>> getPatterns(UnaryOperator<IVariable> lookup) {
      this.strokeOrder = lookup.apply(IVariable.wrap(this.strokeOrderRaw, this.registries)).asBoolean(true);
      List<IVariable> patsRaw = lookup.apply(IVariable.wrap(this.patternsRaw, this.registries)).asListOrSingleton(this.registries);
      ArrayList<Pair<HexPattern, HexCoord>> out = new ArrayList<>();

      for (IVariable ivar : patsRaw) {
         JsonElement json = ivar.unwrap();
         AbstractPatternComponent.RawPattern raw = (AbstractPatternComponent.RawPattern)new Gson().fromJson(json, AbstractPatternComponent.RawPattern.class);
         HexDir dir = HexDir.fromString(raw.startdir);
         HexPattern pat = HexPattern.fromAngles(raw.signature, dir);
         HexCoord origin = new HexCoord(raw.q, raw.r);
         out.add(new Pair(pat, origin));
      }

      return out;
   }

   @Override
   public boolean showStrokeOrder() {
      return this.strokeOrder;
   }

   @Override
   public void onVariablesAvailable(UnaryOperator<IVariable> lookup, Provider registries) {
      this.registries = registries;
      this.strokeOrder = IVariable.wrap(this.strokeOrderRaw, registries).asBoolean(true);
      super.onVariablesAvailable(lookup, registries);
   }
}
