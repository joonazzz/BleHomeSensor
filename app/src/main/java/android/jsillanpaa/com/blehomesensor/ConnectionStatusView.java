package android.jsillanpaa.com.blehomesensor;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;


/**
 * TODO: document your custom view class.
 */
public class ConnectionStatusView extends TextView {


    public static final int STATE_DISCONNECTED = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;


    public ConnectionStatusView(Context context) {
        super(context);
        init(null, 0);
    }

    public ConnectionStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ConnectionStatusView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        setState(STATE_DISCONNECTED);

    }

    public void setState(int state) {
        switch (state){

            case STATE_DISCONNECTED:
                setText("Disconnected");
                setTextColor(Color.RED);
                break;
            case STATE_CONNECTING:
                setText("Connecting");
                setTextColor(Color.YELLOW);
                break;
            case STATE_CONNECTED:
                setText("Connected");
                setTextColor(Color.GREEN);
                break;
        }
    }

}
