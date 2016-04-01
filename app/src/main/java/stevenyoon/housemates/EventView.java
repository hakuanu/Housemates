package stevenyoon.housemates;

import android.graphics.Color;
import android.view.View;
import android.content.Context;
import android.util.AttributeSet;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by Mikael on 3/31/16.
 */



public class EventView extends View {
    public static int max_event_id = 0;
    private int event_id;
    Paint p = new Paint();

    public EventView(Context context) {
        super(context);
        event_id = max_event_id;
        max_event_id ++;
        this.setMeasuredDimension(50,100);
    }

    public EventView(Context context, AttributeSet attrs) {
        super(context, attrs);
        event_id = max_event_id;
        max_event_id ++;
        this.setMeasuredDimension(50,100);
    }

    public int getId() {
        return this.event_id;
    }

    @Override
    public void onDraw(Canvas canvas) {
        p.setColor(Color.GREEN);
        //canvas.drawRect(0, 50 * event_id + 100, 100, 100 * event_id + 100, p);
        canvas.drawRect(200,200,300,250,p);
        System.out.println("Finished drawing");
    }

    /*@Override
    public void onMeasure(int w, int h) {
        super.onMeasure(w, h);
    }*/

}
