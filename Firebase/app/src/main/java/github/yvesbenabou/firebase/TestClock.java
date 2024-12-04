package github.yvesbenabou.firebase;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TestClock {
    public static int year = 2024;
    public static int month = 11;
    public static int date = 5;
    public static int hrs = 8;
    private static int counter = 0;
    private static boolean test = false;
    private static final String TAG = "TestClock";
    public static Date refreshTime() {
        if (TestClock.counter == 0 && !TestClock.test) {
            date += 2;
            TestClock.test = true;
        }
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"));
        cal.set(year, month, date, hrs, 1);
        Date output = cal.getTime();

        hrs += 2;
        if(hrs > 22) {
            TestClock.counter++;
            TestClock.counter %= 5;
            TestClock.hrs = 8;
            TestClock.date++;
        }
        return output;
    }
}
