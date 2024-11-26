package github.yvesbenabou.firebase;

public class Salle {
    private  String num;   // Numéro de la salle
    private Status state; // État de la salle
    private String end;
    private int[] pos;    // Position de la salle, par exemple (x, y)

    // Constructeur
    public Salle(String num, Status state, String end, int[] pos) {
        this.num = num;
        this.state = state;
        this.pos = pos;
        this.end = end;
    }

    public String getEnd() {
        return end;
    }

    // Getters et Setters
    public String getNum() {
        return this.num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public Status getState() {
        return this.state;
    }

    public void setState(Status state) {
        this.state = state;
    }

    public int[] getPos() {
        return this.pos;
    }

    public int getEtage(){
        return ((int)num.charAt(1));
    }

    public void setPos(int[] pos) {
        if (pos.length == 2 ){
            this.pos = pos;
        }
    }
}
