package edu.brown.cs.jjeon5.bacon;

import java.util.Objects;

public class Node {
  private String id;
  private String name;
  private String movieName;
  private Node previous;
  private double weight;

  public Node(String id, String name, String movieName, Node previous,
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

  public Node getPrevious() {
    return previous;
  }

  public double getWeight() {
    return weight;
  }

  public void setWeight(double w) {
    weight = w;
  }

  public void setPrevious(Node n) {
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
    if (!(o instanceof Node)) {
      return false;
    }
    Node c = (Node) o;
    return id.equals(c.getId()) && name.equals(c.getName());
  }
}
