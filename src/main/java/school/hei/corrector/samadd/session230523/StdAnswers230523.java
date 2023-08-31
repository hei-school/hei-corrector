package school.hei.corrector.samadd.session230523;

import school.hei.corrector.StdAnswers;
import school.hei.utils.Log;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Map;

import static java.net.http.HttpClient.newHttpClient;
import static school.hei.corrector.samadd.session230523.ExamSession230523.SCORE_PER_STUDENT;
import static school.hei.utils.Utils.saveScore;

public record StdAnswers230523(
    String stdRef,
    String uri)
    implements StdAnswers {

  @Override
  public int correct() {
    Log.info("[Correction d'un étudiant...] Réf étudiante : " + stdRef);
    int score = 0;

    score = score + correctUri();

    Log.info("[... Correction d'un étudiant] Réf étudiante : " + stdRef + ", points obtenus : " + score + "\n");
    saveScore(SCORE_PER_STUDENT, stdRef, "all", score);
    return score;
  }

  @Override
  public Map<String, Integer> scorePerQuestion() {
    return SCORE_PER_STUDENT.get(stdRef);
  }

  public int correctUri() {
    var score = 0;

    var baseUriStr = uri == null ? "" : uri.split("\\?")[0].trim();
    var httpClient = newHttpClient();
    try {
      var trueRequest = HttpRequest.newBuilder().uri(new URI(baseUriStr + "?a=48&b=3")).GET().build();
      var trueResponse = httpClient.send(trueRequest, BodyHandlers.ofString());
      if (trueResponse.statusCode() == 200 && trueResponse.body().contains("51")) {
        Log.info("Réponse attendue : 51 en code 200, et a été obtenue: 1 point");
        score++;
      } else {
        Log.error("Réponse attendue : 51 en code 200, mais a obtenu: " + trueResponse);
      }
    } catch (Exception e) {
      Log.error("Impossible d'appeller baseUrl=" + baseUriStr);
    }

    return score;
  }
}
