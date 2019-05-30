package io.ameal.testutilevc;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import io.ameal.evc.SpeechToText.IntegratedAndroidSTT;
import io.ameal.evc.SpeechToText.RegexCommandStructure;
import io.ameal.evc.SpeechToText.RegexContainer;
import io.ameal.evc.SpeechToText.VoiceCommand;

public class MainActivity extends AppCompatActivity
{
    private LinearLayout layout;
    private Button button;
    private HashMap<String, Integer> colorMap;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        colorMap = new HashMap<>();
        colorMap.put("grøn", Color.GREEN);
        colorMap.put("rød",Color.RED);
        colorMap.put("blå", Color.BLUE);
        colorMap.put("gul", Color.YELLOW);
        colorMap.put("sort", Color.BLACK);
        colorMap.put("hvid", Color.WHITE);

        layout = findViewById(R.id.linearLayout);
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                IntegratedAndroidSTT.getInstance().listenForSingleCommand();
            }
        });

        IntegratedAndroidSTT.getInstance().init(this);
        IntegratedAndroidSTT.getInstance().setSpeechLength(8000);
        IntegratedAndroidSTT.getInstance().addVoiceCommand(new VoiceCommand()
        {
            @Override
            public RegexContainer getRegexContainer()
            {
                return new RegexContainer(new ArrayList<RegexCommandStructure>()
                {{
                    add(new RegexCommandStructure("", "(\\bskift\\b)|(\\bændre\\b)|(\\bsæt\\b)", ""));
                    add(new RegexCommandStructure("", "(\\btil\\b)", "").keyWordComesNext());
                }});
            }

            @Override
            public boolean speechCommand(RegexContainer regexContainer)
            {
                return !changeColor(regexContainer.getKeyWords().get(0));
            }

            @Override
            public void onSuccessCallback(RegexContainer regexContainer)
            {
                Toast.makeText(MainActivity.this, "Den farve kan jeg ikke vise...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void noCommandFound()
            {
                Toast.makeText(MainActivity.this, "Fandt ikke noget...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public String getKeyWordSeparator()
            {
                return null;
            }
        });

        allowVoiceRecord();

    }

    private boolean changeColor(String keyWord)
    {
        Integer chosenColor = colorMap.get(keyWord);
        if(chosenColor != null)
        {
            layout.setBackgroundColor(colorMap.get(keyWord));
            return true;
        }
        return false;

    }


    private void allowVoiceRecord()
    {
        int MY_PERMISSIONS_RECORD_AUDIO = 1;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    MY_PERMISSIONS_RECORD_AUDIO);
        }
    }
}
