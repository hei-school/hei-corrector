package school.hei.corrector.session161222;

import org.junit.jupiter.api.Test;
import school.hei.corrector.Corrector;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Corrector_All_Test {

    Corrector corrector = new Corrector();

    @Test
    void canCorrect_all() {
        var examSession = new ExamSession161222();

        var scorePerStudent = corrector.correct(examSession);

        var std21052Answers = scorePerStudent.get("STD21052");
        assertEquals(1, std21052Answers.get("Q21P1"));
        assertEquals(1, std21052Answers.get("Q21P2"));
        assertEquals(1, std21052Answers.get("Q21P3"));
        assertEquals(1, std21052Answers.get("Q21P4"));
        assertEquals(1, std21052Answers.get("Q21P5"));
        assertEquals(1, std21052Answers.get("Q21P6"));
        var std21007Answers = scorePerStudent.get("STD21007");
        assertEquals(1, std21007Answers.get("Q21P1"));
        assertEquals(1, std21007Answers.get("Q21P2"));
        assertEquals(0, std21007Answers.get("Q21P3"));
        assertEquals(0, std21007Answers.get("Q21P4"));
        assertEquals(0, std21007Answers.get("Q21P5"));
        assertEquals(1, std21007Answers.get("Q21P6"));
    }
}