package com.seibel.distanthorizons.common.render.openGl.glObject.enums;

public class GLEnums {
   public static String getString(int glEnum) {
      switch (glEnum) {
         case 0:
            return "GL_ZERO";
         case 1:
            return "GL_ONE";
         case 768:
            return "GL_SRC_COLOR";
         case 769:
            return "GL_ONE_MINUS_SRC_COLOR";
         case 770:
            return "GL_SRC_ALPHA";
         case 771:
            return "GL_ONE_MINUS_SRC_ALPHA";
         case 772:
            return "GL_DST_ALPHA";
         case 773:
            return "GL_ONE_MINUS_DST_ALPHA";
         case 774:
            return "GL_DST_COLOR";
         case 775:
            return "GL_ONE_MINUS_DST_COLOR";
         case 32769:
            return "GL_CONSTANT_COLOR";
         case 32770:
            return "GL_ONE_MINUS_CONSTANT_COLOR";
         case 32771:
            return "GL_CONSTANT_ALPHA";
         case 32772:
            return "GL_ONE_MINUS_CONSTANT_ALPHA";
         default:
            switch (glEnum) {
               case 35632:
                  return "GL_FRAGMENT_SHADER";
               case 35633:
                  return "GL_VERTEX_SHADER";
               case 36313:
                  return "GL_GEOMETRY_SHADER";
               default:
                  switch (glEnum) {
                     case 0:
                        return "GL_ZERO";
                     case 5386:
                        return "GL_INVERT";
                     case 7680:
                        return "GL_KEEP";
                     case 7681:
                        return "GL_REPLACE";
                     case 7682:
                        return "GL_INCR";
                     case 7683:
                        return "GL_DECR";
                     case 34055:
                        return "GL_INCR_WRAP";
                     case 34056:
                        return "GL_DECR_WRAP";
                     default:
                        switch (glEnum) {
                           case 512:
                              return "GL_NEVER";
                           case 513:
                              return "GL_LESS";
                           case 514:
                              return "GL_EQUAL";
                           case 515:
                              return "GL_LEQUAL";
                           case 516:
                              return "GL_GREATER";
                           case 517:
                              return "GL_NOTEQUAL";
                           case 518:
                              return "GL_GEQUAL";
                           case 519:
                              return "GL_ALWAYS";
                           default:
                              switch (glEnum) {
                                 case 33984:
                                    return "GL_TEXTURE0";
                                 case 33985:
                                    return "GL_TEXTURE1";
                                 case 33986:
                                    return "GL_TEXTURE2";
                                 case 33987:
                                    return "GL_TEXTURE3";
                                 case 33988:
                                    return "GL_TEXTURE4";
                                 case 33989:
                                    return "GL_TEXTURE5";
                                 case 33990:
                                    return "GL_TEXTURE6";
                                 case 33991:
                                    return "GL_TEXTURE7";
                                 case 33992:
                                    return "GL_TEXTURE8";
                                 case 33993:
                                    return "GL_TEXTURE9";
                                 case 33994:
                                    return "GL_TEXTURE10";
                                 case 33995:
                                    return "GL_TEXTURE11";
                                 case 33996:
                                    return "GL_TEXTURE12";
                                 case 33997:
                                    return "GL_TEXTURE13";
                                 case 33998:
                                    return "GL_TEXTURE14";
                                 case 33999:
                                    return "GL_TEXTURE15";
                                 case 34000:
                                    return "GL_TEXTURE16";
                                 case 34001:
                                    return "GL_TEXTURE17";
                                 case 34002:
                                    return "GL_TEXTURE18";
                                 case 34003:
                                    return "GL_TEXTURE19";
                                 case 34004:
                                    return "GL_TEXTURE20";
                                 case 34005:
                                    return "GL_TEXTURE21";
                                 case 34006:
                                    return "GL_TEXTURE22";
                                 case 34007:
                                    return "GL_TEXTURE23";
                                 case 34008:
                                    return "GL_TEXTURE24";
                                 case 34009:
                                    return "GL_TEXTURE25";
                                 case 34010:
                                    return "GL_TEXTURE26";
                                 case 34011:
                                    return "GL_TEXTURE27";
                                 case 34012:
                                    return "GL_TEXTURE28";
                                 case 34013:
                                    return "GL_TEXTURE29";
                                 case 34014:
                                    return "GL_TEXTURE30";
                                 case 34015:
                                    return "GL_TEXTURE31";
                                 default:
                                    switch (glEnum) {
                                       case 6912:
                                          return "GL_POINT";
                                       case 6913:
                                          return "GL_LINE";
                                       case 6914:
                                          return "GL_FILL";
                                       default:
                                          switch (glEnum) {
                                             case 1028:
                                                return "GL_FRONT";
                                             case 1029:
                                                return "GL_BACK";
                                             case 1030:
                                             case 1031:
                                             default:
                                                switch (glEnum) {
                                                   case 5120:
                                                      return "GL_BYTE";
                                                   case 5121:
                                                      return "GL_UNSIGNED_BYTE";
                                                   case 5122:
                                                      return "GL_SHORT";
                                                   case 5123:
                                                      return "GL_UNSIGNED_SHORT";
                                                   case 5124:
                                                      return "GL_INT";
                                                   case 5125:
                                                      return "GL_UNSIGNED_INT";
                                                   case 5126:
                                                      return "GL_FLOAT";
                                                   case 5127:
                                                   case 5128:
                                                   case 5129:
                                                   default:
                                                      return "GL_UNKNOWN(" + glEnum + ")";
                                                   case 5130:
                                                      return "GL_DOUBLE";
                                                }
                                             case 1032:
                                                return "GL_FRONT_AND_BACK";
                                          }
                                    }
                              }
                        }
                  }
            }
      }
   }

   public static int getTypeSize(int glTypeEnum) {
      switch (glTypeEnum) {
         case 5120:
         case 5121:
            return 1;
         case 5122:
         case 5123:
            return 2;
         case 5124:
         case 5125:
            return 4;
         case 5126:
            return 4;
         case 5127:
         case 5128:
         case 5129:
         default:
            throw new IllegalArgumentException("Unknown type enum: " + getString(glTypeEnum));
         case 5130:
            return 8;
      }
   }
}
