package edu.brown.cs.jjeon5.bacon;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import edu.brown.cs.jjeon5sp86.main.MapManager;

/**
 * Dijkstra class.
 * 
 * @author sangha
 *
 */
public class Dijkstra {

  private MapManager bm;
  private final int size = 11;

  /**
   *
   * @param mapManager
   *          db that runs queries.
   */
  public Dijkstra(MapManager mapManager) {
    this.bm = mapManager;
  }

  /**
   *
   * @param start
   *          starting dnode
   * @param end
   *          ending dnode
   * @return dnode
   * @throws SQLException
   *           thrown for SQLError
   */
  public DNode findPath(DNode start, DNode end) throws SQLException {
    Map<String, Double> frontierWeights = new HashMap<String, Double>();

    PriorityQueue<DNode> frontier = new PriorityQueue<DNode>(size,
        new Comparator<DNode>() {
          @Override
          public int compare(DNode a, DNode b) {
            return Double.compare(a.getWeight(), b.getWeight());
          }
        });
    frontier.add(start);

    Set<DNode> explored = new HashSet<DNode>();

    while (frontier.size() != 0) {
      DNode node = frontier.poll();
      if (node.getId().equals(end.getId())) {
        return node;
      }
      explored.add(node);
      List<DNode> neighbors = bm.getNeighbors(node);
      for (DNode n : neighbors) {
        if (!explored.contains(n) && !frontier.contains(n)) {
          n.setWeight(node.getWeight() + n.getWeight());
          frontierWeights.put(n.getId(), n.getWeight());
          n.setPrevious(node);
          frontier.add(n);
        } else if (frontier.contains(n) && frontierWeights
            .get(n.getId()) > (node.getWeight() + n.getWeight())) {
          frontier.remove(n);
          n.setWeight(node.getWeight() + n.getWeight());
          frontierWeights.put(n.getId(), n.getWeight());
          n.setPrevious(node);
          frontier.add(n);
        }
      }
    }

    return null;
  }

}
