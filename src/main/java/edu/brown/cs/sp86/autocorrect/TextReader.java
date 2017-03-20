package edu.brown.cs.sp86.autocorrect;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

/**
 * Reads the corpus and computes probabilities.
 * @author sangha
 *
 */
public class TextReader {

  private List<String> words = new ArrayList<String>();
  private Set<String> uniqueWords = new HashSet<String>();

  /**
   *
   * @param file corpus file path to be read.
   * @return corpus
   */
  public List<String> fileRead(String file)  {
    BufferedReader fileReader = null;
    try {
      String line = "";
      fileReader = new BufferedReader(new FileReader(file));

      // Read the file line by line
      while ((line = fileReader.readLine()) != null) {
        for (String token : line.split(" ")) {
          String cleaned = token.replaceAll("\\p{P}", "").toLowerCase().trim();
          words.add(cleaned);
          uniqueWords.add(cleaned);
        }
      }
      System.out.printf("corpus %s added\n", file);
      return words;
    } catch (IOException e) {
    } finally {
      try {
        fileReader.close();
      } catch (IOException e) {
      } catch (NullPointerException e) {
        System.out.println("ERROR: Enter valid file path");
      }
    }
    return null;
  }
  /**
   *
   * @return corpus from the data.
   */
  public List<String> getCorpus() {
    return this.words;
  }
  /**
   *
   * @return unique corpus from the data.
   */
  public Set<String> getUniqueCorpus() {
    return this.uniqueWords;
  }
}
