package school.hei.corrector.session021222;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static school.hei.corrector.StdAnswersDatabase.examSession021222_std21058;

class StdAnswers021222_std21058_Test {

    @Test
    void on_examSession021222_std21058_has_correct_q21p1() {
        StdAnswers021222 answers = examSession021222_std21058();

        var score = answers.correctQ21P1();

        assertEquals(1, score);
    }

    @Test
    void on_examSession021222_std21058_has_correct_q21p2() {
        StdAnswers021222 answers = examSession021222_std21058();

        var score = answers.correctQ21P2();

        assertEquals(1, score);
    }

    @Test
    void on_examSession021222_std21058_has_correct_q21p3() {
        StdAnswers021222 answers = examSession021222_std21058();

        var score = answers.correctQ21P3();

        assertEquals(1, score);
    }

    @Test
    void on_examSession021222_std21058_has_correct_q21p4() {
        StdAnswers021222 answers = examSession021222_std21058();

        var score = answers.correctQ21P4();

        assertEquals(1, score);
    }

    @Test
    void on_examSession021222_std21065_has_NOT_correct_q21p5() {
        StdAnswers021222 answers = examSession021222_std21058();

        var score = answers.correctQ21P5();

        assertEquals(0, score);
    }

    @Test
    void on_examSession021222_std21065_has_NOT_correct_q21p6() {
        StdAnswers021222 answers = examSession021222_std21058();

        var score = answers.correctQ21P6();

        assertEquals(0, score);
    }
}