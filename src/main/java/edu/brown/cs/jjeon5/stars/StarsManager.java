package edu.brown.cs.jjeon5.stars;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Stars manager class that processes the commands.
 */
public class StarsManager {
  private KDTree<Star> tree;
  private HashMap<String, Star> starMap;

  /**
   * Constructor for the star manager.
   */
  public StarsManager() {
    this.tree = null;
    this.starMap = new HashMap<String, Star>();
  }

  /**
   * Processes the stars command and reads the csv file.
   *
   * @param parts
   *          String array of the input line split with space.
   */
  public void starsCommand(String[] parts) {
    if (parts.length == 1) {
      System.out.println("ERROR: Specify a file.");
      return;
    }
    String filename = parts[1];
    String starline = "";
    List<Star> stars = new ArrayList<Star>();
    try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
      br.readLine();
      while ((starline = br.readLine()) != null) {
        String[] starData = starline.split(",");
        Star s = new Star(starData[0], starData[1], starData[2], starData[3],
            starData[4]);
        stars.add(s);
        starMap.put(starData[1], s);
      }
      Star[] starArray = stars.toArray(new Star[0]);
      System.out.println(
          String.format("Read %d stars from %s", starArray.length, parts[1]));
      tree = new KDTree<Star>(starArray);
    } catch (IOException e) {
      System.out.println("ERROR: File could not be loaded.");
      return;
    }
  }

  /**
   * Processes the neighbors command and finds nearest neighbors.
   *
   * @param parts
   *          String array of the input line split with space.
   * @param str
   *          input string on the commandline
   */
  public void neighborsCommand(String[] parts, String str) {
    int k = 0;
    try {
      k = Integer.parseInt(parts[1]);
    } catch (Exception e) {
      System.out.println("ERROR: Invalid command.");
    }

    int commandNum = 0;
    try {
      Double.parseDouble(parts[2]);
      commandNum = 5;
    } catch (Exception e) {
      commandNum = 3;
    }

    if (commandNum == 3) {
      String a = str.substring(str.indexOf(" ") + 1);
      String b = a.substring(a.indexOf(" ") + 1);
      String name = b.replaceAll("\"", "");
      Star s = starMap.get(name);
      List<Star> list = tree.findNearest(k, s);
      for (int i = 0; i < list.size(); i++) {
        System.out.println(list.get(i).getId());
      }
    } else if (commandNum == 5) {
      Star s = new Star("testpt", "testpt", parts[2], parts[3], parts[4]);
      List<Star> list = tree.findNearest(k, s);
      for (int i = 0; i < list.size(); i++) {
        System.out.println(list.get(i).getId());
      }
    } else {
      System.out.println("ERROR: Ivalid command.");
    }
  }

  /**
   * Processes the radius command.
   *
   * @param parts
   *          String array of the input line split with space.
   */
  public void radiusCommand(String[] parts) {
    double r = 0.0;
    try {
      r = Double.parseDouble(parts[1]);
    } catch (Exception e) {
      System.out.println("ERROR: Invalid command.");
    }

    if (parts.length == 3) {
      String name = parts[2].replaceAll("\"", "");
      Star s = starMap.get(name);
      List<Star> list = tree.radiusSearch(r, s);
      for (int i = 0; i < list.size(); i++) {
        System.out.println(list.get(i).getId());
      }
    } else if (parts.length == 5) {
      Star s = new Star("testpt", "testpt", parts[2], parts[3], parts[4]);
      List<Star> list = tree.radiusSearch(r, s);
      for (int i = 0; i < list.size(); i++) {
        System.out.println(list.get(i).getId());
      }
    } else {
      System.out.println("ERROR: Ivalid command.");
    }
  }
}
