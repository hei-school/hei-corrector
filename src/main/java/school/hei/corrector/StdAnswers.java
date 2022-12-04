package school.hei.corrector;

import java.util.Map;

public interface StdAnswers {
    String stdRef();


    int correct();

    Map<String, Integer> scorePerQuestion();
}
