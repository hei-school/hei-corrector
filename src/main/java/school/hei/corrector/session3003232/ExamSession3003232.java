package school.hei.corrector.session3003232;

import school.hei.corrector.ExamSession;
import school.hei.corrector.StdAnswers;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static school.hei.utils.Utils.infallibleSupply;

public class ExamSession3003232 implements ExamSession {

    private static final String ANSWERS_SHEET_PATH = "/answers_30-03-23_2.csv";
    private static final int STDREF_INDEX_IN_SHEET = 3;
    private static final int THEORYSCORE_INDEX_IN_SHEET = 2;
    private static final int Q4_INDEX_IN_SHEET = 4;
    private static final int Q5_INDEX_IN_SHEET = 5;
    public static final Map<String/*stdRef*/, Map<String/*question*/, Integer>> SCORE_PER_STUDENT = new HashMap<>();

    @Override
    public String name() {
        return "WEB2+ (1/2) - Session du 30 mars 2023";
    }

    @Override
    public String answersSheetPath() {
        return ANSWERS_SHEET_PATH;
    }

    @Override
    public StdAnswers toStdAnswers(String line) {
        var asArray = line.split(",");
        return new StdAnswers3003232(
                infallibleSupply(() -> asArray[STDREF_INDEX_IN_SHEET].trim()),
                Integer.parseInt(infallibleSupply(() -> asArray[THEORYSCORE_INDEX_IN_SHEET]).split("/")[0].trim()),
                infallibleSupply(() -> asArray[Q4_INDEX_IN_SHEET].trim()),
                infallibleSupply(() -> asArray[Q5_INDEX_IN_SHEET].trim()));
    }

    @Override
    public Map<String, Map<String, Integer>> scorePerStudent() {
        return SCORE_PER_STUDENT;
    }

    @Override
    public String scorePerStudentAsString() {
        return SCORE_PER_STUDENT.keySet().stream().map(stdRef ->
                Stream.of(
                        stdRef,
                        SCORE_PER_STUDENT.get(stdRef).get("THEORY").toString(),
                        SCORE_PER_STUDENT.get(stdRef).get("Q4").toString(),
                        SCORE_PER_STUDENT.get(stdRef).get("Q5").toString()
                ).collect(joining(","))
        ).collect(joining("\n"));
    }
}
