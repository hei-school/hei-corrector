package school.hei.corrector.bigsum;

import org.junit.jupiter.api.Test;
import school.hei.corrector.Corrector;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Collector_All_Test {

  Corrector corrector = new Corrector();

  @Test
  void canCorrect_all() {
    var examSession = new ExamSession231221();

    var scorePerStudent = corrector.correct(examSession);

    var std21034Answers = scorePerStudent.get("STD21034");
    assertEquals(4, std21034Answers.get("THEORY"));
    assertEquals(2, std21034Answers.get("Q3"));
    assertEquals(0, std21034Answers.get("Q4"));
    assertEquals(1, std21034Answers.get("Q6"));
    assertEquals(0, std21034Answers.get("Q8"));

    var std21103Answers = scorePerStudent.get("STD21103");
    assertEquals(2, std21103Answers.get("THEORY"));
    assertEquals(1, std21103Answers.get("Q3")); // put null as Q3b
    assertEquals(0, std21103Answers.get("Q6"));

    var std21152Answers = scorePerStudent.get("STD21052");
    assertEquals(2, std21152Answers.get("THEORY"));
    assertEquals(1, std21152Answers.get("Q3")); // put an URL instead of the required SHA

    var std21027Answers = scorePerStudent.get("STD21027");
    assertEquals(1, std21027Answers.get("Q4"));

    var std21065Answers = scorePerStudent.get("STD21065");
    assertEquals(3, std21065Answers.get("Q7"));
    assertEquals(2, std21065Answers.get("Q8"));
    assertEquals(13, std21065Answers.get("all"));
  }
}