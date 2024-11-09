package github.yvesbenabou.firebase;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

public final class CancelButton extends FloatingActionButton {
  private DoorButton mDoorButton;

  public CancelButton(@NonNull Context context) {
    super(context);
  }

  public CancelButton(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public CancelButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public void setup(DoorButton button) {
    this.mDoorButton = button;
  }

  public void hide() {
    this.mDoorButton.hide();
  }
}
