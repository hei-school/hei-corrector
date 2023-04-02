package school.hei.corrector.session3003232;

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

import static school.hei.corrector.session3003232.ExamSession3003232.SCORE_PER_STUDENT;
import static school.hei.utils.Utils.branchMustHaveCommit;
import static school.hei.utils.Utils.cloneRepo;
import static school.hei.utils.Utils.execShCmdInMn;
import static school.hei.utils.Utils.getCandidateBranchNameFromPR;
import static school.hei.utils.Utils.getCandidateRepoUrlFromPR;
import static school.hei.utils.Utils.getGHPullRequest;
import static school.hei.utils.Utils.randomRepoName;
import static school.hei.utils.Utils.saveScore;

public record StdAnswers3003232(
        String stdRef,
        int theoryScore, String q4, String q5)
        implements StdAnswers {

    @Override
    public int correct() {
        Log.info("[Correction d'un étudiant...] Réf étudiante : " + stdRef);
        int score = 0;

        // Theory
        score += theoryScore;
        saveScore(SCORE_PER_STUDENT, stdRef, "THEORY", theoryScore);
        Log.info(String.format("Théorie : %d points / 7 points", theoryScore));
        // Practice
        score = score + correctQ4();
        score = score + correctQ5();

        Log.info("[... Correction d'un étudiant] Réf étudiante : "
            + stdRef + ", points obtenus : " + score + " points / 11 points\n");
        return score;
    }

    @Override
    public Map<String, Integer> scorePerQuestion() {
        return SCORE_PER_STUDENT.get(stdRef);
    }

    public int correctQ4() {
        var score = 0;
        try {
            URL prURL = new URL(q4);
            GHPullRequest ghPr = getGHPullRequest(prURL);
            Log.info("[Q4...] Analyse de la PR " + prURL);

            // N'a pas dépassé la date limite
            Date prUpdatedAt = ghPr.getUpdatedAt();
            Date deadline = Date.from(Instant.parse("2023-03-30T07:45:00Z"));
            if (prUpdatedAt.compareTo(deadline) > 0 &&
                // Following students have been allowed to submit with delay
                !stdRef.equals("STD21102")) {
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

            Log.info("[...Q4] " + score + " / 2 points.");
        } catch (Exception e) {
            Log.error("[...Q4] " + e);
        }

        saveScore(SCORE_PER_STUDENT, stdRef, "Q4", score);
        return score;
    }

    public int correctQ5() {
        var score = 0;
        try {
            URL prURL = new URL(q4);
            URL candidateRepoUrl = getCandidateRepoUrlFromPR(prURL);
            String branch = getCandidateBranchNameFromPR(prURL);
            Log.info("[Q5...] Vérification de " + q5 + " dans " + branch);
            cloneRepo(candidateRepoUrl, randomRepoName(stdRef), branch);

            String randomRepoName = randomRepoName(stdRef);
            Git git = branchMustHaveCommit(candidateRepoUrl, randomRepoName, branch, q5);
            git
                    .checkout()
                    .setName(q5)
                    .call();

            Log.info(execShCmdInMn(String.format("cd %s ; npm install --legacy-peer-deps", randomRepoName), false, 10));
            String testsLog = execShCmdInMn(String.format("cd %s ; cd src/__tests__ ; mv Student.cy.js Student ; rm *.cy.js ; mv Student Student.cy.js ; cd ../.. ; CI=true npm test", randomRepoName), true, 3);
            if (testsLog.contains("Tests:\u001B[39m        \u001B[31m1") || testsLog.contains("Skipped:\u001B[39m      \u001B[31m1") || testsLog.contains("Skipped:\u001B[39m      \u001B[31m2")) { //TODO: should be only one if exam is not broken
                Log.info("Les tests cassés sont exécutés");
                score += 3;
            }
            Log.info(testsLog);

            Log.info(String.format("[...Q5] %d points / 3 points", score));
        } catch (Exception e) {
            Log.error("[...Q5] " + e);
        }

        saveScore(SCORE_PER_STUDENT, stdRef, "Q5", score);
        return score;
    }
}
