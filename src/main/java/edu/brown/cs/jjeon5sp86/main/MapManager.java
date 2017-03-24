package edu.brown.cs.jjeon5sp86.main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import edu.brown.cs.jjeon5.bacon.DNode;
import edu.brown.cs.jjeon5.bacon.Dijkstra;
import edu.brown.cs.jjeon5.stars.KDTree;
import edu.brown.cs.jjeon5.stars.Node;

import java.util.HashSet;
import java.util.Iterator;


public class MapManager {
  private Connection conn = null;
  
  public void setupDB(String filePath) {
    try {
      // this line loads the driver manager class, and must be
      // present for everything else to work properly
      Class.forName("org.sqlite.JDBC");
      String urlToDB = "jdbc:sqlite:" + filePath;
      conn = DriverManager.getConnection(urlToDB);
      // these two lines tell the database to enforce foreign
      // keys during operations, and should be present
      Statement stat = conn.createStatement();
      stat.executeUpdate("PRAGMA foreign_keys = ON;");
      System.out.println("map set to " + filePath);
    } catch (Exception e) {
      System.out.println("ERROR: Cannot connect to database.");
    }
  }
  
  public void routeCommand(List<String> tokens, KDTree<Node> tree) {
	  
	  int mode = 0;
	  try {
		  Double.parseDouble(tokens.get(1));
	  } catch(Exception e) {
		  mode = 1;
	  }
	  
	  DNode start = null;
	  DNode end = null;
	  if (mode == 0) {
		  Node n1 = new Node("testpt", tokens.get(1), tokens.get(2));
		  Node n2 = new Node("testpt2", tokens.get(3), tokens.get(4));
          List<Node> list = tree.findNearest(1, n1);
          List<Node> list2 = tree.findNearest(1, n2);
          Node a = list.get(0);
          Node b = list2.get(0);
          
          
        	  start = new DNode(a.getId(), Double.parseDouble(tokens.get(1)), 
        			  Double.parseDouble(tokens.get(2)), "", null, 0);
        	  end = new DNode(b.getId(), Double.parseDouble(tokens.get(3)), 
        			  Double.parseDouble(tokens.get(4)), "", null, 0);

	  } else {
		  try {
			  String id1 = getIntersection(tokens.get(1), tokens.get(2)).get(0);
			  String id2 = getIntersection(tokens.get(3), tokens.get(4)).get(0);
			  
			  List<String> latlon1 = queryLatLon(id1);
			  List<String> latlon2 = queryLatLon(id2);
			  
			  start = new DNode(id1, Double.parseDouble(latlon1.get(0)), 
        			  Double.parseDouble(latlon1.get(1)), "", null, 0);
			  end = new DNode(id2, Double.parseDouble(latlon2.get(0)), 
        			  Double.parseDouble(latlon2.get(1)), "", null, 0);
			  
		  } catch (Exception e) {
			  
		  }
		  
	  }
	  
	  try {
		  Dijkstra dj = new Dijkstra(this);
	      DNode n = dj.findPath(start, end);

	      if (n == null) {
	        System.out.println(start.getId() + " -/- " + end.getId());
	        //return "";
	      }

	      DNode current = n;
	      List<String> output = new ArrayList<String>();
	      while (current.getPrevious() != null) {
	        String s = current.getPrevious().getId() + " -> " + current.getId()
	            + " : " + current.getPath();
	        output.add(s);
	        current = current.getPrevious();
	      }

	      //String toReturn = "";
	      for (int i = output.size() - 1; i >= 0; i--) {
	        //toReturn += output.get(i) + "\n";
	        System.out.println(output.get(i));
	      }
	  } catch (Exception e) {
		  
	  }
	  
	  
  }
  
  public List<String> getIntersection(String id1, String id2) throws SQLException {
    List<String> intersec = new ArrayList<String>();
    Set<String> firstNodes = queryStartEnd(id1);
    Set<String> secondNodes = queryStartEnd(id2);
    Iterator<String> itr = firstNodes.iterator();
    while (itr.hasNext()) {
      String id = itr.next();
      if (secondNodes.contains(id)) {
        intersec.add(id);
      }
    }
    return intersec;
  }


