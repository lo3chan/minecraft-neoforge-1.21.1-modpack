package software.bernie.geckolib.animatable.instance;

import com.google.common.base.Suppliers;
import java.util.function.Supplier;
import org.apache.commons.lang3.mutable.MutableObject;
import software.bernie.geckolib.GeckoLibServices;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.constant.dataticket.DataTicket;

public abstract class AnimatableInstanceCache {
   protected final GeoAnimatable animatable;
   protected final Supplier<GeoRenderProvider> renderProvider;

   public AnimatableInstanceCache(GeoAnimatable animatable) {
      this.animatable = animatable;
      this.renderProvider = Suppliers.memoize(() -> {
         if (this.animatable instanceof SingletonGeoAnimatable singleton && GeckoLibServices.PLATFORM.isPhysicalClient()) {
            MutableObject<GeoRenderProvider> consumer = new MutableObject(GeoRenderProvider.DEFAULT);
            singleton.createGeoRenderer(consumer::setValue);
            return (GeoRenderProvider)consumer.getValue();
         } else {
            return null;
         }
      });
   }

   public abstract <T extends GeoAnimatable> AnimatableManager<T> getManagerForId(long var1);

   public <D> void addDataPoint(long uniqueId, DataTicket<D> dataTicket, D data) {
      this.getManagerForId(uniqueId).setData(dataTicket, data);
   }

   public <D> D getDataPoint(long uniqueId, DataTicket<D> dataTicket) {
      return this.getManagerForId(uniqueId).getData(dataTicket);
   }

   public Object getRenderProvider() {
      return this.renderProvider.get();
   }
}
