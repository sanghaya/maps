package edu.brown.cs.jjeonsp86.maps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import edu.brown.cs.jjeon5.bacon.DNode;

public class DNodeTest {

  @Test
  public void testConstruction() {
    DNode n = new DNode("/n/1", 1.0, 3.0, "", null, 0);
    assertNotNull(n);
  }

  @Test
  public void testEquals() {
    DNode n = new DNode("/n/1", 1.0, 3.0, "", null, 0);
    DNode m = new DNode("/n/1", 1.0, 3.0, "hello", null, 0);
    assertEquals(n.equals(m), true);
  }
}
