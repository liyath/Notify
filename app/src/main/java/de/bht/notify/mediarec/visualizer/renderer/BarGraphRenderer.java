package de.bht.notify.mediarec.visualizer.renderer;

/**
 * Created by peachi on 30.07.2016.
 */

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import de.bht.notify.mediarec.visualizer.AudioData;
import de.bht.notify.mediarec.visualizer.FFTData;

public class BarGraphRenderer extends Renderer {
    private int mDivisions;
    private Paint mPaint;
    private boolean mTop;

    /**
     * Renders the FFT data as a series of lines, in histogram form
     *
     * @param divisions - must be a power of 2. Controls how many lines to draw
     * @param paint     - Paint to draw lines with
     * @param top       - whether to draw the lines at the top of the canvas, or the bottom
     */
    public BarGraphRenderer(int divisions,
                            Paint paint,
                            boolean top) {
        super();
        mDivisions = divisions;
        mPaint = paint;
        mTop = top;
    }

    @Override
    public void onRender(Canvas canvas, AudioData data, Rect rect) {
        // Do nothing, we only display FFT data
    }

    /* Real components were at even indices, and odd components were at odd indices.
    *  So to get magnitudes, you'd need to do something like:
    *
    *    uint32_t mag[N/2];
    *    for (int i = 0; i < N/2; i++) {
    *         mag[i] = fft[2*i]*fft[2*i] + fft[2*i+1]*fft[2*i+1];
    *    }
    */
    @Override
    public void onRender(Canvas canvas, FFTData data, Rect rect) {
        for (int i = 0; i < data.bytes.length / mDivisions; i++) {
            mFFTPoints[i * 4] = i * 4 * mDivisions;
            mFFTPoints[i * 4 + 2] = i * 4 * mDivisions;
            byte rfk = data.bytes[mDivisions * i];
            byte ifk = data.bytes[mDivisions * i + 1];
            float magnitude = (rfk * rfk + ifk * ifk);
            int dbValue = (int) (10 * Math.log10(magnitude));

            if (mTop) {
                mFFTPoints[i * 4 + 1] = 0;
                mFFTPoints[i * 4 + 3] = (dbValue * 2 - 10);
            } else {
                mFFTPoints[i * 4 + 1] = rect.height();
                mFFTPoints[i * 4 + 3] = rect.height() - (dbValue * 2 - 10);
            }
        }

        canvas.drawLines(mFFTPoints, mPaint);
    }
}