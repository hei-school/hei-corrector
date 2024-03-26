package school.hei.corrector.soratra;

import school.hei.corrector.ExamSession;
import school.hei.corrector.StdAnswers;
import school.hei.utils.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static school.hei.utils.Utils.infallibleSupply;

public class ExamSession250324 implements ExamSession {

  private static final String ANSWERS_SHEET_PATH = "/soratra/answers_26-03-24.csv";
  private static final int STDREF_INDEX_IN_SHEET = 2;
  private static final int PERSONAL_HOSTING_INDEX_IN_SHEET = 4;
  private static final int GIT_URL_INDEX_IN_SHEET = 6;
  private static final int PREPROD_HEALTH_DB_URL_INDEX_IN_SHEET = 7;
  private static final int PREPROD_HEALTH_BUCKET_URL_INDEX_IN_SHEET = 8;
  private static final int PREPROD_API_URL_INDEX_IN_SHEET = 9;

  public static final Map<String/*stdRef*/, Map<String/*question*/, Integer>> SCORE_PER_STUDENT = new HashMap<>();

  @Override
  public String name() {
    return "PROG3 / PROG5P1 (Soratra) - Session du 25 mars 2024";
  }

  @Override
  public String answersSheetPath() {
    return ANSWERS_SHEET_PATH;
  }

  @Override
  public StdAnswers toStdAnswers(String line) {
    var asArray = line.split(",");
    return new StdAnswers250324(
        infallibleSupply(() -> asArray[STDREF_INDEX_IN_SHEET].trim()),
        infallibleSupply(() -> asArray[PERSONAL_HOSTING_INDEX_IN_SHEET].trim()),
        noEndingSlash(infallibleSupply(() -> asArray[GIT_URL_INDEX_IN_SHEET].trim())),
        noEndingSlash(infallibleSupply(() -> asArray[PREPROD_HEALTH_DB_URL_INDEX_IN_SHEET].trim())),
        noEndingSlash(infallibleSupply(() -> asArray[PREPROD_HEALTH_BUCKET_URL_INDEX_IN_SHEET].trim())),
        noEndingSlash(infallibleSupply(() -> asArray[PREPROD_API_URL_INDEX_IN_SHEET].trim()))
    );
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

  private static String noEndingSlash(String s) {
    try {
      return s.charAt(s.length() - 1) == '/' ? s.substring(0, s.length() - 1) : s;
    } catch (Exception e) {
      Log.error("noEndingSlash : " + e);
      return null;
    }
  }
}
