public class Player {
    private double x, y; // Pitch coordinates in meters (Length: 0-105, Width: 0-68)
    private String name;
    private boolean isHomeTeam; // true = your team (blue), false = opponent (red)

    public Player(String name, double x, double y, boolean isHomeTeam) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.isHomeTeam = isHomeTeam;
    }

    public double getX()
    { return x; }
    public void setX(double x)
    { this.x = x; }

    public double getY()
    { return y; }
    public void setY(double y)
    { this.y = y; }

    public String getName()
    { return name; }
    public boolean isHomeTeam()
    { return isHomeTeam; }


}