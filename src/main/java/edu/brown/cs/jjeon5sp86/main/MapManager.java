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
import java.util.HashSet;

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
  
  public Set<String> queryWays() throws SQLException {
    System.out.println("reading data");
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
    System.out.println("done reading");
    System.out.println(nodeList);
    return nodeList;
  }
  
  public List<String> queryLatLon(String id) throws SQLException {
    System.out.println("reading data");
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
    System.out.println("done reading");
    return coordinates;
  }
}