package com.github.teocci.algo.ai.javafx.base.model;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2018-Aug-23
 */
public class Vector2D
{
    private double x, y;

    private int n = 2;

    public Vector2D(double angle)
    {
        this.x = Math.cos(angle);
        this.y = Math.sin(angle);
    }

    public Vector2D(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    public Vector2D() {}

    // return the length of the vector
    public int length()
    {
        return 2;
    }

    // return the inner product of this Vector a and b
    public double dot(Vector2D that)
    {
        if (this.length() != that.length())
            throw new IllegalArgumentException("dimensions disagree");

        return x * that.getX() + y *that.getY();
    }

    // return the Euclidean norm of this Vector
    public double magnitude()
    {
        return Math.sqrt(dot(this));
    }

    // return the Euclidean distance between this and that
    public double distanceTo(Vector2D that)
    {
        if (length() != that.length())
            throw new IllegalArgumentException("dimensions disagree");
        return minus(that).magnitude();
    }

    // return this + that
    public Vector2D plus(Vector2D that)
    {
        if (length() != that.length())
            throw new IllegalArgumentException("dimensions disagree");
        Vector2D c = new Vector2D(x + that.getX(), y + that.getY());

        return c;
    }

    // return this - that
    public Vector2D minus(Vector2D that)
    {
        if (this.length() != that.length())
            throw new IllegalArgumentException("dimensions disagree");
        Vector2D c = new Vector2D(x - that.getX(), y - that.getY());

        return c;
    }

    // create and return a new object whose value is (this * factor)
    public Vector2D scale(double factor)
    {
        Vector2D c = new Vector2D(x * factor, y * factor);

        return c;
    }


    // return the corresponding unit vector
    public Vector2D direction()
    {
        if (magnitude() == 0.0) throw new ArithmeticException("zero-vector has no direction");
        return scale(1.0 / magnitude());
    }

    // return a string representation of the vector
    public String toString()
    {
        StringBuilder s = new StringBuilder();
        s.append('(');
        s.append(x);
        s.append(", ");
        s.append(y);
        s.append(')');
        return s.toString();
    }

    // Normalize a vectors length....
    public Vector2D normalize()
    {
        Vector2D v = new Vector2D();

        double length = Math.sqrt(x * x + y * y);
        if (length != 0) {
            v.setX(x / length);
            v.setY(y / length);
        }

        return v;
    }

    public double getX()
    {
        return x;
    }

    public void setX(double x)
    {
        this.x = x;
    }

    public double getY()
    {
        return y;
    }

    public void setY(double y)
    {
        this.y = y;
    }
}
