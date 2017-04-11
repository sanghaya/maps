package edu.brown.cs.jjeonsp86.maps;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import edu.brown.cs.jjeon5sp86.main.MapManager;

public class QueryTest {

  @Test
  public void testQueryWay() throws SQLException {
    MapManager dbtwo = new MapManager();
    dbtwo.setupDB("data/maps/smallMaps.sqlite3");
    Set<String> ways;
    ways = dbtwo.queryWays();
    assertNotNull(ways);
    assertEquals(ways.size(), 6);
  }
  
  @Test
  public void testQueryStartEnd() throws SQLException {
    MapManager dbtwo = new MapManager();
    dbtwo.setupDB("data/maps/smallMaps.sqlite3");
    Set<String> nodes;
    nodes = dbtwo.queryStartEnd("Chihiro Ave");
    assertNotNull(nodes);
    assertEquals(nodes.size(), 3);
  }
  
  @Test
  public void testQueryFromId() throws SQLException {
    MapManager dbtwo = new MapManager();
    dbtwo.setupDB("data/maps/smallMaps.sqlite3");
    List<String> way;
    way = dbtwo.queryFromId("/w/0");
    assertNotNull(way);
    assertEquals(way.size(), 3);
    assertEquals(way.get(0).equals("Chihiro Ave"), true);
  }
}
