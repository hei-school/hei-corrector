package school.hei.corrector.samadd.session230523;

import school.hei.corrector.ExamSession;
import school.hei.corrector.StdAnswers;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static school.hei.utils.Utils.infallibleSupply;

public class ExamSession230523 implements ExamSession {

  private static final String ANSWERS_SHEET_PATH = "/projet1p3/samadd/answers_23-05-23.csv";
  private static final int STDREF_INDEX_IN_SHEET = 2;
  private static final int URI_INDEX_IN_SHEET = 4;

  public static final Map<String/*stdRef*/, Map<String/*question*/, Integer>> SCORE_PER_STUDENT = new HashMap<>();

  @Override
  public String name() {
    return "PROJET1P3 (Sam Add) - Session du 23 juin 2023";
  }

  @Override
  public String answersSheetPath() {
    return ANSWERS_SHEET_PATH;
  }

  @Override
  public StdAnswers toStdAnswers(String line) {
    var asArray = line.split(",");
    return new StdAnswers230523(
        infallibleSupply(() -> asArray[STDREF_INDEX_IN_SHEET].trim()),
        infallibleSupply(() -> asArray[URI_INDEX_IN_SHEET].trim()));
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
            SCORE_PER_STUDENT.get(stdRef).get("all").toString()
        ).collect(joining(","))
    ).collect(joining("\n"));
  }
}
