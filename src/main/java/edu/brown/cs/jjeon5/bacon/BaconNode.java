package edu.brown.cs.jjeon5.bacon;

import java.util.Objects;

public class BaconNode {
  private String id;
  private String name;
  private String movieName;
  private BaconNode previous;
  private double weight;

  public BaconNode(String id, String name, String movieName, BaconNode previous,
      double weight) {
    this.id = id;
    this.movieName = movieName;
    this.name = name;
    this.previous = previous;
    this.weight = weight;
  }

  public String getMovie() {
    return movieName;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public BaconNode getPrevious() {
    return previous;
  }

  public double getWeight() {
    return weight;
  }

  public void setWeight(double w) {
    weight = w;
  }

  public void setPrevious(BaconNode n) {
    previous = n;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name);
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof BaconNode)) {
      return false;
    }
    BaconNode c = (BaconNode) o;
    return id.equals(c.getId()) && name.equals(c.getName());
  }
}
