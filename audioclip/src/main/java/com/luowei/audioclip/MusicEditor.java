package com.luowei.audioclip;

import android.media.MediaExtractor;
import android.media.MediaFormat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Created by 骆巍 on 2015/11/26.
 */
public class MusicEditor {
    public static int REQUEST_FINISH = 1000;

    public static void editorMusic(InputStream is, File targetFile, int start, int end, int allTime) throws IOException {
        byte[] byteMusic = getMusicByte(is);
        editorMusic(byteMusic, targetFile, start, end, allTime);
    }

    public static void editorMusic(File file, File targetFile, int start, int end, int allTime) throws IOException {
        byte[] byteMusic = getMusicByte(file);
        editorMusic(byteMusic, targetFile, start, end, allTime);
    }

    public static void editorMusic(byte[] bytes, File targetFile, int start, int end, int allTime) throws IOException {
        if (bytes == null) return;
        int editorStart = (start * REQUEST_FINISH) / allTime;
        int editorStop = (end * REQUEST_FINISH) / allTime;

        int num = bytes.length;
        int editorStart2 = editorStart * (num / REQUEST_FINISH);
        int count = (editorStop * (num / REQUEST_FINISH)) - editorStart2;
        FileOutputStream fout = new FileOutputStream(targetFile);
        fout.write(bytes, editorStart2, count);
        fout.close();
    }

    public static byte[] getMusicByte(File file) throws IOException {
        if (!file.exists()) {
            return null;
        }
        return getMusicByte(new FileInputStream(file));
    }

    public static byte[] getMusicByte(InputStream is) throws IOException {
        if (is == null) return null;
        int num = is.available();
        byte[] buffer = new byte[num];
        is.read(buffer);
        is.close();
        return buffer;
    }
}
