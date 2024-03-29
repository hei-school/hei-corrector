package school.hei.corrector.session021222;

import school.hei.corrector.ExamSession;
import school.hei.corrector.StdAnswers;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static school.hei.utils.Utils.infallibleSupply;

public class ExamSession021222 implements ExamSession {

    private static final String ANSWERS_SHEET_PATH = "/answers_02-12-22.csv";
    private static final int STDREF_INDEX_IN_SHEET = 2;
    private static final int Q21P1_INDEX_IN_SHEET = 3;
    private static final int Q21P2_INDEX_IN_SHEET = 4;
    private static final int Q21P3_INDEX_IN_SHEET = 5;
    private static final int Q21P4_INDEX_IN_SHEET = 6;
    private static final int Q21P5_INDEX_IN_SHEET = 7;
    private static final int Q21P6_INDEX_IN_SHEET = 8;

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
        return new StdAnswers021222(
                infallibleSupply(() -> asArray[STDREF_INDEX_IN_SHEET].trim()),
                infallibleSupply(() -> asArray[Q21P1_INDEX_IN_SHEET].trim()),
                infallibleSupply(() -> asArray[Q21P2_INDEX_IN_SHEET].trim()),
                infallibleSupply(() -> asArray[Q21P3_INDEX_IN_SHEET].trim()),
                infallibleSupply(() -> asArray[Q21P4_INDEX_IN_SHEET].trim()),
                infallibleSupply(() -> asArray[Q21P5_INDEX_IN_SHEET].trim()),
                infallibleSupply(() -> asArray[Q21P6_INDEX_IN_SHEET].trim()));
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
                        SCORE_PER_STUDENT.get(stdRef).get("Q21P1").toString(),
                        SCORE_PER_STUDENT.get(stdRef).get("Q21P2").toString(),
                        SCORE_PER_STUDENT.get(stdRef).get("Q21P3").toString(),
                        SCORE_PER_STUDENT.get(stdRef).get("Q21P4").toString(),
                        SCORE_PER_STUDENT.get(stdRef).get("Q21P5").toString(),
                        SCORE_PER_STUDENT.get(stdRef).get("Q21P6").toString()
                ).collect(joining(","))
        ).collect(joining("\n"));
    }
}
