package ru.nsu.ccfit.skokova.bitap;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BitapSearcher {

    private List<Long> indexes = new ArrayList<>();

    /**Returns List of beginning indexes of words in source file "approximately equal" to a pattern**/
    public List<Long> search(String sourceName, String pattern, int maxMistakes) {
        try (Reader reader = openSource(sourceName)) {
            int c;
            long end = 0;
            long begin = 0;
            StringBuilder stringBuilder = new StringBuilder();
            while ((c = reader.read()) >= 0) {
                char character = (char)c;
                if (Character.isWhitespace(character)) {
                    String candidate = stringBuilder.toString();
                    //System.out.println("Candidate: " + candidate);
                    find(candidate, pattern, maxMistakes, begin);
                    stringBuilder.setLength(0);
                    end++;
                    begin = end;
                } else {
                    stringBuilder.append(character);
                    end++;
                }
            }
            if (!stringBuilder.toString().equals("")) {
                String candidate = stringBuilder.toString();
                //System.out.println("Candidate: " + candidate);
                find(candidate, pattern, maxMistakes, begin);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return indexes;
    }

    private void find(String haystack, String needle, int maxMistakes, long index) {

        int alphabetRange = 128;
        int firstMatchedText = -1;

        long[] R = new long[maxMistakes + 1];

        long[] patternMask = new long[alphabetRange];
        for (int i = 0; i <= maxMistakes; i++) {
            R[i] = 1;
        }

        for (int i = 0; i < needle.length(); ++i) {
            patternMask[(int) needle.charAt(i)] |= 1 << i;
        }
        int count = 0;

        while (haystack.length() > count) {

            long old = 0;
            long nextOld = 0;

            for (int d = 0; d <= maxMistakes; ++d) {
                long substitution = (old | (R[d] & patternMask[haystack.charAt(count)])) << 1;
                long insertion = old | ((R[d] & patternMask[haystack.charAt(count)]) << 1);
                long deletion = (nextOld | (R[d] & patternMask[haystack.charAt(count)])) << 1;

                old = R[d];
                R[d] = substitution | insertion | deletion | 1;
                nextOld = R[d];
            }

            if (0 < (R[maxMistakes] & (1 << needle.length()))) {
                if ((firstMatchedText == -1) || (count - firstMatchedText > needle.length())) {
                    firstMatchedText = count;
                    indexes.add(index);
                }
            }

            count++;
        }
    }

    private Reader openSource(String sourceName) throws FileNotFoundException {
        return new FileReader(new File(sourceName));
    }
}
