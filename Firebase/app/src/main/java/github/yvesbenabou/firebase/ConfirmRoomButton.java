package github.yvesbenabou.firebase;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

public final class ConfirmRoomButton extends FloatingActionButton implements Database_Input {
    private final String floors = "étages";
    private DoorButton mDoorButton;
    public static int green;
    public static int red;
    private String room = "";

    public ConfirmRoomButton(@NonNull Context context) {
        super(context);
    }

    public ConfirmRoomButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ConfirmRoomButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setup(DoorButton button) {
        this.mDoorButton = button;
        green = getColorNormal();
        red = getColorDisabled();
    }

    public void hide() {
        this.mDoorButton.hide();
    }

    @Override
    public void take_room() {
        FirebaseDatabase.getInstance().getReference().child(floors).child(String.valueOf(room.charAt(1))).child(room).setValue(Status.RESERVED.ordinal());
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }
}
