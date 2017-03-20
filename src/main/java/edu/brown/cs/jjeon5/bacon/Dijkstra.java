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

  public Node findPath(Node start, Node end) throws SQLException {
    Map<String, Double> frontierWeights = new HashMap<String, Double>();

    PriorityQueue<Node> frontier = new PriorityQueue<Node>(11,
        new Comparator<Node>() {
          @Override
          public int compare(Node a, Node b) {
            return Double.compare(a.getWeight(), b.getWeight());
          }
        });
    frontier.add(start);

    Set<Node> explored = new HashSet<Node>();

    while (frontier.size() != 0) {
      Node node = frontier.poll();
      // System.out.println(node.getName());
      if (node.getId().equals(end.getId())) {
        return node;
      }
      explored.add(node);
      List<Node> neighbors = bm.getNeighbors(node);
      for (Node n : neighbors) {
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
