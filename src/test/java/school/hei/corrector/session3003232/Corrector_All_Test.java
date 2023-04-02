package school.hei.corrector.session3003232;

import org.junit.jupiter.api.Test;
import school.hei.corrector.Corrector;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Corrector_All_Test {

    Corrector corrector = new Corrector();

    @Test
    void canCorrect_all() {
        var examSession = new ExamSession3003232();

        var scorePerStudent = corrector.correct(examSession);

        var STD21052 = scorePerStudent.get("STD21052");
        assertEquals(0, STD21052.get("THEORY"));
        assertEquals(2, STD21052.get("Q4"));
        assertEquals(3, STD21052.get("Q5"));

        var STD21102 = scorePerStudent.get("STD21102");
        assertEquals(0, STD21102.get("THEORY"));
        assertEquals(0, STD21102.get("Q4"));
        assertEquals(0, STD21102.get("Q5"));

        var STD21014 = scorePerStudent.get("STD21014");
        assertEquals(2, STD21014.get("THEORY"));
        assertEquals(2, STD21014.get("Q4"));
        assertEquals(0, STD21014.get("Q5"));

        var STD21107 = scorePerStudent.get("STD21107");
        assertEquals(4, STD21107.get("THEORY"));
        assertEquals(2, STD21107.get("Q4"));
        assertEquals(3, STD21107.get("Q5"));

        var std21038Answers = scorePerStudent.get("STD21038");
        assertEquals(4, std21038Answers.get("THEORY"));
        assertEquals(2, std21038Answers.get("Q4"));
        assertEquals(3, std21038Answers.get("Q5"));
    }
}