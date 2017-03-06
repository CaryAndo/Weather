package codes.cary.weather.utils;

import android.content.Context;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by cary on 3/3/17.
 */

public class StorageUtils {
    private static final String TAG = StorageUtils.class.getCanonicalName();

    /**
     * Write a Serializable Object to a file
     *
     * @param context  The app Context
     * @param fileName The name of the file to write to
     * @param object   The Serializable Object to write
     */
    public static void writeSerializableToFile(Context context, String fileName, Serializable object) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeObject(object);
            out.flush();
            byte[] objectBytes = bos.toByteArray();
            writeByteArrayToFile(context, fileName, objectBytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                // Don't care
            }
        }
    }

    /**
     * Read a Serializable Object from a file
     *
     * @param context  The app Context
     * @param fileName The anme of the file to read from
     * @return The Serializable Object that was read
     */
    public static Serializable readSerializableFromFile(Context context, String fileName) {
        ByteArrayInputStream byteArrayInputStream = null;

        try {
            byte[] bytes = readByteArrayFromFile(context, fileName);

            byteArrayInputStream = new ByteArrayInputStream(bytes);
            ObjectInput oi = new ObjectInputStream(byteArrayInputStream);
            Object o = oi.readObject();

            return (Serializable) o;
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (byteArrayInputStream != null) {
                try {
                    byteArrayInputStream.close();
                } catch (IOException e) {
                    // Don't care
                }
            }
        }
    }

    /**
     * Write an array of bytes to a file in private app storage
     *
     * @param context  The app Context
     * @param fileName The name of the file to write to
     * @param bytes    The bytes to write in the file
     * @throws IOException
     */
    public static void writeByteArrayToFile(Context context, String fileName, byte[] bytes) throws IOException {
        FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
        fos.write(bytes);
        fos.flush();
        fos.close();

        Log.d(getTag(), "Successfully wrote bytes to: " + fileName);
    }

    /**
     * Read an array of bytes from a file in private app storage
     *
     * @param context  The app Context
     * @param fileName The name of the file from which to read
     * @return The bytes contained in the file
     * @throws IOException
     */
    public static byte[] readByteArrayFromFile(Context context, String fileName) throws IOException {
        InputStream fis = context.openFileInput(fileName);
        ByteArrayOutputStream bufferStream = new ByteArrayOutputStream();

        byte[] dataBuffer = new byte[1024];
        int readBytes;

        while ((readBytes = fis.read(dataBuffer, 0, dataBuffer.length)) != -1) {
            bufferStream.write(dataBuffer, 0, readBytes);
        }

        bufferStream.flush();

        Log.d(getTag(), "Successfully read bytes from: " + fileName);
        return bufferStream.toByteArray();
    }

    private static String getTag() {
        return TAG;
    }
}
