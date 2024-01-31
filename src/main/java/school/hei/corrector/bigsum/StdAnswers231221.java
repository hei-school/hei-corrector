package school.hei.corrector.bigsum;

import org.eclipse.jgit.api.Git;
import school.hei.corrector.StdAnswers;
import school.hei.utils.Log;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static java.lang.Integer.parseInt;
import static java.net.http.HttpClient.newHttpClient;
import static school.hei.corrector.bigsum.ExamSession231221.SCORE_PER_STUDENT;
import static school.hei.utils.Utils.branchMustHaveCommit;
import static school.hei.utils.Utils.execShCmdIn1mn;
import static school.hei.utils.Utils.randomRepoName;
import static school.hei.utils.Utils.saveScore;

public class StdAnswers231221 implements StdAnswers {
  private final String stdRef, theory, q3a, q3b, q4, q6, q7a, q7b, q8a;

  public StdAnswers231221(String stdRef,
                          String theory,
                          String q3a, String q3b, String q4, String q6, String q7a, String q7b, String q8a) {
    this.stdRef = stdRef;
    this.theory = theory;
    this.q3a = q3a;
    this.q3b = q3b;
    this.q4 = q4;
    this.q6 = q6;
    this.q7a = q7a;
    this.q7b = q7b;
    this.q8a = q8a;
  }

  @Override
  public String stdRef() {
    return stdRef;
  }

  @Override
  public int correct() {
    Log.info("[Correction d'un étudiant...] Réf étudiante : " + stdRef);
    int score = 0;

    score = score + correctTheory();
    score = score + correctQ3();
    score = score + correctQ4();
    score = score + correctQ6();
    score = score + correctQ7();
    score = score + correctQ8();

    Log.info("Bonus de bug : 2/2");
    score += 2;

    Log.info("[... Correction d'un étudiant] Réf étudiante : " + stdRef + ", points obtenus : " + score + "/15\n");
    saveScore(SCORE_PER_STUDENT, stdRef, "all", score);
    return score;
  }

  private int correctTheory() {
    Log.info("[Theorie]");

    var theoryScore = parseInt(theory.split("/")[0].trim());
    Log.info("Points théorique : " + theoryScore + "/6");

    saveScore(SCORE_PER_STUDENT, stdRef, "THEORY", theoryScore);
    return theoryScore;
  }

  private String branchName = "preprod";

  private int correctQ3() {
    Log.info("[Q3a et Q3b] Utilise poja 8.0.x ou 9.0.x ?");

    var q3aScore = 0;
    Git git = null;
    var repoName = randomRepoName(stdRef);
    try {
      Log.info("Clonant : " + q3a + ", branche : " + branchName);
      git = branchMustHaveCommit(new URL(q3a), repoName, branchName, q3b);
      q3aScore++;
    } catch (Exception e) {
      Log.error("Branche non clonable : " + branchName);

      branchName = "prod";
      Log.info("Clonant : " + q3a + ", branche : " + branchName);
      try {
        repoName = randomRepoName(stdRef);
        git = branchMustHaveCommit(new URL(q3a), repoName, branchName, q3b);
        q3aScore++;
      } catch (Exception ex) {
        Log.error("Branche non clonable : " + branchName);
      }
    }

    Log.info("Vérification de la version poja");
    try {
      git.checkout().setName(q3b).call();
    } catch (Exception e) {
      Log.error("Impossible de checkout : " + q3b);
    }
    try {
      var pojaConf = Files.readString(Path.of(repoName + "/poja.yml"));
      var isVersionGood = pojaConf.contains("8.0") || pojaConf.contains("9.0");
      if (isVersionGood) {
        Log.info("Bonne version de poja");
        q3aScore++;
      } else {
        Log.error("Mauvaise version de poja");
      }
    } catch (IOException e) {
      Log.error("poja.yml non lisible");
    }

    Log.info("Points Q3 : " + q3aScore + "/2");
    saveScore(SCORE_PER_STUDENT, stdRef, "Q3", q3aScore);
    return q3aScore;
  }

