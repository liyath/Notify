package de.bht.notify.mediarec.utils;

/**
 * Created by peachi on 30.07.2016.
 */
import java.io.IOException;

import android.content.Context;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import de.bht.notify.mediarec.R;

//import de.bht.notify.mediarec.R;

public class TunnelPlayerWorkaround {
    private static final String TAG = "TunnelPlayerWorkaround";

    private static final String SYSTEM_PROP_TUNNEL_DECODE_ENABLED = "tunnel.decode";

    private TunnelPlayerWorkaround()
    {
    }

    /**
     * Obtain "tunnel.decode" system property value
     *
     * @param context Context
     * @return Whether tunnel player is enabled
     */
    public static boolean isTunnelDecodeEnabled(Context context)
    {
        return SystemPropertiesProxy.getBoolean(
                context, SYSTEM_PROP_TUNNEL_DECODE_ENABLED, false);
    }

    /**
     * Create silent MediaPlayer instance to avoid tunnel player issue
     *
     * @param context Context
     * @return MediaPlayer instance
     */

    public static MediaPlayer createSilentMediaPlayer(Context context)
    {

        boolean result = false;

        MediaPlayer mp = null;
        try {
            mp = MediaPlayer.create(context, R.raw.sinus);
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);

            // NOTE: start() is no needed
            // mp.start();

            result = true;
        } catch (RuntimeException e) {
            Log.e(TAG, "createSilentMediaPlayer()", e);
        } finally {
            if (!result && mp != null) {
                try {
                    mp.release();
                } catch (IllegalStateException e) {
                }
            }
        }

        return mp;
    }

}