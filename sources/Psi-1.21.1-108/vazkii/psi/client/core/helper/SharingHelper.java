package vazkii.psi.client.core.helper;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.platform.NativeImage;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import vazkii.psi.common.Psi;

public final class SharingHelper {
   private static final String CLIENT_ID = "d5d2258f3526156";

   public static void uploadAndShare(String title, String export) {
      String url = uploadImage(title, export);

      try {
         String contents = "## "
            + title
            + "  \n### [Image + Code]("
            + url
            + ")\n(to get the code click the link, RES won't show it)\n\n---\n*REPLACE THIS WITH A DESCRIPTION OF YOUR SPELL  \nMake sure you read the rules before posting. Look on the sidebar: https://www.reddit.com/r/psispellcompendium/  \nDelete this part before you submit.*";
         String encodedContents = URLEncoder.encode(contents, StandardCharsets.UTF_8);
         String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8);
         String redditUrl = "https://old.reddit.com/r/psispellcompendium/submit?title=" + encodedTitle + "&text=" + encodedContents;
         Util.getPlatform().openUri(new URI(redditUrl));
      } catch (Exception var7) {
         Psi.logger.error("Error when trying to create a reddit post", var7);
      }
   }

   public static void uploadAndOpen(String title, String export) {
      String url = uploadImage(title, export);

      try {
         Util.getPlatform().openUri(new URI(url));
      } catch (Exception var4) {
         Psi.logger.error("Error when trying to open uploaded image URL", var4);
      }
   }

   public static String uploadImage(String title, String export) {
      try {
         CloseableHttpClient client = HttpClients.createDefault();

         String var11;
         label51: {
            try {
               String desc = "Spell Code:\n\n" + export;
               String url = "https://api.imgur.com/3/image";
               HttpPost post = new HttpPost(url);
               List<NameValuePair> list = new ArrayList<>();
               list.add(new BasicNameValuePair("type", "base64"));
               list.add(new BasicNameValuePair("image", takeScreenshot()));
               list.add(new BasicNameValuePair("name", title));
               list.add(new BasicNameValuePair("description", desc));
               post.setEntity(new UrlEncodedFormEntity(list));
               post.addHeader("Authorization", "Client-ID d5d2258f3526156");
               HttpResponse res = client.execute(post);
               JsonObject resJson = JsonParser.parseString(EntityUtils.toString(res.getEntity())).getAsJsonObject();
               if (resJson.has("success") && resJson.get("success").getAsBoolean()) {
                  JsonObject data = resJson.get("data").getAsJsonObject();
                  String id = data.get("id").getAsString();
                  var11 = "https://imgur.com/" + id;
                  break label51;
               }
            } catch (Throwable var13) {
               if (client != null) {
                  try {
                     client.close();
                  } catch (Throwable var12) {
                     var13.addSuppressed(var12);
                  }
               }

               throw var13;
            }

            if (client != null) {
               client.close();
            }

            return "N/A";
         }

         if (client != null) {
            client.close();
         }

         return var11;
      } catch (Exception var14) {
         Psi.logger.error("Error when uploading image to imgur", var14);
         return "N/A";
      }
   }

   public static String takeScreenshot() throws Exception {
      Minecraft mc = Minecraft.getInstance();
      NativeImage image = Screenshot.takeScreenshot(mc.getMainRenderTarget());

      String var3;
      try {
         byte[] bArray = image.asByteArray();
         var3 = Base64.getEncoder().encodeToString(bArray);
      } catch (Throwable var5) {
         if (image != null) {
            try {
               image.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (image != null) {
         image.close();
      }

      return var3;
   }
}
