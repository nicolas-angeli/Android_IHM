package github.yvesbenabou.firebase;

import android.graphics.Color;
import android.widget.Button;
import android.view.View;

import androidx.annotation.ColorInt;

public class Salle {
    private  String num;   // Numéro de la salle
    private Status state; // État de la salle
    private String end;
    private Button button;    // Position de la salle, par exemple (x, y)
    private String etage;

    // Constructeur
    public Salle(String num, Status state, String end, Button button) {
        this.num = num;
        this.state = state;
        this.button = button;;
        this.end = end;
        this.etage = String.valueOf(num.charAt(1));
    }

    public void show() {
        // Code pour afficher la salle sur l'écran
        this.button.setVisibility(View.VISIBLE);
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
            try {
                //this.button.setBackgroundColor(Color.parseColor("#FF0000"));
                this.button.setBackgroundColor(ConfirmRoomButton.red);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (state == Status.RESERVED){
            //this.button.setBackgroundColor(Color.parseColor("#FFFF00"));
            try {
                //this.button.setBackgroundColor(Color.parseColor("#FF0000"));
                this.button.setBackgroundColor(Color.YELLOW);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                //this.button.setBackgroundColor(Color.parseColor("#FF0000"));
                this.button.setBackgroundColor(ConfirmRoomButton.green);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public String getEtage(){
        return this.etage;
    }
}