package school.hei.corrector;

import school.hei.corrector.session021222.ExamSession021222;
import school.hei.utils.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Corrector {

    public static void main(String[] args) {
        ExamSession examSession02122 = new ExamSession021222();
        Log.info("Correction de l'examen : " + examSession02122.name());

        var corrector = new Corrector();
        switch (args.length) {
            case 0 -> corrector.correct(examSession02122);
            case 1 -> corrector.correct(examSession02122, args[0]);
            default -> Log.error("""
                    Donnez 0 argument pour corriger tous les étudiants.
                    Donnez 1 argument (la réf étudiante) pour corriger un étudiant particulier.
                    """);
        }
    }

    public Map<String, Map<String, Integer>> correct(ExamSession examSession) {
        var stdAnswersMappedByStdRef = stdAnswersMappedByStdRef(examSession);
        stdAnswersMappedByStdRef.forEach((_stdRef, stdAnswers) -> {
            stdAnswers.correct();
        });

        Log.info("\n##################################\n##################################\n");
        Log.info("Toutes les notes...\n");
        Log.info(examSession.scorePerStudentAsString());
        return examSession.scorePerStudent();
    }

    public int correct(ExamSession examSession, String studentRef) {
        return readStdAnswers(examSession, studentRef).correct();
    }

    public StdAnswers readStdAnswers(ExamSession examSession, String stdRef) {
        return stdAnswersMappedByStdRef(examSession).get(stdRef);
    }

    private Map<String, StdAnswers> stdAnswersMappedByStdRef(ExamSession examSession) {
        var answersPath = examSession.answersSheetPath();
        Log.info("[Chargement des réponses...] Nom du fichier : " + answersPath);

        try (InputStream in = getClass().getResourceAsStream(answersPath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            var map = new HashMap<String, StdAnswers>();
            var linesNb = reader.lines()
                    .map(examSession::toStdAnswers)
                    .peek(stdAnswers -> map.put(stdAnswers.stdRef(), stdAnswers))
                    .count();
            Log.info("[... Chargement des réponses] Nombre de réponses chargées : " + linesNb);
            return map;
        } catch (IOException e) {
            Log.error("[... Chargement des réponses] Erreur en lisant le fichier : " + answersPath);
            throw new RuntimeException(e);
        }
    }
}
