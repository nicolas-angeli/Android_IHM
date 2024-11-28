package github.yvesbenabou.firebase;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.clans.fab.FloatingActionButton;

public final class RoomButton extends FloatingActionButton {
  public static int orange;

  public RoomButton(@NonNull Context context) {
    super(context);
  }

  public RoomButton(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public RoomButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public void setOrange() {
    this.setBackgroundColor(InvisibleButton.orange);
    this.setColorNormal(InvisibleButton.orange);
    this.setColorRipple(InvisibleButton.orange);
    this.setClickable(false);
  }

  public void setGreen() {
    this.setBackgroundColor(ConfirmRoomButton.green);
    this.setColorNormal(ConfirmRoomButton.green);
    this.setColorRipple(ConfirmRoomButton.green);
    this.setClickable(false);
  }

  public void setRed() {
    this.setBackgroundColor(ConfirmRoomButton.red);
    this.setColorNormal(ConfirmRoomButton.red);
    this.setColorRipple(ConfirmRoomButton.red);
    this.setClickable(false);
  }
}
