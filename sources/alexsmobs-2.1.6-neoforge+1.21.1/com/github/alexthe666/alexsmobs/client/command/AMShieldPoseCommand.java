package com.github.alexthe666.alexsmobs.client.command;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonWriter;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public final class AMShieldPoseCommand {
   private static final Map<String, String[]> MODELS = new LinkedHashMap<>();
   private static final Map<String, String[]> CONTEXTS = new LinkedHashMap<>();
   private static final Map<String, double[][]> ARM = new LinkedHashMap<>();
   private static final int PRIMARY = 1;
   private static final Map<String, String> PRIMARY_OF = new LinkedHashMap<>();
   private static final String MODEL_DIR = "assets/alexsmobs/models/item";
   private static String pose;
   private static String context;
   private static Path repoRoot;
   private static boolean repoRootResolved;

   private AMShieldPoseCommand() {
   }

   public static <S> void register(CommandDispatcher<S> dispatcher, BiConsumer<S, Component> feedback) {
      LiteralArgumentBuilder<S> root = (LiteralArgumentBuilder<S>)LiteralArgumentBuilder.literal("shieldpose").executes(c -> show(c.getSource(), feedback));
      root.then(LiteralArgumentBuilder.literal("show").executes(c -> show(c.getSource(), feedback)));
      root.then(LiteralArgumentBuilder.literal("reload").executes(c -> {
         Minecraft.getInstance().reloadResourcePacks();
         say(c.getSource(), feedback, "reloading resources");
         return 1;
      }));
      LiteralArgumentBuilder<S> poseNode = LiteralArgumentBuilder.literal("pose");

      for (String name : MODELS.keySet()) {
         poseNode.then(LiteralArgumentBuilder.literal(name).executes(c -> {
            pose = name;
            return show(c.getSource(), feedback);
         }));
      }

      root.then(poseNode);
      LiteralArgumentBuilder<S> ctxNode = LiteralArgumentBuilder.literal("ctx");

      for (String name : CONTEXTS.keySet()) {
         ctxNode.then(LiteralArgumentBuilder.literal(name).executes(c -> {
            context = name;
            return show(c.getSource(), feedback);
         }));
      }

      root.then(ctxNode);
      root.then(verbs("set", false, feedback));
      root.then(verbs("nudge", true, feedback));
      dispatcher.register(root);
   }

   private static <S> LiteralArgumentBuilder<S> verbs(String name, boolean relative, BiConsumer<S, Component> feedback) {
      return (LiteralArgumentBuilder<S>)((LiteralArgumentBuilder)((LiteralArgumentBuilder)LiteralArgumentBuilder.literal(name)
               .then(vec3("rot", relative, feedback)))
            .then(vec3("trans", relative, feedback)))
         .then(scalar("scale", relative, feedback));
   }

   private static <S> LiteralArgumentBuilder<S> vec3(String key, boolean relative, BiConsumer<S, Component> feedback) {
      return (LiteralArgumentBuilder<S>)LiteralArgumentBuilder.literal(key)
         .then(
            RequiredArgumentBuilder.argument("x", FloatArgumentType.floatArg())
               .then(
                  RequiredArgumentBuilder.argument("y", FloatArgumentType.floatArg())
                     .then(
                        RequiredArgumentBuilder.argument("z", FloatArgumentType.floatArg())
                           .executes(c -> apply(c, feedback, key, relative, new float[]{f(c, "x"), f(c, "y"), f(c, "z")}))
                     )
               )
         );
   }

   private static <S> LiteralArgumentBuilder<S> scalar(String key, boolean relative, BiConsumer<S, Component> feedback) {
      return (LiteralArgumentBuilder<S>)LiteralArgumentBuilder.literal(key)
         .then(RequiredArgumentBuilder.argument("v", FloatArgumentType.floatArg()).executes(c -> {
            float v = f(c, "v");
            return apply(c, feedback, key, relative, new float[]{v, v, v});
         }));
   }

   private static <S> float f(CommandContext<S> c, String name) {
      return FloatArgumentType.getFloat(c, name);
   }

   private static <S> int apply(CommandContext<S> c, BiConsumer<S, Component> feedback, String key, boolean relative, float[] values) {
      S source = (S)c.getSource();
      Path root = repoRoot();
      if (root == null) {
         say(source, feedback, "no checkout found above the game directory — nothing to edit");
         return 0;
      } else {
         String field = fieldName(key);
         String[] keys = CONTEXTS.get(context);
         JsonObject entry = current(root, pose, keys[0]);
         float[] target = values;
         if (relative) {
            float[] currentValue = get(entry, field);
            target = new float[3];

            for (int i = 0; i < 3; i++) {
               target[i] = currentValue[i] + values[i];
            }
         }

         entry.add(field, array(target));
         entry = ordered(entry);

         int written;
         try {
            written = write(root, pose, keys, entry);
         } catch (IOException var13) {
            say(source, feedback, "write failed: " + var13);
            return 0;
         }

         if (written == 0) {
            say(source, feedback, "no model file carried a display block — nothing written");
            return 0;
         } else {
            Minecraft.getInstance().reloadResourcePacks();
            say(
               source,
               feedback,
               pose + " [" + context + "] " + field + " = " + fmt(target, field) + "  (" + written + " file" + (written == 1 ? "" : "s") + ", reloading)"
            );
            return 1;
         }
      }
   }

   private static <S> int show(S source, BiConsumer<S, Component> feedback) {
      Path root = repoRoot();
      if (root == null) {
         say(source, feedback, "no checkout found above the game directory — this is a dev-only tool");
         return 0;
      } else {
         say(source, feedback, "editing " + pose + " [" + context + "]  (/shieldpose pose … | ctx … to switch)");
         String[] keys = CONTEXTS.get(context);

         for (String p : MODELS.keySet()) {
            JsonObject entry = current(root, p, keys[0]);
            String line = p
               + ": rot "
               + fmt(get(entry, "rotation"), "rotation")
               + "  trans "
               + fmt(get(entry, "translation"), "translation")
               + "  scale "
               + fmt(get(entry, "scale"), "scale");
            say(source, feedback, "  " + line);
         }

         return 1;
      }
   }

   private static String fieldName(String key) {
      switch (key) {
         case "rot":
            return "rotation";
         case "trans":
            return "translation";
         default:
            return "scale";
      }
   }

   private static JsonObject current(Path root, String poseName, String displayKey) {
      JsonObject model = readJson(root.resolve("src/main/resources").resolve("assets/alexsmobs/models/item").resolve(MODELS.get(poseName)[1]));
      if (model != null && model.has("display")) {
         JsonElement entry = model.getAsJsonObject("display").get(displayKey);
         if (entry != null && entry.isJsonObject()) {
            return entry.getAsJsonObject().deepCopy();
         }
      }

      return new JsonObject();
   }

   private static JsonObject ordered(JsonObject entry) {
      JsonObject out = new JsonObject();

      for (String key : new String[]{"rotation", "translation", "scale"}) {
         if (entry.has(key)) {
            out.add(key, entry.get(key));
         }
      }

      return out;
   }

   private static float[] get(JsonObject entry, String field) {
      float[] fallback = field.equals("scale") ? new float[]{1.0F, 1.0F, 1.0F} : new float[]{0.0F, 0.0F, 0.0F};
      JsonElement value = entry.get(field);
      if (value == null) {
         return fallback;
      } else if (!value.isJsonArray()) {
         float v = value.getAsFloat();
         return new float[]{v, v, v};
      } else {
         JsonArray array = value.getAsJsonArray();
         float[] out = (float[])fallback.clone();

         for (int i = 0; i < Math.min(3, array.size()); i++) {
            out[i] = array.get(i).getAsFloat();
         }

         return out;
      }
   }

   private static int write(Path root, String poseName, String[] displayKeys, JsonObject entry) throws IOException {
      boolean paired = displayKeys.length == 2;
      int written = 0;

      for (String file : MODELS.get(poseName)) {
         JsonObject[] solved = paired ? solve(entry, file) : null;

         for (Path path : targets(root, file)) {
            JsonObject model = readJson(path);
            if (model != null && model.has("display")) {
               JsonObject display = model.getAsJsonObject("display");
               if (paired) {
                  display.add(displayKeys[0], solved[0].deepCopy());
                  display.add(displayKeys[1], solved[1].deepCopy());
               } else {
                  for (String key : displayKeys) {
                     display.add(key, entry.deepCopy());
                  }
               }

               writeJson(path, model);
               written++;
            }
         }
      }

      return written;
   }

   private static JsonObject[] solve(JsonObject entry, String file) {
      double[] rot = dbl(get(entry, "rotation"));
      double[] trans = dbl(get(entry, "translation"));
      String primary = PRIMARY_OF.get(file);
      double[][] authored = armRotation(primary, true);
      double[] authoredOffset = armOffset(primary, true);
      double[][] world = mul(authored, rotXYZ(rot));
      double[] scaled = new double[]{trans[0] / 16.0, trans[1] / 16.0, trans[2] / 16.0};
      double[] turned = mulVec(authored, scaled);
      double[] position = new double[]{authoredOffset[0] + turned[0], authoredOffset[1] + turned[1], authoredOffset[2] + turned[2]};
      double[][] right = armRotation(file, true);
      double[] rightOffset = armOffset(file, true);
      double[][] left = armRotation(file, false);
      double[] rotRight = eulerXYZ(mul(transpose(right), world));
      double[] rotLeft = eulerXYZ(mul(transpose(left), world));
      rotLeft = new double[]{rotLeft[0], -rotLeft[1], -rotLeft[2]};
      double[] delta = new double[]{position[0] - rightOffset[0], position[1] - rightOffset[1], position[2] - rightOffset[2]};
      double[] transRight = mulVec(transpose(right), delta);

      for (int i = 0; i < 3; i++) {
         transRight[i] *= 16.0;
      }

      return new JsonObject[]{solved(entry, rotRight, transRight), solved(entry, rotLeft, transRight)};
   }

   private static JsonObject solved(JsonObject entry, double[] rotation, double[] translation) {
      JsonObject out = new JsonObject();
      out.add("rotation", array(rotation));
      out.add("translation", array(translation));
      if (entry.has("scale")) {
         out.add("scale", entry.get("scale").deepCopy());
      }

      return out;
   }

   private static double[][] armRotation(String file, boolean rightHand) {
      double[][] arm = ARM.get(file);
      if (arm == null) {
         return rotXYZ(new double[]{0.0, 0.0, 0.0});
      } else {
         int invert = rightHand ? 1 : -1;
         return mul(mul(rotationX(arm[0][0]), rotationY(invert * arm[0][1])), rotationZ(invert * arm[0][2]));
      }
   }

   private static double[] armOffset(String file, boolean rightHand) {
      double[][] arm = ARM.get(file);
      return arm == null ? new double[]{0.0, 0.0, 0.0} : new double[]{(rightHand ? 1 : -1) * arm[1][0], arm[1][1], arm[1][2]};
   }

   private static double[][] rotXYZ(double[] r) {
      return mul(mul(rotationX(r[0]), rotationY(r[1])), rotationZ(r[2]));
   }

   private static double[] eulerXYZ(double[][] m) {
      double b = Math.asin(Math.max(-1.0, Math.min(1.0, m[0][2])));
      double a;
      double c;
      if (Math.abs(m[0][2]) < 0.999999) {
         a = Math.atan2(-m[1][2], m[2][2]);
         c = Math.atan2(-m[0][1], m[0][0]);
      } else {
         a = Math.atan2(m[2][1], m[1][1]);
         c = 0.0;
      }

      return new double[]{Math.toDegrees(a), Math.toDegrees(b), Math.toDegrees(c)};
   }

   private static double[][] rotationX(double deg) {
      double a = Math.toRadians(deg);
      double c = Math.cos(a);
      double s = Math.sin(a);
      return new double[][]{{1.0, 0.0, 0.0}, {0.0, c, -s}, {0.0, s, c}};
   }

   private static double[][] rotationY(double deg) {
      double a = Math.toRadians(deg);
      double c = Math.cos(a);
      double s = Math.sin(a);
      return new double[][]{{c, 0.0, s}, {0.0, 1.0, 0.0}, {-s, 0.0, c}};
   }

   private static double[][] rotationZ(double deg) {
      double a = Math.toRadians(deg);
      double c = Math.cos(a);
      double s = Math.sin(a);
      return new double[][]{{c, -s, 0.0}, {s, c, 0.0}, {0.0, 0.0, 1.0}};
   }

   private static double[][] mul(double[][] a, double[][] b) {
      double[][] out = new double[3][3];

      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 3; j++) {
            out[i][j] = a[i][0] * b[0][j] + a[i][1] * b[1][j] + a[i][2] * b[2][j];
         }
      }

      return out;
   }

   private static double[][] transpose(double[][] m) {
      double[][] out = new double[3][3];

      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 3; j++) {
            out[i][j] = m[j][i];
         }
      }

      return out;
   }

   private static double[] mulVec(double[][] m, double[] v) {
      double[] out = new double[3];

      for (int i = 0; i < 3; i++) {
         out[i] = m[i][0] * v[0] + m[i][1] * v[1] + m[i][2] * v[2];
      }

      return out;
   }

   private static double[] dbl(float[] v) {
      return new double[]{v[0], v[1], v[2]};
   }

   private static List<Path> targets(Path root, String file) {
      List<Path> out = new ArrayList<>();
      out.add(root.resolve("src/main/resources").resolve("assets/alexsmobs/models/item").resolve(file));
      Path versions = root.resolve("versions");
      if (Files.isDirectory(versions)) {
         try (DirectoryStream<Path> nodes = Files.newDirectoryStream(versions)) {
            for (Path node : nodes) {
               out.add(node.resolve("build/resources/main").resolve("assets/alexsmobs/models/item").resolve(file));
            }
         } catch (IOException var9) {
         }
      }

      out.removeIf(p -> !Files.isRegularFile(p));
      return out;
   }

   private static JsonObject readJson(Path path) {
      if (!Files.isRegularFile(path)) {
         return null;
      } else {
         try {
            JsonObject var3;
            try (Reader reader = Files.newBufferedReader(path)) {
               JsonElement parsed = JsonParser.parseReader(reader);
               var3 = parsed.isJsonObject() ? parsed.getAsJsonObject() : null;
            }

            return var3;
         } catch (RuntimeException | IOException var6) {
            return null;
         }
      }
   }

   private static void writeJson(Path path, JsonObject model) throws IOException {
      try (Writer out = Files.newBufferedWriter(path)) {
         JsonWriter json = new JsonWriter(out);
         json.setIndent("\t");
         new Gson().toJson(model, json);
         json.flush();
         out.write("\n");
      }
   }

   private static JsonArray array(float[] value) {
      JsonArray out = new JsonArray();

      for (float v : value) {
         out.add(new JsonPrimitive((Number)(v == Math.rint(v) ? (int)v : v)));
      }

      return out;
   }

   private static JsonArray array(double[] value) {
      JsonArray out = new JsonArray();

      for (double v : value) {
         double r = Math.rint(v * 10000.0) / 10000.0;
         out.add(new JsonPrimitive((Number)(r == Math.rint(r) ? (int)r : r)));
      }

      return out;
   }

   private static String fmt(float[] value, String field) {
      return field.equals("scale") ? trim(value[0]) : trim(value[0]) + "," + trim(value[1]) + "," + trim(value[2]);
   }

   private static String trim(float v) {
      return v == Math.rint(v) ? Integer.toString((int)v) : String.format(Locale.ROOT, "%.4f", v).replaceAll("0+$", "");
   }

   public static boolean available() {
      return repoRoot() != null;
   }

   private static Path repoRoot() {
      if (!repoRootResolved) {
         repoRootResolved = true;

         for (Path dir = Minecraft.getInstance().gameDirectory.toPath().toAbsolutePath(); dir != null; dir = dir.getParent()) {
            if (Files.isRegularFile(dir.resolve("stonecutter.properties.toml")) && Files.isRegularFile(dir.resolve("settings.gradle.kts"))) {
               repoRoot = dir;
               break;
            }
         }
      }

      return repoRoot;
   }

   private static <S> void say(S source, BiConsumer<S, Component> feedback, String message) {
      feedback.accept(source, Component.literal("[shieldpose] " + message));
   }

   static {
      ARM.put("shield_of_the_deep_3d_blocking.json", new double[][]{{-102.25, 13.365, 78.05}, {-0.14142136, 0.08, 0.14142136}});
      MODELS.put("normal", new String[]{"shield_of_the_deep_3d.json", "shield_of_the_deep.json"});
      MODELS.put("blocking", new String[]{"shield_of_the_deep_3d_blocking.json", "shield_of_the_deep_blocking.json"});

      for (String[] files : MODELS.values()) {
         for (String file : files) {
            PRIMARY_OF.put(file, files[1]);
         }
      }

      CONTEXTS.put("first", new String[]{"firstperson_righthand", "firstperson_lefthand"});
      CONTEXTS.put("firstright", new String[]{"firstperson_righthand"});
      CONTEXTS.put("firstleft", new String[]{"firstperson_lefthand"});
      CONTEXTS.put("third", new String[]{"thirdperson_righthand"});
      CONTEXTS.put("thirdleft", new String[]{"thirdperson_lefthand"});
      CONTEXTS.put("gui", new String[]{"gui"});
      CONTEXTS.put("ground", new String[]{"ground"});
      CONTEXTS.put("fixed", new String[]{"fixed"});
      CONTEXTS.put("head", new String[]{"head"});
      pose = "blocking";
      context = "first";
   }
}
