import java.util.Objects;

public class Location {
    double x , y;

    public Location(double x, double y) {
        this.x = x;
        this.y = y;
    }
    @Override
    public boolean equals(Object o){
       Location compar = (Location) o;
        return (compar.x == x && compar.y == y);
    }
}
