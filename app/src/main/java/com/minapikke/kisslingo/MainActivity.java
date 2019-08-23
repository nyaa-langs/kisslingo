package com.minapikke.kisslingo;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;

import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import java.lang.String;
import java.util.ArrayList;

//▼MainActivity class開始▼
public class MainActivity extends AppCompatActivity {

    //private SQLiteDatabase preDatabaseObject;

    private String ylangStr;
    private String tlangStr;
    private String levelStr;
    private String wClassStr;
    private String typeStr;

    private ArrayList<Spinner> spinners = new ArrayList<>();
    private ArrayList<ArrayAdapter<String>> adapters = new ArrayList<>();

    @Override
    //▼onCreate method設定▼
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Creates the spinners and assigns them values.
        initializeSpinners();
        registerFlashcardButtonListener((Button)findViewById(R.id.button));
    }
    //▼onCreate method終了▼

    //▼initializeSpinners method設定▼
    private void initializeSpinners(){
        // ▼spinner　onItemSelected設定▼
        spinners.add((Spinner)findViewById(R.id.yLang));
        spinners.add((Spinner)findViewById(R.id.tLang));
        spinners.add((Spinner)findViewById(R.id.level));
        spinners.add((Spinner)findViewById(R.id.wclass));
        spinners.add((Spinner)findViewById(R.id.type));

        adapters.add(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, DatabaseManager.getInstance().GetDbArray(DbObjectType.Y_LANG)));
        adapters.add(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, DatabaseManager.getInstance().GetDbArray(DbObjectType.T_LANG)));
        adapters.add(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, DatabaseManager.getInstance().GetDbArray(DbObjectType.LEVEL)));
        adapters.add(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, DatabaseManager.getInstance().GetDbArray(DbObjectType.CLASS)));
        adapters.add(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, DatabaseManager.getInstance().GetDbArray(DbObjectType.TYPE)));

        ylangStr =  DatabaseManager.getInstance().GetDbArray(DbObjectType.Y_LANG)[0];
        tlangStr =  DatabaseManager.getInstance().GetDbArray(DbObjectType.T_LANG)[0];
        levelStr =  DatabaseManager.getInstance().GetDbArray(DbObjectType.LEVEL)[0];
        wClassStr = DatabaseManager.getInstance().GetDbArray(DbObjectType.CLASS)[0];
        typeStr =   DatabaseManager.getInstance().GetDbArray(DbObjectType.TYPE)[0];

        for (int i = 0; i < adapters.size(); i++){
            adapters.get(i).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinners.get(i).setAdapter(adapters.get(i));
            // default表示切替
            spinners.get(i).setSelection(0,false);
            spinners.get(i).setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }

        for (int i = 0; i < 5; i++){
            spinners.get(i).setOnItemSelectedListener(new OnItemSelectedListener() {
                //　アイテムが選択された時
                @Override
                public void onItemSelected(AdapterView<?> parent,
                                           View view, int position, long id) {

                    Spinner spinner = (Spinner)parent;
                    System.out.println("Spinner changed!");
                    switch (spinner.getId()){
                        case R.id.yLang:
                            ylangStr = (String)spinner.getSelectedItem();
                            DatabaseManager.getInstance().UpdateYourLanguage(ylangStr);

                            setSpinner(spinners.get(1), DatabaseManager.getInstance().GetDbArray(DbObjectType.T_LANG));
                            setSpinner(spinners.get(2), DatabaseManager.getInstance().GetDbArray(DbObjectType.LEVEL));
                            setSpinner(spinners.get(3), DatabaseManager.getInstance().GetDbArray(DbObjectType.CLASS));
                            setSpinner(spinners.get(4), DatabaseManager.getInstance().GetDbArray(DbObjectType.TYPE));

                            //spinner2-5 のylangStrによる分岐定義
                            if(ylangStr.equals("English")){
                                ((Button)findViewById(R.id.button)).setText(R.string.study_button_en);
                            }else{
                                ((Button)findViewById(R.id.button)).setText(R.string.study_button_jp);
                            }
                            break;
                        case R.id.tLang:
                            tlangStr = (String)spinner.getSelectedItem();
                            DatabaseManager.getInstance().UpdateYourLanguage(ylangStr);
                            setSpinner(spinners.get(2), DatabaseManager.getInstance().GetDbArray(DbObjectType.LEVEL));
                            setSpinner(spinners.get(3), DatabaseManager.getInstance().GetDbArray(DbObjectType.CLASS));
                            setSpinner(spinners.get(4), DatabaseManager.getInstance().GetDbArray(DbObjectType.TYPE));
                            break;
                        case R.id.level:
                            levelStr = (String)spinner.getSelectedItem();
                            DatabaseManager.getInstance().UpdateLevelType(levelStr);
                            setSpinner(spinners.get(3), DatabaseManager.getInstance().GetDbArray(DbObjectType.CLASS));
                            break;
                        case R.id.wclass:
                            wClassStr = (String)spinner.getSelectedItem();
                            DatabaseManager.getInstance().UpdateClassType(wClassStr);
                            setSpinner(spinners.get(4), DatabaseManager.getInstance().GetDbArray(DbObjectType.TYPE));
                            break;
                        case R.id.type:
                            typeStr = (String)spinner.getSelectedItem();
                            break;
                    }


                }

                //　アイテムが選択されなかった
                public void onNothingSelected(AdapterView<?> parent) {
                    //
                }
            });
        }
    }
    //▼initializeSpinners method終了▼

    //▼flashcardButton onClick method設定▼
    private void registerFlashcardButtonListener(Button pFlashcardButton){
        //A key-value pair list of yExamples as keys and tExamples as values.
        final DualHashBidiMap<String,String> examplesList = new DualHashBidiMap<String, String>();
        final LinearLayout linearLayout =  findViewById(R.id.ExamplesScroll);

        pFlashcardButton.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        linearLayout.removeAllViews();
                        DatabaseManager.getInstance().UpdateFlashcards(typeStr);
                        String[] yLangExArray = DatabaseManager.getInstance().GetDbArray(DbObjectType.Y_LANG_EX);
                        String[] furiganaArray = DatabaseManager.getInstance().GetDbArray(DbObjectType.FURIGANA);
                        String[] tLangExfArray = DatabaseManager.getInstance().GetDbArray(DbObjectType.T_LANG_EXF);
                        String[] chikoguyakuArray = DatabaseManager.getInstance().GetDbArray(DbObjectType.CHIKUGOYAKU);
                        for (int i = 0; i < yLangExArray.length; i++){
                            String fullExample = "";
                            if (i < furiganaArray.length)    fullExample += furiganaArray[i] + "\n";
                                else fullExample += "\n";
                            if (i < tLangExfArray.length && !tLangExfArray[i].equals(""))    fullExample += tLangExfArray[i] + "\n";
                                else fullExample += "Could not find a translation!\n";
                            if (i < chikoguyakuArray.length) fullExample += chikoguyakuArray[i];

                            Button button = new Button(getApplicationContext());
                            button.setText(yLangExArray[i]);
                            button.setAllCaps(false);
                            button.setHeight(500);

                            linearLayout.addView(button);
                            examplesList.put(yLangExArray[i], fullExample);
                            AnimateFlashcard(button, examplesList);
                        }
                        if (examplesList.size()<1){
                            displayEmptyList(linearLayout);
                        }
                    }

                });
    }

    private void displayEmptyList(LinearLayout pLayout){
        TextView view = new TextView(getApplicationContext());
        view.setText(R.string.empty_list_en);
        view.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        view.setTextSize(14f);
        view.setTextColor(Color.rgb(100,0,255));
        pLayout.addView(view);
    }
    //▼flashcardButton onClick method終了▼

    //▼animatedFlashcard onClick method設定▼
    private void AnimateFlashcard(final Button pButton, final DualHashBidiMap<String, String> pExamplesList){
        final float scale = getApplicationContext().getResources().getDisplayMetrics().density;

        pButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //If we click the button twice without caching "lastSentence" we would get an empty string.
                        //System.out.println(pButton.getText().toString() + " == " + lastSentence + " : " + pButton.getText().toString().toLowerCase().equals(lastSentence.toLowerCase()) );

                        AnimatorSet anim = (AnimatorSet) AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.flipanim);
                        anim.setTarget(pButton);
                        pButton.setCameraDistance(8000 * scale); //8000 = distance

                        if(pButton.getText()!=null && pExamplesList.containsKey(pButton.getText().toString())){
                            pButton.setText(pExamplesList.get(pButton.getText().toString()));
                        }
                        else pButton.setText(pExamplesList.getKey(pButton.getText().toString()));

                        final String tempText = pButton.getText().toString();
                        anim.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationStart(Animator animation, boolean isReverse) {
                                pButton.setText("");
                            }

                            @Override
                            public void onAnimationEnd(Animator animation, boolean isReverse) {
                                pButton.setText(tempText);
                            }
                        });
                        anim.start();
                    }
                });
    }
    //▼animatedButton onClick method終了▼

    // setSpinnerの定義
    private void setSpinner(Spinner spinner,String[] arr){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, arr);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0, false);
    }

}
// ▼MainActivity class終了▼
