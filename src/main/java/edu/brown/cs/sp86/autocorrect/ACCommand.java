package edu.brown.cs.sp86.autocorrect;

import java.util.Map;
import java.util.List;
/**
 * autocorrect command.
 * @author sangha
 *
 */
public class ACCommand {

  private static MergeAndSuggest mode;
  private InputCheck checker;
  private static Map<String, String> in;
  /**
   * ac command.
   */
  public ACCommand() {
    mode = new MergeAndSuggest();
    checker = new InputCheck();
  }
  /**
   *
   * @param tokens broken down string.
   * @param line input string
   */
  public void acCom(List<String> tokens, String line) {
    boolean isLetter;
    in = checker.check(tokens, line);
    isLetter = Character.isLetter(line.charAt(line.length() - 1));
    if (tokens.contains("corpus") && tokens.size() > 1) {
      mode.readCorpus(tokens.get(1));
    }

    if (tokens.contains("ac") && isLetter) {
      mode.mergeSuggestions(in.get("ac"), in.get("prefix"),
              Integer.valueOf(in.get("led")), in.get("whitespace"),
              in.get("smart"));
    }

    if (tokens.contains("ac") && !isLetter) {
      System.out.println(line);
      System.out.println("");
    }
  }
}
