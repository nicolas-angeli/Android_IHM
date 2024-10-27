package github.yvesbenabou.firebase.libs;

public class Salle {
    private  String num;   // Numéro de la salle
    private String state; // État de la salle
    private int[] pos;    // Position de la salle, par exemple (x, y)

    // Constructeur
    public Salle(String num, String state, int[] pos) {
        this.num = num;
        this.state = state;
        this.pos = pos;
    }

    // Getters et Setters
    public String getNum() {
        return this.num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int[] getPos() {
        return this.pos;
    }

    public int getEtage(){
        return ((int)num.charAt(2));
    }

    public void setPos(int[] pos) {
        if (pos.length == 2 ){
            this.pos = pos;
        }
    }
}