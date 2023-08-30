package school.hei.corrector.projet1p3.rdc.session230623;

import org.junit.jupiter.api.Test;
import school.hei.corrector.Corrector;
import school.hei.corrector.rdc.session230623.ExamSession230623;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Corrector_All_Test {

  Corrector corrector = new Corrector();

  @Test
  void canCorrect_all() {
    var examSession = new ExamSession230623();

    var scorePerStudent = corrector.correct(examSession);

    var answers21038 = scorePerStudent.get("STD21038");
    assertEquals(7, answers21038.get("all"));

    var answers21065 = scorePerStudent.get("STD21065");
    assertEquals(3, answers21065.get("all"));

    var answers21107 = scorePerStudent.get("STD21107");
    assertEquals(7, answers21107.get("all"));

    var answers21052 = scorePerStudent.get("STD21052");
    assertEquals(7, answers21052.get("all"));

    var answers21058 = scorePerStudent.get("STD21058");
    assertEquals(5, answers21058.get("all"));
  }
}