package school.hei.corrector.session021222;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import school.hei.corrector.StdAnswers;
import school.hei.utils.Log;

import java.io.File;

import static org.eclipse.jgit.api.Git.cloneRepository;

public record StdAnswers021222(
        String stdRef,
        String q21p1, String q21p2, String q21p3, String q21p4, String q21p5, String q21p6)
        implements StdAnswers {

    private static final int RANDOM_APPENDIX_TO_REPO_FOLDER_NAME =
            // so that scoring is idempotent
            (int) (Math.random() * 1_000_000);

    private String getRepoName() {
        return ExamSession021222.class.getSimpleName() + " - " + stdRef + " - " + RANDOM_APPENDIX_TO_REPO_FOLDER_NAME;
    }

    @Override
    public int correct() {
        Log.info("[Correction d'un étudiant...] Réf étudiante : " + stdRef);
        int score = 0;

        score = score + correctQ21P1();

        Log.info("[... Correction d'un étudiant] Réf étudiante : " + stdRef + ", points obtenus : " + score);
        return score;
    }

    public int correctQ21P1() {
        try {
            cloneRepo();
            return 1;
        } catch (Exception e) {
            Log.info("[... Correction d'un étudiant] Réf étudiante : " + stdRef + ", erreur : " + e);
            return 0;
        }
    }

    private Git cloneRepo() throws GitAPIException {
        CloneCommand cloneCommand = cloneRepository()
                .setCloneSubmodules(true)
                .setURI(q21p1)
                .setBranch("dev")
                .setCloneSubmodules(true)
                .setDirectory(new File(getRepoName()));
        return cloneCommand.call();
    }
}
