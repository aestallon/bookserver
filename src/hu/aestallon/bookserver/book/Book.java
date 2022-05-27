package hu.aestallon.bookserver.book;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * This class encapsulates books stored in plaintext.
 *
 * <p>The first line of the book must be its title.
 */
public class Book {
    /**
     * A regex describing all illegal characters in a canonical word.
     *
     * <p>Canonical letters are the latin letters (represented as
     * {@code \p{javaLetter}} to include any US-ASCII characters as well),
     * the apostrophe and the hyphen. For implementation reasons the
     * standard horizontal whitespace is also included.
     *
     * <p>All other characters are matched by this pattern.
     */
    private static final String NON_LETTER = "[^[\\p{javaLetter}\s'-]]";
    /** The pattern compiled from {@link #NON_LETTER}. */
    private static final Pattern NON_LETTER_PATTERN = Pattern.compile(NON_LETTER);
    /** The pattern describing the start of dialogue in a text. */
    private static final Pattern DIALOGUE_PATTERN = Pattern.compile("--|—");

    private final String title;
    private final Map<String, Long> countOfWords;

    public Book(String filePath) throws IOException {
        countOfWords = loadWords(filePath);
        title = readTitle(filePath);
    }

    public String getTitle() {
        return title;
    }

    // This is very retarded...
    private String readTitle(String filePath) {
        try (var br = new BufferedReader(new FileReader(filePath))) {
            return br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Map<String, Long> loadWords(String filePath) throws IOException {
        try (var br = new BufferedReader(new FileReader(filePath))) {
            List<String> wordList = new LinkedList<>();
            String line;
            while ((line = br.readLine()) != null) {
                line = DIALOGUE_PATTERN.matcher(line).replaceAll(" ");
                String cleanedLine = NON_LETTER_PATTERN.matcher(line).replaceAll("");
                String[] words = cleanedLine.split(" ");
                Arrays.stream(words)
                        .filter(word -> !"".equals(word))
                        .forEach(word -> {
                            if ("I".equals(word)) wordList.add(word);
                            else wordList.add(word.toLowerCase());
                        });
            }
            return wordList.stream()
                    .collect(Collectors.groupingBy(word -> word, Collectors.counting()));
        }
    }

    public List<Map.Entry<String, Long>> getMostFrequentWords(int n) {
        if (n < 1 || n > countOfWords.size()) {
            return Collections.emptyList();
        }
        return countOfWords.entrySet().stream()
                .sorted(Comparator
                        .comparingLong((Map.Entry<String, Long> entry) -> entry.getValue())
                        .reversed())
                .limit(n)
                .collect(Collectors.toList());
    }

    // Άπαξ λεγόμενον - word said only once
    public Set<String> getHapaxLegomena() {
        return countOfWords.keySet().stream()
                .filter(word -> countOfWords.get(word) == 1L)
                .sorted()
                .collect(Collectors.toCollection(TreeSet::new));
    }

    public List<Map.Entry<String, Long>> getWordsContaining(String s) {
        return countOfWords.entrySet().stream()
                .filter(entry -> entry.getKey().contains(s))
                .sorted(Comparator
                        .comparingLong((Map.Entry<String, Long> entry) -> entry.getValue())
                        .reversed())
                .collect(Collectors.toList());
    }

    public long getWordCount() {
        return countOfWords.values().stream()
                .mapToLong(Long::longValue)
                .sum();
    }

    public long getDistinctWordCount() {
        return countOfWords.size();
    }

    public int getNumberOfWordsOfPercentage(double percentage) {
        long targetCount = (long) (getWordCount() * percentage / 100);
        SortedSet<String> wordsInDescendingFrequency = getWordsInDescendingFrequency();
        int numberOfWords = 0;
        long countCovered = 0;
        Iterator<String> iterator = wordsInDescendingFrequency.iterator();
        while (countCovered < targetCount) {
            numberOfWords++;
            long currCount = countOfWords.get(iterator.next());
            countCovered += currCount;
        }
        return numberOfWords;
    }

    private SortedSet<String> getWordsInDescendingFrequency() {
        return countOfWords.entrySet().stream()
                .sorted(Comparator
                        .comparingLong((Map.Entry<String, Long> entry) -> entry.getValue())
                        .reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    public List<String> getLongestWords(int n) {
        return countOfWords.keySet().stream()
                .sorted(Comparator.comparingInt(String::length).reversed())
                .limit(n)
                .toList();
    }
}