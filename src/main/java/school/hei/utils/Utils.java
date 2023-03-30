package school.hei.utils;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GitHub;
import school.hei.corrector.session021222.ExamSession021222;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class Utils {
    public static String infallibleSupply(Supplier<String> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            return null;
        }
    }

    public static void saveScore(
        Map<String/*stdRef*/, Map<String/*question*/, Integer>> SCORE_PER_STUDENT,
        String stdRef, String question, int score) {
        Map<String, Integer> scorePerQuestion;

        var oldScorePerQuestion = SCORE_PER_STUDENT.get(stdRef);
        if (oldScorePerQuestion == null) {
            scorePerQuestion = new HashMap<>();
        } else {
            scorePerQuestion = SCORE_PER_STUDENT.get(stdRef);
        }

        scorePerQuestion.put(question, score);
        SCORE_PER_STUDENT.put(stdRef, scorePerQuestion);
    }


    public static String randomRepoName(String stdRef) {
        int randomAppendix = (int) (Math.random() * 1_000_000_000); // so that scoring is idempotent
        return ExamSession021222.class.getSimpleName() + "-" + stdRef + "-" + randomAppendix;
    }

    public static Git cloneRepo(URL remoteUrl, String localRepoName, String branchName) throws GitAPIException {
        var git = Git
            .cloneRepository()
            .setCloneSubmodules(true)
            .setURI(remoteUrl.toString())
            .setBranch(branchName)
            .setCloneSubmodules(true)
            .setDirectory(new File(localRepoName))
            .call();
        Log.info("... clonage réussi dans : " + localRepoName);
        return git;
    }

    public static Git branchMustHaveCommit(URL remoteUrl, String localRepoName, String branch, String commitSha)
        throws GitAPIException {
        Log.info("Vérification du commit : " + commitSha);
        Git git = cloneRepo(remoteUrl, localRepoName, branch);
        if (!branchHasCommit(git, branch, commitSha)) {
            throw new RuntimeException("La branche " + branch + " ne contient pas le commit : " + commitSha);
        }
        return git;
    }

    private static boolean branchHasCommit(Git git, String branchName, String commitSha) throws GitAPIException {
        return git.
            branchList()
            .setContains(commitSha)
            .call()
            .stream()
            .map(Ref::getName)
            .anyMatch(refName -> refName.contains(branchName));
    }

    public static URL getCandidateRepoUrlFromPR(URL prURL) {
        GHPullRequest ghPR = getGHPullRequest(prURL);
        return ghPR.getHead().getRepository().getHtmlUrl();
    }

    public static String getCandidateBranchNameFromPR(URL prURL) {
        GHPullRequest ghPR = getGHPullRequest(prURL);
        return ghPR.getHead().getLabel().split(":")[1];
    }

    public static GHPullRequest getGHPullRequest(URL prURL) {
        try {
            int prNumber = Integer.parseInt(prURL.toString().split("/")[6]);
            String ownerName = getOwnerNameFromPRUrl(prURL);
            String repoName = getRepoNameFromPRUrl(prURL);
            return GitHub
                .connect()
                .getRepository(ownerName + '/' + repoName)
                .getPullRequest(prNumber);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getOwnerNameFromPRUrl(URL prURL) {
        return prURL.toString().split("/")[3];
    }

    private static String getRepoNameFromPRUrl(URL prURL) {
        return prURL.toString().split("/")[4];
    }

    public static String execShCmdIn1mn(String cmd, boolean shouldLogOutput) {
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", cmd});
            if(!process.waitFor(1, TimeUnit.MINUTES)) {
                Log.error("L'exécution a pris trop de temps (>1mn) : abandon de la commande !");
                process.destroyForcibly();
            }
            if (shouldLogOutput) {
                Scanner inputScanner = new Scanner(process.getInputStream()).useDelimiter("\\A");
                String inputMessage = inputScanner.hasNext() ? inputScanner.next() : "";

                Scanner errorScanner = new Scanner(process.getErrorStream()).useDelimiter("\\A");
                String errorMessage = errorScanner.hasNext() ? ("\nError(s):\n" + errorScanner.next()) : "";

                return inputMessage + errorMessage;
            }
            return "";
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
