package school.hei.corrector.session3003231;

import org.junit.jupiter.api.Test;
import school.hei.corrector.Corrector;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Corrector_All_Test {

    Corrector corrector = new Corrector();

    @Test
    void canCorrect_all() {
        var examSession = new ExamSession3003231();

        var scorePerStudent = corrector.correct(examSession);

        var std21007Answers = scorePerStudent.get("STD21007");
        assertEquals(3, std21007Answers.get("THEORY"));
        assertEquals(2, std21007Answers.get("Q3"));
        assertEquals(1, std21007Answers.get("Q4"));

        var std21018Answers = scorePerStudent.get("STD21018");
        assertEquals(4, std21018Answers.get("THEORY"));
        assertEquals(2, std21018Answers.get("Q3"));
        assertEquals(0, std21018Answers.get("Q4"));

        var std21052Answers = scorePerStudent.get("STD21052");
        assertEquals(3, std21052Answers.get("THEORY"));
        assertEquals(2, std21052Answers.get("Q3"));
        assertEquals(3, std21052Answers.get("Q4"));

        var std21089Answers = scorePerStudent.get("STD21089");
        assertEquals(0, std21089Answers.get("THEORY"));
        assertEquals(2, std21089Answers.get("Q3"));
        assertEquals(1, std21089Answers.get("Q4"));

        var std21107Answers = scorePerStudent.get("STD21107");
        assertEquals(3, std21107Answers.get("THEORY"));
        assertEquals(2, std21107Answers.get("Q3"));
        assertEquals(0, std21107Answers.get("Q4"));
    }
}