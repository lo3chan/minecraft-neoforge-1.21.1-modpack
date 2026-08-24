package me.lucko.spark.common.util;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.ImmutableListMultimap.Builder;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import me.lucko.spark.common.sampler.node.StackTraceNode;
import me.lucko.spark.common.util.classfinder.ClassFinder;
import me.lucko.spark.lib.asm.ClassReader;
import me.lucko.spark.lib.asm.ClassVisitor;
import me.lucko.spark.lib.asm.Label;
import me.lucko.spark.lib.asm.MethodVisitor;

public final class MethodDisambiguator {
   private final ClassFinder classFinder;
   private final Map<String, MethodDisambiguator.ComputedClass> cache;

   public MethodDisambiguator(ClassFinder classFinder) {
      this.classFinder = classFinder;
      this.cache = new ConcurrentHashMap<>();
   }

   public Optional<MethodDisambiguator.MethodDescription> disambiguate(StackTraceNode element) {
      String desc = element.getMethodDescription();
      return desc != null
         ? Optional.of(new MethodDisambiguator.MethodDescription(element.getMethodName(), desc))
         : this.disambiguate(element.getClassName(), element.getMethodName(), element.getLineNumber());
   }

   public Optional<MethodDisambiguator.MethodDescription> disambiguate(String className, String methodName, int lineNumber) {
      MethodDisambiguator.ComputedClass computedClass = this.cache.get(className);
      if (computedClass == null) {
         try {
            computedClass = this.compute(className);
         } catch (Throwable var6) {
            computedClass = MethodDisambiguator.ComputedClass.EMPTY;
         }

         this.cache.put(className, computedClass);
      }

      List<MethodDisambiguator.MethodDescription> descriptions = computedClass.descriptionsByName.get(methodName);
      switch (descriptions.size()) {
         case 0:
            return Optional.empty();
         case 1:
            return Optional.of(descriptions.get(0));
         default:
            return Optional.ofNullable(computedClass.descriptionsByLine.get(lineNumber));
      }
   }

   private MethodDisambiguator.ComputedClass compute(String className) throws IOException {
      final Builder<String, MethodDisambiguator.MethodDescription> descriptionsByName = ImmutableListMultimap.builder();
      final Map<Integer, MethodDisambiguator.MethodDescription> descriptionsByLine = new HashMap<>();
      ClassReader classReader = this.getClassReader(className);
      classReader.accept(new ClassVisitor(458752) {
         @Override
         public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            final MethodDisambiguator.MethodDescription description = new MethodDisambiguator.MethodDescription(name, descriptor);
            descriptionsByName.put(name, description);
            return new MethodVisitor(458752) {
               @Override
               public void visitLineNumber(int line, Label start) {
                  descriptionsByLine.put(line, description);
               }
            };
         }
      }, 458752);
      return new MethodDisambiguator.ComputedClass(descriptionsByName.build(), ImmutableMap.copyOf(descriptionsByLine));
   }

   private ClassReader getClassReader(String className) throws IOException {
      String resource = className.replace('.', '/') + ".class";
      InputStream is = ClassLoader.getSystemResourceAsStream(resource);

      ClassReader var12;
      label81: {
         try {
            if (is != null) {
               var12 = new ClassReader(is);
               break label81;
            }
         } catch (Throwable var10) {
            if (is != null) {
               try {
                  is.close();
               } catch (Throwable var8) {
                  var10.addSuppressed(var8);
               }
            }

            throw var10;
         }

         if (is != null) {
            is.close();
         }

         Class<?> clazz = this.classFinder.findClass(className);
         if (clazz != null) {
            InputStream isx = clazz.getClassLoader().getResourceAsStream(resource);

            ClassReader var5;
            label86: {
               try {
                  if (isx != null) {
                     var5 = new ClassReader(isx);
                     break label86;
                  }
               } catch (Throwable var9) {
                  if (isx != null) {
                     try {
                        isx.close();
                     } catch (Throwable var7) {
                        var9.addSuppressed(var7);
                     }
                  }

                  throw var9;
               }

               if (isx != null) {
                  isx.close();
               }

               throw new IOException("Unable to get resource: " + className);
            }

            if (isx != null) {
               isx.close();
            }

            return var5;
         }

         throw new IOException("Unable to get resource: " + className);
      }

      if (is != null) {
         is.close();
      }

      return var12;
   }

   private static final class ComputedClass {
      private static final MethodDisambiguator.ComputedClass EMPTY = new MethodDisambiguator.ComputedClass(ImmutableListMultimap.of(), ImmutableMap.of());
      private final ListMultimap<String, MethodDisambiguator.MethodDescription> descriptionsByName;
      private final Map<Integer, MethodDisambiguator.MethodDescription> descriptionsByLine;

      private ComputedClass(
         ListMultimap<String, MethodDisambiguator.MethodDescription> descriptionsByName, Map<Integer, MethodDisambiguator.MethodDescription> descriptionsByLine
      ) {
         this.descriptionsByName = descriptionsByName;
         this.descriptionsByLine = descriptionsByLine;
      }
   }

   public static final class MethodDescription {
      private final String name;
      private final String description;

      private MethodDescription(String name, String description) {
         this.name = name;
         this.description = description;
      }

      public String getName() {
         return this.name;
      }

      public String getDescription() {
         return this.description;
      }

      @Override
      public String toString() {
         return this.name + this.description;
      }
   }
}
