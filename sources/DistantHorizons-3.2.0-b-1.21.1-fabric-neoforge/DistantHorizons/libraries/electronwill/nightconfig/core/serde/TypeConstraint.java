package DistantHorizons.libraries.electronwill.nightconfig.core.serde;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public final class TypeConstraint {
   private final Type fullType;
   private Optional<Class<?>> rawClass = null;

   static TypeConstraint[] mapArray(Type[] t) {
      TypeConstraint[] c = new TypeConstraint[t.length];

      for (int i = 0; i < t.length; i++) {
         c[i] = new TypeConstraint(t[i]);
      }

      return c;
   }

   public TypeConstraint(Type fullType) {
      this.fullType = fullType;
   }

   public Type getFullType() {
      return this.fullType;
   }

   public Optional<Class<?>> getSatisfyingRawType() {
      if (this.rawClass == null) {
         this.rawClass = Optional.ofNullable(findSatisfyingRawType(this.fullType));
      }

      return this.rawClass;
   }

   public Optional<TypeConstraint[]> resolveTypeArgumentsFor(Class<?> classToFind) {
      return Optional.ofNullable(resolveTypeArgumentsFor(this.fullType, classToFind, new HashMap<>()));
   }

   @Override
   public String toString() {
      return String.format("TypeConstraint[%s, rawType=%s]", this.fullType, this.getSatisfyingRawType());
   }

   private static final Class<?> findSatisfyingRawType(Type t) {
      if (t instanceof Class) {
         return (Class<?>)t;
      } else if (t instanceof ParameterizedType) {
         return findSatisfyingRawType(((ParameterizedType)t).getRawType());
      } else if (t instanceof GenericArrayType) {
         Type componentType = ((GenericArrayType)t).getGenericComponentType();
         Class<?> componentClass = findSatisfyingRawType(componentType);
         return componentClass == null ? null : Array.newInstance(componentClass, 0).getClass();
      } else if (t instanceof WildcardType) {
         WildcardType w = (WildcardType)t;
         Type[] lowerBounds = w.getLowerBounds();
         Type[] upperBounds = w.getUpperBounds();
         if (upperBounds.length == 1) {
            Type upper = upperBounds[0];
            return lowerBounds.length == 1 && upper == Object.class ? findSatisfyingRawType(lowerBounds[0]) : findSatisfyingRawType(upper);
         } else {
            return lowerBounds.length == 1 && upperBounds.length == 0 ? findSatisfyingRawType(lowerBounds[0]) : null;
         }
      } else if (t instanceof TypeVariable) {
         Type[] bounds = ((TypeVariable)t).getBounds();
         return bounds.length == 1 ? findSatisfyingRawType(bounds[0]) : null;
      } else {
         return null;
      }
   }

   private static TypeConstraint[] resolveTypeArgumentsFor(Type t, Class<?> classToFind, Map<TypeVariable<?>, Type> resolvedVariables) {
      if (t instanceof Class) {
         return t == classToFind ? null : findParent((Class<?>)t, parent -> resolveTypeArgumentsFor(parent, classToFind, resolvedVariables));
      } else if (t instanceof ParameterizedType) {
         ParameterizedType pt = (ParameterizedType)t;
         Type rawType = pt.getRawType();
         Type[] actualTypeArgs = pt.getActualTypeArguments();

         for (int i = 0; i < actualTypeArgs.length; i++) {
            Type typeArg = actualTypeArgs[i];
            if (typeArg instanceof WildcardType) {
               WildcardType wildcard = (WildcardType)typeArg;
               Class<Object> cls = (Class<Object>)rawType;
               TypeVariable<Class<Object>> declaredTypeParam = cls.getTypeParameters()[i];
               actualTypeArgs[i] = refineWildcard(wildcard, declaredTypeParam, resolvedVariables);
            } else {
               actualTypeArgs[i] = resolveIfVariable(typeArg, resolvedVariables);
            }
         }

         if (rawType == classToFind) {
            return mapArray(actualTypeArgs);
         } else {
            TypeVariable<?>[] declaredTypeArgs = ((Class)rawType).getTypeParameters();

            for (int ix = 0; ix < declaredTypeArgs.length; ix++) {
               resolvedVariables.put(declaredTypeArgs[ix], actualTypeArgs[ix]);
            }

            return findParent((Class<?>)rawType, parent -> resolveTypeArgumentsFor(parent, classToFind, resolvedVariables));
         }
      } else if (t instanceof TypeVariable) {
         Type[] bounds = ((TypeVariable)t).getBounds();
         TypeConstraint[] res = null;

         for (Type bound : bounds) {
            bound = resolveIfVariable(bound, resolvedVariables);
            res = resolveTypeArgumentsFor(bound, classToFind, resolvedVariables);
            if (res != null) {
               break;
            }
         }

         return res;
      } else {
         if (t instanceof WildcardType) {
            WildcardType w = (WildcardType)t;
            TypeConstraint[] res = null;

            for (Type boundx : w.getUpperBounds()) {
               res = resolveTypeArgumentsFor(resolveIfVariable(boundx, resolvedVariables), classToFind, resolvedVariables);
               if (res != null) {
                  return res;
               }
            }

            for (Type boundxx : w.getLowerBounds()) {
               res = resolveTypeArgumentsFor(resolveIfVariable(boundxx, resolvedVariables), classToFind, resolvedVariables);
               if (res != null) {
                  return res;
               }
            }
         }

         return null;
      }
   }

   private static Type resolveIfVariable(Type t, Map<TypeVariable<?>, Type> resolvedVariables) {
      if (t instanceof TypeVariable) {
         Type resolved = resolvedVariables.get(t);
         if (resolved != null) {
            return resolved;
         }
      }

      return t;
   }

   private static <R> R findParent(Class<?> cls, Function<Type, R> f) {
      R res = null;
      Type parentClass = cls.getGenericSuperclass();
      if (parentClass != null) {
         res = f.apply(parentClass);
      }

      if (res == null) {
         Type[] parentInterfaces = cls.getGenericInterfaces();

         for (Type parent : parentInterfaces) {
            res = f.apply(parent);
            if (res != null) {
               break;
            }
         }
      }

      return res;
   }

   private static Type wildcardLowerBound(WildcardType t) {
      Type[] bounds = t.getLowerBounds();
      return bounds.length > 0 ? bounds[0] : null;
   }

   private static Type wildcardUpperBound(WildcardType t) {
      Type[] bounds = t.getUpperBounds();
      return bounds.length > 0 ? bounds[0] : null;
   }

   static Type refineWildcard(WildcardType wildcard, TypeVariable<Class<Object>> declaredTypeParam, Map<TypeVariable<?>, Type> resolvedVariables) {
      if (wildcard instanceof TypeConstraint.RefinedWildcard) {
         return (TypeConstraint.RefinedWildcard)wildcard;
      } else {
         Type upperBound = wildcardUpperBound(wildcard);
         Type[] lowerBounds = wildcard.getLowerBounds();
         Type[] declaredUpperBounds = declaredTypeParam.getBounds();
         if (declaredUpperBounds.length != 0
            && (declaredUpperBounds.length != 1 || declaredUpperBounds[0] != Object.class && declaredUpperBounds[0] != upperBound)) {
            Type[] refinedUpper;
            if (upperBound != null && upperBound != Object.class) {
               List<Type> upper = new ArrayList<>(declaredUpperBounds.length + 1);
               upper.add(upperBound);

               for (int i = 0; i < declaredUpperBounds.length; i++) {
                  Type declaredUpper = declaredUpperBounds[i];
                  if (upperBound != declaredUpper) {
                     upper.add(declaredUpper);
                  }
               }

               refinedUpper = upper.toArray(new Type[upper.size()]);
            } else {
               refinedUpper = declaredUpperBounds;
            }

            return (Type)(lowerBounds.length == 1 && refinedUpper.length == 1 && lowerBounds[0].equals(refinedUpper[0])
               ? lowerBounds[0]
               : new TypeConstraint.RefinedWildcard(lowerBounds, refinedUpper));
         } else {
            return wildcard;
         }
      }
   }

   static final class ManuallyParameterized implements ParameterizedType {
      private final Type rawType;
      private final Type[] arguments;

      public ManuallyParameterized(Type rawType, Type... arguments) {
         this.rawType = Objects.requireNonNull(rawType);
         this.arguments = arguments;
      }

      @Override
      public Type[] getActualTypeArguments() {
         return this.arguments;
      }

      @Override
      public Type getOwnerType() {
         return null;
      }

      @Override
      public Type getRawType() {
         return this.rawType;
      }

      @Override
      public String toString() {
         return this.arguments.length == 0
            ? this.rawType.toString()
            : this.rawType + "<" + String.join(", ", Arrays.stream(this.arguments).map(t -> t.toString()).toArray(String[]::new)) + ">";
      }

      @Override
      public boolean equals(Object obj) {
         if (!(obj instanceof ParameterizedType)) {
            return false;
         } else if (obj == this) {
            return true;
         } else {
            ParameterizedType other = (ParameterizedType)obj;
            return null == other.getOwnerType()
               && Objects.equals(this.rawType, other.getRawType())
               && Arrays.equals((Object[])this.arguments, (Object[])other.getActualTypeArguments());
         }
      }
   }

   static final class RefinedWildcard implements WildcardType {
      private final Type[] lowerBounds;
      private final Type[] upperBounds;

      RefinedWildcard(Type[] lowerBounds, Type[] upperBounds) {
         this.lowerBounds = lowerBounds;
         this.upperBounds = upperBounds;
      }

      @Override
      public Type[] getLowerBounds() {
         return this.lowerBounds;
      }

      @Override
      public Type[] getUpperBounds() {
         return this.upperBounds;
      }

      @Override
      public String toString() {
         String lower = this.lowerBounds.length == 0 ? "" : " >: " + Arrays.<Type>asList(this.lowerBounds);
         String upper = "<: " + Arrays.<Type>asList(this.upperBounds);
         return "?" + lower + " " + upper;
      }

      @Override
      public boolean equals(Object obj) {
         if (!(obj instanceof WildcardType)) {
            return false;
         } else if (obj == this) {
            return true;
         } else {
            WildcardType other = (WildcardType)obj;
            return Arrays.equals((Object[])this.lowerBounds, (Object[])other.getLowerBounds())
               && Arrays.equals((Object[])this.upperBounds, (Object[])other.getUpperBounds());
         }
      }

      @Override
      public int hashCode() {
         return Objects.hash(this.lowerBounds, this.upperBounds);
      }
   }
}
