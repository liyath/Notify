package de.bht.notify.mediarec.exceptions;

/**
 * Created by sandra.kriemann on 28.07.16.
 */
public class WavFileException extends Exception {
    public WavFileException() {
        super();
    }

    public WavFileException(String message) {
        super(message);
    }

    public WavFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public WavFileException(Throwable cause) {
        super(cause);
    }

}
