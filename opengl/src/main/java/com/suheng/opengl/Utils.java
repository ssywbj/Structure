package com.suheng.opengl;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.os.Build;
import android.util.Log;

import androidx.annotation.DrawableRes;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RawRes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;

public final class Utils {

    public static final int TEXTURE_NONE = -1;
    public static final int BYTES_PER_FLOAT = 4;
    private static final String TAG = "Utils";

    //https://blog.51cto.com/u_16213413/12183539
    //https://juejin.cn/post/6943395747245064206
    public static String loadShader(Context context, @RawRes int resId) {
        StringBuilder builder = new StringBuilder();

        InputStream inputStream = null;
        BufferedReader reader = null;
        try {
            inputStream = context.getResources().openRawResource(resId);
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append('\n');
            }
        } catch (IOException e) {
            Log.e(TAG, "loadShader error", e);
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (IOException e) {
                Log.e(TAG, "close reader error: " + e);
            }
            try {
                if (inputStream != null) inputStream.close();
            } catch (IOException e) {
                Log.e(TAG, "close inputStream error: " + e);
            }
        }

        return builder.toString();
    }

    public static int glCreateProgram(Context context, @RawRes int vertexShader, @RawRes int fragmentShader) {
        return glCreateProgram(loadShader(context, vertexShader), loadShader(context, fragmentShader));
    }

    public static int glCreateProgram(String vertexShaderCode, String fragmentShaderCode) {
        final int program = GLES20.glCreateProgram();
        final int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        final int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);
        //shaders can be deleted after the program is linked.
        GLES20.glDeleteShader(vertexShader);
        GLES20.glDeleteShader(fragmentShader);

        return program;
    }

    public static int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    public static int loadTexture(Context context, @DrawableRes int resId) {
        /*int[] textureObjectIds = new int[1];
        GLES20.glGenTextures(1, textureObjectIds, 0);
        if (textureObjectIds[0] == 0) {
            Log.e(TAG, "Could not generate a new OpenGL texture object.");
            return 0;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId, options);
        if (bitmap == null) {
            Log.e(TAG, "Resource ID " + resId + " could not be decoded.");
            GLES20.glDeleteTextures(1, textureObjectIds, 0);
            return 0;
        }

        // bind
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureObjectIds[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR_MIPMAP_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();

        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
        // unbind
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        return textureObjectIds[0];*/

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId, options);
        if (bitmap == null) {
            Log.e(TAG, "Resource ID " + resId + " could not be decoded.");
            return 0;
        }
        return loadTexture(bitmap, true);
    }

    public static int loadTexture(@NonNull Bitmap bitmap, boolean isRecycle) {
        final int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        if (textures[0] == 0) {
            Log.e(TAG, "Could not generate a new OpenGL texture object.");
            return 0;
        }

        //bind
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR_MIPMAP_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        if (isRecycle) {
            bitmap.recycle();
        }

        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
        //unbind
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        return textures[0];
    }

    public static @Nullable int[] glGenTextures(final int len) {
        final int[] textures = new int[len];
        GLES20.glGenTextures(len, textures, 0);
        if (textures[0] == 0) {
            Log.e(TAG, "Could not generate a new OpenGL texture object.");
            return null;
        }
        return textures;
    }

    public static void texImage2D(@NonNull Bitmap bitmap, int texture, boolean isRecycle) {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR_MIPMAP_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        if (isRecycle) {
            bitmap.recycle();
        }

        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
    }

    public static boolean supportGlEs20(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        return activityManager.getDeviceConfigurationInfo().reqGlEsVersion >= 0x20000;
    }

    @Nullable
    public static ByteBuffer glCreateReadPixels(@IntRange(from = 1) int width, @IntRange(from = 1) int height) {
        if (width < 1 || height < 1) {
            return null;
        }

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(width * height * 4);
        byteBuffer.position(0);
        GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, byteBuffer);
        return byteBuffer;
    }

    public static void bufferToOutStream(@NonNull Buffer buffer, @NonNull OutputStream outStream
            , @IntRange(from = 1) int width, @IntRange(from = 1) int height) throws RuntimeException {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        //bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
        //bitmap.recycle();

        Matrix matrix = new Matrix();
        matrix.setRotate(180); //flip vertically
        matrix.postScale(-1, 1); //flip horizontally
        Bitmap dst = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        dst.compress(Bitmap.CompressFormat.PNG, 100, outStream);
        dst.recycle();

        bitmap.recycle();
    }

    public static boolean bufferToFile(@NonNull Buffer buffer, @NonNull String path
            , @IntRange(from = 1) int width, @IntRange(from = 1) int height) {
        boolean isSuccess = false;
        OutputStream outStream = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                outStream = Files.newOutputStream(Paths.get(path));
            } else {
                outStream = new FileOutputStream(path);
            }
            bufferToOutStream(buffer, outStream, width, height);
            isSuccess = true;
        } catch (IOException | RuntimeException e) {
            Log.e(TAG, "bufferToFile error", e);
        } finally {
            if (outStream != null) {
                try {
                    outStream.close();
                } catch (IOException e) {
                    Log.e(TAG, "close OutputStream error", e);
                }
            }
        }

        return isSuccess;
    }

    public static void bufferToFile(@NonNull Buffer buffer, @NonNull File file
            , @IntRange(from = 1) int width, @IntRange(from = 1) int height) {
        OutputStream outStream = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                outStream = Files.newOutputStream(file.toPath());
            } else {
                outStream = new FileOutputStream(file);
            }
            bufferToOutStream(buffer, outStream, width, height);
        } catch (IOException | RuntimeException e) {
            Log.e(TAG, "bufferToFile error", e);
        } finally {
            if (outStream != null) {
                try {
                    outStream.close();
                } catch (IOException e) {
                    Log.e(TAG, "close OutputStream error", e);
                }
            }
        }
    }

    public static int loadTextureFromBitmap(Bitmap bitmap) {
        int[] texture = new int[1];

        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        android.opengl.GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

        return texture[0];
    }

    /**
     * Convert a 4x4 matrix that is stored in column-major order
     * to a string.
     */
    public static String mat4ToString(float[] matrix) {
        if (matrix.length < 16) {
            return "not a 4x4 matrix";
        }
        StringBuilder str = new StringBuilder();
        for (int col = 0; col < 4; col++) {
            for (int row = 0; row < 4; row++) {
                str.append(String.format(Locale.getDefault(), "%.5f", matrix[4 * row + col]));
                str.append(',');
            }
            str.deleteCharAt(str.length() - 1);
            str.append('\n');
        }
        return str.toString();
    }
}
