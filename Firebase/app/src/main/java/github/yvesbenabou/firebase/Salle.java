package github.yvesbenabou.firebase;

import android.widget.Button;
import android.view.View;

public class Salle {
    private  String num;   // Numéro de la salle
    private Status state; // État de la salle
    private String end;
    private Button button;    // Position de la salle, par exemple (x, y)

    // Constructeur
    public Salle(String num, Status state, String end, Button button) {
        this.num = num;
        this.state = state;
        this.button = button;;
        this.end = end;
    }

    public void show() {
     // Code pour afficher la salle sur l'écran
        this.button.setVisibility(View.VISIBLE);
        this.button.setBackgroundColor(0xFFFFFF);
    }

    public void hide() {
        // Code pour masquer la salle de l'écran
        this.button.setVisibility(View.GONE);
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
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
        if(state == Status.CLASS){
            this.button.setBackgroundColor(0xFF0000);
        } else {
            this.button.setBackgroundColor(0xFFFFFF);
        }
    }

    public int getEtage(){
        return ((int)num.charAt(1));
    }
}
