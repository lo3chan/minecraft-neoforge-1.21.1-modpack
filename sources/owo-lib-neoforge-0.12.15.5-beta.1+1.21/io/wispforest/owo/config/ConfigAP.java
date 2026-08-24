package io.wispforest.owo.config;

import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Hook;
import io.wispforest.owo.config.annotation.Nest;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;
import javax.tools.Diagnostic.Kind;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

@SupportedAnnotationTypes({"io.wispforest.owo.config.annotation.Config"})
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@Internal
public class ConfigAP extends AbstractProcessor {
   private static final String WRAPPER_TEMPLATE = "package {package};\n\nimport blue.endless.jankson.Jankson;\nimport io.wispforest.owo.config.ConfigWrapper;\nimport io.wispforest.owo.config.Option;\nimport io.wispforest.owo.util.Observable;\n\nimport java.util.HashMap;\nimport java.util.Map;\nimport java.util.function.Consumer;\n\npublic class {wrapper_class_name} extends ConfigWrapper<{config_class_name}> {\n\n    public final Keys keys = new Keys();\n\n{option_instances}\n\n    private {wrapper_class_name}() {\n        super({config_class_name}.class);\n    }\n\n    private {wrapper_class_name}(Consumer<Jankson.Builder> janksonBuilder) {\n        super({config_class_name}.class, janksonBuilder);\n    }\n\n    public static {wrapper_class_name} createAndLoad() {\n        var wrapper = new {wrapper_class_name}();\n        wrapper.load();\n        return wrapper;\n    }\n\n    public static {wrapper_class_name} createAndLoad(Consumer<Jankson.Builder> janksonBuilder) {\n        var wrapper = new {wrapper_class_name}(janksonBuilder);\n        wrapper.load();\n        return wrapper;\n    }\n\n{accessors}\n\n{type_interfaces}\n\n    public static class Keys {\n{key_constants}\n    }\n}\n";
   private static final String GET_ACCESSOR_TEMPLATE = "public {field_type} {field_name}() {\n    return {option_instance}.value();\n}\n";
   private static final String SET_ACCESSOR_TEMPLATE = "public void {field_name}({field_type} value) {\n    {option_instance}.set(value);\n}\n";
   private static final String SUBSCRIBE_TEMPLATE = "public void subscribeTo{field_name}(Consumer<{field_type}> subscriber) {\n    {option_instance}.observe(subscriber);\n}\n";
   private final Set<TypeElement> nestTypes = new LinkedHashSet<>();
   private Map<TypeMirror, TypeMirror> primitivesToWrappers;

   @Override
   public synchronized void init(ProcessingEnvironment processingEnv) {
      super.init(processingEnv);
      Types typeUtils = processingEnv.getTypeUtils();
      Elements elementUtils = processingEnv.getElementUtils();
      this.primitivesToWrappers = Map.of(
         typeUtils.getPrimitiveType(TypeKind.BYTE),
         elementUtils.getTypeElement("java.lang.Byte").asType(),
         typeUtils.getPrimitiveType(TypeKind.CHAR),
         elementUtils.getTypeElement("java.lang.Character").asType(),
         typeUtils.getPrimitiveType(TypeKind.SHORT),
         elementUtils.getTypeElement("java.lang.Short").asType(),
         typeUtils.getPrimitiveType(TypeKind.INT),
         elementUtils.getTypeElement("java.lang.Integer").asType(),
         typeUtils.getPrimitiveType(TypeKind.LONG),
         elementUtils.getTypeElement("java.lang.Long").asType(),
         typeUtils.getPrimitiveType(TypeKind.FLOAT),
         elementUtils.getTypeElement("java.lang.Float").asType(),
         typeUtils.getPrimitiveType(TypeKind.DOUBLE),
         elementUtils.getTypeElement("java.lang.Double").asType(),
         typeUtils.getPrimitiveType(TypeKind.BOOLEAN),
         elementUtils.getTypeElement("java.lang.Boolean").asType()
      );
   }

   @Override
   public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
      for (TypeElement annotation : annotations) {
         for (Element annotated : roundEnv.getElementsAnnotatedWith(annotation)) {
            if (annotated.getKind() == ElementKind.CLASS) {
               TypeElement clazz = (TypeElement)annotated;
               String className = clazz.getQualifiedName().toString();
               String wrapperName = annotated.getAnnotation(Config.class).wrapperName();

               try {
                  JavaFileObject file = this.processingEnv.getFiler().createSourceFile(wrapperName);

                  try (PrintWriter writer = new PrintWriter(file.openWriter())) {
                     writer.println(
                        this.makeWrapper(wrapperName, className, this.collectFields(Option.Key.ROOT, clazz, clazz.getAnnotation(Config.class).defaultHook()))
                     );
                  }
               } catch (IOException var17) {
                  throw new RuntimeException("Failed to generate config wrapper", var17);
               }
            }
         }
      }

