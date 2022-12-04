package school.hei.corrector;

import java.util.Map;

public interface ExamSession {
    String name();

    String answersSheetPath();

    StdAnswers toStdAnswers(String line);

    Map<String/*stdRef*/, Map<String/*question*/, Integer>> scorePerStudent();

    String scorePerStudentAsString();
}