  private int correctQ4() {
    Log.info("[Q4] health-check sync-stack ok ?");

    var score = 0;
    var repoName = randomRepoName(stdRef);
    try {
      Log.info("Clonant : " + q3a + ", branche : " + branchName);
      branchMustHaveCommit(new URL(q3a), repoName, branchName, q3b);

      try {
        var runAndJob = q4.replace(q3a, "").split("/");
        var runId = runAndJob[runAndJob.length - 3];
        var jobId = runAndJob[runAndJob.length - 1];
        var ghRunView = String.format("cd %s && gh run view %s", repoName, runId);
        Log.info(ghRunView);
        var runView = execShCmdIn1mn(ghRunView, true);
        if (runView.contains("✓ check-sync-stack") && runView.contains(jobId)) {
          score++;
        } else {
          Log.error("Non trouvé : ✓ check-sync-stack && jobId=" + jobId);
        }
      } catch (Exception e) {
        Log.error("Job non lisible: " + q4);
      }
    } catch (Exception e) {
      Log.error("Branche non clonable : " + branchName);
    }

    Log.info("Points Q4 : " + score + "/1");
    saveScore(SCORE_PER_STUDENT, stdRef, "Q4", score);
    return score;
  }

  private int correctQ6() {
    Log.info("[Q6] CI ok ?");

    var score = 0;
    var repoName = randomRepoName(stdRef);
    try {
      Log.info("Clonant : " + q3a + ", branche : " + branchName);
      branchMustHaveCommit(new URL(q3a), repoName, branchName, q3b);

      try {
        var run = q6.replace(q3a, "").split("/");
        var runId = run[run.length - 1];
        var ghRunView = String.format("cd %s && gh run view %s", repoName, runId);
        Log.info(ghRunView);
        var runView = execShCmdIn1mn(ghRunView, true);
        var ciMessage = String.format("✓ %s CI", branchName);
        if (runView.contains(ciMessage)) {
          score++;
        } else {
          Log.error("Non trouvé :" + ciMessage);
        }
      } catch (Exception e) {
        Log.error("Job non lisible: " + q4);
      }
    } catch (Exception e) {
      Log.error("Branche non clonable : " + branchName);
    }

    Log.info("Points Q6 : " + score + "/1");
    saveScore(SCORE_PER_STUDENT, stdRef, "Q6", score);
    return score;
  }

  private int correctQ7() {
    int score = 0;
    Log.info("[Q7] Grosse addition marche ?");

    var baseUri = "";
    try {
      baseUri = q7b.substring(0, q7b.indexOf("/Prod")) + "/Prod/big-sum";
    } catch (Exception e) {
      Log.error("baseUri non trouvé");
    }
    Log.info("baseUri=" + baseUri);
    if ("8".equals(add(baseUri, "1", "7"))) {
      Log.error("1+7==8 ==> 1 points");
      score += 1;
    } else {
      Log.error("1+7!=8");
    }

    if ("100000000000000000000000000007".equals(add(baseUri, "100000000000000000000000000000", "7"))) {
      Log.error("100000000000000000000000000000+7==100000000000000000000000000007 ==> 2 points");
      score += 2;
    } else {
      Log.error("100000000000000000000000000000+7!=100000000000000000000000000007");
    }

    Log.info("Points Q7 : " + score + "/3");
    saveScore(SCORE_PER_STUDENT, stdRef, "Q7", score);
    return score;
  }

  private int correctQ8() {
    int score = 0;

    int p95 = 0;
    try {
      p95 = parseInt(q8a);
    } catch (Exception e) {
      Log.error("p95 non lisible");
    }
    if (p95 <= 3) {
      Log.error("p95 <=3 : non réaliste");
    } else {
      score += 2;
    }

    Log.info("Points Q8 : " + score + "/2");
    saveScore(SCORE_PER_STUDENT, stdRef, "Q8", score);
    return score;
  }


  private String add(String baseUri, String a, String b) {
    try {
      return newHttpClient().send(HttpRequest.newBuilder()
              .GET()
              .uri(new URI(baseUri + String.format("?a=%s&b=%s", a, b)))
              .build(), BodyHandlers.ofString())
          .body();
    } catch (Exception e) {
      Log.error("API invocation failed: " + baseUri);
      return "";
    }
  }

  @Override
  public Map<String, Integer> scorePerQuestion() {
    return SCORE_PER_STUDENT.get(stdRef);
  }
}