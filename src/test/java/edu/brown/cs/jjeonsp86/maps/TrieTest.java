package edu.brown.cs.jjeonsp86.maps;

import org.junit.Test;

import edu.brown.cs.sp86.autocorrect.Trie;

import static org.junit.Assert.*;

public class TrieTest {

    @Test
    public void testNullConstruction() {
      Trie b = new Trie();
      assertNotNull(b);
    }
    
    @Test
    public void testInsertWord() {
      Trie b = new Trie();
      b.insertWord(b.getRoot(), "hello");
      assertNotNull(b);
    }
    
    @Test
    public void testInsertEmptyWord() {
      Trie b = new Trie();
      b.insertWord(b.getRoot(), "");
      assertNotNull(b);
    }
    
    @Test
    public void testInsertMultipleWord() {
      Trie b = new Trie();
      b.insertWord(b.getRoot(), "hello");
      b.insertWord(b.getRoot(), "hi");
      assertNotNull(b);
    }
    
    @Test
    public void testFindWord() {
      Trie b = new Trie();
      b.insertWord(b.getRoot(), "find");
      assertNotNull(b);
      assertTrue(b.findWord("find").contains("find"));
    }
        
    @Test
    public void testFindChildren() {
      Trie b = new Trie();
      b.insertWord(b.getRoot(), "h");
      b.insertWord(b.getRoot(), "hi");
      assertTrue(b.findWord("h").size() == 2);
    }
    
    @Test
    public void testMany() {
      Trie b = new Trie();
      b.insertWord(b.getRoot(), "h");
      b.insertWord(b.getRoot(), "hi");
      b.insertWord(b.getRoot(), "zebra");
      b.insertWord(b.getRoot(), "abc");
      b.insertWord(b.getRoot(), "hello");
      assertTrue(b.findWord("h").size() == 3);
    }
 
    @Test
    public void testlowerUppercase() {
      Trie b = new Trie();
      b.insertWord(b.getRoot(), "H");
      b.insertWord(b.getRoot(), "Hi");
      b.insertWord(b.getRoot(), "zebra");
      b.insertWord(b.getRoot(), "abc");
      b.insertWord(b.getRoot(), "hi");
      assertTrue(b.findWord("h").size() == 1);
      assertTrue(b.findWord("H").size() == 2);
    }
}

