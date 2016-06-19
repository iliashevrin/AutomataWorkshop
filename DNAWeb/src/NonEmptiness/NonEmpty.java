package NonEmptiness;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public class NonEmpty{
    public static void main(String[] args) {
        Numbered numbered = new Numbered(System.in);
        NumberedAnalyzer analyzer = new NumberedAnalyzer(numbered);
        System.out.println(analyzer.isEmptyOrWord());
    }

    public static String checkEmptiness(String dna) {
        InputStream dnaStream = new ByteArrayInputStream(dna.getBytes(StandardCharsets.UTF_8));
        Numbered numbered = new Numbered(dnaStream);
        NumberedAnalyzer analyzer = new NumberedAnalyzer(numbered);
        return analyzer.isEmptyOrWord();
    }
}
