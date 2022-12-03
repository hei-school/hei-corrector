package school.hei.corrector;

import org.junit.jupiter.api.Test;
import school.hei.corrector.session021222.ExamSession021222;
import school.hei.corrector.session021222.StdAnswers021222;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CorrectorTest {

    Corrector corrector = new Corrector();

    @Test
    void canLoadAnswer() {
        var examSession = new ExamSession021222();
        var stdRef = "STD21065";

        StdAnswers021222 stdAnswers = (StdAnswers021222) corrector.readStdAnswers(examSession, stdRef);

        assertEquals(stdRef, stdAnswers.stdRef());
        assertEquals("https://github.com/Mahefaa/hei-prog3p1-exam-021222", stdAnswers.q21p1());
        assertEquals("49c76a0ad7be9c31de56e138030e04e00edfe0f6", stdAnswers.q21p2());
        assertEquals("49c76a0ad7be9c31de56e138030e04e00edfe0f6", stdAnswers.q21p2());
        assertEquals("4d5f843f7783f96aebd9084e133429e766ce8880", stdAnswers.q21p3());
        assertEquals("c488a0e48f07fe90b98e184cbac75a0a700ef5db", stdAnswers.q21p4());
        assertEquals("7f86c9034fd4608f380aec19e73847e5a195f1d3", stdAnswers.q21p5());
        assertEquals("3fe8377bfff117dfc21518ebd8cdd1d357655798", stdAnswers.q21p6());
    }
}