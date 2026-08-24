package tannyjung.tanshugetrees.procedures;

import java.lang.invoke.StringConcatFactory;
import java.text.DecimalFormat;

public class ConvertSecondToHourTextProcedure {
   public static String execute(double number) {
      String second = "";
      String minute = "";
      String hour = "";
      double save = 0.0;
      double time_convert = 0.0;
      time_convert = (number / 60.0 - Math.floor(number / 60.0)) * 60.0;
      if (time_convert < 10.0) {
         second = "0" + new DecimalFormat("##").format(time_convert);
      } else {
         second = StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0001">(new DecimalFormat("##").format(time_convert));
      }

      time_convert = number / 60.0;
      if (time_convert < 10.0) {
         minute = "0" + new DecimalFormat("##").format(time_convert);
      } else {
         minute = StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0001">(new DecimalFormat("##").format(time_convert));
      }

      time_convert = number / 3600.0;
      if (time_convert < 10.0) {
         hour = "0" + new DecimalFormat("##").format(time_convert);
      } else {
         hour = StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0001">(new DecimalFormat("##").format(time_convert));
      }

      return (hour + "h " + minute + "m " + second + "s").replace(".0", "");
   }
}
