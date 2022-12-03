package school.hei.corrector.session021222;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import school.hei.corrector.StdAnswers;
import school.hei.utils.Log;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

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

        Log.info("[... Correction d'un étudiant] Réf étudiante : " + stdRef + ", points obtenus : " + score);
        return score;
    }

    public int correctQ21P1() {
        try {
            Log.info("[Q21P1...] Clonage de la dev depuis " + q21p1);
            cloneRepo(randomRepoName());
            Log.info("[...Q21P1] 1 point.");
            return 1;
        } catch (Exception e) {
            Log.error("[...Q21P1] " + e);
            return 0;
        }
    }

    public int correctQ21P2() {
        try {
            Log.info("[Q21P2...] Vérification du commit : " + q21p2);

            String randomRepoName = randomRepoName();
            Git git = cloneRepo(randomRepoName);

            if (!branchHasCommit(git, "dev", q21p2)) {
                Log.error("[...Q21P2] La dev ne contient pas le commit : " + q21p2);
                return 0;
            }

            git
                    .checkout()
                    .setName(q21p2)
                    .call();
            var vazo = Files.readAllLines(Path.of(randomRepoName + "/vazo.txt"));
            var vazoSize = vazo.size();
            if (vazoSize != 1) {
                Log.error("[...Q21P2] Vazo ne doit avoir que _1_ ligne au lieu de : " + vazoSize);
                return 0;
            }

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

    private String randomRepoName() {
        int randomAppendix = (int) (Math.random() * 1_000_000_000); // so that scoring is idempotent
        return ExamSession021222.class.getSimpleName() + " - " + stdRef + " - " + randomAppendix;
    }

    private Git cloneRepo(String randomRepoName) throws GitAPIException {
        var git = Git
                .cloneRepository()
                .setCloneSubmodules(true)
                .setURI(q21p1)
                .setBranch("dev")
                .setCloneSubmodules(true)
                .setDirectory(new File(randomRepoName))
                .call();
        Log.info("... clonage réussi dans : " + randomRepoName);
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
