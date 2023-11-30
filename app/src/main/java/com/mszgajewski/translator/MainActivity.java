package com.mszgajewski.translator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.mszgajewski.translator.databinding.ActivityMainBinding;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    String[] fromLanguage = {"Z", "Angielski", "Francuski", "Hiszpański", "Japoński", "Koreański", "Niemiecki", "Polski", "Włoski"};
    String[] toLanguage = {"Na", "Angielski", "Francuski", "Hiszpański", "Japoński", "Koreański", "Niemiecki", "Polski", "Włoski"};

    private static final int REQUEST_PERMISSION_CODE = 1;
    String  fromLanguageCode, toLanguageCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                fromLanguageCode = getLanguageCode(fromLanguage[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ArrayAdapter fromAdapter = new ArrayAdapter(this, R.layout.spinner_item, fromLanguage);
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.fromSpinner.setAdapter(fromAdapter);

        binding.toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                toLanguageCode = getLanguageCode(toLanguage[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ArrayAdapter toAdapter = new ArrayAdapter(this, R.layout.spinner_item, toLanguage);
        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.toSpinner.setAdapter(toAdapter);

        binding.microphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Powiedz co chcesz przetłumaczyć");
                try {
                    startActivityForResult(intent,REQUEST_PERMISSION_CODE);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.translateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               binding.translatedTextView.setVisibility(View.VISIBLE);
               binding.translatedTextView.setText("");
               if (binding.editText.getText().toString().isEmpty()) {
                   Toast.makeText(MainActivity.this, "Prosze wpisać tekst", Toast.LENGTH_SHORT).show();
               } else if (fromLanguageCode == "") {
                   Toast.makeText(MainActivity.this, "Prosze wybrać z jakiego języka ma być tłumaczenie", Toast.LENGTH_SHORT).show();
               } else if (toLanguageCode == "") {
                   Toast.makeText(MainActivity.this, "Prosze wybrać na jaki język ma być tłumaczenie", Toast.LENGTH_SHORT).show();
               } else {
                   translateText(fromLanguageCode, toLanguageCode, binding.editText.getText().toString());
               }
            }
        });

    }

    private void translateText(String fromLanguageCode,String toLanguageCode, String sourceText) {

        binding.translatedTextView.setText("Pobieranie słownika...");
        TranslatorOptions options = new TranslatorOptions.Builder()
                .setSourceLanguage(fromLanguageCode)
                .setTargetLanguage(toLanguageCode)
                .build();
        Translator translator = Translation.getClient(options);
        DownloadConditions conditions = new DownloadConditions.Builder().build();

        translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                binding.translatedTextView.setText("Tłumaczenie...");
                translator.translate(sourceText).addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        binding.translatedTextView.setText(s);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Błąd tłumaczenia. Spróbuj ponownie.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Błąd tłumaczenia. Sprawdź połaczenie internetowe.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_CODE){
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            binding.editText.setText(result.get(0));
        }
    }

    private String getLanguageCode(String language) {
        String languageCode = "";
        switch(language){
            case "Angielski":
               languageCode = TranslateLanguage.ENGLISH;
               break;
            case "Niemiecki":
                languageCode = TranslateLanguage.GERMAN;
                break;
            case "Francuski":
                languageCode = TranslateLanguage.FRENCH;
                break;
            case "Hiszpański":
                languageCode = TranslateLanguage.SPANISH;
                break;
            case "Koreański":
                languageCode = TranslateLanguage.KOREAN;
                break;
            case "Japoński":
                languageCode = TranslateLanguage.JAPANESE;
                break;
            case "Polski":
                languageCode = TranslateLanguage.POLISH;
                break;
            case "Włoski":
                languageCode = TranslateLanguage.ITALIAN;
                break;
            default:
                languageCode = "";
        }
        return languageCode;
    }
}