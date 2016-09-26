package de.bht.notify.mediarec.visualizer.renderer;

/**
 * Created by peachi on 30.07.2016.
 */
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Debug;
import android.util.Log;

import de.bht.notify.mediarec.MainActivity;
import de.bht.notify.mediarec.visualizer.AudioData;
import de.bht.notify.mediarec.visualizer.FFTData;

public class BarGraphRenderer extends Renderer
{
    private int mDivisions;
    private Paint mPaint;
    private boolean mTop;

    /**
     * Renders the FFT data as a series of lines, in histogram form
     * @param divisions - must be a power of 2. Controls how many lines to draw
     * @param paint - Paint to draw lines with
     * @param top - whether to draw the lines at the top of the canvas, or the bottom
     */
    public BarGraphRenderer(int divisions,
                            Paint paint,
                            boolean top)
    {
        super();
        mDivisions = divisions;
        mPaint = paint;
        mTop = top;
    }

    @Override
    public void onRender(Canvas canvas, AudioData data, Rect rect)
    {
        // Do nothing, we only display FFT data
    }

    @Override
    public void onRender(Canvas canvas, FFTData data, Rect rect)
    {
        double[] mag = new double[data.bytes.length / mDivisions];
        for (int i = 0; i < data.bytes.length / mDivisions; i++) {


            byte re = data.bytes[2 * i];
            byte im = data.bytes[2 * i + 1];

            mag[i] = Math.sqrt((re * re + im * im));


            //Log.d("COMPLEX", "RFK " + rfk + "; IFK " + ifk + "; MAGNITUDE " + magnitude + "; DBVALUE " + dbValue);
        }

        double max_mag = Double.NEGATIVE_INFINITY;
            int max_ind = -1;
        for (int i = 0; i < mag.length; i++) {
            if (mag[i] > max_mag) {
                max_mag = mag[i];
                max_ind = i;
            }
        }

        double freq = max_ind*200/mDivisions;
        Log.d("FREQ", ""+freq);

    /*
        for (int i = 0; i < data.bytes.length / mDivisions; i++) {

            mFFTPoints[i * 4] = i * 4 * mDivisions;
            mFFTPoints[i * 4 + 2] = i * 4 * mDivisions;
            byte rfk = data.bytes[mDivisions * i];
            byte ifk = data.bytes[mDivisions * i + 1];
            ;
            float magnitude = (rfk * rfk + ifk * ifk);
            Log.d("COMPLEX", "RFK "+rfk + "; IFK " + ifk + "; MAGNITUDE " + magnitude);
            int dbValue = (int) (10 * Math.log10(magnitude));
            Log.d("COMPLEX", "RFK "+rfk + "; IFK " + ifk + "; MAGNITUDE " + magnitude + "; DBVALUE " + dbValue);
            if(mTop)
            {
                mFFTPoints[i * 4 + 1] = 0;
                mFFTPoints[i * 4 + 3] = (dbValue * 2 - 10);
            }
            else
            {
                mFFTPoints[i * 4 + 1] = rect.height();
                mFFTPoints[i * 4 + 3] = rect.height() - (dbValue * 2 - 10);
            }
        }


        canvas.drawLines(mFFTPoints, mPaint);
        */
    }
}