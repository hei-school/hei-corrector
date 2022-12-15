package school.hei.corrector.session161222;

import school.hei.corrector.session021222.ExamSession021222;

public class ExamSession161222 extends ExamSession021222 {

    private static final String ANSWERS_SHEET_PATH = "/answers_16-12-22.csv";

    @Override
    public String name() {
        return "PROG3P1 (2/3) - Session du 16 décembre 2022";
    }

    @Override
    public String answersSheetPath() {
        return ANSWERS_SHEET_PATH;
    }
}
