package github.yvesbenabou.firebase;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public final class TakeRoomButton extends FloatingActionButton {
  private final String floors = "étages";

  public TakeRoomButton(@NonNull Context context) {
    super(context);
  }

  public TakeRoomButton(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public TakeRoomButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }
}
