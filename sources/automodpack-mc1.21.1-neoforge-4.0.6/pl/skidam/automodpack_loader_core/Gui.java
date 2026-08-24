package pl.skidam.automodpack_loader_core;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.plaf.ColorUIResource;
import pl.skidam.automodpack_core.GlobalVariables;
import pl.skidam.automodpack_core.utils.PlatformUtils;

public class Gui {
   private String text;
   private Semaphore semaphore;

   public Semaphore open(String text) {
      this.text = text;
      this.semaphore = new Semaphore(0, true);
      if (hasAwtSupport()) {
         CompletableFuture.runAsync(this::window).whenCompleteAsync((o, throwable) -> this.semaphore.release());
      } else {
         CompletableFuture.runAsync(this::openForked).whenCompleteAsync((o, throwable) -> this.semaphore.release());
      }

      return this.semaphore;
   }

   private void window() {
      Semaphore windowSemaphore = new Semaphore(0, true);
      JFrame frame = new JFrame();
      frame.setUndecorated(true);
      frame.setLayout(null);
      frame.setDefaultCloseOperation(3);
      frame.setSize(400, 150);
      frame.setResizable(false);
      Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
      frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height / 2 - frame.getSize().height / 2);
      frame.getContentPane().setBackground(new ColorUIResource(22, 27, 34));
      JLabel RestartText = new JLabel("Restart your game!");
      RestartText.setBounds(0, 10, 400, 32);
      RestartText.setFont(new Font("Segoe UI", 0, 24));
      RestartText.setForeground(Color.green);
      RestartText.setHorizontalAlignment(0);
      JLabel CustomText = new JLabel(this.text);
      CustomText.setBounds(0, 48, 400, 36);
      CustomText.setFont(new Font("Segoe UI", 0, 18));
      CustomText.setForeground(Color.white);
      CustomText.setHorizontalAlignment(0);
      JButton OKButton = new JButton("OK");
      OKButton.setBounds(160, 100, 60, 25);
      OKButton.setBackground(new Color(0, 153, 51));
      OKButton.setForeground(Color.white);
      OKButton.setFont(new Font("Segoe UI", 1, 14));
      OKButton.setFocusPainted(false);
      OKButton.addActionListener(ex -> {
         frame.dispose();
         windowSemaphore.release();
      });
      BufferedImage icon = null;

      try {
         InputStream inputStream = Gui.class.getClassLoader().getResourceAsStream("icon.png");
         if (inputStream != null) {
            icon = ImageIO.read(inputStream);
            inputStream.close();
         }
      } catch (IOException var10) {
         var10.printStackTrace();
      }

      frame.add(OKButton);
      frame.add(CustomText);
      frame.add(RestartText);
      frame.setTitle("AutoModpack Window");
      frame.setIconImage(icon);
      frame.setAlwaysOnTop(true);
      frame.setVisible(true);
      if (this.semaphore != null) {
         this.semaphore.release();
      }

      try {
         windowSemaphore.acquire();
      } catch (InterruptedException var9) {
         var9.printStackTrace();
      }
   }

   private void openForked() {
      try {
         Path javaBinDir = Paths.get(System.getProperty("java.home"), "bin").toAbsolutePath();
         String[] executables = new String[]{"javaw.exe", "java.exe", "java"};
         Path javaPath = null;

         for (String executable : executables) {
            Path path = javaBinDir.resolve(executable);
            if (Files.isRegularFile(path)) {
               javaPath = path;
               break;
            }
         }

         if (javaPath == null) {
            throw new RuntimeException("can't find java executable in " + javaBinDir);
         }

         Process process = new ProcessBuilder(
               javaPath.toString(), "-Xmx100M", "-cp", GlobalVariables.THIS_MOD_JAR.toString(), Gui.class.getName(), "--AM.text=" + this.text
            )
            .redirectOutput(Redirect.INHERIT)
            .redirectError(Redirect.INHERIT)
            .start();
         Thread shutdownHook = new Thread(process::destroy);
         Runtime.getRuntime().addShutdownHook(shutdownHook);
         int rVal = process.waitFor();
         Runtime.getRuntime().removeShutdownHook(shutdownHook);
         if (this.semaphore != null) {
            this.semaphore.release();
         }

         if (rVal != 0) {
            throw new IOException("subprocess exited with code " + rVal);
         }
      } catch (InterruptedException | IOException var9) {
         var9.printStackTrace();
      }
   }

   public static void main(String[] args) throws Exception {
      String text = "Failed to load text";

      for (String arg : args) {
         if (arg.startsWith("--AM.text=")) {
            text = arg.substring(10);
         }
      }

      Gui gui = new Gui();
      gui.text = text;
      gui.window();
      System.exit(0);
   }

   private static boolean hasAwtSupport() {
      if (PlatformUtils.IS_MAC) {
         for (String key : System.getenv().keySet()) {
            if (key.startsWith("JAVA_STARTED_ON_FIRST_THREAD_")) {
               return false;
            }
         }
      }

      return true;
   }
}
