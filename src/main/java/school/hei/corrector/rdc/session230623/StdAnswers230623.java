package school.hei.corrector.rdc.session230623;

import school.hei.corrector.StdAnswers;
import school.hei.utils.Log;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Map;

import static java.net.http.HttpClient.newHttpClient;
import static school.hei.corrector.rdc.session230623.ExamSession230623.SCORE_PER_STUDENT;
import static school.hei.utils.Utils.saveScore;

public record StdAnswers230623(
    String stdRef,
    String theory, String q1, String q4a, String q4b)
    implements StdAnswers {

  @Override
  public int correct() {
    Log.info("[Correction d'un étudiant...] Réf étudiante : " + stdRef);
    int score = 0;

    score = score + correctTheory();
    score = score + correctQ4A();
    score = score + correctQ4B();

    Log.info("[... Correction d'un étudiant] Réf étudiante : " + stdRef + ", points obtenus : " + score + "\n");
    saveScore(SCORE_PER_STUDENT, stdRef, "all", score);
    return score;
  }

  @Override
  public Map<String, Integer> scorePerQuestion() {
    return SCORE_PER_STUDENT.get(stdRef);
  }

  public int correctTheory() {
    Log.info("[Theorie]");

    var theoryScore = Integer.parseInt(theory.split("/")[0].trim());
    Log.info("Score théorique initial: " + theoryScore + " point(s)");
    if ("Entre 400 000 de 500 000 pavés".equals(q1.trim())) {
      Log.info("A reçu 1 point par erreur, la réponse était 40 millions à 50 millions: annulation sur le score théorique:" +
          " -1 point");
      return theoryScore - 1;
    }
    return theoryScore;
  }

  public int correctQ4A() {
    Log.info("[Q4A]");
    var score = 0;

    var baseUriStr = q4a == null ? "" : q4a.split("\\?")[0].trim();
    var httpClient = newHttpClient();
    try {
      var trueRequest = HttpRequest.newBuilder().uri(new URI(baseUriStr + "?longitude=48")).GET().build();
      var trueResponse = httpClient.send(trueRequest, BodyHandlers.ofString());
      if (trueResponse.statusCode() == 200 && trueResponse.body().contains("true")) {
        Log.info("Réponse attendue : true en code 200, et a été obtenue: 1 point");
        score++;
      } else {
        Log.error("Réponse attendue : true en code 200, mais a obtenu: " + trueResponse);
      }

      var falseRequest = HttpRequest.newBuilder().uri(new URI(baseUriStr + "?longitude=5")).GET().build();
      var falseResponse = httpClient.send(falseRequest, BodyHandlers.ofString());
      if (falseResponse.statusCode() == 200 && falseResponse.body().contains("false")) {
        Log.info("Réponse attendue : false en code 200, et a été obtenue: 1 point");
        score++;
      } else {
        Log.error("Réponse attendue : false en code 200, mais a obtenu: " + falseResponse);
      }
    } catch (Exception e) {
      Log.error("Impossible d'appeller baseUrl=" + baseUriStr);
    }

    return score;
  }

  public int correctQ4B() {
    Log.info("[Q4B]");
    var score = 0;

    var baseUriStr = q4b == null ? "" : q4b.split("\\?")[0].trim();
    var httpClient = newHttpClient();
    try {
      var trueRequest = HttpRequest.newBuilder()
          .uri(new URI(baseUriStr + "?longitude=48"))
          .header("Authorization", "bearer jesuisleprm")
          .GET()
          .build();
      var trueResponse = httpClient.send(trueRequest, BodyHandlers.ofString());
      if (trueResponse.statusCode() == 200 && trueResponse.body().contains("true")) {
        Log.info("Réponse attendue : true en code 200, et a été obtenue: 1 point");
        score++;
      } else {
        Log.error("Réponse attendue : true en code 200, mais a obtenu: " + trueResponse);
      }
      var falseRequest = HttpRequest.newBuilder()
          .uri(new URI(baseUriStr + "?longitude=5"))
          .header("Authorization", "bearer jenesuispassuisleprm")
          .GET()
          .build();
      var forbiddenResponse = httpClient.send(falseRequest, BodyHandlers.ofString());
      if (forbiddenResponse.statusCode() == 403) {
        Log.info("Réponse attendue : code 403, et a été obtenue: 1 point");
        score++;
      } else {
        Log.error("Réponse attendue : code 403, mais a obtenu: " + trueResponse);
      }
    } catch (Exception e) {
      Log.error("Impossible d'appeller baseUrl=" + baseUriStr);
    }

    return score;
  }
}
