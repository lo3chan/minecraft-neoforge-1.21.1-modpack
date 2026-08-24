package net.mehvahdjukaar.moonlight.api.util.math.kmeans;

import java.util.LinkedList;
import java.util.List;

public class KMeans {
   static final Double PRECISION = 0.01;

   public static <A> LinkedList<IDataEntry<A>> kMeansPP(DataSet<A> data, int K) {
      LinkedList<IDataEntry<A>> centroids = new LinkedList<>();
      centroids.add(data.randomFromDataSet());

      for (int i = 1; i < K; i++) {
         centroids.add(data.calculateWeighedCentroid());
      }

      return centroids;
   }

   public static <A> void kMeans(DataSet<A> data, int K) {
      List<IDataEntry<A>> centroids = kMeansPP(data, K);
      Double SSE = 1.7976931348623157E308;

      while (true) {
         for (IDataEntry<A> point : data.getColorPoints()) {
            float minDist = 3.4028235E38F;

            for (int i = 0; i < centroids.size(); i++) {
               float dist = centroids.get(i).distTo(point);
               if (dist < minDist) {
                  minDist = dist;
                  point.setClusterNo(i);
               }
            }
         }

         centroids = data.recomputeCentroids(K);
         Double newSSE = data.calculateTotalSSE(centroids);
         if (SSE - newSSE <= PRECISION) {
            return;
         }

         SSE = newSSE;
      }
   }
}
