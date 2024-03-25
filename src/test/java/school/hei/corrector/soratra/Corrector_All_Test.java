package school.hei.corrector.soratra;

import org.junit.jupiter.api.Test;
import school.hei.corrector.Corrector;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Corrector_All_Test {

  Corrector corrector = new Corrector();

  @Test
  void canCorrect_all() {
    var examSession = new ExamSession250324();

    var scorePerStudent = corrector.correct(examSession);

    var std22081Answers = scorePerStudent.get("STD22081");
    assertEquals(4, std22081Answers.get("personalHostingBonus"));
    assertEquals(0, std22081Answers.get("namingMalus"));
    assertEquals(2, std22081Answers.get("pojaVersion"));
    assertEquals(3, std22081Answers.get("preprodDbCheck"));
    assertEquals(3, std22081Answers.get("preprodBucketCheck"));
    assertEquals(6, std22081Answers.get("preprodPutSoratra"));
    assertEquals(6, std22081Answers.get("preprodGetSoratra"));
    assertEquals(24, std22081Answers.get("all"));

    var std22070 = scorePerStudent.get("STD22070");
    assertEquals(20, std22070.get("all"));

    var std22005 = scorePerStudent.get("STD22005");
    assertEquals(0, std22005.get("personalHostingBonus"));
    assertEquals(0, std22005.get("namingMalus"));
    assertEquals(2, std22005.get("pojaVersion"));
    assertEquals(3, std22005.get("preprodDbCheck"));
    assertEquals(3, std22005.get("preprodBucketCheck"));
    assertEquals(0, std22005.get("preprodPutSoratra"));
    assertEquals(0, std22005.get("preprodGetSoratra"));
    assertEquals(8, std22005.get("all"));
  }
}