 package edu.brown.cs.jjeon5.stars;

import java.util.Objects;

/**
 * Created by jaehyunjeon on 2/8/17.
 */
public class Star implements KDable<Star> {
  private String id;
  private String name;
  private double x;
  private double y;
  private double z;

  /**
   * Creates a specific star specified by name and x, y, z coordinates.
   *
   * @param id
   *          A string that describes the id of the star.
   * @param name
   *          A string that describes the name of the star.
   * @param x
   *          A string that is the x coordinate of the location of the star.
   * @param y
   *          A string that is the y coordinate of the location of the star.
   * @param z
   *          A string that is the z coordinate of the location of the star.
   */
  public Star(String id, String name, String x, String y, String z) {
    this.id = id;
    this.name = name;
    this.x = Double.parseDouble(x);
    this.y = Double.parseDouble(y);
    this.z = Double.parseDouble(z);
  }

  /**
   * Return the id of this star.
   *
   * @return String that describes the id of this star.
   */
  public String getId() {
    return id;
  }

  /**
   * Return the name of this star.
   *
   * @return String that describes the name of this star.
   */
  public String getName() {
    return name;
  }

  /**
   * Return the x coordinate of this star.
   *
   * @return Double that is the x coordinate.
   */
  public double getX() {
    return x;
  }

  /**
   * Return the y coordinate of this star.
   *
   * @return Double that is the y coordinate.
   */
  public double getY() {
    return y;
  }

  /**
   * Return the z coordinate of this star.
   *
   * @return Double that is the z coordinate.
   */
  public double getZ() {
    return z;
  }

  @Override
  public int compareDim(int dim, Star b) {
    if (dim == 0) {
      return Double.compare(x, b.getX());
    } else if (dim == 1) {
      return Double.compare(y, b.getY());
    } else if (dim == 2) {
      return Double.compare(z, b.getZ());
    } else {
      // error??
      return -1;
    }
  }

  @Override
  public double distance(Star b) {
    return Math.sqrt(Math.pow((x - b.getX()), 2) + Math.pow((y - b.getY()), 2)
        + Math.pow((z - b.getZ()), 2));
  }

  /**
   * Return the String representation of this star.
   *
   * @return String like such: "Sun, (0, 0, 0)"
   */
  @Override
  public String toString() {
    return String.format("%s, %s, (%f, %f, %f)", id, name, x, y, z);
  }

  @Override
  public double distanceAtDim(int dim, Star b) {
    if (dim == 0) {
      return Math.abs(x - b.getX());
    } else if (dim == 1) {
      return Math.abs(y - b.getY());
    } else if (dim == 2) {
      return Math.abs(z - b.getZ());
    } else {
      // error??
      return -1;
    }
  }

  @Override
  public int getDimension() {
    return 3;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, x, y, z);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Star)) {
      return false;
    }

    Star s = (Star) obj;
    return (id == s.id && name == s.name && x == s.getX() && y == s.getY()
        && z == s.getZ());
  }
}
