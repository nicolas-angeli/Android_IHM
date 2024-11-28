package github.yvesbenabou.firebase;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.database.FirebaseDatabase;

public final class InvisibleButton extends FloatingActionButton {
  public static int orange;

  public InvisibleButton(@NonNull Context context) {
    super(context);
  }

  public InvisibleButton(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public InvisibleButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public void setup() {
    orange = getColorDisabled();
  }
}
