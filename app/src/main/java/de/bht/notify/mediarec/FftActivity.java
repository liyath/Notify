package de.bht.notify.mediarec;
//Import relative packages

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;


import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;

//The main class of Android Application
public class FftActivity extends Activity {
    //Components and Variables for Main View
    Button wavBtn;
    Button stepBtn;
    //Components and Variables for WAV View
    Button w_backBtn;
    String w_audioName;
    Button wav1Btn;
    Button wav2Btn;
    Button wav3Btn;
    Button wav4Btn;
    TextView subtitleTv;
    Button generateBtn;
    Button play_orgBtn;
    Button play_fftBtn;
    SurfaceView orgWave_sufView;
    SurfaceView fftWave_sufView;
    byte[] org_buffer;
    float org_oldX = 0;
    float org_oldY = 0;
    float fft_oldX = 0;
    float fft_oldY = 0;
    float fft_db_oldX = 0;
    float fft_db_oldY = 0;
    Paint mPaint;
    ImageView imv1;
    Button dbBtn;

    //Components and Variables for STEP View
    int step = 0;
    double[] Arr;
    double[] Real;
    double[] Img;
    Complex[] In;
    Complex[] Out;
    Complex[][] F;
    EditText text1, text2, text3, text4;
    TextView view1, view2, view3;
    Button btn1, btn2, btn3, s_backBtn;
    ImageView img1;
    ImageButton imgBtn1;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    //The onCreate function is used to initialize the Android application
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        wavBtn = (Button) findViewById(R.id.wavBtn);
        wavBtn.setOnClickListener(new ClickEvent());
        stepBtn = (Button) findViewById(R.id.stepBtn);
        stepBtn.setOnClickListener(new ClickEvent());
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    //The layoutSwitch function is used to switch layout among Main View, Step View, and WAV View
    public void layoutSwitch(String layout_name) {
        if (layout_name == "WAV") {
            setContentView(R.layout.wav_file);
            w_backBtn = (Button) findViewById(R.id.w_backBtn);
            w_backBtn.setOnClickListener(new ClickEvent());
            w_audioName = "";
            wav1Btn = (Button) findViewById(R.id.wav1Btn);
            wav1Btn.setOnClickListener(new ClickEvent());
            wav2Btn = (Button) findViewById(R.id.wav2Btn);
            wav2Btn.setOnClickListener(new ClickEvent());
            wav3Btn = (Button) findViewById(R.id.wav3Btn);
            wav3Btn.setOnClickListener(new ClickEvent());
            wav4Btn = (Button) findViewById(R.id.wav4Btn);
            wav4Btn.setOnClickListener(new ClickEvent());
            subtitleTv = (TextView) findViewById(R.id.subtitleTv);
            generateBtn = (Button) findViewById(R.id.generateBtn);
            generateBtn.setOnClickListener(new ClickEvent());
            play_orgBtn = (Button) findViewById(R.id.play_orgBtn);
            play_orgBtn.setOnClickListener(new ClickEvent());
            play_fftBtn = (Button) findViewById(R.id.play_fftBtn);
            play_fftBtn.setOnClickListener(new ClickEvent());
            orgWave_sufView = (SurfaceView) findViewById(R.id.orgWave_sufView);
            fftWave_sufView = (SurfaceView) findViewById(R.id.fftWave_sufView);
            imv1 = (ImageView) findViewById(R.id.imv1);
            dbBtn = (Button) findViewById(R.id.dbBtn);
            dbBtn.setOnClickListener(new ClickEvent());
            //fftdB_sufView = (SurfaceView)findViewById(R.id.fftdB_sufView);
            mPaint = new Paint();
            mPaint.setColor(Color.GREEN);
            mPaint.setStrokeWidth(1);
            org_oldX = 0;
            org_oldY = orgWave_sufView.getHeight() / 2;
            fft_oldX = 0;
            fft_oldY = fftWave_sufView.getHeight() / 2;
            fft_db_oldX = 0;
            //fft_db_oldY = fftdB_sufView.getHeight();
        } else if (layout_name == "STEP") {
            setContentView(R.layout.step_by_step);
            text1 = (EditText) findViewById(R.id.editText1);
            text2 = (EditText) findViewById(R.id.editText2);
            text3 = (EditText) findViewById(R.id.editText3);
            text4 = (EditText) findViewById(R.id.editText4);
            view1 = (TextView) findViewById(R.id.textView1);
            view2 = (TextView) findViewById(R.id.textView2);
            view3 = (TextView) findViewById(R.id.textView3);
            btn1 = (Button) findViewById(R.id.button1);
            btn2 = (Button) findViewById(R.id.button2);
            btn3 = (Button) findViewById(R.id.button3);
            btn2.setEnabled(false);
            btn3.setEnabled(false);
            img1 = (ImageView) findViewById(R.id.imageView1);
            img1.setVisibility(ImageView.INVISIBLE);
            imgBtn1 = (ImageButton) findViewById(R.id.imageButton1);
            imgBtn1.setVisibility(ImageButton.INVISIBLE);
            s_backBtn = (Button) findViewById(R.id.s_backBtn);
            s_backBtn.setOnClickListener(new ClickEvent());
            Arr = new double[4];
            Real = new double[4];
            Img = new double[4];
            In = new Complex[4];
            Out = new Complex[4];
            F = new Complex[4][4];
            btn1.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    step = 0;
                    if (text1.getText().toString().equals("") || text2.getText().toString().equals("") || text3.getText().toString().equals("") || text4.getText().toString().equals("")) {
                        return;
                    }
                    Arr[0] = Double.parseDouble(text1.getText().toString());
                    Arr[1] = Double.parseDouble(text2.getText().toString());
                    Arr[2] = Double.parseDouble(text3.getText().toString());
                    Arr[3] = Double.parseDouble(text4.getText().toString());
                    for (int i = 0; i < 4; i++)
                        In[i] = new Complex(Arr[i], 0);
                    view2.setText("The Array is: \n [" + Arr[0] + ", " + Arr[1] + ", " + Arr[2] + ", " + Arr[3] + "]");
                    btn2.setEnabled(true);
                    btn3.setEnabled(true);
                }

                ;
            });

            // Next Step button
            btn3.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (step >= 0)
                        if (step < 4) {
                            img1.setVisibility(ImageView.INVISIBLE);
                            imgBtn1.setVisibility(ImageButton.VISIBLE);
                            step++;
                            switch (step) {
                                case 1:
                                    view3.setText(
                                            "First step: \n" +
                                                    "N=4, \n" +
                                                    "Y[0]=x[0]W0+x[1]W0+x[2]W0+x[3]W0 \n" +
                                                    "Y[1]=x[0]W0+x[1]W1+x[2]W2+x[3]W3 \n" +
                                                    "Y[2]=x[0]W0+x[1]W2+x[2]W4+x[3]W6 \n" +
                                                    "Y[3]=x[0]W0+x[1]W3+x[2]W6+x[3]W9");
                                    img1.setBackgroundResource(R.drawable.step1);
                                    break;
                                case 2:
                                    view3.setText(
                                            "Second step: \n" +
                                                    "W0=cos(0)+i*sin(0)= 1 \n" +
                                                    "W1=cos(pi/2)+i*sin(pi/2)= i \n" +
                                                    "W2=cos(pi)+i*sin(pi)= -1 \n" +
                                                    "W3=cos(pi*3/2)+i*sin(pi*3/2)= -i \n" +
                                                    "W4=cos(2*pi)+i*sin(2*pi)= 1 \n" +
                                                    "W6=cos(3*pi)+i*sin(3*pi)= -1 \n" +
                                                    "W9=cos(pi*9/2)+i*sin(pi*9/2)= i");
                                    img1.setBackgroundResource(R.drawable.step2);
                                    break;
                                case 3:
                                    F[0][0] = new Complex(1, 0);
                                    F[0][1] = new Complex(1, 0);
                                    F[0][2] = new Complex(1, 0);
                                    F[0][3] = new Complex(1, 0);
                                    F[1][0] = new Complex(1, 0);
                                    F[1][1] = new Complex(0, 1);
                                    F[1][2] = new Complex(-1, 0);
                                    F[1][3] = new Complex(0, -1);
                                    F[2][0] = new Complex(1, 0);
                                    F[2][1] = new Complex(-1, 0);
                                    F[2][2] = new Complex(1, 0);
                                    F[2][3] = new Complex(-1, 0);
                                    F[3][0] = new Complex(1, 0);
                                    F[3][1] = new Complex(0, -1);
                                    F[3][2] = new Complex(-1, 0);
                                    F[3][3] = new Complex(0, 1);
                                    view3.setText(
                                            "Third step: \n" +
                                                    "The F matrix is: \n" +
                                                    "1  1  1  1 \n" +
                                                    "1  i -1 -i \n" +
                                                    "1 -1  1 -1 \n" +
                                                    "1 -i -1  i");
                                    img1.setBackgroundResource(R.drawable.step3);
                                    break;
                                case 4:
                                    // Calculate FFT results
                                    for (int i = 0; i < 4; i++)
                                        Out[i] = new Complex(0, 0);
                                    for (int i = 0; i < 4; i++)
                                        for (int j = 0; j < 4; j++) {
                                            Out[i] = Out[i].Add(Out[i], Out[i].Mul(F[i][j], In[j]));
                                        }
                                    view3.setText(
                                            "Final Result: \n" +
                                                    "Y is \n" +
                                                    "---------------- \n" +
                                                    Out[0].Show(Out[0]) + "\n" +
                                                    Out[1].Show(Out[1]) + "\n" +
                                                    Out[2].Show(Out[2]) + "\n" +
                                                    Out[3].Show(Out[3])
                                    );
                                    imgBtn1.setVisibility(ImageButton.INVISIBLE);
                            }
                        }
                }

                ;
            });

            // Previous Step button
            btn2.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (step > 0)
                        if (step <= 4) {
                            img1.setVisibility(ImageView.INVISIBLE);
                            imgBtn1.setVisibility(ImageButton.VISIBLE);
                            step--;
                            switch (step) {
                                case 1:
                                    view3.setText(
                                            "First step: \n" +
                                                    "N=4, \n" +
                                                    "Y[0]=x[0]W0+x[1]W0+x[2]W0+x[3]W0 \n" +
                                                    "Y[1]=x[0]W0+x[1]W1+x[2]W2+x[3]W3 \n" +
                                                    "Y[2]=x[0]W0+x[1]W2+x[2]W4+x[3]W6 \n" +
                                                    "Y[3]=x[0]W0+x[1]W3+x[2]W6+x[3]W9");
                                    img1.setBackgroundResource(R.drawable.step1);
                                    break;
                                case 2:
                                    view3.setText(
                                            "Second step: \n" +
                                                    "W0=cos(0)+i*sin(0)= 1 \n" +
                                                    "W1=cos(pi/2)+i*sin(pi/2)= i \n" +
                                                    "W2=cos(pi)+i*sin(pi)= -1 \n" +
                                                    "W3=cos(pi*3/2)+i*sin(pi*3/2)= -i \n" +
                                                    "W4=cos(2*pi)+i*sin(2*pi)= 1 \n" +
                                                    "W6=cos(3*pi)+i*sin(3*pi)= -1 \n" +
                                                    "W9=cos(pi*9/2)+i*sin(pi*9/2)= i");
                                    img1.setBackgroundResource(R.drawable.step2);
                                    break;
                                case 3:
                                    view3.setText(
                                            "Third step: \n" +
                                                    "The F matrix is: \n" +
                                                    "1  1  1  1 \n" +
                                                    "1  i -1 -i \n" +
                                                    "1 -1  1 -1 \n" +
                                                    "1 -i -1  i");
                                    img1.setBackgroundResource(R.drawable.step3);
                                    break;
                                case 0:
                                    view3.setText("click next step to run");
                                    imgBtn1.setVisibility(ImageButton.INVISIBLE);
                                    break;
                            }
                        }
                }

                ;
            });

            imgBtn1.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    img1.setVisibility(ImageView.VISIBLE);
                }
            });
        } else if (layout_name == "MAIN") {
            setContentView(R.layout.main);
            wavBtn = (Button) findViewById(R.id.wavBtn);
            wavBtn.setOnClickListener(new ClickEvent());
            stepBtn = (Button) findViewById(R.id.stepBtn);
            stepBtn.setOnClickListener(new ClickEvent());
        }
    }

    //Set up filename according to which file the user has chosen
    public void setAudioFileName(String filename) {
        w_audioName = filename;
        subtitleTv.setText("You have three options: (" + filename + ")");
    }

    //The w_setAllVisible function is used to manage visibility
    public void w_setAllVisible() {
        subtitleTv.setVisibility(SurfaceView.VISIBLE);
        generateBtn.setVisibility(SurfaceView.VISIBLE);
        play_orgBtn.setVisibility(SurfaceView.VISIBLE);
        play_fftBtn.setVisibility(SurfaceView.VISIBLE);
        play_fftBtn.setEnabled(false);
        orgWave_sufView.setVisibility(SurfaceView.VISIBLE);
        fftWave_sufView.setVisibility(SurfaceView.VISIBLE);
        //fftdB_sufView.setVisibility(SurfaceView.VISIBLE);
        org_oldX = 0;
        org_oldY = orgWave_sufView.getHeight() / 2;
        fft_oldX = 0;
        fft_oldY = fftWave_sufView.getHeight() / 2;
        fft_db_oldX = 0;
        //fft_db_oldY = fftdB_sufView.getHeight();
        imv1.setVisibility(ImageView.INVISIBLE);
        dbBtn.setVisibility(Button.INVISIBLE);
    }

    //The mag_sqrd function is used to perform simple calculation
    public double mag_sqrd(double re, double im) {
        return re * re + im * im;
    }

    //The fft_func function is used to perform FFT on specific audio file
    public void fft_func() {
        InputStream inputStream = getResources().openRawResource(R.raw.wav_text_1);
        if (w_audioName == "wav1.wav") {
            inputStream = getResources().openRawResource(R.raw.wav_text_1);
        } else if (w_audioName == "wav2.wav") {
            inputStream = getResources().openRawResource(R.raw.wav_text_2);
        } else if (w_audioName == "wav3.wav") {
            inputStream = getResources().openRawResource(R.raw.wav_text_3);
        } else if (w_audioName == "wav4.wav") {
            inputStream = getResources().openRawResource(R.raw.wav_text_4);
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String readLine = null;
        int bufferSize = 256;
        double[] buf = new double[bufferSize];
        org_oldX = 0;
        org_oldY = orgWave_sufView.getHeight() / 2;
        fft_oldX = 0;
        fft_oldY = fftWave_sufView.getHeight() / 2;
        fft_db_oldX = 0;
        int it = 0;
        try {
            while ((readLine = br.readLine()) != null) {
                buf[it] = Double.parseDouble(readLine);
                it++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        SimpleDraw_org(buf, orgWave_sufView.getHeight() / 2, orgWave_sufView);
        DoubleFFT_1D ft = new DoubleFFT_1D(buf.length);
        double[] img_input = new double[buf.length];
        double[] real_input = new double[buf.length];
        for (int j = 0; j < buf.length; j++) {
            real_input[j] = buf[j];
            img_input[j] = 0.0;
        }
        ft.realForward(real_input);
        double[] re_dB = new double[buf.length];
        for (int j = 0; j < buf.length; j++) {
            re_dB[j] = 10 * Math.log10((double) mag_sqrd(real_input[j], img_input[j]));
        }
        double max = -1;
        for (int j = 0; j < buf.length; j++) {
            if (real_input[j] > max) {
                max = real_input[j];
            }
        }
        for (int j = 0; j < buf.length; j++) {
            if (real_input[j] < max * 0.1) {
                real_input[j] = 0;
            }
        }
        ft.realInverse(real_input, true);
        SimpleDraw_fft(real_input, fftWave_sufView.getHeight() / 2, fftWave_sufView);
    }

    //The SimpleDraw_org function is used to draw original graph of the chosen audio file
    void SimpleDraw_org(double[] buffer, int baseLine, SurfaceView sfv) {
        Canvas canvas = sfv.getHolder().lockCanvas(
                new Rect(0, 0, sfv.getWidth(), sfv.getHeight()));
        canvas.drawColor(Color.BLACK);
        float y;
        org_oldX = 0;
        org_oldY = (float) ((-1.0) * buffer[0] * 50.0 + baseLine * 1.0);
        ;
        for (int i = 1; i < buffer.length; i++) {
            float x = (float) (i * 1.8);
            y = (float) ((-1.0) * buffer[i] * 50.0 + baseLine * 1.0);
            canvas.drawLine(org_oldX, org_oldY, x, y, mPaint);
            //Log.e("here is x and y", "(" + x + ", " + y + "), (" + org_oldX + ", " + org_oldY + ")");
            org_oldX = x;
            org_oldY = y;
        }
        sfv.getHolder().unlockCanvasAndPost(canvas);
    }

    //The SimpleDraw_fft function is used to draw FFT processed graph of the chosen audio file
    void SimpleDraw_fft(double[] buffer, int baseLine, SurfaceView sfv) {
        Canvas canvas = sfv.getHolder().lockCanvas(
                new Rect(0, 0, sfv.getWidth(), sfv.getHeight()));
        canvas.drawColor(Color.BLACK);
        float y;
        fft_oldX = 0;
        fft_oldY = (float) ((-1.0) * buffer[0] * 50.0 + baseLine * 1.0);
        ;
        for (int i = 1; i < buffer.length; i++) {
            float x = (float) (i * 1.8);
            y = (float) ((-1.0) * buffer[i] * 50.0 + baseLine * 1.0);
            canvas.drawLine(fft_oldX, fft_oldY, x, y, mPaint);
            fft_oldX = x;
            fft_oldY = y;
        }
        sfv.getHolder().unlockCanvasAndPost(canvas);
    }

    //The SimpleDrawFTT function is used to draw DB/Hz graph.
    void SimpleDrawFFT() {
        if (w_audioName == "wav1.wav") {
            imv1.setImageResource(R.drawable.a3);
            imv1.setVisibility(ImageView.VISIBLE);
        } else if (w_audioName == "wav2.wav") {
            imv1.setImageResource(R.drawable.a3);
            imv1.setVisibility(ImageView.VISIBLE);
        } else if (w_audioName == "wav3.wav") {
            imv1.setImageResource(R.drawable.wav_3);
            imv1.setVisibility(ImageView.VISIBLE);
        } else if (w_audioName == "wav4.wav") {
            imv1.setImageResource(R.drawable.wav_4);
            imv1.setVisibility(ImageView.VISIBLE);
        }
        canvas.drawColor(Color.BLACK);
        float y;
        fft_db_oldX = 0;
        fft_db_oldY = (float) ((-1) * (buffer[1]));
        for (int i = 2; i < buffer.length; i++) {
            float x = (float) i;
            if (buffer[i] > 0) {
                continue;
            }
            y = (float) ((-1) * (buffer[i]));
            canvas.drawLine(fft_db_oldX, fft_db_oldY, x, y, mPaint);
            fft_db_oldX = x;
            fft_db_oldY = y;
        }
        sfv.getHolder().unlockCanvasAndPost(canvas);*/
    }

    //The GetFrequencyIntensity function is used to perform calculation to get frequency intensity.
    public double GetFrequencyIntensity(double re, double im) {
        return Math.sqrt((re * re) + (im * im));
    }

    //The play_fft function is used to play FFT processed audio file.
    public void play_fft() {
        InputStream inputStream = getResources().openRawResource(R.raw.wav1);
        ;
        if (w_audioName == "wav1.wav") {
            inputStream = getResources().openRawResource(R.raw.wav1);
        } else if (w_audioName == "wav2.wav") {
            inputStream = getResources().openRawResource(R.raw.wav2);
        } else if (w_audioName == "wav3.wav") {
            inputStream = getResources().openRawResource(R.raw.wav3);
        } else if (w_audioName == "wav4.wav") {
            inputStream = getResources().openRawResource(R.raw.wav4);
        }
        try {
            int bufferSize = 256;
            int i = 0;
            byte[] org_buffer = new byte[bufferSize];
            int minBufferSize = AudioTrack.getMinBufferSize(8192, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT);
            AudioTrack at = new AudioTrack(AudioManager.STREAM_MUSIC, 8192, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, minBufferSize, AudioTrack.MODE_STREAM);
            at.play();
            DoubleFFT_1D ft = new DoubleFFT_1D(org_buffer.length);
            while ((i = inputStream.read(org_buffer, 0, bufferSize)) != -1) {
                double[] img_input = new double[org_buffer.length];
                double[] real_input = new double[org_buffer.length];
                for (int j = 0; j < org_buffer.length; j++) {
                    real_input[j] = (double) org_buffer[j];
                    img_input[j] = 0.0;
                }
                ft.realForward(real_input);
                double max = -1;
                for (int j = 0; j < org_buffer.length; j++) {
                    if (real_input[j] > max) {
                        max = real_input[j];
                    }
                }
                for (int j = 0; j < org_buffer.length; j++) {
                    if (real_input[j] < max * 0.1) {
                        real_input[j] = 0;
                    }
                }
                ft.realInverse(real_input, true);

                byte[] finall = new byte[org_buffer.length];
                for (int j = 0; j < org_buffer.length; j++) {
                    finall[j] = (byte) GetFrequencyIntensity(real_input[j], img_input[j]);
                }
                at.write(finall, 0, i);
            }
            inputStream.close();
            at.stop();
        } catch (IOException e) {
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Fft Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://de.bht.notify.mediarec/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Fft Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://de.bht.notify.mediarec/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    //ClickEvent is used to process all mouse click actions.
    class ClickEvent implements OnClickListener {
        public void onClick(View v) {
            //Main View Buttons
            if (v == wavBtn) {
                layoutSwitch("WAV");
            } else if (v == stepBtn) {
                layoutSwitch("STEP");
            } else if (v == w_backBtn) {
                layoutSwitch("MAIN");
            } else if (v == s_backBtn) {
                layoutSwitch("MAIN");
            } else if (v == wav1Btn) {
                setAudioFileName("wav1.wav");
                w_setAllVisible();
            } else if (v == wav2Btn) {
                setAudioFileName("wav2.wav");
                w_setAllVisible();
            } else if (v == wav3Btn) {
                setAudioFileName("wav3.wav");
                w_setAllVisible();
            } else if (v == wav4Btn) {
                setAudioFileName("wav4.wav");
                w_setAllVisible();
            } else if (v == generateBtn) {
                w_setAllVisible();
                dbBtn.setVisibility(Button.VISIBLE);
                fft_func();
                play_fftBtn.setEnabled(true);
            } else if (v == play_fftBtn) {
                play_fft();
            } else if (v == play_orgBtn) {
                MediaPlayer mediaPlayer;
                if (w_audioName == "wav1.wav") {
                    mediaPlayer = MediaPlayer.create(v.getContext(), R.raw.wav1);
                    mediaPlayer.start();
                } else if (w_audioName == "wav2.wav") {
                    mediaPlayer = MediaPlayer.create(v.getContext(), R.raw.wav2);
                    mediaPlayer.start();
                } else if (w_audioName == "wav3.wav") {
                    mediaPlayer = MediaPlayer.create(v.getContext(), R.raw.wav3);
                    mediaPlayer.start();
                } else if (w_audioName == "wav4.wav") {
                    mediaPlayer = MediaPlayer.create(v.getContext(), R.raw.wav4);
                    mediaPlayer.start();
                }
            } else if (v == dbBtn) {
                SimpleDrawFFT();
            }
        }
    }

    //Complex is a class used to define complex number
    public class Complex {
        double re = 0;
        double im = 0;

        public Complex(double r, double i) {
            this.re = r;
            this.im = i;
        }

        Complex() {
        }

        Complex Add(Complex c1, Complex c2) {
            Complex c = new Complex();
            c.re = c1.re + c2.re;
            c.im = c1.im + c2.im;
            return c;
        }

        Complex Sub(Complex c1, Complex c2) {
            Complex c = new Complex();
            c.re = c1.re - c2.re;
            c.im = c1.im - c2.im;
            return c;
        }

        Complex Mul(Complex c1, Complex c2) {
            Complex c = new Complex();
            c.re = (c1.re * c2.re) - (c1.im * c2.im);
            c.im = (c1.im * c2.re) + (c1.re * c2.im);
            return c;
        }

        String Show(Complex c) {
            String str = c.re + " + " + c.im + "i";
            return str;
        }

    }
}