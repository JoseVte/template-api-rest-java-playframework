package models;

public abstract class Model {
    public static String TABLE = "";

    public abstract void emptyToNull();

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract int hashCode();
}