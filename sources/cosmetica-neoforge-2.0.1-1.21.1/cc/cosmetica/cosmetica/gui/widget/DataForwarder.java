package cc.cosmetica.cosmetica.gui.widget;

import cc.cosmetica.kupe.api.Context;
import cc.cosmetica.kupe.api.State;
import cc.cosmetica.kupe.api.gui.Component;
import cc.cosmetica.kupe.api.gui.SizedElement;
import cc.cosmetica.kupe.api.maths.Dimensions;
import cc.cosmetica.kupe.api.maths.Margins;
import cc.cosmetica.kupe.api.maths.Vec3;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.apache.commons.lang3.tuple.Triple;

public class DataForwarder extends Component {
   protected DataForwarder() {
   }

   public List<Component> build() {
      return ImmutableList.of();
   }

   public Dimensions intrinsicSize(List<? extends SizedElement> children, Margins padding, Context context) {
      return Dimensions.NONE;
   }

   public Dimensions minimumSize(List<? extends SizedElement> children, Margins padding, int vw, int vh) {
      return Dimensions.NONE;
   }

   public static <U, V> DataForwarder merge(
      State<Triple<Vec3, U, V>> o, State<Float> x, State<Float> y, State<Float> z, State<? extends U> u, State<? extends V> v
   ) {
      final class DataForwarder3 extends DataForwarder {
         @Override
         public List<Component> build() {
            float xValue = (Float)x.acquire(this);
            float yValue = (Float)y.acquire(this);
            float zValue = (Float)z.acquire(this);
            U uValue = (U)u.acquire(this);
            V vValue = (V)v.acquire(this);
            o.set(Triple.of(Vec3.of(xValue, yValue, zValue), uValue, vValue));
            return super.build();
         }
      }

      return new DataForwarder3();
   }
}
