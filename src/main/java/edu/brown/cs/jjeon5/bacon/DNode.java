package edu.brown.cs.jjeon5.bacon;

import java.util.Objects;

public class DNode {
  private String id;
  private double lat;
  private double lon;
  private String pathName;
  private DNode previous;
  private double weight;

  public DNode(String id, double lat, double lon, String pathName, DNode previous,
      double weight) {
    this.id = id;
    this.lat = lat;
    this.lon = lon;
    this.pathName = pathName;
    this.previous = previous;
    this.weight = weight;
  }

  public String getPath() {
    return pathName;
  }

  public double getLat() {
	return lat;
  }
  
  public double getLon() {
	return lon;
  }
  
  public String getId() {
    return id;
  }

  public DNode getPrevious() {
    return previous;
  }

  public double getWeight() {
    return weight;
  }

  public void setWeight(double w) {
    weight = w;
  }

  public void setPrevious(DNode n) {
    previous = n;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, lat, lon);
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof DNode)) {
      return false;
    }
    DNode c = (DNode) o;
    return id.equals(c.getId()) && (Double.compare(lat, c.getLat()) == 0) && (Double.compare(lon, c.getLon()) == 0);
  }

  public DNode clone() {
    DNode n = new DNode(id, lat, lon,  pathName, previous, weight);
    return n;
  }
}
