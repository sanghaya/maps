package edu.brown.cs.jjeon5sp86.main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import edu.brown.cs.jjeon5.bacon.DNode;
import edu.brown.cs.jjeon5.bacon.Dijkstra;
import edu.brown.cs.jjeon5.stars.KDTree;
import edu.brown.cs.jjeon5.stars.Node;
import edu.brown.cs.sp86.autocorrect.Trie;
/**
 * MapManager class to manage commands and run queries.
 * @author sangha
 *
 */
public class MapManager {
  private Connection conn = null;
  private Trie trie = new Trie();
  private KDTree<Node> tree = null;
  private ConcurrentMap<String, String> traffic;
  private static final int ONESECOND = 1000;
  private static final int LONGTIME = 10000;
  private boolean threadStop = false;
  /**
   * MapManager constructor.
   */
  public MapManager() {
    traffic = new ConcurrentHashMap<String, String>();
    TrafficUpdate t = new TrafficUpdate("Thread-1");
    t.start();
  }
  /**
   *
   * @return Dictionary of traffic
   */
  public Map<String, String> getTraffic() {
    Map<String, String> toReturn = new HashMap<String, String>(traffic);
    return toReturn;
  }
  /**
   * close thread.
   */
  public void closeThread() {
    threadStop = true;
  }

  /**
   * Traffic update class.
   * @author sangha
   *
   */
  class TrafficUpdate implements Runnable {
    private Thread t;
    private String threadName;

    TrafficUpdate(String name) {
      threadName = name;
    }

