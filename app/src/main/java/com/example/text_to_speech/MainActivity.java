package com.example.text_to_speech;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Locale locale = new Locale("ru-Ru");
    EditText et;

    //Текст в речь

    private TextToSpeech tts;
    boolean ttsEnabled;

    public void speak(String text) {
        if (!ttsEnabled) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ttsGreater21(text);
        }else {
            ttsUnder20(text);
        }
    }

    @SuppressWarnings("deprecation")
    private void ttsUnder20(String text) {
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, map);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater21(String text) {
        String utteranceId = this.hashCode() + " ";
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }

    //Речь в текст
    private static final int VR_REQUEST = 999;

    private void listenToSpeech() {
        Intent listenIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        listenIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
        listenIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Произнесите слова");
        listenIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        listenIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);


        startActivityForResult(listenIntent, VR_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Обработка распознанных слов
        if (requestCode == VR_REQUEST && resultCode == RESULT_OK) {
            ArrayList<String> suggestedWords = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            if (et.getText().toString().trim().length() != 0) {
                et.append(" ");
            }
            et.append(suggestedWords.get(0));
            for (int i = 1; i < suggestedWords.size(); i++) {
                et.append(' ' + suggestedWords.get(i));
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        et = (EditText) findViewById(R.id.editText);
        Button speakBtn = (Button) findViewById(R.id.speakText);
        Button listerBtn = (Button) findViewById(R.id.listenText);

        //Текст в речь
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    if (tts.isLanguageAvailable(new Locale(Locale.getDefault().getLanguage()))
                            == TextToSpeech.LANG_AVAILABLE) {
                        tts.setLanguage(locale);        //Установка языка
                    }else {
                        tts.setLanguage(Locale.US);
                    }
                    tts.setPitch(1f);                    //Не помню, но что-то связанное с речью
                    tts.setSpeechRate(0.7f);             //Установка темпа речи
                    tts.setVoice(tts.getDefaultVoice()); //Установка голоса
                    ttsEnabled = true;
                }else if (status == TextToSpeech.ERROR) {
                    Toast.makeText(getApplicationContext(), R.string.tts_error, Toast.LENGTH_LONG).show();
                    ttsEnabled = false;
                }
            }
        });

        speakBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak(et.getText().toString());
            }
        });

        //Речь в текст
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> intActivities = packageManager.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);

        if (intActivities.size() != 0) {
            listerBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listenToSpeech();
                }
            });
        }else {
            listerBtn.setEnabled(false);
            Toast.makeText(this, "Speech recognition not supported", Toast.LENGTH_LONG).show();
        }
    }
}