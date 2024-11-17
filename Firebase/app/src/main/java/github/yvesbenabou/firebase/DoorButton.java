package github.yvesbenabou.firebase;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.clans.fab.FloatingActionButton;

public final class DoorButton extends FloatingActionButton {
  private ImageView takeRoomBubble;
  private ConfirmRoomButton crb;
  private ModifyButton modifyButton;
  private CancelButton cancelButton;
  private TextView selectedTimeTextView;
  private EditText roomText;

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
  }

  public void show() {
    this.takeRoomBubble.setVisibility(View.VISIBLE);
    this.crb.setVisibility(View.VISIBLE);
    this.modifyButton.setVisibility(View.VISIBLE);
    this.cancelButton.setVisibility(View.VISIBLE);
    this.selectedTimeTextView.setVisibility(View.VISIBLE);
    this.roomText.setVisibility(View.VISIBLE);
    notFound();
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
