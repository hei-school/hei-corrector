package school.hei.corrector.session3003231;

import school.hei.corrector.ExamSession;
import school.hei.corrector.StdAnswers;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static school.hei.utils.Utils.infallibleSupply;

public class ExamSession3003231 implements ExamSession {

    private static final String ANSWERS_SHEET_PATH = "/answers_30-03-23_1.csv";
    private static final int STDREF_INDEX_IN_SHEET = 3;
    private static final int THEORYSCORE_INDEX_IN_SHEET = 2;
    private static final int Q3_INDEX_IN_SHEET = 4;
    private static final int Q4_INDEX_IN_SHEET = 5;

    public static final Map<String/*stdRef*/, Map<String/*question*/, Integer>> SCORE_PER_STUDENT = new HashMap<>();

    @Override
    public String name() {
        return "PROG3P1 (2/3) - Session du 02 décembre 2022";
    }

    @Override
    public String answersSheetPath() {
        return ANSWERS_SHEET_PATH;
    }

    @Override
    public StdAnswers toStdAnswers(String line) {
        var asArray = line.split(",");
        return new StdAnswers3003231(
                infallibleSupply(() -> asArray[STDREF_INDEX_IN_SHEET].trim()),
                Integer.parseInt(infallibleSupply(() -> asArray[THEORYSCORE_INDEX_IN_SHEET]).split("/")[0].trim()),
                infallibleSupply(() -> asArray[Q3_INDEX_IN_SHEET].trim()),
                infallibleSupply(() -> asArray[Q4_INDEX_IN_SHEET].trim()));
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
                        SCORE_PER_STUDENT.get(stdRef).get("Q3").toString(),
                        SCORE_PER_STUDENT.get(stdRef).get("Q4").toString()
                ).collect(joining(","))
        ).collect(joining("\n"));
    }
}
