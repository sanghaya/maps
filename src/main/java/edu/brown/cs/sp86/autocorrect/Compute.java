package edu.brown.cs.sp86.autocorrect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * construtor that computes LED, bi/unigram.
 * @author sangha
 *
 */
public class Compute {
  private Map<String, Integer> dictionary;


  /**
   * Empty Constructor.
   */
  public Compute() {
    dictionary = new HashMap<String, Integer>();
  }

  /**
   *
   * @param a String one.
   * @param b String two
   * @return the edit distance of two strings a, b
   */
  public int computeEditDistance(String a, String b) {

    if (a == b) {
      return 0;
    }
    if (b.length() == 0) {
      return a.length();
    }

    //initialize matrix and put initial condition
    int[][] matrix = new int[a.length() + 1][b.length() + 1];

    for (int i = 0; i < a.length() + 1; i++) {
      matrix[i][0] = i;
    }

    for (int j = 0; j < b.length() + 1; j++) {
      matrix[0][j] = j;
    }

    int dist = 0;

    for (int i = 1; i < a.length() + 1; i++) {
      char row = a.charAt(i - 1);
      for (int j = 1; j < b.length() + 1; j++) {
        char column = b.charAt(j - 1);

        if (row != column) {
          dist = 1;
        } else {
          dist = 0;
        }

        matrix[i][j] = Math.min(Math.min(matrix[i - 1][j] + 1,
                matrix[i][j - 1] + 1), matrix[i - 1][j - 1] + dist);
      }
    }
    return matrix[a.length()][b.length()];
  }
  /**
   *
   * @param words builds unigram in a dictionary with given corpus.
   */
  public void buildUnigram(List<String> words) {
    for (String word : words) {
      if (dictionary.containsKey(word)) {
        dictionary.put(word, dictionary.get(word) + 1);
      } else {
        dictionary.put(word, 1);
      }
    }
  }
  /**
   *
   * @param a String to be found.
   * @return unigram probability
   */
  public int getUnigram(String a) {
    return this.dictionary.get(a);
  }
  /**
   *
   * @param words corpus to be read
   * @param a previous string to be compared.
   * @param b next string to be considered
   * @return bigram probability of b given a
   */
  public int getBigram(List<String> words, String a, String b) {
    if (b == null) {
      return 0;
    }
    int count = 0;
    for (int i = 0; i < words.size() - 1; i++) {
      if (words.get(i) == a && words.get(i + 1) == b) {
        count++;
      }
    }
    return count;
  }
  /**
   *
   * @param uniqueWords corpus of unique words drawn from the data.
   * @param lastWord word that needs to be split
   * @return dictionary of all the possible splits put as key and value pair
   */
  public Map<String, String> splitWords(List<String> uniqueWords,
          String lastWord) {
    Map<String, String> candidates = new HashMap<String, String>();
    for (int i = 0; i < lastWord.length() - 1; i++) {
      String first = lastWord.substring(0, i + 1);
      String second = lastWord.substring(i + 1);
      if (uniqueWords.contains(first) && uniqueWords.contains(second)) {
        candidates.put(first, second);
      }
    }
    return candidates;
  }
  /**
   * SmartRank algorithm that counts the proximity between two words.
   * @param corpus list of given word
   * @param prev previous word
   * @param last word to be corrected
   * @return count of pairs in the given range
   */
  public int countSmart(List<String> corpus, String prev, String last) {
    if (prev == null) {
      return 0;
    }
    int count = 0;
    int k = 0;
    int leftover = corpus.size() / 5;
    int split = 5;
    while (k < corpus.size() / 5) {
      for (int i = 5 * k; i < 5 * k + split - 1; i++) {
        for (int j = i + 1; j < 5 * k + split; j++) {
          if (corpus.get(i) == prev && corpus.get(j) == last) {
            count++;
          }
        }
      }
      k++;
      if (k == (corpus.size() / 5) - 1) {
        split = leftover;
      }
    }
    return count;
  }
  /**
   *
   * @return probability dictionary of given corpus.
   */
  public Map<String, Integer> getDictionary() {
    return this.dictionary;
  }
}
