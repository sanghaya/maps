package edu.brown.cs.jjeonsp86.maps;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import edu.brown.cs.jjeon5.stars.KDTree;
import edu.brown.cs.jjeon5.stars.Node;

public class TreeTest {

  @Test
  public void testConstruction() {
    Node n = new Node("/n/1", "0", "0");
    Node m = new Node("/n/2", "3", "4");
    Node l = new Node("/n/3", "4", "5");
    Node[] list = { n, m, l };

    KDTree<Node> t = new KDTree<Node>(list);
    assertNotNull(t);
  }

  @Test
  public void testNearestNeighbor() {
    Node n = new Node("/n/1", "0", "0");
    Node m = new Node("/n/2", "3", "4");
    Node l = new Node("/n/3", "4", "5");
    Node[] list = { n, m, l };

    KDTree<Node> t = new KDTree<Node>(list);
    List<Node> al = t.findNearest(1, new Node("/n/4", "1", "1"));
    assertEquals(al.get(0).equals(n), true);
  }
  
  @Test
  public void testNearestNeighborTwo() {
    Node n = new Node("/n/1", "0", "0");
    Node m = new Node("/n/2", "3", "4");
    Node l = new Node("/n/3", "4", "5");
    Node a = new Node("/n/6", "0.1", "0.1");
    Node b = new Node("/n/5", "3.5", "4.7");
    Node c = new Node("/n/4", "4.3", "5.1");
    
    Node[] list = { n, m, l, a, b, c };

    KDTree<Node> t = new KDTree<Node>(list);
    List<Node> al = t.findNearest(1, new Node("/n/4", "1", "1"));
    assertEquals(al.get(0).equals(a), true);
  }
  
}
