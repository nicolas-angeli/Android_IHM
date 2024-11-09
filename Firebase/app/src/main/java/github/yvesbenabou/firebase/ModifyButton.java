package github.yvesbenabou.firebase;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.clans.fab.FloatingActionButton;

public final class ModifyButton extends FloatingActionButton {
  public static int hour;
  public static int mins;

  public ModifyButton(@NonNull Context context) {
    super(context);
  }

  public ModifyButton(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public ModifyButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

}
