package edu.brown.cs.sp86.autocorrect;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Comparator;
import java.util.Collections;
import java.util.ListIterator;

/**
 * Class thet merges and make suggestions.
 * @author sangha
 *
 */
public class MergeAndSuggest {

  private TextReader textReader;
  private Trie trie;
  private Compute compute;
  private Map<String, String> whiteCandidates;
  private static int fixedLed = 3;
  /**
   * initialize classes.
   */
  public MergeAndSuggest() {
    textReader = new TextReader();
    trie = new Trie();
    compute = new Compute();
    whiteCandidates = new HashMap<String, String>();
  }
  /**
   * Read the corpus.
   * @param file corpus file path
   */
  public void readCorpus(String file) {
    textReader.fileRead(file);
  }
 
  /**
   * suggestions for actors.
   * @param trie Trie
   * @param actors set of actors
   * @param line line to autocorrect
   * @return list of autocorrected input
   */
  public List<String> actorSuggetions(Trie trie,
          List<String> actors, String line) {
    // List<String> preCandidates = new ArrayList<String>();
    List<String> ledCandidates = new ArrayList<String>();
    //preCandidates = trie.findWord(line);
    for (int i = 0; i < actors.size(); i++) {
      if (Math.abs(actors.get(i).length() - line.length()) <= fixedLed) {
        int dist = compute.computeEditDistance(line, actors.get(i));
        if (dist <= fixedLed) {
          ledCandidates.add(actors.get(i));
        }
      }
    }
    return ledCandidates;
  }
  /**
   * Merge and make suggestions.
   * @param line command line
   * @param isTry "on" and "off" for prefix matching
   * @param isLed distance for LED
   * @param isWhite "on" and "off" for whitespace
   * @param isSmart "on" and "off" for smart matching
   * @return list of string of autocorrect suggestions
   */
  public List<String> mergeSuggestions(String line, String isTry, int isLed,
          String isWhite, String isSmart) {
    String cleaned;
    String[] sentence;
    String lastWord;
    String prevWord;
    List<String> merged = new ArrayList<String>();

    cleaned = line.replaceAll("[\\p{P}&&[^\u0027]]", "").toLowerCase();
    cleaned = cleaned.replaceAll(" +", " ");
    cleaned = cleaned.replace("'", " ");
    sentence = cleaned.split(" ");
    lastWord = sentence[sentence.length - 1];

    if (sentence.length > 1) {
      prevWord = sentence[sentence.length - 2];
    } else {
      prevWord = null;
    }

    List<String> corpus = textReader.getCorpus();
    List<String> uniqueCorpus = new ArrayList<String>(
            textReader.getUniqueCorpus());
    List<String> preCandidates = new ArrayList<String>();
    List<String> ledCandidates = new ArrayList<String>();
    compute.buildUnigram(corpus);

    //build Trie
    for (int i = 0; i < uniqueCorpus.size(); i++) {
      trie.insertWord(trie.getRoot(), uniqueCorpus.get(i));
    }
    //prefix suggestions
    if (isTry.equals("on")) {
      preCandidates = trie.findWord(lastWord);
    }
    //led suggestions
    if (isLed >= 0) {
      for (int i = 0; i < corpus.size(); i++) {
        if (Math.abs(corpus.get(i).length() - lastWord.length()) <= isLed) {
          int dist = compute.computeEditDistance(lastWord, corpus.get(i));
          if (dist <= isLed) {
            ledCandidates.add(corpus.get(i));
          }
        }
      }
    }

    //whitespace suggestions
    if (isWhite.equals("on")) {
      whiteCandidates = compute.splitWords(uniqueCorpus, lastWord);
    }

    List<String> temp = new ArrayList<String>();
    Set<String> tempSet = new HashSet<String>();
    temp.addAll(preCandidates);
    temp.addAll(ledCandidates);

    tempSet = new HashSet<String>(temp);
    merged = new ArrayList<String>(tempSet);
    return rankSuggestions(merged, whiteCandidates, line,
            cleaned, lastWord, prevWord, isSmart);
  }
  /**
   * ranks suggestions.
   * @param merged merged suggestions from different mode
   * @param white dictionary of split words
   * @param line command line input
   * @param cleaned trimmed data
   * @param lastWord word that needs correcting
   * @param prevWord word before last word for bigram
   * @param isSmart "on" and "off" for smart
   * @return suggestions
   */
  public List<String> rankSuggestions(List<String> merged,
          Map<String, String> white, String line, String cleaned,
          String lastWord, String prevWord, String isSmart) {
    List<String> topFive = new ArrayList<String>();
    merged.addAll(white.keySet());

    //matches the same word
    if (merged.contains(lastWord)) {
      topFive.add(lastWord);
      merged.remove(lastWord);
    }

    topFive.addAll(rankSort(merged, prevWord, isSmart));
    ListIterator<String> iter = topFive.listIterator();
    int count = 0;
    System.out.println("ac " + line);
    if (textReader.getCorpus().size() == 0 && topFive.size() == 0) {
      System.exit(1);
    }
    if (topFive.size() == 0) {
      System.out.println("");
    }
    while (iter.hasNext() && count < 5) {
      String suggestion = iter.next();
      if (white.containsKey(suggestion)) {
        suggestion += " " + white.get(suggestion);
      }
      System.out.println(cleaned.substring(0,
              cleaned.length() - lastWord.length()) + suggestion);
      count++;
    }
    return topFive.subList(0, count);
  }
  /**
   *
   * @param candidates suggestions from different mode.
   * @param prevWord previous word before the target, can be null.
   * @param smartRank "on" and "off" for smart mode.
   * @return ordered list of suggestions
   */
  private List<String> rankSort(List<String> candidates,
          String prevWord, String smartRank) {

    Collections.sort(candidates, new Comparator<String>() {
      @Override
      public int compare(String s1, String s2) {
        List<String> corpus = textReader.getCorpus();
        if (smartRank == "on") {
          int firstSmart = compute.countSmart(corpus, prevWord, s1);
          int secondSmart = compute.countSmart(corpus, prevWord, s2);
          if (firstSmart != secondSmart) {
            if (firstSmart < secondSmart) {
              return 1;
            } else {
              return -1;
            }
          }
        }
        int firstBi = compute.getBigram(corpus, s1, prevWord);
        int secondBi = compute.getBigram(corpus, s2, prevWord);
        if (firstBi != secondBi) {
          if (firstBi < secondBi) {
            return 1;
          } else {
            return -1;
          }
          //look at unigram
        } else {
          int firstUni = compute.getUnigram(s1);
          int secondUni = compute.getUnigram(s2);
          if (firstUni != secondUni) {
            if (firstUni < secondUni) {
              return 1;
            } else {
              return -1;
            }
            //look at alphabetical order
          } else {
              return s1.compareTo(s2);
          }
        }
      }
    });
    return candidates;
  }
}