    /**
     * Run class for threading.
     */
    public void run() {
      try {
        long timeStamp = 0;
        for (int i = LONGTIME; i > 0; i--) {
          if (!threadStop) {
            getHTML("http://localhost:8080/?last=" + timeStamp);
            timeStamp = Instant.now().getEpochSecond();
            System.out.println(timeStamp);
            Thread.sleep(ONESECOND);
          }
        }
      } catch (Exception e) {
        System.out.println(e);
      }

    }
    /**
     * Start function to start multi-threading.
     */
    public void start() {
      if (t == null) {
        t = new Thread(this, threadName);
        t.start();
      }
    }
    /**
     *
     * @param urlToRead input URL
     * @throws Exception when HTML not found
     */
    public void getHTML(String urlToRead) throws Exception {
      StringBuilder result = new StringBuilder();
      URL url = new URL(urlToRead);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");
      BufferedReader rd = new BufferedReader(
          new InputStreamReader(conn.getInputStream()));
      String line;
      while ((line = rd.readLine()) != null) {
        result.append(line);
      }
      rd.close();
      String data = result.toString();
      data = data.replaceAll("\\[", "").replaceAll("\\]", "")
          .replaceAll("\"", "").replaceAll(" ", "");
      String[] array = data.split(",");
      if (array.length >= 2) {
        for (int i = 0; i < array.length; i = i + 2) {
          traffic.putIfAbsent(array[i], array[i + 1]);
        }
      }
    }
  }
  /**
   *
   * @param tokens String input in REPL
   * @throws SQLException thrown when query goes wrong
   */
  public void mapCommand(List<String> tokens) throws SQLException {
    if (tokens.size() >= 2) {
      if (tokens.get(0).equals("map")) {
        setupDB(tokens.get(1));
        Set<String> traversable = queryWays();
        List<Node> nodes = new ArrayList<Node>();
        for (String n : traversable) {
          List<String> coordinates = queryLatLon(n);
          Node node = new Node(n, coordinates.get(0), coordinates.get(1));
          nodes.add(node);
        }
        Node[] nodesArray = nodes.toArray(new Node[0]);
        tree = new KDTree<Node>(nodesArray);
        // populate Trie
        Set<String> names = queryNames();
        buildTrie(names);
      } else if (tokens.get(0).equals("nearest")) {
        Node n = new Node("testpt", tokens.get(1), tokens.get(2));
        List<Node> list = tree.findNearest(1, n);
        for (int i = 0; i < list.size(); i++) {
          System.out.println(list.get(i).getId());
        }
      } else if (tokens.get(0).equals("ways")) {
        findBoundedWay(tokens.subList(1, 5));
      } else if (tokens.get(0).equals("route")) {
        routeCommand(tokens);
      } else if (tokens.get(0).equals("suggest")) {
        genSuggestions(tokens.get(1));
      } else {
        System.out.println("ERROR: wrong command");
      }
    } else {
      System.out.println("ERROR: you need more input");
    }
  }
  /**
   *
   * @param filePath path of the database
   */
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
  /**
   *
   * @param tokens repl input
   * @return list of list of strings of lats and lons
   */
  public List<List<String>> routeCommand(List<String> tokens) {
    List<List<String>> ways = new ArrayList<List<String>>();
    int mode = 0;
    try {
      Double.parseDouble(tokens.get(1));
    } catch (Exception e) {
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
      }
      DNode current = n;
      List<String> output = new ArrayList<String>();
      while (current.getPrevious() != null) {
        List<String> one;
        List<String> two;
        List<String> eachPath = new ArrayList<>();
        String s = current.getPrevious().getId() + " -> " + current.getId()
            + " : " + current.getPath();
        output.add(s);
        one = queryLatLon(current.getPrevious().getId());
        two = queryLatLon(current.getId());
        eachPath.add(one.get(0));
        eachPath.add(one.get(1));
        eachPath.add(two.get(0));
        eachPath.add(two.get(1));
        ways.add(eachPath);
        current = current.getPrevious();
      }
      for (int i = output.size() - 1; i >= 0; i--) {
        System.out.println(output.get(i));
      }
    } catch (Exception e) {
    }
    return ways;
  }
  /**
   *
   * @param name1 street name
   * @param name2 street name
   * @return ids of intersecting nodes
   * @throws SQLException when SQL query is wrong
   */
  public List<String> getIntersection(String name1, String name2)
      throws SQLException {
    List<String> intersec = new ArrayList<String>();
    String name1alt = "";
    String name2alt = "";
    Set<String> firstNodes = queryStartEnd(name1);
    if (name1.contains((" St"))) {
      name1alt = name1.replace(" St", " Street");
      firstNodes.addAll(queryStartEnd(name1alt));
    }
    Set<String> secondNodes = queryStartEnd(name2);
    if (name2.contains((" St"))) {
      name2alt = name2.replace(" St", " Street");
      secondNodes.addAll(queryStartEnd(name2alt));
    }
    Iterator<String> itr = firstNodes.iterator();
    while (itr.hasNext()) {
      String id = itr.next();
      if (secondNodes.contains(id)) {
        intersec.add(id);
      }
    }
    return intersec;
  }
  /**
   *
   * @param n Dijkstra node
   * @return neighbor nodes
   * @throws SQLException when query is wrong
   */
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

      double dist = Math.pow((lat - n.getLat()), 2)
          + Math.pow((lon - n.getLon()), 2);

      DNode newNode = new DNode(endNode, lat, lon, wayId, n, dist);
      neighbors.add(newNode);
    }
    rs.close();
    prep.close();

    return neighbors;
  }
  /**
   *
   * @return set of starting and ending node ids
   * @throws SQLException thrown
   */
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
  /**
   *
   * @param name way's name
   * @return start, end nodes
   * @throws SQLException thrown
   */
  public Set<String> queryStartEnd(String name) throws SQLException {
    Set<String> endList = new HashSet<>();
    try {
      PreparedStatement prep;
      prep = conn.prepareStatement("SELECT start, end FROM way WHERE name = ?");
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
  /**
   *
   * @param id way's id
   * @return name, start, end nodes
   * @throws SQLException thrown
   */
  public List<String> queryFromId(String id) throws SQLException {
    List<String> nodes = new ArrayList<>();
    try {
      PreparedStatement prep;
      prep = conn
          .prepareStatement("SELECT name, start, end FROM way WHERE id = ?");
      prep.setString(1, id);
      ResultSet rs = prep.executeQuery();
      while (rs.next()) {
        nodes.add(rs.getString(1));
        nodes.add(rs.getString(2));
        nodes.add(rs.getString(3));
      }
      rs.close();
      prep.close();
    } catch (SQLException e) {
      nodes = null;
    }
    return nodes;
  }
  /**
   * query for bounding box.
   * @param lat1 latitude of topleft
   * @param lon1 longitude of topleft
   * @param lat2 latitude of bottom right
   * @param lon2 latitude of bottom right
   * @return nodes in between
   * @throws SQLException thrown
   */
  public Set<String> queryNodes(Double lat1, Double lon1, Double lat2,
      Double lon2) throws SQLException {
    Set<String> nodeList = new HashSet<>();
    try {
      PreparedStatement prep;
      prep = conn.prepareStatement("SELECT id FROM node WHERE latitude > ? AND "
          + "latitude < ? AND longitude < ? And longitude > ?");
      prep.setDouble(1, lat2);
      prep.setDouble(2, lat1);
      prep.setDouble(3, lon2);
      prep.setDouble(4, lon1);
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
  /**
   *
   * @param id node's id
   * @return lat, lon
   * @throws SQLException thrown
   */
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
  /**
   *
   * @param id node's id
   * @return way's id
   * @throws SQLException thrown
   */
  public Set<String> queryWayFromNode(String id) throws SQLException {
    Set<String> wayIdList = new HashSet<>();
    try {
      PreparedStatement prep;
      prep = conn
          .prepareStatement("SELECT id FROM way WHERE start = ? or end = ?");
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
  /**
   *
   * @param inpt word to be corrected
   * @return possible suggestions
   * @throws SQLException thrown
   */
  public List<String> genSuggestions(String inpt) throws SQLException {
    List<String> candidates = new ArrayList<String>();
    candidates = trie.findWord(inpt);
    for (int i = 0; i < candidates.size(); i++) {
      System.out.println(candidates.get(i));
      if (i > 2) {
        return candidates.subList(0, 3);
      }
    }
    return candidates;
  }
  /**
   * query all way's names.
   * @return set of names
   * @throws SQLException thrown
   */
  public Set<String> queryNames() throws SQLException {
    Set<String> names = new HashSet<>();
    try {
      PreparedStatement prep;
      prep = conn.prepareStatement("SELECT name FROM way");
      ResultSet rs = prep.executeQuery();
      while (rs.next()) {
        names.add(rs.getString(1));
      }
      rs.close();
      prep.close();
    } catch (SQLException e) {
      names = null;
    }
    return names;
  }
  /**
   * find ways inside bounding box.
   * @param tokens REPL input
   * @return list of way ids
   * @throws SQLException thrown
   */
  public List<String> findBoundedWay(List<String> tokens) throws SQLException {
    Double lat1 = Double.valueOf(tokens.get(0));
    Double lat2 = Double.valueOf(tokens.get(2));
    Double lon1 = Double.valueOf(tokens.get(1));
    Double lon2 = Double.valueOf(tokens.get(3));
    Set<String> boundedNodes = new HashSet<String>();
    Set<String> boundedWay = new HashSet<String>();
    boundedNodes = queryNodes(lat1, lon1, lat2, lon2);

    for (String each : boundedNodes) {
      boundedWay.addAll(queryWayFromNode(each));
    }
    List<String> boundedWayList = new ArrayList<String>(boundedWay);
    for (int i = boundedWayList.size() - 1; i >= 0; i--) {
      System.out.println(boundedWayList.get(i));
    }
    return boundedWayList;
  }
  /**
   *
   * @param wayId way ids
   * @return lats and lons of start/end
   * @throws SQLException thrown
   */
  public List<List<String>> guiData(List<String> wayId) throws SQLException {
    List<String> nodes = new ArrayList<>();
    List<List<String>> ways = new ArrayList<List<String>>();
    for (String id : wayId) {
      List<String> inpt = new ArrayList<>();
      nodes = queryFromId(id);
      List<String> start = queryLatLon(nodes.get(1));
      List<String> end = queryLatLon(nodes.get(2));
      // inpt.add(nodes.get(0));
      inpt.add(id);
      inpt.add(start.get(0));
      inpt.add(start.get(1));
      inpt.add(end.get(0));
      inpt.add(end.get(1));
      ways.add(inpt);
    }
    return ways;
  }
  /**
   *
   * @param names way's names
   * @return trie
   */
  public Trie buildTrie(Set<String> names) {
    for (String name : names) {
      trie.insertWord(trie.getRoot(), name);
    }
    return trie;
  }
  /**
   *
   * @param n number of neighbor to search
   * @param pt node in KDtree
   * @return list of neighbors
   */
  public List<Node> findNearestHelper(int n, Node pt) {
    return tree.findNearest(n, pt);
  }
}
