package school.hei.corrector;

public interface ExamSession {
    String name();
    String answersSheetPath();
    StdAnswers toStdAnswers(String line);
}
