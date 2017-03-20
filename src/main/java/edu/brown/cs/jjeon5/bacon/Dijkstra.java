package edu.brown.cs.jjeon5.bacon;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class Dijkstra {

  private BaconManager bm;

  public Dijkstra(BaconManager bm) {
    this.bm = bm;
  }

  public BaconNode findPath(BaconNode start, BaconNode end) throws SQLException {
    Map<String, Double> frontierWeights = new HashMap<String, Double>();

    PriorityQueue<BaconNode> frontier = new PriorityQueue<BaconNode>(11,
        new Comparator<BaconNode>() {
          @Override
          public int compare(BaconNode a, BaconNode b) {
            return Double.compare(a.getWeight(), b.getWeight());
          }
        });
    frontier.add(start);

    Set<BaconNode> explored = new HashSet<BaconNode>();

    while (frontier.size() != 0) {
      BaconNode node = frontier.poll();
      // System.out.println(node.getName());
      if (node.getId().equals(end.getId())) {
        return node;
      }
      explored.add(node);
      List<BaconNode> neighbors = bm.getNeighbors(node);
      for (BaconNode n : neighbors) {
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
