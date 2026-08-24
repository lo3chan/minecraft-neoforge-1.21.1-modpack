package vazkii.psi.api.spell;

import net.minecraft.network.chat.Component;

public class StatLabel {
   private StringBuilder tooltip;
   private byte order;

   public StatLabel(double value) {
      this.init();
      this.append(this.formatDouble(value));
   }

   public StatLabel(String value, boolean translate) {
      this.init();
      this.append(translate ? this.translate(value) : value);
   }

   public StatLabel(String value) {
      this.init();
      this.append(value);
   }

   private void init() {
      this.tooltip = new StringBuilder();
      this.order = 3;
   }

   @Override
   public String toString() {
      return this.tooltip.toString();
   }

   public StatLabel plus() {
      this.order = 1;
      return this.append(" + ");
   }

   public StatLabel add(String value, boolean translate) {
      return this.plus().append(translate ? this.translate(value) : value);
   }

   public StatLabel add(String value) {
      return this.plus().append(value);
   }

   public StatLabel add(StatLabel other) {
      return this.plus().append(other);
   }

   public StatLabel add(double value) {
      return this.plus().append(this.formatDouble(value));
   }

   public StatLabel minus() {
      this.order = 1;
      return this.append(" - ");
   }

   public StatLabel sub(String value, boolean translate) {
      return this.minus().append(translate ? this.translate(value) : value);
   }

   public StatLabel sub(String value) {
      return this.minus().append(value);
   }

   public StatLabel sub(StatLabel other) {
      return this.minus().append(other);
   }

   public StatLabel sub(double value) {
      return this.minus().append(this.formatDouble(value));
   }

   public StatLabel times() {
      if (this.order < 2) {
         this.parenthesize();
      }

      this.order = 2;
      return this.append(" × ");
   }

   public StatLabel mul(String value, boolean translate) {
      return this.times().append(translate ? this.translate(value) : value);
   }

   public StatLabel mul(String value) {
      return this.times().append(value);
   }

   public StatLabel mul(StatLabel other) {
      return this.times().append(other);
   }

   public StatLabel mul(double value) {
      return this.times().append(this.formatDouble(value));
   }

   public StatLabel div() {
      if (this.order < 2) {
         this.parenthesize();
      }

      this.order = 2;
      return this.append(" ÷ ");
   }

   public StatLabel div(String value, boolean translate) {
      return this.div().append(translate ? this.translate(value) : value);
   }

   public StatLabel div(String value) {
      return this.div().append(value);
   }

   public StatLabel div(StatLabel other) {
      return this.div().append(other);
   }

   public StatLabel div(double value) {
      return this.div().append(this.formatDouble(value));
   }

   public StatLabel floor() {
      return this.prepend("⌊").append("⌋");
   }

   public StatLabel ceil() {
      return this.prepend("⌈").append("⌉");
   }

   public StatLabel round() {
      return this.prepend("⌊").append("⌉");
   }

   public StatLabel min(double value) {
      return this.prepend("min(").append(", ").append(this.formatDouble(value)).append(")");
   }

   public StatLabel min(StatLabel other) {
      this.prepend("min(").append(", ").append(other).append(")");
      return this;
   }

   public StatLabel max(double value) {
      return this.prepend("max(").append(", ").append(this.formatDouble(value)).append(")");
   }

   public StatLabel max(StatLabel other) {
      return this.prepend("max(").append(", ").append(other).append(")");
   }

   public StatLabel abs() {
      return this.prepend("|").append("|");
   }

   public StatLabel parenthesize() {
      this.order = 3;
      return this.prepend("(").append(")");
   }

   public StatLabel square() {
      return this.append("²");
   }

   public StatLabel cube() {
      return this.append("³");
   }

   public StatLabel pow(String str, boolean translate) {
      return this.append("^(").append(translate ? this.translate(str) : str).append(")");
   }

   public StatLabel pow(String str) {
      return this.append("^(").append(str).append(")");
   }

   public StatLabel pow(StatLabel other) {
      return this.append("^(").append(other).append(")");
   }

   public StatLabel pow(double value) {
      return this.append("^(").append(this.formatDouble(value)).append(")");
   }

   public StatLabel sqrt() {
      return this.parenthesize().prepend("√");
   }

   public StatLabel prepend(String str) {
      this.tooltip.insert(0, str);
      return this;
   }

   public StatLabel prepend(String str, boolean translate) {
      return translate ? this.prepend(this.translate(str)) : this.prepend(str);
   }

   public StatLabel prepend(char c) {
      this.tooltip.insert(0, c);
      return this;
   }

   public StatLabel prepend(double d) {
      return this.prepend(this.formatDouble(d));
   }

   public StatLabel prepend(StatLabel other) {
      this.tooltip.insert(0, other.tooltip);
      return this;
   }

   public StatLabel append(String str) {
      this.tooltip.append(str);
      return this;
   }

   public StatLabel append(String str, boolean translate) {
      return translate ? this.append(this.translate(str)) : this.append(str);
   }

   public StatLabel append(char c) {
      this.tooltip.append(c);
      return this;
   }

   public StatLabel append(StatLabel other) {
      this.tooltip.append((CharSequence)other.tooltip);
      return this;
   }

   public StatLabel append(double d) {
      return this.append(this.formatDouble(d));
   }

   private String translate(String str) {
      return Component.translatable(str).getString();
   }

   private String formatDouble(double value) {
      String s = String.valueOf(value);
      if (s.endsWith(".0")) {
         s = s.substring(0, s.length() - 2);
      }

      return s;
   }
}
