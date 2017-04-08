package edu.brown.cs.jjeon5sp86.main;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.HashSet;
import java.util.Iterator;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import spark.ExceptionHandler;
import spark.ModelAndView;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.freemarker.FreeMarkerEngine;
import freemarker.template.Configuration;

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
  private static final Gson GSON = new Gson();
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
  private static MapManager db;


  private Main(String[] args) {
    db = new MapManager();
    this.args = args;
  }

  private void run() throws SQLException {

    OptionParser parser = new OptionParser();
    parser.accepts("gui");
    parser.accepts("port").withRequiredArg().ofType(Integer.class)
            .defaultsTo(DEFAULT_PORT);
    OptionSet options = parser.parse(args);
    if (options.has("gui")) {
      runSparkServer((int) options.valueOf("port"));
    }
    //set of commands that are allowed
    //Set<String> commands = Stream.of("map").collect(Collectors.toSet());
    String line;
    BufferedReader cmds = new BufferedReader(
            new InputStreamReader(System.in));
    try {
    	KDTree<Node> tree = null;
      while ((line = cmds.readLine()) != null) {
        List<String> tokens = null;
        tokens = inputProcessor(line);
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
      }
    } catch (IOException e) {
      System.out.println("ERROR: No command to read");
    }
  }
  private static FreeMarkerEngine createEngine() {
    Configuration config = new Configuration();
    File templates = new File("src/main/resources/spark/template/freemarker");
    try {
      config.setDirectoryForTemplateLoading(templates);
    } catch (IOException ioe) {
      System.out.printf("ERROR: Unable use %s for template loading.%n",
              templates);
      System.exit(1);
    }
    return new FreeMarkerEngine(config);
  }
  
  private void runSparkServer(int port) {
    Spark.port(port);
    Spark.externalStaticFileLocation("src/main/resources/static");
    Spark.exception(Exception.class, new ExceptionPrinter());

    FreeMarkerEngine freeMarker = createEngine();
    Spark.get("/maps", new FrontHandler(), freeMarker);
    Spark.post("/getWays", new ResultsHandler());
  }
  
  private static class FrontHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      Map<String, Object> variables = ImmutableMap.of("title",
              "Maps", "result", "");
      return new ModelAndView(variables, "draw.ftl");
    }
  }
  
  private static class ResultsHandler implements Route {
    @Override
    public String handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      Map<String, Object> variables = null;
      try {
    	  System.out.println(Arrays.asList(qm.value("a"), qm.value("b"), qm.value("c"), qm.value("d")));
    	  List<String> ways = db.findBoundedWay(Arrays.asList(qm.value("a"), qm.value("b"), qm.value("c"), qm.value("d")));
    	  List<List<String>> temp = new ArrayList<List<String>>();
          temp = db.guiData(ways);
          variables = ImmutableMap.of("ways", temp);
      } catch (Exception e) {
    	  System.out.println(e);
      }
      
      return GSON.toJson(variables);
    }
  }
  
  private static class ExceptionPrinter implements ExceptionHandler {
    @Override
    public void handle(Exception e, Request req, Response res) {
      res.status(500);
      StringWriter stacktrace = new StringWriter();
      try (PrintWriter pw = new PrintWriter(stacktrace)) {
        pw.println("<pre>");
        e.printStackTrace(pw);
        pw.println("</pre>");
      }
      res.body(stacktrace.toString());
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