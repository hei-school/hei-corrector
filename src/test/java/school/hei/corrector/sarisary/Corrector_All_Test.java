package school.hei.corrector.sarisary;

import org.junit.jupiter.api.Test;
import school.hei.corrector.Corrector;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Corrector_All_Test {

  Corrector corrector = new Corrector();

  @Test
  void canCorrect_all() {
    var examSession = new ExamSession220224();

    var scorePerStudent = corrector.correct(examSession);

    var std21039Answers = scorePerStudent.get("STD21039");
    assertEquals(8, std21039Answers.get("all"));

    var std21034Answers = scorePerStudent.get("STD21034");
    assertEquals(0, std21034Answers.get("levelBonus"));
    assertEquals(0, std21034Answers.get("namingMalus"));
    assertEquals(3, std21034Answers.get("pojaVersion"));
    assertEquals(2, std21034Answers.get("preprodDbCheck"));
    assertEquals(3, std21034Answers.get("preprodBucketCheck"));
    assertEquals(11, std21034Answers.get("all"));

    var std22086Answers = scorePerStudent.get("STD22086");
    assertEquals(4, std22086Answers.get("levelBonus"));
    assertEquals(0, std22086Answers.get("namingMalus"));
    assertEquals(3, std22086Answers.get("pojaVersion"));
    assertEquals(0, std22086Answers.get("preprodDbCheck"));
    assertEquals(0, std22086Answers.get("preprodBucketCheck"));
    assertEquals(7, std22086Answers.get("all"));

    var std21107Answers = scorePerStudent.get("STD21107");
    assertEquals(3, std21107Answers.get("preprodPutBw"));
    assertEquals(3, std21107Answers.get("prodPutBw"));
    assertEquals(20, std21107Answers.get("all"));
  }
}