  public List<DNode> getNeighbors(DNode n) throws SQLException {
	  List<DNode> neighbors = new ArrayList<DNode>();
	  
	  PreparedStatement prep;
      prep = conn.prepareStatement(
              "SELECT id, end FROM way WHERE type != ? AND type != ? AND start == ?");
      prep.setString(1, "unclassified");
      prep.setString(2, "");
      prep.setString(3, n.getId());
      ResultSet rs = prep.executeQuery();
      while (rs.next()) {
    	String wayId = rs.getString(1);
    	String endNode = rs.getString(2);
        
    	List<String> coo = queryLatLon(endNode);
    	double lat = Double.parseDouble(coo.get(0));
    	double lon = Double.parseDouble(coo.get(1));
    	
    	double dist = Math.pow((lat-n.getLat()), 2) + Math.pow((lon-n.getLon()), 2);
    	
    	DNode newNode = new DNode(endNode, lat, lon, wayId, n, dist);
    	neighbors.add(newNode);
      }
      rs.close();
      prep.close();
      
      return neighbors;
  }
  
  public Set<String> queryWays() throws SQLException {
    Set<String> nodeList = new HashSet<>();
    try {
      PreparedStatement prep;
      prep = conn.prepareStatement(
              "SELECT start, end FROM way WHERE type != ? OR type != ?");
      prep.setString(1, "unclassified");
      prep.setString(2, "");
      ResultSet rs = prep.executeQuery();
      while (rs.next()) {
        nodeList.add(rs.getString(1));
        nodeList.add(rs.getString(2));
      }
      rs.close();
      prep.close();
    } catch (SQLException e) {
      nodeList = null;
    }
    return nodeList;
  }

  
  public Set<String> queryStartEnd(String name) throws SQLException {
    Set<String> endList = new HashSet<>();
    try {
      PreparedStatement prep;
      prep = conn.prepareStatement(
              "SELECT start, end FROM way WHERE name = ?");
      prep.setString(1, name);
      ResultSet rs = prep.executeQuery();
      while (rs.next()) {
        endList.add(rs.getString(1));
        endList.add(rs.getString(2));
      }
      rs.close();
      prep.close();
    } catch (SQLException e) {
      endList = null;
    }
    return endList;
  }
  
  public Set<String> queryNodes() throws SQLException {
    Set<String> nodeList = new HashSet<>();
    try {
      PreparedStatement prep;
      prep = conn.prepareStatement(
              "SELECT id FROM node");
      ResultSet rs = prep.executeQuery();
      while (rs.next()) {
        nodeList.add(rs.getString(1));
      }
      rs.close();
      prep.close();
    } catch (SQLException e) {
      nodeList = null;
    }
    return nodeList;
  }

  
  public List<String> queryLatLon(String id) throws SQLException {
    List<String> coordinates = new ArrayList<>();
    try {
      PreparedStatement prep;
      prep = conn.prepareStatement(
              "SELECT latitude, longitude FROM node WHERE id = ?");
      prep.setString(1, id);
      ResultSet rs = prep.executeQuery();
      while (rs.next()) {
        coordinates.add(rs.getString(1));
        coordinates.add(rs.getString(2));
      }
      rs.close();
      prep.close();
    } catch (SQLException e) {
      coordinates = null;
    }
    return coordinates;
  }
  
  public Set<String> queryWayFromNode(String id) throws SQLException {
    Set<String> wayIdList = new HashSet<>();
    try {
      PreparedStatement prep;
      prep = conn.prepareStatement(
              "SELECT id FROM way WHERE start = ? or end = ?");
      prep.setString(1, id);
      prep.setString(2, id);
      ResultSet rs = prep.executeQuery();
      while (rs.next()) {
        wayIdList.add(rs.getString(1));
      }
      rs.close();
      prep.close();
    } catch (SQLException e) {
      wayIdList = null;
    }
    return wayIdList;
  }
}