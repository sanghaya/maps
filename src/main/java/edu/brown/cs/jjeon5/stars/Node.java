package edu.brown.cs.jjeon5.stars;

import java.util.Objects;

public class Node implements KDable<Node> {
  private String id;
  private double lat;
  private double lon;

  public Node(String id, String lat, String lon) {
    this.id = id;
    this.lat = Double.parseDouble(lat);
    this.lon = Double.parseDouble(lon);
  }

  public String getId() {
    return id;
  }

  public double getLat() {
    return lat;
  }

  public double getLon() {
    return lon;
  }

  @Override
  public int compareDim(int dim, Node b) {
    if (dim == 0) {
      return Double.compare(lat, b.getLat());
    } else if (dim == 1) {
      return Double.compare(lon, b.getLon());
    } else {
      // error??
      return -1;
    }
  }

  @Override
  public double distance(Node b) {
    return Math.pow((lat - b.getLat()), 2) + Math.pow((lon - b.getLon()), 2);
  }

  /**
   * Return the String representation of this node.
   *
   * @return String like such: "ab, (0, 0)"
   */
  @Override
  public String toString() {
    return String.format("%s, (%f, %f)", id, lat, lon);
  }

  @Override
  public double distanceAtDim(int dim, Node b) {
    if (dim == 0) {
      return Math.abs(lat - b.getLat());
    } else if (dim == 1) {
      return Math.abs(lon - b.getLon());
    } else {
      // error??
      return -1;
    }
  }

  @Override
  public int getDimension() {
    return 2;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, lat, lon);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Node)) {
      return false;
    }

    Node n = (Node) obj;
    return (id == n.id && lat == n.getLat() && lon == n.getLon());
  }
}
