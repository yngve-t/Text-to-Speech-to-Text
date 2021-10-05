package com.example.text_to_speech;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class Splash extends AppCompatActivity {
    public static TextToSpeech mTTS = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // инициализация преобразования текста в речь
        mTTS = new TextToSpeech(Splash.this, new TextToSpeech.OnInitListener()
        {
            @Override
            public void onInit(int status)
            {
                if (status == TextToSpeech.SUCCESS)
                {
                    int result = mTTS.setLanguage(Locale.getDefault());

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
                    {
                        Log.e("TTS", "Извините, этот язык не поддерживается");
                    }
                    else
                    {
                        startActivity(new Intent(Splash.this, MainActivity.class)); // ЗАПУСК основной программы
                    }
                }
                else
                {
                    Log.e("TTS", "Ошибка!");
                }
            }
        });

        //finish();
    }

    public static void Speech(String text, int mode)
    {
        if (mode == 0)
        {
            mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
        else if (mode == 1)
        {
            mTTS.speak(text, TextToSpeech.QUEUE_ADD, null);
        }
    }

    @Override
    protected void onDestroy()
    {
        if (mTTS != null)
        {
            mTTS.stop();
            mTTS.shutdown();
        }

        super.onDestroy();
    }
}
