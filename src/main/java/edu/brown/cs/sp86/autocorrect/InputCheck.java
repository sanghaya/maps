package edu.brown.cs.sp86.autocorrect;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Class for input check in CLI.
 * @author sangha
 *
 */
public class InputCheck {

  private Map<String, String> inpt;
  /**
   * Constructor for Input Check.
   */
  public InputCheck() {
    inpt = new HashMap<String, String>();
    inpt.put("corpus", "filepath");
    inpt.put("prefix", "off");
    inpt.put("led", "0");
    inpt.put("whitespace", "off");
    inpt.put("smart", "off");
  }
  /**
   *
   * @param tokens list of string.
   * @param line command line
   * @return dictionary of all inputs
   */
  public Map<String, String> check(List<String> tokens, String line) {
    if (tokens == null || tokens.size() == 0) {
      System.out.println("ERROR: Please enter valid commands");
      System.exit(1);
    }
    if (tokens.get(0).equals("corpus")) {
      if (tokens.size() == 2) {
        inpt.put("corpus", tokens.get(1));
      } else {
        System.out.println("ERROR: You need 1 filepath");
      }
    }
    if (tokens.get(0).equals("ac")) {
      if (tokens.size() > 1) {
        inpt.put("ac", line.substring(3));
      } else {
        System.out.println("ERROR: You need word or sentence to correct");
      }
    }
    if (tokens.get(0).equals("prefix")) {
      if (tokens.size() == 1) {
        System.out.println("prefix " + inpt.get("prefix"));
      } else if (tokens.size() == 2) {
        inpt.put("prefix", tokens.get(1));
      } else {
        System.out.println("ERROR: Specify On and Off");
      }
    }
    if (tokens.get(0).equals("whitespace")) {
      if (tokens.size() == 1) {
        System.out.println("whitespace " + inpt.get("whitespace"));
      } else if (tokens.size() == 2) {
        inpt.put("whitespace", tokens.get(1));
      } else {
        System.out.println("ERROR: Specify On and Off");
      }
    }
    if (tokens.get(0).equals("led")) {
      if (tokens.size() == 1) {
        System.out.println("led " + inpt.get("led"));
      } else if (tokens.size() == 2) {
        inpt.put("led", tokens.get(1));
      } else {
        System.out.println("ERROR: Specify Number for Levhenstein Distance");
      }
    }
    if (tokens.get(0).equals("smart")) {
      if (tokens.size() == 1) {
        System.out.println("smart " + inpt.get("smart"));
      } else if (tokens.size() == 2) {
        inpt.put("smart", tokens.get(1));
      } else {
        System.out.println("ERROR: Specify On and Off");
      }
    }
    return inpt;
  }
}
