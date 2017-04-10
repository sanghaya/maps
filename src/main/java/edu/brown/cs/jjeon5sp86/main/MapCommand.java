package edu.brown.cs.jjeon5sp86.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.sql.SQLException;

import edu.brown.cs.jjeon5.stars.KDTree;
import edu.brown.cs.jjeon5.stars.Node;

public class MapCommand {

  private MapManager db;
  private KDTree<Node> tree;
  
  public MapCommand(MapManager db, KDTree<Node> tree) {
    this.db = db;
    this.tree = tree;
  }
  
  public void mapCommand(List<String> tokens) throws SQLException {
    if (tokens.size() >= 2) {
       if (tokens.get(0).equals("map")) {
          db.setupDB(tokens.get(1));
          Set<String> traversable = db.queryWays();
          List<Node> nodes = new ArrayList<Node>();
          for(String n:traversable) {
              List<String> coordinates = db.queryLatLon(n);
              Node node = new Node(n, coordinates.get(0), coordinates.get(1));
              nodes.add(node);
          }
          Node[] nodesArray = nodes.toArray(new Node[0]);
          tree = new KDTree<Node>(nodesArray);
          //populate Trie
          Set<String> names = db.queryNames();
          db.buildTrie(names);
      } else if (tokens.get(0).equals("nearest")) {
          Node n = new Node("testpt", tokens.get(1), tokens.get(2));
          List<Node> list = tree.findNearest(1, n);
          for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i).getId());
          }
      } else if (tokens.get(0).equals("ways")) {
        db.findBoundedWay(tokens.subList(1, 5));
      } else if (tokens.get(0).equals("route")) {
        db.routeCommand(tokens, tree);
      } else if (tokens.get(0).equals("suggest")) {
        db.genSuggestions(tokens.get(1));
      } else {
        System.out.println("ERROR: wrong command");
      }
    } else {
      System.out.println("ERROR: you need more input");
    }
  }
}