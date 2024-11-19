package github.yvesbenabou.firebase;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.clans.fab.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public final class DoorButton extends FloatingActionButton {
  private ImageView takeRoomBubble;
  private ConfirmRoomButton crb;
  private ModifyButton modifyButton;
  private CancelButton cancelButton;
  private TextView selectedTimeTextView;
  private EditText roomText;
  private SimpleDateFormat dateFormat;

  public DoorButton(@NonNull Context context) {
    super(context);
  }

  public DoorButton(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public DoorButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public void setup(ImageView imageView, ConfirmRoomButton confirmButton,
                    CancelButton cancelButton, ModifyButton modifyButton,
                    TextView selectedTimeTextView, EditText roomText) {
    this.takeRoomBubble = imageView;
    this.crb = confirmButton;
    this.modifyButton = modifyButton;
    this.cancelButton = cancelButton;
    this.selectedTimeTextView = selectedTimeTextView;
    this.roomText = roomText;
    this.dateFormat = new SimpleDateFormat("HH:mm");
    dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
  }

  public void show() {
    this.takeRoomBubble.setVisibility(View.VISIBLE);
    this.crb.setVisibility(View.VISIBLE);
    this.modifyButton.setVisibility(View.VISIBLE);
    this.cancelButton.setVisibility(View.VISIBLE);
    this.selectedTimeTextView.setVisibility(View.VISIBLE);
    this.roomText.setVisibility(View.VISIBLE);
    notFound();
    setTime();
  }

  public void setTime() {
    String time = dateFormat.format(new Date());
    int hour = Integer.parseInt(time.substring(0, 2));
    int minute = Integer.parseInt(time.substring(3, 5));

    if(hour < 23) {
      hour++;
      if(minute > 45 && hour <23) {
        hour++;
        minute = 0;
      } else if(minute > 45) {
        minute = 59;
      } else {
        minute = 0;
      }
    }
    else if(hour == 23) minute = 59;
    String time_str = String.format("%02d:%02d", hour, minute);
    this.selectedTimeTextView.setText(time_str);
  }

  public boolean verify(String time) {
    String real_time = dateFormat.format(new Date());
    int real_hour = Integer.parseInt(real_time.substring(0, 2));
    int real_minute = Integer.parseInt(real_time.substring(3, 5));

    int hour = Integer.parseInt(time.substring(0, 2));
    int minute = Integer.parseInt(time.substring(3, 5));

    if(real_hour < hour || (hour == real_hour && real_minute < minute)) {
      return true;
    }
    return false;
  }

  public void hide() {
    this.takeRoomBubble.setVisibility(View.GONE);
    this.crb.setVisibility(View.GONE);
    this.modifyButton.setVisibility(View.GONE);
    this.cancelButton.setVisibility(View.GONE);
    this.selectedTimeTextView.setVisibility(View.GONE);
    this.roomText.setVisibility(View.GONE);
    this.roomText.setHint("Entrez votre n° de salle");
    this.roomText.setText("");
  }

  // Fonction appelée lorsque le texte correspond bien à une salle
  public void salleFound() {
    this.crb.setColorNormal(this.crb.green);
    this.crb.setColorRipple(this.crb.green);
    this.crb.setClickable(true);
  }

  // Fonction appelée lorsque le texte ne correspond pas à une salle
  public void notFound() {
    this.crb.setColorNormal(this.crb.red);
    this.crb.setColorRipple(this.crb.red);
    this.crb.setClickable(false);
  }
}
