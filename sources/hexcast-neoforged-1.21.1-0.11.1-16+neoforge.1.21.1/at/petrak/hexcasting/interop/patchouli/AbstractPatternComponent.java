package at.petrak.hexcasting.interop.patchouli;

import at.petrak.hexcasting.api.casting.math.HexCoord;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.interop.utils.PatternDrawingUtil;
import at.petrak.hexcasting.interop.utils.PatternEntry;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.function.UnaryOperator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.world.phys.Vec2;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.IVariable;

public abstract class AbstractPatternComponent implements ICustomComponent {
   protected transient int x;
   protected transient int y;
   protected transient float hexSize;
   private transient List<PatternEntry> patterns;
   private transient List<Vec2> zappyPoints;

   public void build(int x, int y, int pagenum) {
      this.x = x == -1 ? 58 : x;
      this.y = y == -1 ? 70 : y;
   }

   public abstract List<Pair<HexPattern, HexCoord>> getPatterns(UnaryOperator<IVariable> var1);

   public abstract boolean showStrokeOrder();

   public void render(GuiGraphics graphics, IComponentRenderContext context, float pticks, int mouseX, int mouseY) {
      PatternDrawingUtil.drawPattern(
         graphics, this.x, this.y, this.patterns, this.zappyPoints, this.showStrokeOrder(), -2963256, -928275806, -936236237, -2133734196
      );
   }

   public void onVariablesAvailable(UnaryOperator<IVariable> lookup, Provider registries) {
      List<Pair<HexPattern, HexCoord>> patterns = this.getPatterns(lookup);
      PatternDrawingUtil.PatternRenderingData data = PatternDrawingUtil.loadPatterns(
         patterns, this.showStrokeOrder() ? 0.2F : 0.0F, this.showStrokeOrder() ? 0.8F : 1.0F
      );
      this.hexSize = data.hexSize();
      this.patterns = data.patterns();
      this.zappyPoints = data.pathfinderDots();
   }

   protected static class RawPattern {
      protected String startdir;
      protected String signature;
      protected int q;
      protected int r;
   }
}
