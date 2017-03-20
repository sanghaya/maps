package edu.brown.cs.jjeon5.bacon;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import spark.ModelAndView;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.TemplateViewRoute;

public class BaconManager {
  private static Connection conn;
  private static BaconManager bm;
  private Map<String, String> actorIdtoName;
  private Map<String, String> actorNametoId;
  private Map<String, String> movieIdtoName;
  private Map<String, List<String>> actorToMovies;
  private Map<String, List<String>> movieToActors;
  private Map<Node, List<Node>> neighborsCache;

  public BaconManager() {
    actorIdtoName = new HashMap<String, String>();
    actorNametoId = new HashMap<String, String>();
    movieIdtoName = new HashMap<String, String>();
    actorToMovies = new HashMap<String, List<String>>();
    movieToActors = new HashMap<String, List<String>>();
    neighborsCache = new HashMap<Node, List<Node>>();
  }

  /**
   * Handle requests to the front page of bacon website.
   *
   */
  public static class BaconHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      bm = new BaconManager();
      String[] parts = { "mdb", "data/bacon/smallBacon.sqlite3" };
      bm.setupDB(parts);
      Map<String, Object> variables = ImmutableMap.of("title", "bacon");
      return new ModelAndView(variables, "bacon.ftl");
    }
  }

  public static class FindPathHandler implements Route {
    @Override
    public String handle(Request req, Response res) {

      QueryParamsMap qm = req.queryMap();
      String start = qm.value("start");
      String end = qm.value("end");

      System.out.println(start);

      String s = bm.connectCommand("connect \"" + start + "\" \"" + end + "\"");

      // Map<String, Object> variables = ImmutableMap.of("score", 1, "isValid",
      // 1);
      return s;
    }
  }

  public void setupDB(String[] parts) {
    if (parts.length > 2) {
      System.out.println("ERROR: Invalid Command");
      return;
    }

    String dbloc = parts[1];
    try {
      // this line loads the driver manager class, and must be
      // present for everything else to work properly
      Class.forName("org.sqlite.JDBC");
      String urlToDB = "jdbc:sqlite:" + dbloc;
      conn = DriverManager.getConnection(urlToDB);
      // these two lines tell the database to enforce foreign
      // keys during operations, and should be present
      Statement stat = conn.createStatement();
      stat.executeUpdate("PRAGMA foreign_keys = ON;");

      System.out.println("db set to " + dbloc);
    } catch (Exception e) {
      System.out.println("ERROR: Cannot connect to database.");
    }
  }

  public String connectCommand(String line) {
    // System.out.println("connecting");
    String temp = line.substring(line.indexOf("\"") + 1);
    String actor1 = temp.substring(0, temp.indexOf("\""));
    String actor2 = temp.substring(
        temp.indexOf("\"", temp.indexOf("\"") + 1) + 1, temp.length() - 1);

    // System.out.println("??");
    try {
      // System.out.println("trying");
      Node start = getActorNode(actor1);
      Node end = getActorNode(actor2);

      Dijkstra dj = new Dijkstra(this);
      Node n = dj.findPath(start, end);

      if (n == null) {
        System.out.println(actor1 + " -/- " + actor2);
        return "";
      }

      Node current = n;
      List<String> output = new ArrayList<String>();
      while (current.getPrevious() != null) {
        String s = current.getPrevious().getName() + " -> " + current.getName()
            + " : " + current.getMovie();
        output.add(s);
        current = current.getPrevious();
      }

      String toReturn = "";
      for (int i = output.size() - 1; i >= 0; i--) {
        toReturn += output.get(i) + "\n";
        // System.out.println(output.get(i));
      }
      return toReturn;
    } catch (Exception e) {
      System.out.println("ERROR: error in searching");
      e.printStackTrace(System.out);
    }
    return "";
  }

  public List<Node> getNeighbors(Node n) throws SQLException {
    // if (neighborsCache.containsKey(n)) {
    // return neighborsCache.get(n);
    // } else {
    List<Node> neighbors = new ArrayList<Node>();
    List<String> movies = getMovies(n.getId());
    String name = n.getName();
    String[] parts = name.split(" ");
    String lastName = parts[parts.length - 1];
    String c = lastName.charAt(0) + "";

    for (String movie : movies) {
      List<String> allActors = getActors(movie);
      double weight = 1.0 / allActors.size();
      String movieName = getMovieName(movie);
      List<Node> possibleBacons = getBacons(c, movieName, weight, allActors);
      neighbors.addAll(possibleBacons);
    }

    // neighborsCache.put(n, neighbors);
    return neighbors;
    // }

  }

  private String getMovieName(String id) throws SQLException {
    if (movieIdtoName.containsKey(id)) {
      // System.out.println("used caching name");
      return movieIdtoName.get(id);
    } else {

      String name = "";

      PreparedStatement prep;
      prep = conn
          .prepareStatement("SELECT name FROM film WHERE id = ? LIMIT 1;");
      prep.setString(1, id);
      ResultSet rs = prep.executeQuery();

      while (rs.next()) {
        name = rs.getString(1);
      }
      rs.close();

      movieIdtoName.put(id, name);

      return name;
    }
  }

  private List<Node> getBacons(String c, String movieName, double w,
      List<String> actors) throws SQLException {
    List<Node> bacons = new ArrayList<Node>();

    for (String actor : actors) {
      String name = "";
      if (actorIdtoName.containsKey(actor)) {
        name = actorIdtoName.get(actor);
        // System.out.println("used caching name");
      } else {
        PreparedStatement prep;
        prep = conn
            .prepareStatement("SELECT name FROM actor WHERE id = ? LIMIT 1;");
        prep.setString(1, actor);
        ResultSet rs = prep.executeQuery();

        while (rs.next()) {
          name = rs.getString(1);
        }
        rs.close();

        actorIdtoName.put(actor, name);
      }

      if (!name.isEmpty()) {
        if (c.equals(name.charAt(0) + "")) {
          Node n = new Node(actor, name, movieName, null, w);
          bacons.add(n);
        }
      }
    }

    return bacons;
  }

  private Node getActorNode(String name) throws SQLException {
    String id = "";
    if (actorNametoId.containsKey(name)) {
      id = actorNametoId.get(name);
      // System.out.println("used caching name");
    } else {

      PreparedStatement prep;
      prep = conn
          .prepareStatement("SELECT id FROM actor WHERE name = ? LIMIT 1;");
      prep.setString(1, name);
      ResultSet rs = prep.executeQuery();

      int count = 0;

      while (rs.next()) {
        count++;
        id = rs.getString(1);
      }
      rs.close();

      if (count == 0) {
        throw new SQLException();
      }
      actorNametoId.put(name, id);
    }
    Node n = new Node(id, name, "", null, 0);
    return n;
  }

  private List<String> getMovies(String id) throws SQLException {
    if (actorToMovies.containsKey(id)) {
      // System.out.println("used caching1");
      return actorToMovies.get(id);

    } else {
      List<String> movies = new ArrayList<String>();

      PreparedStatement prep;
      prep = conn
          .prepareStatement("SELECT film FROM actor_film WHERE actor = ?;");
      prep.setString(1, id);
      ResultSet rs = prep.executeQuery();

      while (rs.next()) {
        movies.add(rs.getString(1));
      }
      rs.close();

      actorToMovies.put(id, movies);
      return movies;
    }

  }

  private List<String> getActors(String movie) throws SQLException {
    if (movieToActors.containsKey(movie)) {
      // System.out.println("used caching2");
      return movieToActors.get(movie);
    } else {
      List<String> actors = new ArrayList<String>();

      PreparedStatement prep;
      prep = conn
          .prepareStatement("SELECT actor FROM actor_film WHERE film = ?;");
      prep.setString(1, movie);
      ResultSet rs = prep.executeQuery();

      while (rs.next()) {
        actors.add(rs.getString(1));
      }
      rs.close();

      movieToActors.put(movie, actors);

      return actors;
    }

  }

}
