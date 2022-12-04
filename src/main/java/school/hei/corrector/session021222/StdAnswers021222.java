package school.hei.corrector.session021222;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import school.hei.corrector.StdAnswers;
import school.hei.utils.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static school.hei.corrector.session021222.ExamSession021222.SCORE_PER_STUDENT;
import static school.hei.corrector.session021222.ExamSession021222.saveScore;

public record StdAnswers021222(
        String stdRef,
        String q21p1, String q21p2, String q21p3, String q21p4, String q21p5, String q21p6)
        implements StdAnswers {

    @Override
    public int correct() {
        Log.info("[Correction d'un étudiant...] Réf étudiante : " + stdRef);
        int score = 0;

        score = score + correctQ21P1();
        score = score + correctQ21P2();
        score = score + correctQ21P3();
        score = score + correctQ21P4();
        score = score + correctQ21P5();
        score = score + correctQ21P6();

        Log.info("[... Correction d'un étudiant] Réf étudiante : " + stdRef + ", points obtenus : " + score + "\n");
        return score;
    }

    @Override
    public Map<String, Integer> scorePerQuestion() {
        return SCORE_PER_STUDENT.get(stdRef);
    }

    public int correctQ21P1() {
        var score = 0;
        try {
            String branch = "dev";
            Log.info("[Q21P1...] Clonage de la " + branch + " depuis " + q21p1);
            cloneRepo(randomRepoName(), branch);

            Log.info("[...Q21P1] 1 point.");
            score = 1;
        } catch (Exception e) {
            Log.error("[...Q21P1] " + e);
        }

        saveScore(stdRef, "Q21P1", score);
        return score;
    }

    public int correctQ21P2() {
        var score = 0;
        try {
            String branch = "dev";
            Log.info("[Q21P2...] Vérification de " + q21p2 + " dans " + branch);
            String randomRepoName = randomRepoName();
            Git git = branchMustHaveCommit(randomRepoName, branch, q21p2);

            git
                    .checkout()
                    .setName(q21p2)
                    .call();
            List<String> vazo = vazoMustHaveExpectedLinesNb(randomRepoName, 1);

            titleIsAtExpectedLine(vazo, 0);

            Log.info("[...Q21P2] 1 point.");
            score = 1;
        } catch (Exception e) {
            Log.error("[...Q21P2] " + e);
        }

        saveScore(stdRef, "Q21P2", score);
        return score;
    }

    public int correctQ21P3() {
        var score = 0;

        try {
            String branch = "feat-and1";
            Log.info("[Q21P3...] Vérification de " + q21p3);
            String randomRepoName = randomRepoName();
            Git git = cloneRepo(randomRepoName, branch); // actually commit is NOT in this branch IF rebased at Q21P6

            git
                    .checkout()
                    .setName(q21p3)
                    .call();
            List<String> vazo = vazoMustHaveExpectedLinesNb(randomRepoName, 2);

            titleIsAtExpectedLine(vazo, 0);
            and1IsAtExpectedLine(vazo, 1);

            Log.info("[...Q21P3] 1 point.");
            score = 1;
        } catch (Exception e) {
            Log.error("[...Q21P3] " + e);
        }

        saveScore(stdRef, "Q21P3", score);
        return score;
    }

    public int correctQ21P4() {
        var score = 0;
        try {
            String branch = "feat-and2";
            Log.info("[Q21P4...] Vérification de " + q21p4);
            and2CommitMustBeOnBranch(branch, q21p4);

            Log.info("[...Q21P4] 1 point.");
            score = 1;
        } catch (Exception e) {
            Log.error("[...Q21P4] " + e);
        }

        saveScore(stdRef, "Q21P4", score);
        return score;
    }

    public int correctQ21P5() {
        var score = 0;
        try {
            String branch = "dev";
            Log.info("[Q21P5...] Vérification de " + q21p5);
            and2CommitMustBeOnBranch(branch, q21p5);

            Log.info("[...Q21P5] 1 point.");
            score = 1;
        } catch (Exception e) {
            Log.error("[...Q21P5] " + e);
        }

        saveScore(stdRef, "Q21P5", score);
        return score;
    }

    public int correctQ21P6() {
        var score = 0;
        try {
            String branch = "dev";
            Log.info("[Q21P6...] Vérification de " + q21p6 + " dans " + branch);
            String randomRepoName = randomRepoName();
            Git git = branchMustHaveCommit(randomRepoName, branch, q21p6);

            git
                    .checkout()
                    .setName(q21p6)
                    .call();
            List<String> vazo = vazoMustHaveExpectedLinesNb(randomRepoName, 3);

            titleIsAtExpectedLine(vazo, 0);
            and1IsAtExpectedLine(vazo, 1);
            and2IsAtExpectedLine(vazo, 2);

            Log.info("[...Q21P6] 1 point.");
            score = 1;
        } catch (Exception e) {
            Log.error("[...Q21P6] " + e);
        }

        saveScore(stdRef, "Q21P6", score);
        return score;
    }

    private String randomRepoName() {
        int randomAppendix = (int) (Math.random() * 1_000_000_000); // so that scoring is idempotent
        return ExamSession021222.class.getSimpleName() + " - " + stdRef + " - " + randomAppendix;
    }

    private Git cloneRepo(String randomRepoName, String branch) throws GitAPIException {
        var git = Git
                .cloneRepository()
                .setCloneSubmodules(true)
                .setURI(q21p1)
                .setBranch(branch)
                .setCloneSubmodules(true)
                .setDirectory(new File(randomRepoName))
                .call();
        Log.info("... clonage réussi dans : " + randomRepoName);
        return git;
    }

    private Git branchMustHaveCommit(String repoName, String branch, String commitSha)
            throws GitAPIException {
        Log.info("Vérification du commit : " + commitSha);
        Git git = cloneRepo(repoName, branch);
        if (!branchHasCommit(git, branch, commitSha)) {
            throw new RuntimeException("La branche " + branch + " ne contient pas le commit : " + commitSha);
        }
        return git;
    }

    private boolean branchHasCommit(Git git, String branchName, String commitSha) throws GitAPIException {
        return git.
                branchList()
                .setContains(commitSha)
                .call()
                .stream()
                .map(Ref::getName)
                .anyMatch(refName -> refName.contains(branchName));
    }

    private static List<String> vazoMustHaveExpectedLinesNb(String repoName, int expectedLinesNb) throws IOException {
        var vazo = Files.readAllLines(Path.of(repoName + "/vazo.txt"));
        var vazoSize = vazo.size();
        if (vazoSize != expectedLinesNb) {
            throw new RuntimeException("Vazo doit avoir " + expectedLinesNb + "  lignes au lieu de : " + vazoSize);
        }
        return vazo;
    }

    private void titleIsAtExpectedLine(List<String> vazoLines, int lineNb) {
        var and2 = vazoLines.get(lineNb).trim();
        if (!and2.contains("Vazon'ny lavitra")) {
            throw new RuntimeException("Le titre est mauvais : " + and2);
        }
    }

    private void and1IsAtExpectedLine(List<String> vazoLines, int lineNb) {
        var and2 = vazoLines.get(lineNb).trim();
        if (!and2.contains("Andininy voalohany")) {
            throw new RuntimeException("Le and1 est mauvais : " + and2);
        }
    }

    private void and2IsAtExpectedLine(List<String> vazoLines, int lineNb) {
        var and2 = vazoLines.get(lineNb).trim();
        if (!and2.contains("Andininy faharoa")) {
            throw new RuntimeException("Le and2 est mauvais : " + and2);
        }
    }

    private void and2CommitMustBeOnBranch(String branch, String commitSha) throws GitAPIException, IOException {
        String randomRepoName = randomRepoName();
        Git git = branchMustHaveCommit(randomRepoName, branch, commitSha);

        git
                .checkout()
                .setName(commitSha)
                .call();
        List<String> vazo = vazoMustHaveExpectedLinesNb(randomRepoName, 2);

        titleIsAtExpectedLine(vazo, 0);
        and2IsAtExpectedLine(vazo, 1);
    }
}