      return true;
   }

   private List<ConfigAP.ConfigField> collectFields(Option.Key parent, TypeElement clazz, boolean defaultHook) {
      Messager messager = this.processingEnv.getMessager();
      ArrayList<ConfigAP.ConfigField> list = new ArrayList<>();

      for (Element field : clazz.getEnclosedElements()) {
         if (field.getKind() == ElementKind.FIELD) {
            TypeMirror fieldType = field.asType();
            String fieldName = field.getSimpleName().toString();
            if (fieldType.getKind() == TypeKind.TYPEVAR) {
               messager.printMessage(Kind.ERROR, "Generic field types are not allowed in config classes");
            }

            TypeElement typeElement = null;
            if (fieldType.getKind() == TypeKind.DECLARED) {
               typeElement = (TypeElement)((DeclaredType)fieldType).asElement();
               if (typeElement == clazz) {
                  messager.printMessage(Kind.ERROR, "Illegal self-reference in nested config object");
               }
            }

            if (typeElement != null && field.getAnnotation(Nest.class) != null) {
               this.nestTypes.add(typeElement);
               list.add(
                  new ConfigAP.NestField(
                     fieldName, this.collectFields(parent.child(fieldName), typeElement, defaultHook), typeElement.getSimpleName().toString()
                  )
               );
            } else {
               list.add(new ConfigAP.ValueField(fieldName, parent.child(fieldName), field.asType(), defaultHook || field.getAnnotation(Hook.class) != null));
            }
         }
      }

      return list;
   }

   private String makeWrapper(String wrapperClassName, String configClassName, List<ConfigAP.ConfigField> fields) {
      String baseWrapper = "package {package};\n\nimport blue.endless.jankson.Jankson;\nimport io.wispforest.owo.config.ConfigWrapper;\nimport io.wispforest.owo.config.Option;\nimport io.wispforest.owo.util.Observable;\n\nimport java.util.HashMap;\nimport java.util.Map;\nimport java.util.function.Consumer;\n\npublic class {wrapper_class_name} extends ConfigWrapper<{config_class_name}> {\n\n    public final Keys keys = new Keys();\n\n{option_instances}\n\n    private {wrapper_class_name}() {\n        super({config_class_name}.class);\n    }\n\n    private {wrapper_class_name}(Consumer<Jankson.Builder> janksonBuilder) {\n        super({config_class_name}.class, janksonBuilder);\n    }\n\n    public static {wrapper_class_name} createAndLoad() {\n        var wrapper = new {wrapper_class_name}();\n        wrapper.load();\n        return wrapper;\n    }\n\n    public static {wrapper_class_name} createAndLoad(Consumer<Jankson.Builder> janksonBuilder) {\n        var wrapper = new {wrapper_class_name}(janksonBuilder);\n        wrapper.load();\n        return wrapper;\n    }\n\n{accessors}\n\n{type_interfaces}\n\n    public static class Keys {\n{key_constants}\n    }\n}\n"
         .replace("{wrapper_class_name}", wrapperClassName)
         .replace("{package}", configClassName.substring(0, configClassName.lastIndexOf(".")))
         .replace("{config_class_name}", configClassName);
      ConfigAP.Writer accessorMethods = new ConfigAP.Writer(new StringBuilder());
      ConfigAP.Writer optionInstances = new ConfigAP.Writer(new StringBuilder());
      ConfigAP.Writer keyConstants = new ConfigAP.Writer(new StringBuilder());
      ConfigAP.Writer typeInterfaces = new ConfigAP.Writer(new StringBuilder());

      for (TypeElement nestType : this.nestTypes) {
         typeInterfaces.beginLine("public interface ").write(nestType.getSimpleName().toString()).endLine(" {");
         typeInterfaces.beginBlock();

         for (Element enclosed : nestType.getEnclosedElements()) {
            if (enclosed.getKind() == ElementKind.FIELD && enclosed.getAnnotation(Nest.class) == null) {
               typeInterfaces.beginLine(enclosed.asType().toString()).write(" ").write(enclosed.getSimpleName().toString()).endLine("();");
               typeInterfaces.beginLine("void ").write(enclosed.getSimpleName().toString()).write("(").write(enclosed.asType().toString()).endLine(" value);");
            }
         }

         typeInterfaces.endBlock();
         typeInterfaces.line("}");
      }

      keyConstants.beginBlock();

      for (ConfigAP.ConfigField field : fields) {
         field.appendAccessors(accessorMethods, optionInstances, keyConstants);
      }

      return baseWrapper.replace("{option_instances}", optionInstances.finish())
         .replace("{type_interfaces}\n", typeInterfaces.finish())
         .replace("{key_constants}", keyConstants.finish())
         .replace("{accessors}\n", accessorMethods.finish());
   }

   private String makeGetAccessor(String fieldName, Option.Key fieldKey, TypeMirror fieldType) {
      return "public {field_type} {field_name}() {\n    return {option_instance}.value();\n}\n"
         .replace("{option_instance}", this.constantNameOf(fieldKey))
         .replace("{field_name}", fieldName)
         .replace("{field_type}", fieldType.toString());
   }

   private String makeSetAccessor(String fieldName, Option.Key fieldKey, TypeMirror fieldType) {
      return "public void {field_name}({field_type} value) {\n    {option_instance}.set(value);\n}\n"
         .replace("{option_instance}", this.constantNameOf(fieldKey))
         .replace("{field_name}", fieldName)
         .replace("{field_type}", fieldType.toString());
   }

   private String makeSubscribe(String fieldName, Option.Key fieldKey, TypeMirror fieldType) {
      return "public void subscribeTo{field_name}(Consumer<{field_type}> subscriber) {\n    {option_instance}.observe(subscriber);\n}\n"
         .replace("{option_instance}", this.constantNameOf(fieldKey))
         .replace("{field_name}", fieldName)
         .replace("{field_type}", this.primitivesToWrappers.getOrDefault(fieldType, fieldType).toString());
   }

   private String constantNameOf(Option.Key key) {
      return key.asString().replace(".", "_");
   }

   private static String capitalize(String string) {
      return string.substring(0, 1).toUpperCase(Locale.ROOT) + string.substring(1);
   }

   private interface ConfigField {
      void appendAccessors(ConfigAP.Writer var1, ConfigAP.Writer var2, ConfigAP.Writer var3);
   }

   private record NestField(String nestName, List<ConfigAP.ConfigField> children, String typeName) implements ConfigAP.ConfigField {
      @Override
      public void appendAccessors(ConfigAP.Writer accessors, ConfigAP.Writer optionInstances, ConfigAP.Writer keyConstants) {
         String nestClassName = ConfigAP.capitalize(this.nestName);
         if (nestClassName.equals(this.typeName)) {
            nestClassName = nestClassName + "_";
         }

         accessors.beginLine("public final ").write(nestClassName).write(" ").write(this.nestName).write(" = new ").write(nestClassName).endLine("();");
         accessors.beginLine("public class ").write(nestClassName).write(" implements ").write(this.typeName).endLine(" {");
         accessors.beginBlock();

         for (ConfigAP.ConfigField child : this.children) {
            child.appendAccessors(accessors, optionInstances, keyConstants);
         }

         accessors.endBlock();
         accessors.line("}");
      }
   }

   private final class ValueField implements ConfigAP.ConfigField {
      private final String name;
      private final Option.Key key;
      private final TypeMirror type;
      private final boolean makeSubscribe;

      private ValueField(String name, Option.Key key, TypeMirror type, boolean makeSubscribe) {
         this.name = name;
         this.key = key;
         this.type = type;
         this.makeSubscribe = makeSubscribe;
      }

      @Override
      public void appendAccessors(ConfigAP.Writer accessors, ConfigAP.Writer optionInstances, ConfigAP.Writer keyConstants) {
         keyConstants.line("public final Option.Key " + ConfigAP.this.constantNameOf(this.key) + " = new Option.Key(\"" + this.key.asString() + "\");");
         optionInstances.line(
            "private final Option<"
               + ConfigAP.this.primitivesToWrappers.getOrDefault(this.type, this.type)
               + "> "
               + ConfigAP.this.constantNameOf(this.key)
               + " = this.optionForKey(this.keys."
               + ConfigAP.this.constantNameOf(this.key)
               + ");"
         );
         accessors.append(ConfigAP.this.makeGetAccessor(this.name, this.key, this.type)).write("\n");
         accessors.append(ConfigAP.this.makeSetAccessor(this.name, this.key, this.type)).write("\n");
         if (this.makeSubscribe) {
            accessors.append(ConfigAP.this.makeSubscribe(ConfigAP.capitalize(this.name), this.key, this.type)).write("\n");
         }
      }
   }

   private static class Writer implements CharSequence {
      private final StringBuilder builder;
      private int indentLevel = 1;

      private Writer(StringBuilder builder) {
         this.builder = builder;
      }

      public ConfigAP.Writer beginLine(CharSequence text) {
         this.builder.append(" ".repeat(this.indentLevel * 4)).append(text);
         return this;
      }

      public void endLine(CharSequence text) {
         this.builder.append(text).append("\n");
      }

      public void line(CharSequence text) {
         this.builder.append("    ".repeat(this.indentLevel)).append(text).append("\n");
      }

      public ConfigAP.Writer append(String text) {
         for (String line : text.split("\n")) {
            this.line(line);
         }

         return this;
      }

      public ConfigAP.Writer write(CharSequence text) {
         this.builder.append(text);
         return this;
      }

      public void beginBlock() {
         this.indentLevel++;
      }

      public void endBlock() {
         this.indentLevel--;
      }

      public String finish() {
         if (this.builder.isEmpty()) {
            return "";
         } else {
            if (this.builder.charAt(this.builder.length() - 1) == '\n') {
               this.builder.deleteCharAt(this.builder.length() - 1);
            }

            return this.builder.toString();
         }
      }

      @Override
      public int length() {
         return this.builder.length();
      }

      @Override
      public char charAt(int index) {
         return this.builder.charAt(index);
      }

      @NotNull
      @Override
      public CharSequence subSequence(int start, int end) {
         return this.builder.subSequence(start, end);
      }

      @NotNull
      @Override
      public String toString() {
         return this.builder.toString();
      }
   }
}
