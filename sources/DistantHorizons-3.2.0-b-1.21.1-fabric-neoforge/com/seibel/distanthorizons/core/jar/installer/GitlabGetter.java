package com.seibel.distanthorizons.core.jar.installer;

import DistantHorizons.libraries.electronwill.nightconfig.core.Config;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

public class GitlabGetter {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   public static GitlabGetter INSTANCE = new GitlabGetter();
   public static final String GitlabApi = "https://gitlab.com/api/v4/projects/";
   public final String projectID;
   public final String GitProjID;
   public ArrayList<Config> projectPipelines = new ArrayList<>();
   private static final Map<String, Config> commitInfo = new HashMap<>();
   private static final Map<Number, ArrayList<Config>> pipelineInfo = new HashMap<>();

   public GitlabGetter() {
      this("18204078");
   }

   public GitlabGetter(String projectID) {
      this.projectID = projectID;
      this.GitProjID = "https://gitlab.com/api/v4/projects/" + projectID + "/";

      try {
         this.projectPipelines = WebDownloader.parseWebJsonList(this.GitProjID + "pipelines");
      } catch (Exception var3) {
         LOGGER.error("Unable to get project pipelines, error: [" + var3.getMessage() + "].", var3);
      }
   }

   public Config getCommitInfo(String commit) {
      if (!commitInfo.containsKey(commit)) {
         try {
            commitInfo.put(commit, WebDownloader.parseWebJson(this.GitProjID + "repository/commits/" + commit));
         } catch (Exception var3) {
            LOGGER.error("Unable to get commit info for project [" + this.GitProjID + "], commit: [" + commit + "], error: [" + var3.getMessage() + "].", var3);
            return Config.inMemory();
         }
      }

      return commitInfo.get(commit);
   }

   public ArrayList<Config> getPipelineInfo(Number pipeline) {
      if (!pipelineInfo.containsKey(pipeline)) {
         try {
            pipelineInfo.put(pipeline, WebDownloader.parseWebJsonList(this.GitProjID + "pipelines/" + pipeline + "/jobs?per_page=100"));
         } catch (Exception var3) {
            LOGGER.error("Unable to get [" + pipeline + "]'s pipeline info, error: [" + var3.getMessage() + "].", var3);
            return new ArrayList<>();
         }
      }

      return pipelineInfo.get(pipeline);
   }

   public Map<String, URL> getDownloads(Number pipelineID) {
      Map<String, URL> downloads = new HashMap<>();
      ArrayList<Config> currentPipelineInfo = this.getPipelineInfo(pipelineID);

      try {
         for (Config cfg : currentPipelineInfo) {
            if (cfg.get("stage").equals("build")) {
               downloads.put(cfg.<String>get("name").split("\\[|\\]")[1], new URL(this.GitProjID + "jobs/" + cfg.get("id") + "/artifacts"));
            }
         }
      } catch (Exception var6) {
         LOGGER.error("Unable to get downloads for pipeline [" + pipelineID + "], error: [" + var6.getMessage() + "].", var6);
      }

      return downloads;
   }

   public static void main(String[] args) {
      GitlabGetter gitlabGetter = new GitlabGetter();
      System.out.println(gitlabGetter.getDownloads(gitlabGetter.projectPipelines.get(0).get("id")));
   }

   @Nullable
   public static URL getLatestForVersion(String mcVer) {
      try {
         return new URL("https://gitlab.com/distant-horizons-team/distant-horizons/-/jobs/artifacts/main/download?job=build:%20%5B" + mcVer + "%5D");
      } catch (Exception var2) {
         LOGGER.error("Unable to get latest URL for MC version [" + mcVer + "], error: [" + var2.getMessage() + "].", var2);
         return null;
      }
   }
}
