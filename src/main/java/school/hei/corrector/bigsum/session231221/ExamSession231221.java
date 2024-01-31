package school.hei.corrector.bigsum.session231221;

import school.hei.corrector.StdAnswers;
import school.hei.corrector.bigsum.ExamSessionBigSum;

import static school.hei.utils.Utils.infallibleSupply;

public class ExamSession231221 extends ExamSessionBigSum {

  @Override
  public String name() {
    return "PROG5P1 - Code maintenable";
  }

  @Override
  public String answersSheetPath() {
    return "/bigsum/answers_2023_12_21.csv";
  }

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
}
