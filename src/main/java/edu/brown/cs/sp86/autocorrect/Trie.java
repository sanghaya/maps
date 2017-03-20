package edu.brown.cs.sp86.autocorrect;

import java.util.List;
import java.util.ArrayList;

/**
 * Class for Trie.
 * @author sangha
 *
 */
public class Trie {

  private TrieNode root;
  /**
   * Constructs emptry Trie with a root node.
   */
  public Trie() {
    root = new TrieNode();
  }
  /**
   *
   * @return root of the node.
   */
  public TrieNode getRoot() {
    return this.root;
  }
  /**
   *
   * @param parent root of the node.
   * @param s string to be inserted into the Trie
   */
  public void insertWord(TrieNode parent, String s) {
    if (s.isEmpty()) {
      return;
    }
    char letter = s.charAt(0);
    int index = letter - 'a';
    parent.setLeaf(false);
    if (parent.getChild(index) == null) {
      parent.setChild(letter);
      parent.getChild(index).setParent(parent);
    }
    if (s.length() > 1) {
      insertWord(parent.getChild(index), s.substring(1));
    } else {
      parent.getChild(index).setLeaf(true);
      parent.getChild(index).setWord(true);
    }
  }
  /**
   *
   * @param s string to be found in the Trie.
   * @return List of String that share prefixes with given string
   */
  public List<String> findWord(String s) {
    List<String> candidates = new ArrayList<String>();
    TrieNode lastChar = root;
    TrieNode temp = null;
    while (s.length() != 0 && lastChar != null) {
      temp = lastChar;
      lastChar = lastChar.getChild(s.charAt(0) - 'a');
      s = s.substring(1);
    }
    if (lastChar == null) {
      lastChar = temp;
    }
    return findWordHelper(candidates, lastChar);
  }
  /**
   * Helper for FindWord.
   * @param candidates List of string of possible suggestions
   * @param lastChar Node where prefix ends
   * @return List of String that share prefixes with given string
   */
  public List<String> findWordHelper(List<String> candidates,
          TrieNode lastChar) {
    if (lastChar.getWord()) {
      candidates.add(printWord(lastChar));
    }
    if (!lastChar.getLeaf()) {
      for (int i = 0; i < lastChar.getChildSize(); i++) {
        if (lastChar.getChild(i) != null) {
          findWordHelper(candidates, lastChar.getChild(i));
        }
      }
    }
    return candidates;
  }
  /**
   *
   * @param node current node to add character
   * @return String of a word
   */
  public String printWord(TrieNode node) {
    if (node.getParent() == null) {
      return "";
    } else {
      return printWord(node.getParent())
              + new String(String.valueOf(node.getLetter()));
    }
  }
}