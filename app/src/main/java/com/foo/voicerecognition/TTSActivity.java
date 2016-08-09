package com.foo.voicerecognition;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;


/**
 * 目前只支持5种语言，分别是English、 French 、 German 、 Italian 和 Spanish.
 * 系统要求为android 1.6以上
 */
public class TTSActivity extends AppCompatActivity {

    private static final int MY_DATA_CHECK_CODE = 0;

    private TextToSpeech tts;

    private EditText inputText;

    private TextToSpeech.OnInitListener mListener = new TextToSpeech.OnInitListener() {

        @Override
        public void onInit(int status) {
            if (status == TextToSpeech.SUCCESS) {
                Toast.makeText(TTSActivity.this, "TTS引擎初始化成功", Toast.LENGTH_LONG).show();
                List<TextToSpeech.EngineInfo> engines = tts.getEngines();
                for (TextToSpeech.EngineInfo engine : engines) {
                    e(engine.name + " " + engine.toString());
                }

                // int result = tts.setLanguage(Locale.US);
                int result = tts.setLanguage(Locale.CHINESE);// 需要指定语言

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    tts.setLanguage(Locale.US);
                    e("不支持的语言，已修改为 us");
                }
            } else if (status == TextToSpeech.ERROR) {
                e("Error occurred while initializing Text-To-Speech engine");
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputText = (EditText) findViewById(R.id.input_text);
        Button speakButton = (Button) findViewById(R.id.speak_button);
        assert speakButton != null;
        speakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = inputText.getText().toString();
                if (text.length() > 0) {
                    e("Saying: " + text);
                    tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });

        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // success, create the TTS instance
                tts = new TextToSpeech(TTSActivity.this, mListener);
            } else {
                // missing data, install it
                Intent installIntent = new Intent();
                installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    private static final String TAG = "==##";

    public void e(String msg) {
        Log.e(TAG, msg);
    }
}
