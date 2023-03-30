package school.hei.corrector.session3003231;

import org.eclipse.jgit.api.Git;
import org.kohsuke.github.GHPullRequest;
import school.hei.corrector.StdAnswers;
import school.hei.utils.Log;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import static school.hei.corrector.session3003231.ExamSession3003231.SCORE_PER_STUDENT;
import static school.hei.utils.Utils.branchMustHaveCommit;
import static school.hei.utils.Utils.cloneRepo;
import static school.hei.utils.Utils.execShCmdIn1mn;
import static school.hei.utils.Utils.getCandidateBranchNameFromPR;
import static school.hei.utils.Utils.getCandidateRepoUrlFromPR;
import static school.hei.utils.Utils.getGHPullRequest;
import static school.hei.utils.Utils.randomRepoName;
import static school.hei.utils.Utils.saveScore;

public record StdAnswers3003231(
        String stdRef,
        int theoryScore, String q3, String q4)
        implements StdAnswers {

    @Override
    public int correct() {
        Log.info("[Correction d'un étudiant...] Réf étudiante : " + stdRef);
        int score = 0;

        // Theory
        score += theoryScore;
        saveScore(SCORE_PER_STUDENT, stdRef, "THEORY", theoryScore);
        Log.info(String.format("Théorie : %d points / 4 points", theoryScore));
        // Practice
        score = score + correctQ3();
        score = score + correctQ4();

        Log.info("[... Correction d'un étudiant] Réf étudiante : "
            + stdRef + ", points obtenus : " + score + " points / 12 points\n");
        return score;
    }

    @Override
    public Map<String, Integer> scorePerQuestion() {
        return SCORE_PER_STUDENT.get(stdRef);
    }

    public int correctQ3() {
        var score = 0;
        try {
            URL prURL = new URL(q3);
            GHPullRequest ghPr = getGHPullRequest(prURL);
            Log.info("[Q3...] Analyse de la PR " + prURL);

            // N'a pas dépassé la date limite
            Date prUpdatedAt = ghPr.getUpdatedAt();
            Date deadline = Date.from(Instant.parse("2023-03-30T07:45:00Z"));
            if (prUpdatedAt.compareTo(deadline) > 0) {
                score -= 1_000;
                saveScore(SCORE_PER_STUDENT, stdRef, "Q3", score);
                throw new RuntimeException(
                    "[ELIMINATOIRE] Vous avez modifié votre PR après le délai limite : " + prUpdatedAt + ", " + deadline);
            }

            // Pas trop de lignes
            if (ghPr.getDeletions() + ghPr.getAdditions() > 100) {
                throw new RuntimeException(
                    "Trop de lignes modifiées (>100) : le reviewer aurait rejeté votre PR sans la lire");
            } else {
                Log.info("PR lisible car pas trop de lignes (<100) : 2 points");
                score += 2;
            }

            Log.info("[...Q3] " + score + " / 2 points.");
        } catch (Exception e) {
            Log.error("[...Q3] " + e);
        }

        saveScore(SCORE_PER_STUDENT, stdRef, "Q3", score);
        return score;
    }

    public int correctQ4() {
        var score = 0;
        try {
            URL prURL = new URL(q3);
            URL candidateRepoUrl = getCandidateRepoUrlFromPR(prURL);
            String branch = getCandidateBranchNameFromPR(prURL);
            Log.info("[Q4...] Vérification de " + q4 + " dans " + branch);
            cloneRepo(candidateRepoUrl, randomRepoName(stdRef), branch);

            String randomRepoName = randomRepoName(stdRef);
            Git git = branchMustHaveCommit(candidateRepoUrl, randomRepoName, branch, q4);
            git
                    .checkout()
                    .setName(q4)
                    .call();

            Log.info(execShCmdIn1mn(String.format("cd %s ; npm install", randomRepoName), false));
            String testsLog = execShCmdIn1mn(String.format("pwd ; cd %s ; CI=true npm test", randomRepoName), true);
            if (testsLog.contains("Tests:       2 passed, 2 total")) {
                Log.info("Tous les tests passent : 6 points");
                score += 6;
            } else {
                Log.info("Des tests échouent : " +
                    "voir si useState, useEffect et setTimeout ont été tenté (1 point de chaque)");
                String counterJs = Files.readString(Path.of(String.format("./%s/src/Counter.js", randomRepoName)));
                if (counterJs.contains("useState")) {
                    Log.info("useState trouvé : 1 point");
                    score += 1;
                }
                if (counterJs.contains("setTimeout")) {
                    Log.info("setTimeout trouvé : 1 point");
                    score += 1;
                }
                if (counterJs.contains("useEffect")) {
                    Log.info("useEffect trouvé : 1 point");
                    score += 1;
                }
            }
            Log.info(testsLog);

            Log.info(String.format("[...Q4] %d points / 6 points", score));
        } catch (Exception e) {
            Log.error("[...Q4] " + e);
        }

        saveScore(SCORE_PER_STUDENT, stdRef, "Q4", score);
        return score;
    }
}
