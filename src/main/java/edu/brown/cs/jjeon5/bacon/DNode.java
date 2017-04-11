package edu.brown.cs.jjeon5.bacon;

import java.util.Objects;

/**
 * Dijkstra Node class.
 *
 * @author sangha
 *
 */
public class DNode {
  private String id;
  private double lat;
  private double lon;
  private String pathName;
  private DNode previous;
  private double weight;

  /**
   *
   * @param id
   *          node id
   * @param lat
   *          latitude
   * @param lon
   *          longitude
   * @param pathName
   *          way name
   * @param previous
   *          previous node id
   * @param weight
   *          length of the way
   */
  public DNode(String id, double lat, double lon, String pathName,
      DNode previous, double weight) {
    this.id = id;
    this.lat = lat;
    this.lon = lon;
    this.pathName = pathName;
    this.previous = previous;
    this.weight = weight;
  }

  /**
   *
   * @return way's name
   */
  public String getPath() {
    return pathName;
  }

  /**
   *
   * @return node's latitude
   */
  public double getLat() {
    return lat;
  }

  /**
   *
   * @return node's longitude
   */
  public double getLon() {
    return lon;
  }

  /**
   *
   * @return node's id
   */
  public String getId() {
    return id;
  }

  /**
   *
   * @return previous node's id
   */
  public DNode getPrevious() {
    return previous;
  }

  /**
   *
   * @return weight of the node
   */
  public double getWeight() {
    return weight;
  }

  /**
   *
   * @param w
   *          weight of double
   */
  public void setWeight(double w) {
    weight = w;
  }

  /**
   *
   * @param n
   *          previous node n
   */
  public void setPrevious(DNode n) {
    previous = n;
  }

  /**
   * Override hash.
   */
  @Override
  public int hashCode() {
    return Objects.hash(id, lat, lon);
  }

  /**
   * Override equals.
   */
  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof DNode)) {
      return false;
    }
    DNode c = (DNode) o;
    return id.equals(c.getId()) && (Double.compare(lat, c.getLat()) == 0)
        && (Double.compare(lon, c.getLon()) == 0);
  }

  /**
   * clone() for proxy.
   *
   * @return n dnode
   */
  public DNode clone() {
    DNode n = new DNode(id, lat, lon, pathName, previous, weight);
    return n;
  }
}
