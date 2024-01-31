package school.hei.corrector.bigsum;

import school.hei.corrector.ExamSession;
import school.hei.corrector.StdAnswers;
import school.hei.corrector.bigsum.session231221.StdAnswers231221;

import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.joining;
import static school.hei.utils.Utils.infallibleSupply;

public abstract class ExamSessionBigSum implements ExamSession {
  private static final int STDREF_INDEX_IN_SHEET = 4;
  private static final int THEORY_INDEX_IN_SHEET = 2;
  private static final int Q3A_INDEX_IN_SHEET = 7;
  private static final int Q3B_INDEX_IN_SHEET = 8;
  private static final int Q4_INDEX_IN_SHEET = 9;
  private static final int Q6_INDEX_IN_SHEET = 11;
  private static final int Q7A_INDEX_IN_SHEET = 12;
  private static final int Q7B_INDEX_IN_SHEET = 13;
  private static final int Q8A_INDEX_IN_SHEET = 14;

  public static final Map<String/*stdRef*/, Map<String/*question*/, Integer>> SCORE_PER_STUDENT = new HashMap<>();

  @Override
  public StdAnswers toStdAnswers(String line) {
    var asArray = line.split(",");
    return new StdAnswers231221(
        infallibleSupply(() -> asArray[STDREF_INDEX_IN_SHEET].trim()),
        infallibleSupply(() -> asArray[THEORY_INDEX_IN_SHEET].trim()),
        infallibleSupply(() -> asArray[Q3A_INDEX_IN_SHEET].trim()),
        infallibleSupply(() -> asArray[Q3B_INDEX_IN_SHEET].trim()),
        infallibleSupply(() -> asArray[Q4_INDEX_IN_SHEET].trim()),
        infallibleSupply(() -> asArray[Q6_INDEX_IN_SHEET].trim()),
        infallibleSupply(() -> asArray[Q7A_INDEX_IN_SHEET].trim()),
        infallibleSupply(() -> asArray[Q7B_INDEX_IN_SHEET].trim()),
        infallibleSupply(() -> asArray[Q8A_INDEX_IN_SHEET].trim()));
  }

  @Override
  public Map<String, Map<String, Integer>> scorePerStudent() {
    return SCORE_PER_STUDENT;
  }

  @Override
  public String scorePerStudentAsString() {
    return SCORE_PER_STUDENT.keySet().stream().map(stdRef ->
        String.join(",",
            stdRef,
            SCORE_PER_STUDENT.get(stdRef).get("all").toString()
        )
    ).collect(joining("\n"));
  }
}
