package school.hei.corrector.soratra;

import com.fasterxml.jackson.databind.ObjectMapper;
import school.hei.corrector.StdAnswers;
import school.hei.utils.Log;

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
import static school.hei.corrector.soratra.ExamSession250324.SCORE_PER_STUDENT;
import static school.hei.utils.Utils.cloneRepo;
import static school.hei.utils.Utils.execShCmdIn1mn;
import static school.hei.utils.Utils.randomRepoName;
import static school.hei.utils.Utils.saveScore;

public record StdAnswers250324(
    String stdRef,
    String personalHosting,
    String gitUrl,
    String preprodDbHealthUrl,
    String preprodBucketHealthUrl,
    String preprodApiUrl)
    implements StdAnswers {

  @Override
  public int correct() {
    Log.info("[Correction d'un étudiant...] Réf étudiante : " + stdRef);
    var score = 0;

    var levelBonus = personalHostingBonus();
    saveScore(SCORE_PER_STUDENT, stdRef, "personalHostingBonus", levelBonus);
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
    saveScore(SCORE_PER_STUDENT, stdRef, "preprodBucketCheck", preprodBucketCheck);
    score += preprodBucketCheck;

    var preprodPutBw = checkPutSoratra(preprodApiUrl);
    saveScore(SCORE_PER_STUDENT, stdRef, "preprodPutSoratra", preprodPutBw);
    score += preprodPutBw;

    var preprodGetBw = checkGetBw(preprodApiUrl);
    saveScore(SCORE_PER_STUDENT, stdRef, "preprodGetSoratra", preprodGetBw);
    score += preprodGetBw;

    Log.info("[... Correction d'un étudiant] Réf étudiante : " + stdRef + ", points obtenus : " + score + "\n");
    saveScore(SCORE_PER_STUDENT, stdRef, "all", score);
    return score;
  }

  @Override
  public Map<String, Integer> scorePerQuestion() {
    return SCORE_PER_STUDENT.get(stdRef);
  }

  int personalHostingBonus() {
    Log.info("Attribution de bonus si hébergement personnel");

    if ("Sur vos subnets privés personnels".equals(personalHosting)) {
      Log.info("Bonus hébergement : 4 points");
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
    Log.info("Verification de la version de poja (12.4.1)");

    var score = 0;
    try {

      var repoName = randomRepoName(stdRef);
      var branchName = "preprod";
      Log.info("Clonant : " + gitUrl + ", branche : " + branchName);
      cloneRepo(new URL(gitUrl), repoName, branchName);
      Log.info("Dépôt cloné : 0 point (et oui, plus de de bonus pour ça, fin de la gentillesse...)");
      score += 0;

      var pojaConf = Files.readString(Path.of(repoName + "/poja.yml"));
      if (pojaConf.contains("12.4.1")) {
        Log.info("poja v12.4.1 trouvée : 2 points");
        score += 2;
      } else {
        Log.error("poja v12.4.1 non trouvée : 0 point");
      }

      return score;
    } catch (Exception e) {
      Log.error("Version de poja non reconnue : 0 point");
      return score;
    }
  }

  int checkRun(String branchName, String runUrl, List<String> toLookFor) {
    Log.info("health-check ? branch=" + branchName + ", runUrl=" + runUrl + ", toLookFor=" + toLookFor);

    var score = 0;
    try {

      var repoName = randomRepoName(stdRef);
      Log.info("Clonant : " + gitUrl + ", branche : " + branchName);
      cloneRepo(new URL(gitUrl), repoName, branchName);

      var runAndJob = runUrl.replace(gitUrl, "").split("/");
      var runId = runAndJob[runAndJob.length - 3];
      var ghRunView = String.format("cd %s && gh run view %s", repoName, runId);
      Log.info(ghRunView);
      var runView = execShCmdIn1mn(ghRunView, true);
      if (toLookFor.stream().anyMatch(runView::contains)) {
        Log.info("Trouvé : 3 points");
        score += 3;
        return score;
      } else {
        Log.error("Non trouvé : 0 point");
        return 0;
      }
    } catch (Exception e) {
      Log.error("Erreur : " + e);
      return score;
    }
  }

  int checkPutSoratra(String baseUrl) {
    var target = baseUrl + "/soratra/" + stdRef;
    Log.info("PUT " + target);

    var score = 0;
    try {

      var request = HttpRequest.newBuilder()
          .uri(URI.create(target))
          .PUT(BodyPublishers.ofString("ry lanitra mangamanga"))
          .build();
      HttpResponse<String> response = newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
      var statusCode = response.statusCode();
      if (statusCode == 200) {
        Log.info("200 OK reçu : 6 points");
        score += 6;
      } else {
        Log.error("Statut http reçu : " + statusCode);
      }

      return score;
    } catch (Exception e) {
      Log.error("Erreur : " + e);
      return score;
    }
  }

  int checkGetBw(String baseUrl) {
    var target = baseUrl + "/soratra/" + stdRef;
    Log.info("GET " + target);

    var score = 0;
    try {

      var request = HttpRequest.newBuilder()
          .uri(URI.create(target))
          .GET()
          .build();
      HttpResponse<String> response = newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
      var statusCode = response.statusCode();
      if (statusCode == 200) {
        Log.info("200 OK reçu : 2 points");
        score += 2;
      } else {
        Log.error("Statut http reçu : " + statusCode);
      }

      var soratra = new ObjectMapper().readValue(response.body(), Soratra.class);
      Log.info("Format de réponse correct, déserialisation OK : 2 points");
      score += 2;

      if (isPresignedSoratraUrl(soratra.getOriginal_url()) && isPresignedSoratraUrl(soratra.getTransformed_url())) {
        Log.info("Les Url sont présignées, et sont de poja.soratra : 2 points");
        score += 2;
      } else {
        Log.error("Les URL ne sont pas présignées, ou ne sont pas de poja.sarisary : " + soratra);
      }

      return score;
    } catch (Exception e) {
      Log.error("Erreur : " + e);
      return score;
    }
  }

  private boolean isPresignedSoratraUrl(String url) {
    return url.toLowerCase().contains("prod-bucket-poja-soratra-" + stdRef.toLowerCase()) &&
        url.contains("X-Amz-Security-Token");
  }
}
