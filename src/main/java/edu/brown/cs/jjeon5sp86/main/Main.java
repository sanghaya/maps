package edu.brown.cs.jjeon5sp86.main;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.sql.SQLException;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import edu.brown.cs.sp86.autocorrect.MergeAndSuggest;
import edu.brown.cs.jjeon5.stars.KDTree;
import edu.brown.cs.jjeon5.stars.Node;
import edu.brown.cs.jjeon5.stars.Star;
import edu.brown.cs.sp86.autocorrect.ACCommand;

/**
 * The Main class of our project. This is where execution begins.
 *
 * @author sp86
 */
public final class Main {

  private static final int DEFAULT_PORT = 4567;

  /**
   * The initial method called when execution begins.
   *
   * @param args
   *          An array of command line arguments
   * @throws SQLException if SQL table not found
   */
  public static void main(String[] args) throws SQLException {
    new Main(args).run();
  }
  private String[] args;
  private ACCommand ac;
  private static MergeAndSuggest mode;
  private static Map<String, String> in;
  private static MapManager db;


  private Main(String[] args) {
    mode = new MergeAndSuggest();
    ac = new ACCommand();
    db = new MapManager();
    this.args = args;
  }

  private void run() throws SQLException {

    OptionParser parser = new OptionParser();
    parser.accepts("gui");
    parser.accepts("port").withRequiredArg().ofType(Integer.class)
            .defaultsTo(DEFAULT_PORT);
    OptionSet options = parser.parse(args);
    //boolean isGui = false;
    if (options.has("gui")) {
      //runSparkServer((int) options.valueOf("port"));
      //isGui = true;
    }
    //set of commands that are allowed
    Set<String> commands = Stream.of("map").collect(Collectors.toSet());
    String line;
    BufferedReader cmds = new BufferedReader(
            new InputStreamReader(System.in));
    try {
    	KDTree<Node> tree = null;
      while ((line = cmds.readLine()) != null) {
        List<String> tokens = null;
        tokens = inputProcessor(line);
        
        
        if(tokens.get(0).equals("map")) {
        	System.out.println("map starting");
        	db.setupDB(tokens.get(1));
            Set<String> traversable = db.queryWays();
            List<Node> nodes = new ArrayList<Node>();
            for(String n:traversable) {
            	List<String> coordinates = db.queryLatLon(n);
            	System.out.println(coordinates);
            	Node node = new Node(n, coordinates.get(0), coordinates.get(1));
            	nodes.add(node);
            }
            Node[] nodesArray = nodes.toArray(new Node[0]);
            tree = new KDTree<Node>(nodesArray);
            System.out.println(tree.getRoot().getObject().getId());
        } else if(tokens.get(0).equals("nearest")) {
        	System.out.println("nearest starting");
        	Node n = new Node("testpt", tokens.get(1), tokens.get(2));
            List<Node> list = tree.findNearest(1, n);
            for (int i = 0; i < list.size(); i++) {
              System.out.println(list.get(i).getId());
            }
        }
        
        //if (!commands.contains(key)) {
        //  System.out.println("ERROR: Wrong Command");
        //}
      }
    } catch (IOException e) {
      System.out.println("ERROR: No command to read");
    }
  }

  private List<String> inputProcessor(String line) {
    List<String> list = new ArrayList<String>();
    Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(line);
    while (m.find()) {
      list.add(m.group(1).replace("\"", ""));
    }
    return list;
  }
}