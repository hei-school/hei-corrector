package school.hei.corrector.samadd.session230523;

import org.junit.jupiter.api.Test;
import school.hei.corrector.Corrector;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Corrector_All_Test {

  Corrector corrector = new Corrector();

  @Test
  void canCorrect_all() {
    var examSession = new ExamSession230523();

    var scorePerStudent = corrector.correct(examSession);

    var answers21065 = scorePerStudent.get("STD21065");
    assertEquals(1, answers21065.get("all"));

    var answers21107 = scorePerStudent.get("STD21107");
    assertEquals(1, answers21107.get("all"));

    var answers21052 = scorePerStudent.get("STD21044");
    assertEquals(0, answers21052.get("all"));
  }
}