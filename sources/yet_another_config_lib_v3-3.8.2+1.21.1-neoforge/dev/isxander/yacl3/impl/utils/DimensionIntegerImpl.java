package dev.isxander.yacl3.impl.utils;

import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.api.utils.MutableDimension;

public class DimensionIntegerImpl implements MutableDimension<Integer> {
   private int x;
   private int y;
   private int width;
   private int height;

   public DimensionIntegerImpl(int x, int y, int width, int height) {
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
   }

   public Integer x() {
      return this.x;
   }

   public Integer y() {
      return this.y;
   }

   public Integer width() {
      return this.width;
   }

   public Integer height() {
      return this.height;
   }

   public Integer xLimit() {
      return this.x + this.width;
   }

   public Integer yLimit() {
      return this.y + this.height;
   }

   public Integer centerX() {
      return this.x + this.width / 2;
   }

   public Integer centerY() {
      return this.y + this.height / 2;
   }

   public boolean isPointInside(Integer x, Integer y) {
      return x >= this.x() && x <= this.xLimit() && y >= this.y() && y <= this.yLimit();
   }

   @Override
   public MutableDimension<Integer> clone() {
      return new DimensionIntegerImpl(this.x, this.y, this.width, this.height);
   }

   public MutableDimension<Integer> setX(Integer x) {
      this.x = x;
      return this;
   }

   public MutableDimension<Integer> setY(Integer y) {
      this.y = y;
      return this;
   }

   public MutableDimension<Integer> setWidth(Integer width) {
      this.width = width;
      return this;
   }

   public MutableDimension<Integer> setHeight(Integer height) {
      this.height = height;
      return this;
   }

   public Dimension<Integer> withX(Integer x) {
      return this.clone().setX(x);
   }

   public Dimension<Integer> withY(Integer y) {
      return this.clone().setY(y);
   }

   public Dimension<Integer> withWidth(Integer width) {
      return this.clone().setWidth(width);
   }

   public Dimension<Integer> withHeight(Integer height) {
      return this.clone().setHeight(height);
   }

   public MutableDimension<Integer> move(Integer x, Integer y) {
      this.x = this.x + x;
      this.y = this.y + y;
      return this;
   }

   public MutableDimension<Integer> expand(Integer width, Integer height) {
      this.width = this.width + width;
      this.height = this.height + height;
      return this;
   }

   public Dimension<Integer> moved(Integer x, Integer y) {
      return this.clone().move(x, y);
   }

   public Dimension<Integer> expanded(Integer width, Integer height) {
      return this.clone().expand(width, height);
   }
}
