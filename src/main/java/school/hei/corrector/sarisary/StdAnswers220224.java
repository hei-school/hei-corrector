package school.hei.corrector.sarisary;

import com.fasterxml.jackson.databind.ObjectMapper;
import school.hei.corrector.StdAnswers;
import school.hei.utils.Log;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static java.net.http.HttpClient.newHttpClient;
import static school.hei.corrector.sarisary.ExamSession220224.SCORE_PER_STUDENT;
import static school.hei.utils.Utils.cloneRepo;
import static school.hei.utils.Utils.execShCmdIn1mn;
import static school.hei.utils.Utils.randomRepoName;
import static school.hei.utils.Utils.saveScore;

public record StdAnswers220224(
    String stdRef,
    String group,
    String gitUrl,
    String preprodDbHealthUrl,
    String preprodBucketHealthUrl,
    String preprodApiUrl,
    String prodApiUrl)
    implements StdAnswers {

  @Override
  public int correct() {
    Log.info("[Correction d'un étudiant...] Réf étudiante : " + stdRef);
    var score = 0;

    var levelBonus = levelBonus();
    saveScore(SCORE_PER_STUDENT, stdRef, "levelBonus", levelBonus);
    score += levelBonus;

    var namingMalus = namingMalus();
    saveScore(SCORE_PER_STUDENT, stdRef, "namingMalus", namingMalus);
    score += namingMalus;

    var pojaVersion = pojaVersion();
    saveScore(SCORE_PER_STUDENT, stdRef, "pojaVersion", pojaVersion);
    score += pojaVersion;

    var preprodDbCheck = checkRun("preprod", preprodDbHealthUrl, List.of("✓ check-sync-stack", "✓ health-check-infra / check-sync-stack"));
    saveScore(SCORE_PER_STUDENT, stdRef, "preprodDbCheck", preprodDbCheck);
    score += preprodDbCheck;

    var preprodBucketCheck = checkRun("preprod", preprodDbHealthUrl, List.of("✓ check-bucket", "✓ health-check-infra / check-bucket"));
    Log.info("Multiple point bucket : x1.5");
    preprodBucketCheck = (int) (preprodBucketCheck * 1.5);
    saveScore(SCORE_PER_STUDENT, stdRef, "preprodBucketCheck", preprodBucketCheck);
    score += preprodBucketCheck;

    var preprodPutBw = checkPutBw(preprodApiUrl);
    saveScore(SCORE_PER_STUDENT, stdRef, "preprodPutBw", preprodPutBw);
    score += preprodPutBw;

    var preprodGetBw = checkGetBw(preprodApiUrl);
    saveScore(SCORE_PER_STUDENT, stdRef, "preprodGetBw", preprodGetBw);
    score += preprodGetBw;

    var prodPutBw = checkPutBw(prodApiUrl);
    saveScore(SCORE_PER_STUDENT, stdRef, "prodPutBw", prodPutBw);
    score += prodPutBw;

    var prodGetBw = checkGetBw(prodApiUrl);
    saveScore(SCORE_PER_STUDENT, stdRef, "prodGetBw", prodGetBw);
    score += prodGetBw;

    Log.info("[... Correction d'un étudiant] Réf étudiante : " + stdRef + ", points obtenus : " + score + "\n");
    saveScore(SCORE_PER_STUDENT, stdRef, "all", score);
    return score;
  }

  @Override
  public Map<String, Integer> scorePerQuestion() {
    return SCORE_PER_STUDENT.get(stdRef);
  }

  int levelBonus() {
    Log.info("Attribution de bonus si L2");

    if ("H1".equals(group) || "H2".equals(group)) {
      Log.info("Bonus niveau L2 : 4 points");
      return 4;
    }
    return 0;
  }

  int namingMalus() {
    Log.info("Vérification de noms: stdRef et nom du projet");

    try {
      var score = 0;
      if (!stdRef.equals(stdRef.trim())) {
        Log.error("Espace superflu dans stdRef : malus -1 point");
        score -= 1;
      }

      if (!stdRef.equals(stdRef.toUpperCase())) {
        Log.error("stdRef non majuscule : malus -1 point");
        score -= 1;
      }

      if (!gitUrl.toLowerCase().contains(stdRef.toLowerCase())) {
        Log.error("stdRef non trouvée dans nom du projet git : malus -2 points");
        score -= 2;
      }

      return score;
    } catch (Exception e) {
      Log.error("Problème critique dans le nommage : malus -4 points");
      return -4;
    }
  }

  int pojaVersion() {
    Log.info("Verification de la version de poja (11.2.0)");

    try {
      var score = 0;

      var repoName = randomRepoName(stdRef);
      var branchName = "preprod";
      Log.info("Clonant : " + gitUrl + ", branche : " + branchName);
      cloneRepo(new URL(gitUrl), repoName, branchName);
      Log.info("Dépôt cloné : 1 point (mais quelle gentillesse...)");
      score += 1;

      var pojaConf = Files.readString(Path.of(repoName + "/poja.yml"));
      if (pojaConf.contains("11.2.0")) {
        Log.info("poja v11.2.0 trouvée : 2 points");
        score += 2;
      } else {
        Log.error("poja v11.2.0 non trouvée : 0 point");
      }

      return score;
    } catch (Exception e) {
      Log.error("Version de poja non reconnue : 0 point");
      return 0;
    }
  }

  int checkRun(String branchName, String runUrl, List<String> toLookFor) {
    Log.info("health-check ? branch=" + branchName + ", runUrl=" + runUrl + ", toLookFor=" + toLookFor);

    try {
      var score = 0;

      var repoName = randomRepoName(stdRef);
      Log.info("Clonant : " + gitUrl + ", branche : " + branchName);
      cloneRepo(new URL(gitUrl), repoName, branchName);

      var runAndJob = runUrl.replace(gitUrl, "").split("/");
      var runId = runAndJob[runAndJob.length - 3];
      var ghRunView = String.format("cd %s && gh run view %s", repoName, runId);
      Log.info(ghRunView);
      var runView = execShCmdIn1mn(ghRunView, true);
      if (toLookFor.stream().anyMatch(runView::contains)) {
        Log.info("Trouvé : 2 points");
        score += 2;
        return score;
      } else {
        Log.error("Non trouvé : 0 point");
        return 0;
      }
    } catch (Exception e) {
      Log.error("Erreur : " + e);
      return 0;
    }
  }

  int checkPutBw(String baseUrl) {
    var target = baseUrl + "/black-and-white/" + stdRef;
    Log.info("PUT " + target);

    try {
      var score = 0;

      var imageUrl = getClass().getClassLoader().getResource("./sarisary/poja.png");
      var imageFile = new File(imageUrl.toURI());
      var request = HttpRequest.newBuilder()
          .uri(URI.create(target))
          .PUT(BodyPublishers.ofFile(imageFile.toPath()))
          .build();
      HttpResponse<String> response = newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
      var statusCode = response.statusCode();
      if (statusCode == 200) {
        Log.info("200 OK reçu : 3 points");
        score += 3;
      } else {
        Log.error("Statut http reçu : " + statusCode);
      }

      return score;
    } catch (Exception e) {
      Log.error("Erreur : " + e);
      return 0;
    }
  }

  int checkGetBw(String baseUrl) {
    var target = baseUrl + "/black-and-white/" + stdRef;
    Log.info("GET " + target);

    try {
      var score = 0;

      var request = HttpRequest.newBuilder()
          .uri(URI.create(target))
          .GET()
          .build();
      HttpResponse<String> response = newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
      var statusCode = response.statusCode();
      if (statusCode == 200) {
        Log.info("200 OK reçu : 1 point");
        score += 1;
      } else {
        Log.error("Statut http reçu : " + statusCode);
      }

      var bw = new ObjectMapper().readValue(response.body(), BlackAndWhite.class);
      Log.info("Format de réponse correct, déserialisation OK : 1 point");
      score += 1;

      if (isPresignedSarisaryUrl(bw.getOriginal_url()) && isPresignedSarisaryUrl(bw.getTransformed_url())) {
        Log.info("Les Url sont présignées, et sont de poja.sarisary : 1 point");
        score += 1;
      } else {
        Log.error("Les URL ne sont pas présignées, ou ne sont pas de poja.sarisary : " + bw);
      }

      return score;
    } catch (Exception e) {
      Log.error("Erreur : " + e);
      return 0;
    }
  }

  private boolean isPresignedSarisaryUrl(String url) {
    return url.toLowerCase().contains("prod-bucket-poja-sarisary-" + stdRef.toLowerCase()) &&
        url.contains("X-Amz-Security-Token");
  }
}
