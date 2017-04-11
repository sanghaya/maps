package edu.brown.cs.jjeonsp86.maps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import edu.brown.cs.jjeon5.stars.Node;

public class NodeTest {

  @Test
  public void testConstruction() {
    Node n = new Node("/n/1", "41.8", "-72.0");
    assertNotNull(n);
  }

  @Test
  public void testEquals() {
    Node n = new Node("/n/1", "41.8", "-72.0");
    Node m = new Node("/n/1", "41.8", "-72.0");
    assertEquals(n.equals(m), true);
  }

  @Test
  public void testDistance() {
    Node n = new Node("/n/1", "0", "0");
    Node m = new Node("/n/2", "3", "4");
    assert (n.distance(m) == 5.0);
  }
}
