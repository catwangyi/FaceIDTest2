package com.wang.faceidtest2.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.wang.faceidtest2.model.DrawInfo;
import com.wang.faceidtest2.util.DrawHelper;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @version $Rev$
 * @auther wangyi
 * @des ${TODO}
 * @updateAuther $Auther$
 * @updateDes ${TODO}
 */
public class FaceRectView extends View {
    private static final String TAG = "FaceRectView";
    private CopyOnWriteArrayList<DrawInfo> faceRectList = new CopyOnWriteArrayList<>();

    public FaceRectView(Context context) {
        this(context, null);
    }

    public FaceRectView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (faceRectList != null && faceRectList.size() > 0) {
            for (int i = 0; i < faceRectList.size(); i++) {
                DrawHelper.drawFaceRect(canvas, faceRectList.get(i), Color.YELLOW, 5);
            }
        }
    }

    public void clearFaceInfo() {
        faceRectList.clear();
        postInvalidate();
    }

    public void addFaceInfo(DrawInfo faceInfo) {
        faceRectList.add(faceInfo);
        postInvalidate();
    }

    public void addFaceInfo(List<DrawInfo> faceInfoList) {
        faceRectList.addAll(faceInfoList);
        postInvalidate();
    }
}
