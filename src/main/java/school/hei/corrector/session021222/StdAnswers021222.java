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

        Log.info("[... Correction d'un étudiant] Réf étudiante : " + stdRef + ", points obtenus : " + score);
        return score;
    }

    public int correctQ21P1() {
        try {
            String branch = "dev";
            Log.info("[Q21P1...] Clonage de la " + branch + " depuis " + q21p1);
            cloneRepo(randomRepoName(), branch);

            Log.info("[...Q21P1] 1 point.");
            return 1;
        } catch (Exception e) {
            Log.error("[...Q21P1] " + e);
            return 0;
        }
    }

    public int correctQ21P2() {
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

            var title = vazo.get(0).trim();
            if (!title.contains("Vazon'ny lavitra")) {
                Log.error("[...Q21P2] Le titre est mauvais : " + title);
                return 0;
            }

            Log.info("[...Q21P2] 1 point.");
            return 1;
        } catch (Exception e) {
            Log.error("[...Q21P2] " + e);
            return 0;
        }
    }

    public int correctQ21P3() {
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

            var and1 = vazo.get(1).trim();
            if (!and1.contains("Andininy voalohany")) {
                Log.error("[...Q21P3] Le and1 est mauvais : " + and1);
                return 0;
            }

            Log.info("[...Q21P3] 1 point.");
            return 1;
        } catch (Exception e) {
            Log.error("[...Q21P3] " + e);
            return 0;
        }
    }

    private static List<String> vazoMustHaveExpectedLinesNb(String repoName, int expectedLinesNb) throws IOException {
        var vazo = Files.readAllLines(Path.of(repoName + "/vazo.txt"));
        var vazoSize = vazo.size();
        if (vazoSize != expectedLinesNb) {
            throw new RuntimeException("Vazo ne doit avoir que _2_ lignes au lieu de : " + vazoSize);
        }
        return vazo;
    }

    public int correctQ21P4() {
        try {
            String branch = "feat-and2";
            Log.info("[Q21P4...] Vérification de " + q21p4);
            String randomRepoName = randomRepoName();
            Git git = branchMustHaveCommit(randomRepoName, branch, q21p4);

            git
                    .checkout()
                    .setName(q21p4)
                    .call();
            List<String> vazo = vazoMustHaveExpectedLinesNb(randomRepoName, 2);

            var and2 = vazo.get(1).trim();
            if (!and2.contains("Andininy faharoa")) {
                Log.error("[...Q21P4] Le and2 est mauvais : " + and2);
                return 0;
            }

            Log.info("[...Q21P4] 1 point.");
            return 1;
        } catch (Exception e) {
            Log.error("[...Q21P4] " + e);
            return 0;
        }
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
}
