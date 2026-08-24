package software.bernie.geckolib.cache.object;

import java.util.List;
import java.util.Optional;
import software.bernie.geckolib.loading.json.raw.ModelProperties;

public record BakedGeoModel(List<GeoBone> topLevelBones, ModelProperties properties) {
   public Optional<GeoBone> getBone(String name) {
      for (GeoBone bone : this.topLevelBones) {
         GeoBone childBone = this.searchForChildBone(bone, name);
         if (childBone != null) {
            return Optional.of(childBone);
         }
      }

      return Optional.empty();
   }

   public GeoBone searchForChildBone(GeoBone parent, String name) {
      if (parent.getName().equals(name)) {
         return parent;
      } else {
         for (GeoBone bone : parent.getChildBones()) {
            if (bone.getName().equals(name)) {
               return bone;
            }

            GeoBone subChildBone = this.searchForChildBone(bone, name);
            if (subChildBone != null) {
               return subChildBone;
            }
         }

         return null;
      }
   }
}
