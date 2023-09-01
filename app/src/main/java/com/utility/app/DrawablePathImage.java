package com.utility.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

// https://gist.github.com/JakeWharton/2856179
//https://github.com/ajithvgiri/canvas/blob/master/canvaslibrary/src/main/java/com/ajithvgiri/canvaslibrary/CanvasView.java

public class DrawablePathImage extends AppCompatImageView {
    public DrawablePathImage(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        setMeasuredDimension(width, height);
    }

    ArrayList<String> pathToDraw = new ArrayList<>();

    public DrawablePathImage(Context context) {
        super(context);
    }

    public void setPathToDraw(ArrayList<String> pathToDraw) {
        this.pathToDraw = pathToDraw;
        this.postInvalidate();

    }

    public void clearPath() {
        this.pathToDraw.clear();
        this.postInvalidate();

    }

    @Override
    public void setBackgroundResource(int resid) {
        super.setBackgroundResource(resid);
    }


    @Override
    public void onDraw(Canvas c) {
        super.onDraw(c);

        if (pathToDraw.size() > 0) {
            Paint paint = new Paint();
            Path path = new Path();
            paint.setColor(Color.TRANSPARENT);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeWidth(5);

            c.drawPaint(paint);

            for (int j = 0; j < pathToDraw.size() - 1; j++) {
                JSONArray array;
                try {
                    array = new JSONArray(pathToDraw.get(j));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                JSONObject object1 = null;
                try {
                    object1 = array.getJSONObject(0);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                try {
                    path.moveTo((float) object1.getDouble("x") * c.getWidth(), (float) object1.getDouble("y") * c.getHeight());
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                for (int i = 0; i < array.length() - 1; i++) {
                    JSONObject object = null;
                    try {
                        object = array.getJSONObject(i);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    try {

                        path.lineTo((float) object.getDouble("x") * c.getWidth(), (float) object.getDouble("y") * c.getHeight());
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
                JSONObject object2 = null;
                try {
                    object2 = array.getJSONObject(array.length() - 1);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                try {
                    path.moveTo((float) object2.getDouble("x"), (float) object2.getDouble("y"));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                path.close();
                paint.setStrokeWidth(5);
                paint.setPathEffect(null);
                paint.setColor(Color.RED);
                paint.setStyle(Paint.Style.STROKE);
                c.drawPath(path, paint);
            }


        } else {
            System.out.println("print issue");
        }
    }

}