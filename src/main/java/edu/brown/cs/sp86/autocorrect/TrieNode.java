package edu.brown.cs.sp86.autocorrect;

/**
 * Class of Node of a Trie.
 * @author sangha
 *
 */
public class TrieNode {
  private char letter;
  private boolean isWord;
  private boolean isLeaf;
  private TrieNode[] children;
  private TrieNode parent;
  private final int size = 53;

  /**
   * empty constructor for root node.
   */
  public TrieNode() {
    this.isWord = false;
    this.isLeaf = true;
    this.children = new TrieNode[size];
  }
  /**
   *
   * @param c character to be put into the node.
   */
  public TrieNode(char c) {
    this();
    this.letter = c;
  }
  /**
   *
   * @return boolean value of the whether node is a leaf.
   */
  public boolean getLeaf() {
    return this.isLeaf;
  }
  /**
   *
   * @param value boolean value to set the leaf.
   */
  public void setLeaf(boolean value) {
    this.isLeaf = value;
  }
  /**
   *
   * @return boolean value of isWord.
   */
  public boolean getWord() {
    return this.isWord;
  }
  /**
   *
   * @param value boolean value to set the leaf as word.
   */
  public void setWord(boolean value) {
    this.isWord = value;
  }
  /**
   *
   * @return letter of the node.
   */
  public char getLetter() {
    return letter;
  }
  /**
   *
   * @param letter set letter of the node.
   */
  public void setLetter(char letter) {
    this.letter = letter;
  }
  /**
   *
   * @param index specifies which child.
   * @return child TrieNode
   */
  public TrieNode getChild(int index) {
    return children[index];
  }
  /**
   *
   * @return size of the children.
   */
  public int getChildSize() {
    return children.length;
  }
  /**
   *
   * @param letter set child with given letter.
   */
  public void setChild(char letter) {
    int index;
    if (Character.isUpperCase(letter)) {
      index = letter - 'A';
    } else if (Character.isLowerCase(letter)) {
      index = letter - 'G';
    } else {
      index = 52;
    }
    this.children[index] = new TrieNode(letter);
  }
  /**
   *
   * @return parent Trie Node.
   */
  public TrieNode getParent() {
    return parent;
  }
  /**
   *
   * @param parent set parent Trie Node.
   */
  public void setParent(TrieNode parent) {
    this.parent = parent;
  }
}
