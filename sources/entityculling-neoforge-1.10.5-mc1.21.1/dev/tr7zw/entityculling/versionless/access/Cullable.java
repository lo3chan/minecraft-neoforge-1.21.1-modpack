package dev.tr7zw.entityculling.versionless.access;

public interface Cullable {
   void setTimeout();

   boolean isForcedVisible();

   void setCulled(boolean var1);

   boolean isCulled();

   void setOutOfCamera(boolean var1);

   boolean isOutOfCamera();
}
