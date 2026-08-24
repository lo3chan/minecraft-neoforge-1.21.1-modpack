package com.seibel.distanthorizons.api.methods.events.sharedParameterObjects;

import com.seibel.distanthorizons.api.enums.rendering.EDhApiFogFalloff;
import com.seibel.distanthorizons.api.enums.rendering.EDhApiHeightFogDirection;
import com.seibel.distanthorizons.api.enums.rendering.EDhApiHeightFogMixMode;
import java.awt.Color;

public class DhApiMutableFogRenderParam extends DhApiFogRenderParam {
   public void setFogColor(Color fogColor) {
      this.fogColor = fogColor;
   }

   public void setFarFogFalloff(EDhApiFogFalloff farFogFalloff) {
      this.farFogFalloff = farFogFalloff;
   }

   public void setFarFogStartPercent(float farFogStartPercent) {
      this.farFogStartPercent = farFogStartPercent;
   }

   public void setFarFogEndPercent(float farFogEndPercent) {
      this.farFogEndPercent = farFogEndPercent;
   }

   public void setFarFogMinThickness(float farFogMinThickness) {
      this.farFogMinThickness = farFogMinThickness;
   }

   public void setFarFogMaxThickness(float farFogMaxThickness) {
      this.farFogMaxThickness = farFogMaxThickness;
   }

   public void setFarFogDensity(float farFogDensity) {
      this.farFogDensity = farFogDensity;
   }

   public void setHeightFogFalloff(EDhApiFogFalloff heightFogFalloff) {
      this.heightFogFalloff = heightFogFalloff;
   }

   public void setHeightFogMixingMode(EDhApiHeightFogMixMode heightFogMixingMode) {
      this.heightFogMixingMode = heightFogMixingMode;
   }

   public void setHeightFogDirection(EDhApiHeightFogDirection heightFogDirection) {
      this.heightFogDirection = heightFogDirection;
   }

   public void setHeightFogBaseHeight(float heightFogBaseHeight) {
      this.heightFogBaseHeight = heightFogBaseHeight;
   }

   public void setHeightFogStartPercent(float heightFogStartPercent) {
      this.heightFogStartPercent = heightFogStartPercent;
   }

   public void setHeightFogEndPercent(float heightFogEnd) {
      this.heightFogEndPercent = heightFogEnd;
   }

   public void setHeightFogMinThickness(float heightFogMinThickness) {
      this.heightFogMinThickness = heightFogMinThickness;
   }

   public void setHeightFogMaxThickness(float heightFogMaxThickness) {
      this.heightFogMaxThickness = heightFogMaxThickness;
   }

   public void setHeightFogDensity(float heightFogDensity) {
      this.heightFogDensity = heightFogDensity;
   }

   public DhApiMutableFogRenderParam(DhApiFogRenderParam parent) {
      super(parent);
   }

   public DhApiMutableFogRenderParam copy() {
      return new DhApiMutableFogRenderParam(this);
   }
}
