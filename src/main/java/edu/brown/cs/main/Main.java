package edu.brown.cs.main;

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
  private String[] args;;
  private ACCommand ac;
  private static MergeAndSuggest mode;
  private static Map<String, String> in;


  private Main(String[] args) {
    mode = new MergeAndSuggest();
    ac = new ACCommand();
    this.args = args;
  }

  private void run() throws SQLException {

    OptionParser parser = new OptionParser();
    parser.accepts("gui");
    parser.accepts("port").withRequiredArg().ofType(Integer.class)
            .defaultsTo(DEFAULT_PORT);
    OptionSet options = parser.parse(args);
    boolean isGui = false;
    if (options.has("gui")) {
      //runSparkServer((int) options.valueOf("port"));
      isGui = true;
    }
    //set of commands that are allowed
    Set<String> commands = Stream.of("mdb", "connect", "corpus", "prefix",
            "whitespace", "led", "smart", "stars", "radius", "neighbors",
            "ac").collect(Collectors.toSet());
    String line;
    BufferedReader cmds = new BufferedReader(
            new InputStreamReader(System.in));
    try {
      while ((line = cmds.readLine()) != null) {
        List<String> tokens = null;
        tokens = inputProcessor(line);
        //each commands separate
        ac.acCom(tokens, line);
        String key = tokens.get(0);
        if (!commands.contains(key)) {
          System.out.println("ERROR: Wrong Command");
        }
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
