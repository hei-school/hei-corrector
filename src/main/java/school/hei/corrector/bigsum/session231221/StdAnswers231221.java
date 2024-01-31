package school.hei.corrector.bigsum.session231221;

import school.hei.corrector.bigsum.StdAnswersBigSum;

public class StdAnswers231221 extends StdAnswersBigSum {
  public StdAnswers231221(
      String stdRef,
      String theory,
      String q3a, String q3b, String q4, String q6, String q7a, String q7b, String q8a) {
    super(stdRef, theory, q3a, q3b, q4, q6, q7a, q7b, q8a);
  }

  @Override
  protected boolean hasBonusBug() {
    return true;
  }
}
