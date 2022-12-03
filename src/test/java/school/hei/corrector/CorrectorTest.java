package school.hei.corrector;

import org.junit.jupiter.api.Test;
import school.hei.corrector.session021222.ExamSession021222;
import school.hei.corrector.session021222.StdAnswers021222;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static school.hei.corrector.StdAnswersDatabase.examSession021222_std21065;

public class CorrectorTest {

    Corrector corrector = new Corrector();

    @Test
    void canLoadAnswer() {
        var examSession = new ExamSession021222();
        var stdRef = "STD21065";

        StdAnswers021222 answers = (StdAnswers021222) corrector.readStdAnswers(examSession, stdRef);

        assertEquals(examSession021222_std21065(), answers);
    }
}