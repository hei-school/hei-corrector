package school.hei.corrector.session021222;

import org.junit.jupiter.api.Test;
import school.hei.corrector.Corrector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static school.hei.corrector.StdAnswersDatabase.*;

public class Corrector_PerStudentRef_Test {

    Corrector corrector = new Corrector();

    @Test
    void canLoadAnswer_STD21058() {
        var examSession = new ExamSession021222();
        var stdRef = "STD21058";

        StdAnswers021222 answers = (StdAnswers021222) corrector.readStdAnswers(examSession, stdRef);

        assertEquals(examSession021222_std21058(), answers);
    }

    @Test
    void canCorrect_STD21058() {
        var examSession = new ExamSession021222();
        var stdRef = "STD21058";

        var score = corrector.correct(examSession, stdRef);

        assertEquals(4, score);
    }

    @Test
    void canLoadAnswer_STD21062() {
        var examSession = new ExamSession021222();
        var stdRef = "STD21062";

        StdAnswers021222 answers = (StdAnswers021222) corrector.readStdAnswers(examSession, stdRef);

        assertEquals(examSession021222_std21062(), answers);
    }

    @Test
    void canCorrect_STD21062() {
        var examSession = new ExamSession021222();
        var stdRef = "STD21062";

        var score = corrector.correct(examSession, stdRef);

        assertEquals(1, score);
    }

    @Test
    void canLoadAnswer_STD21065() {
        var examSession = new ExamSession021222();
        var stdRef = "STD21065";

        StdAnswers021222 answers = (StdAnswers021222) corrector.readStdAnswers(examSession, stdRef);

        assertEquals(examSession021222_std21065(), answers);
    }

    @Test
    void canCorrect_STD21065() {
        var examSession = new ExamSession021222();
        var stdRef = "STD21065";

        var score = corrector.correct(examSession, stdRef);

        assertEquals(6, score);
    }

    @Test
    void canCorrect_STD21103() {
        var examSession = new ExamSession021222();
        var stdRef = "STD21103";

        var score = corrector.correct(examSession, stdRef);

        assertEquals(5, score);
    }
}