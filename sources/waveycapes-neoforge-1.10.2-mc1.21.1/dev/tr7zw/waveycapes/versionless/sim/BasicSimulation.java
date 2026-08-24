package dev.tr7zw.waveycapes.versionless.sim;

import dev.tr7zw.waveycapes.versionless.util.CapePoint;
import dev.tr7zw.waveycapes.versionless.util.Vector3;
import java.util.List;

public interface BasicSimulation {
   void simulate();

   void setGravityDirection(Vector3 var1);

   float getGravity();

   void setGravity(float var1);

   boolean isSneaking();

   void setSneaking(boolean var1);

   boolean init(int var1);

   boolean empty();

   void applyMovement(Vector3 var1);

   List<CapePoint> getPoints();
}
