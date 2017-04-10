package edu.brown.cs.jjeon5sp86.main;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.InputStreamReader;
import java.io.BufferedReader;

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

import edu.brown.cs.jjeon5.stars.KDTree;
import edu.brown.cs.jjeon5.stars.Node;

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
  private static KDTree<Node> tree;
  private static MapCommand map;

  private Main(String[] args) {
    db = new MapManager();
    tree = null;
    map = new MapCommand(db, tree);
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
    String line;
    BufferedReader cmds = new BufferedReader(
            new InputStreamReader(System.in));
    try {
      while ((line = cmds.readLine()) != null) {
        List<String> tokens = null;
        tokens = inputProcessor(line);
        map.mapCommand(tokens);
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
    Spark.post("/getWaysInBox", new ResultsHandler());
    Spark.post("/getNearest", new NearHandler());
    Spark.post("/getPathFromNode", new PathHandler());
    Spark.post("/suggestion", new SuggestHandler());
  }
  
  private static class FrontHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
    	String[] parts = { "map", "data/maps/maps.sqlite3" };
    	try {
    	  map.mapCommand(Arrays.asList(parts));
    	} catch (Exception e) {
    		System.out.println(e);
    	}
    	Map<String, Object> variables = ImmutableMap.of("title",
              "Maps");
    	return new ModelAndView(variables, "draw.ftl");
    }
  }
  
  private static class ResultsHandler implements Route {
    @Override
    public String handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      Map<String, Object> variables = null;
      try {
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
  
  private static class NearHandler implements Route {
    @Override
    public String handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      Map<String, Object> variables = null;
      try {
        Node n = new Node("testpt", qm.value("lat"), qm.value("lon"));
        List<Node> list = tree.findNearest(1, n);
        variables = ImmutableMap.of("point", list.get(0));
      } catch (Exception e) {
          System.out.println(e);
      }
      return GSON.toJson(variables);
    }
  }
  
  private static class PathHandler implements Route {
    @Override
    public String handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      Map<String, Object> variables = null;
      List<List<String>> ways = new ArrayList<>();
      try {
        ways = db.routeCommand(Arrays.asList("route", qm.value("a"), qm.value("b"), qm.value("c"), qm.value("d")), tree);
        variables = ImmutableMap.of("ways", ways);
      } catch (Exception e) {
          System.out.println(e);
      }
      return GSON.toJson(variables);
    }
  }
  
  private static class SuggestHandler implements Route {
    @Override
    public String handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      Map<String, Object> variables = null;
      List<String> suggest;
      try {
        suggest = db.genSuggestions(qm.value("text"));
        variables = ImmutableMap.of("options", suggest);
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