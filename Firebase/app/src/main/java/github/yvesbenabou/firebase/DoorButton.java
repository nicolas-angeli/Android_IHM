package github.yvesbenabou.firebase;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.clans.fab.FloatingActionButton;

public final class DoorButton extends FloatingActionButton {
  private ImageView takeRoomBubble;
  private ConfirmRoomButton ctrb;
  private ModifyButton modifyButton;
  private CancelButton cancelButton;

  public DoorButton(@NonNull Context context) {
    super(context);
  }

  public DoorButton(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public DoorButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public void setup(ImageView imageView, ConfirmRoomButton confirmButton, CancelButton cancelButton, ModifyButton modifyButton) {
    this.takeRoomBubble = imageView;
    this.ctrb = confirmButton;
    this.modifyButton = modifyButton;
    this.cancelButton = cancelButton;
  }

  public void show() {
    this.takeRoomBubble.setVisibility(View.VISIBLE);
    this.ctrb.setVisibility(View.VISIBLE);
    this.modifyButton.setVisibility(View.VISIBLE);
    this.cancelButton.setVisibility(View.VISIBLE);
  }

  public void hide() {
    this.takeRoomBubble.setVisibility(View.GONE);
    this.ctrb.setVisibility(View.GONE);
    this.modifyButton.setVisibility(View.GONE);
    this.cancelButton.setVisibility(View.GONE);
  }

  public void setConfirmTakeRoomButton() {
    this.ctrb.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        // On Click
        // Write data to Firebase Database
        ctrb.take_room("2307");
      }
    });
  }
}
