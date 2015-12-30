package com.luowei.audioclips;

import android.app.ProgressDialog;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.luowei.audioclip.ClipsFrameLayout;
import com.luowei.audioclip.MusicEditor;
import com.luowei.audioclip.soundfile.SoundFile;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    ClipsFrameLayout clipsFrameLayout;
    TextView tvDuration;
    Button btnPlay;
    MediaPlayer mp;
    //填写自己的音乐文件路径,这里是我的音乐文件路径
    File file = new File(Environment.getExternalStorageDirectory() + "/Music/Download", "nsn.mp3");
    SoundFile soundFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        clipsFrameLayout = (ClipsFrameLayout) findViewById(R.id.clipsFrameLayout);
        tvDuration = (TextView) findViewById(R.id.tvDuration);
        btnPlay = (Button) findViewById(R.id.btnPlay);
        mp = new MediaPlayer();
        if (!file.exists()) {
            Toast.makeText(MainActivity.this, "文件不存在 " + file, Toast.LENGTH_LONG).show();
            return;
        }
        final ProgressDialog pd = new ProgressDialog(MainActivity.this);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setTitle("Loading...");
        pd.setCancelable(false);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                pd.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    SoundFile.ProgressListener listener = new SoundFile.ProgressListener() {
                        public boolean reportProgress(double fractionComplete) {
                            pd.setProgress((int) (pd.getMax() * fractionComplete));
                            return true;
                        }
                    };
                    soundFile = SoundFile.create(file.getAbsolutePath(), listener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                try {
                    pd.dismiss();
                    mp.setDataSource(file.getAbsolutePath());
                    mp.prepare();
                    int second = (mp.getDuration() / 1000);
                    clipsFrameLayout.setMaxProgress(second);
                    clipsFrameLayout.setProgress(clipsFrameLayout.getStartClips());
                    tvDuration.setText(getFormatTime(second));
                    mp.seekTo(clipsFrameLayout.getStartClips() * 1000);

                    //监听剪辑开始位置的触摸
                    clipsFrameLayout.setStartClipsTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            switch (event.getAction()) {
                                case MotionEvent.ACTION_UP:
                                    if (mp.isPlaying()) mp.pause();
                                    btnPlay.setText("播放");
                                    clipsFrameLayout.setProgress(clipsFrameLayout.getStartClips());
                                    mp.seekTo(clipsFrameLayout.getProgress() * 1000);
                                    break;
                            }
                            return false;
                        }
                    });
                    clipsFrameLayout.setSoundFile(soundFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    public String getFormatTime(int second) {
        SimpleDateFormat sdf = new SimpleDateFormat("m:ss");
        return sdf.format(new Date(second * 1000));
    }

    public void playOnClick(View view) {
        if ("播放".equals(btnPlay.getText())) {
            if (clipsFrameLayout.getProgress() < clipsFrameLayout.getStartClips() || clipsFrameLayout.getProgress() >= clipsFrameLayout.getEndClips()) {
                mp.seekTo(clipsFrameLayout.getStartClips() * 1000);
            }
            mp.start();
            btnPlay.setText("暂停");
            btnPlay.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if ("暂停".equals(btnPlay.getText())) {
                        int p = mp.getCurrentPosition() / 1000;
                        if (p < clipsFrameLayout.getStartClips() || p > clipsFrameLayout.getEndClips() ||
                                !hasWindowFocus()) {
                            mp.pause();
                            btnPlay.setText("播放");
                            return;
                        }
                        clipsFrameLayout.setProgress(p);
                        btnPlay.postDelayed(this, 250);
                    }
                }
            }, 250);
        } else {
            mp.pause();
            btnPlay.setText("播放");
        }
    }

    public void clipsOnClick(View view) {
        try {
            String filename = file.getName();
            File targetFile = new File(Environment.getExternalStorageDirectory() + "/audioclips", filename.substring(0, filename.lastIndexOf(".")) + System.currentTimeMillis() + filename.substring(filename.lastIndexOf(".")));
            if (!targetFile.getParentFile().exists()) targetFile.getParentFile().mkdirs();
            MusicEditor.editorMusic(file, targetFile, clipsFrameLayout.getStartClips(),
                    clipsFrameLayout.getEndClips(), clipsFrameLayout.getMaxProgress());
            Toast.makeText(this, "剪辑成功,文件保存在: " + targetFile, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "剪辑失败,请查看错误日志", Toast.LENGTH_LONG).show();
        }
    }
}
