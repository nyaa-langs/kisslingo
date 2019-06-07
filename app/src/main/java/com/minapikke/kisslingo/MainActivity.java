package com.minapikke.kisslingo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.content.res.AssetManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import java.io.File;
import java.lang.String;
import android.util.Log;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.InputStream;

//▼MainActivity class開始▼
public class MainActivity extends AppCompatActivity {

    private final static String DB_NAME="example_sentences.db";
    private final static String DB_TABLE="ExampleSentences";
    private final static int DB_VERSION=1;

    private SQLiteDatabase DatabaseObject;
    //private SQLiteDatabase preDatabaseObject;

    private String ylangStr;
    private String tlangStr;
    private String levelStr;
    private String wclassStr;
    private String typeStr;


    @Override
    // ▼onCreate method設定▼
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // ▼pre-made database設定▼
        //Database();

        //▼csvファイルからDatabaseへの落とし込み
        DatabaseHelper DbHelperObject = new DatabaseHelper(MainActivity.this);
        DatabaseObject =
                DbHelperObject.getWritableDatabase();

        String dropTable = "DROP TABLE IF EXISTS " + DB_TABLE;

        String createTable = "CREATE TABLE " + DB_TABLE +
                "(id integer primary key autoincrement, ylang text, tlang text, level text, wclass text, type text, tlang_ex text, ylang_ex text, tlang_exf, furigana text, chikugoyaku text)";

        // 古いテーブルを破棄
        DatabaseObject.execSQL(dropTable);
        // テーブルを作成
        DatabaseObject.execSQL(createTable);

       // csvからのデータの書き込み
        writetoDatabase("jap_verb.csv");
        //writetoDatabase("eng_verb.csv");


        // ▼spinner　onItemSelected設定▼
        final Spinner spinner1 = (Spinner) findViewById(R.id.spinner1);
        final Spinner spinner2 = (Spinner) findViewById(R.id.spinner2);
        final Spinner spinner3 = (Spinner) findViewById(R.id.spinner3);
        final Spinner spinner4 = (Spinner) findViewById(R.id.spinner4);
        final Spinner spinner5 = (Spinner) findViewById(R.id.spinner5);

        ArrayAdapter<String> adapter1
                = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.YL));
        ArrayAdapter<String> adapter2
                = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.TLE));
        ArrayAdapter<String> adapter3
                = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.LJ));
        ArrayAdapter<String> adapter4
                = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.CJ));
        ArrayAdapter<String> adapter5
                = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.TJ));

        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner1.setAdapter(adapter1);
        spinner2.setAdapter(adapter2);
        spinner3.setAdapter(adapter3);
        spinner4.setAdapter(adapter4);
        spinner5.setAdapter(adapter5);

        // spinner未選択時
        ylangStr = getResources().getStringArray(R.array.YL)[0];
        tlangStr = getResources().getStringArray(R.array.TLE)[0];
        levelStr = getResources().getStringArray(R.array.LJ)[0];
        wclassStr = getResources().getStringArray(R.array.CJ)[0];
        typeStr = getResources().getStringArray(R.array.TJ)[0];

        // default表示切替
        spinner1.setSelection(0, false);
        spinner2.setSelection(0, false);
        spinner3.setSelection(0, false);
        spinner4.setSelection(0, false);
        spinner5.setSelection(0, false);

        // spinner1へリスナーを登録
        spinner1.setOnItemSelectedListener(new OnItemSelectedListener() {
            //　アイテムが選択された時
            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {

                Spinner spinner1 = (Spinner)parent;
                ylangStr = (String)spinner1.getSelectedItem();

                //spinner2-5 のylangStrによる分岐定義
                if(ylangStr.equals("English")){
                    setSpinner(spinner2, getResources().getStringArray(R.array.TLE));
                    setSpinner(spinner3, getResources().getStringArray(R.array.LJ));
                    setSpinner(spinner4, getResources().getStringArray(R.array.CJ));
                    setSpinner(spinner5, getResources().getStringArray(R.array.TJ));
                }else{
                    setSpinner(spinner2, getResources().getStringArray(R.array.TLJ));
                    setSpinner(spinner3, getResources().getStringArray(R.array.LE));
                    setSpinner(spinner4, getResources().getStringArray(R.array.CE));
                    setSpinner(spinner5, getResources().getStringArray(R.array.TE));
                }
                /*else if( ylang.equals("日本語")) {
                    setSpinner(spinner2, getResources().getStringArray(R.array.TLJ));
                    setSpinner(spinner3, getResources().getStringArray(R.array.LE));
                    setSpinner(spinner4, getResources().getStringArray(R.array.CE));
                    setSpinner(spinner5, getResources().getStringArray(R.array.TE));
                }else{
                    //ここで、spinner5を画面から消去し、spinner6をsubjectNounList（this/this noun / I / mine etc）に変更したい
                    setSpinner(spinner5, getResources().getStringArray(R.array.WNLE));
                    setSpinner(spinner7, getResources().getStringArray(R.array.TBLJ));
                }*/

            }

            //　アイテムが選択されなかった
            public void onNothingSelected(AdapterView<?> parent) {
                //
            }

        });

        // spinner2へリスナーを登録
        spinner2.setOnItemSelectedListener(new OnItemSelectedListener() {
            //　アイテムが選択された時
            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {

                Spinner spinner2 = (Spinner)parent;
                tlangStr = (String)spinner2.getSelectedItem();
            }

            //　アイテムが選択されなかった
            public void onNothingSelected(AdapterView<?> parent) {
                //
            }
        });


        // spinner3へリスナーを登録
        spinner3.setOnItemSelectedListener(new OnItemSelectedListener() {
            //　アイテムが選択された時
            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {

                Spinner spinner3 = (Spinner)parent;
                levelStr = (String)spinner3.getSelectedItem();
            }

            //　アイテムが選択されなかった
            public void onNothingSelected(AdapterView<?> parent) {
                //
            }
        });

        // spinner4へリスナーを登録
        spinner4.setOnItemSelectedListener(new OnItemSelectedListener() {
            //　アイテムが選択された時
            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {

                Spinner spinner4 = (Spinner)parent;
                wclassStr = (String)spinner4.getSelectedItem();
            }

            //　アイテムが選択されなかった
            public void onNothingSelected(AdapterView<?> parent) {
                //
            }
        });

        // spinner5へリスナーを登録
        spinner5.setOnItemSelectedListener(new OnItemSelectedListener() {
            //　アイテムが選択された時
            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {

                Spinner spinner5 = (Spinner)parent;
                wclassStr = (String)spinner5.getSelectedItem();
            }

            //　アイテムが選択されなかった
            public void onNothingSelected(AdapterView<?> parent) {
                //
            }
        });


        // ▼spinner　onItemSelected終了▼



        // ▼button onClick method設定▼
        findViewById(R.id.button)
                .setOnClickListener(
                        new View.OnClickListener() {
                            // ListViewに表示するためのArrayAdapter
                            ArrayAdapter<String> ad;

                            @Override
                            public void onClick(View v) {
                                try {
                                    String selectsql ="SELECT id,ylang,tlang,level,wclass,type,tlang_ex,ylang_ex,tlang_exf,furigana,chikugoyaku FROM ExampleSentences";
                                    //String selectsql ="SELECT id,ylang,tlang,level,wclass,word,subject,tense,type,ylang_ex,tlang_ex,tlang_exf,furigana,chikugoyaku FROM ExampleSentences WHERE class ='" + wclassStr + "' and word ='" + wordStr + "' and subject ='" + subjectStr + "' and tense ='" + tenseStr + "'";

                                    Cursor cursor = DatabaseObject.rawQuery(selectsql,null);

                                    ad = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1);

                                    if(cursor.moveToFirst()){
                                        do{
                                            int id = cursor.getInt(cursor.getColumnIndex("id"));
                                            String wclass = cursor.getString(cursor.getColumnIndex("wclass"));
                                            //String word = cursor.getString(cursor.getColumnIndex("word"));
                                            //String subject = cursor.getString(cursor.getColumnIndex("subject"));
                                            //String tense = cursor.getString(cursor.getColumnIndex("tense"));
                                            String type = cursor.getString(cursor.getColumnIndex("type"));
                                            String tlang_ex = cursor.getString(cursor.getColumnIndex("tlang_ex"));
                                            String ylang_ex = cursor.getString(cursor.getColumnIndex("ylang_ex"));

                                            String row = id + ":" + type + ":" + ylang_ex + ":" + tlang_ex;
                                            //String row = tense + ":" ;
                                            ad.add(row);
                                        }while(cursor.moveToNext());
                                    }
                                } catch(Exception e){
                                    // データベースオブジェクトをクローズ
                                    DatabaseObject.close();
                                    }
                                ((ListView) findViewById(R.id.ListView)).setAdapter(ad);
                            }
                        });
    }
                            //▼button onClick method終了▼

                            //　▼onCreate method終了▼

    // setSpinnerの定義
    private void setSpinner(Spinner spinner,String[] arr){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, arr);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0, false);
    }

    // writetoDatabaseの定義
    private void writetoDatabase(String filename) {
        try{
            AssetManager assetManager = getApplicationContext().getAssets();
            //InputStream inputStream = assetManager.open("jap_verb" + i + ".csv");
            InputStream inputStream = assetManager.open(filename);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferReader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = bufferReader.readLine()) != null) {
                String[] arrr = line.split(",");
                String Insertarrr =
                        "INSERT INTO " + DB_TABLE + "(ylang, tlang, level, wclass, type, tlang_ex, ylang_ex, tlang_exf, furigana, chikugoyaku) VALUES ('" + arrr[1] + "','" + arrr[2] + "','" + arrr[3] + "','" + arrr[4] + "','" + arrr[5] + "','" + arrr[6] + "','" + arrr[7] + "','" + arrr[8] + "','" + arrr[9] + "','" + arrr[10] + "')";

                DatabaseObject.execSQL(Insertarrr);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

        // ▼database method設定▼
    private void Database(){

            // database
            DatabaseHelper DbHelperObject = new DatabaseHelper(MainActivity.this);
            DatabaseObject =
                    DbHelperObject.getWritableDatabase();


            String dropTable = "DROP TABLE IF EXISTS " + DB_TABLE;

            String createTable = "CREATE TABLE " + DB_TABLE +
                    "(id integer primary key autoincrement, ylang text, tlang text, level text, wclass text, type text, tlang_ex text, ylang_ex text, tlang_exf, furigana text, chikugoyaku text)";

            String[] insertData = {
                    "INSERT INTO " + DB_TABLE + "(ylang, tlang, level, wclass, word, subject, tense, type, eng_example, jap_example) VALUES ('日本語','英語', '初級','動詞','study', 'I', '現在・肯定', 'I study English every day.', '私は毎日英語を勉強する')",
                    "INSERT INTO " + DB_TABLE + "(ylang, tlang, level, wclass, word, subject, tense, type, eng_example, jap_example) VALUES ('日本語','英語', '初級','動詞','study', 'I', '現在・肯定', 'Do I study English every day?', '私は毎日英語を勉強する？')",
                    "INSERT INTO " + DB_TABLE + "(ylang, tlang, level, wclass, word, subject, tense, type, eng_example, jap_example) VALUES ('日本語','英語', '初級','動詞','study', 'I', '現在・肯定', 'What do I study every day?', '私は毎日何を勉強する？')",
                    "INSERT INTO " + DB_TABLE + "(ylang, tlang, level, wclass, word, subject, tense, type, eng_example, jap_example) VALUES ('日本語','英語', '初級','動詞','study', 'I', '現在・否定', 'I don’t study it at school.', '私はそれを学校で勉強しない')",
                    "INSERT INTO " + DB_TABLE + "(ylang, tlang, level, wclass, word, subject, tense, type, eng_example, jap_example) VALUES ('日本語','英語', '初級','動詞','study', 'I', '現在・否定', 'Don’t I study it at school?', '私はそれを学校で勉強しない？')",
                    "INSERT INTO " + DB_TABLE + "(ylang, tlang, level, wclass, word, subject, tense, type, eng_example, jap_example) VALUES ('日本語','英語', '初級','動詞','study', 'I', '現在・否定', 'Why don’t I study it at school?', '私はなぜそれを学校で勉強しない？')",
                    "INSERT INTO " + DB_TABLE + "(ylang, tlang, level, wclass, word, subject, tense, type, eng_example, jap_example) VALUES ('日本語','英語', '初級','動詞','study', 'I', '過去・肯定', 'I studied math last night.', '私は昨夜数学を勉強した')",
                    "INSERT INTO " + DB_TABLE + "(ylang, tlang, level, wclass, word, subject, tense, type, eng_example, jap_example) VALUES ('日本語','英語', '初級','動詞','study', 'I', '過去・肯定', 'Did I study math last night?', '私は昨夜数学を勉強した？')",
                    "INSERT INTO " + DB_TABLE + "(ylang, tlang, level, wclass, word, subject, tense, type, eng_example, jap_example) VALUES ('日本語','英語', '初級','動詞','study', 'I', '過去・肯定', 'What did I study last night?', '私は昨夜何を勉強した？')",
                    "INSERT INTO " + DB_TABLE + "(ylang, tlang, level, wclass, word, subject, tense, type, eng_example, jap_example) VALUES ('日本語','英語', '初級','動詞','study', 'I', '過去・否定', 'I didn’t study it this morning.', '私は今朝それを勉強しなかった')",
                    "INSERT INTO " + DB_TABLE + "(ylang, tlang, level, wclass, word, subject, tense, type, eng_example, jap_example) VALUES ('日本語','英語', '初級','動詞','study', 'I', '過去・否定', 'Didn’t I study it this morning?', '私は今朝それを勉強しなかった？')",
                    "INSERT INTO " + DB_TABLE + "(ylang, tlang, level, wclass, word, subject, tense, type, eng_example, jap_example) VALUES ('日本語','英語', '初級','動詞','study', 'I', '過去・否定', 'Why didn’t I study it this morning?', '私はなぜ今朝それを勉強しなかった？')"
            };

            // 古いテーブルを破棄
            DatabaseObject.execSQL(dropTable);
            // テーブルを作成
            DatabaseObject.execSQL(createTable);
            // データ登録
            for(int i = 0; i < insertData.length; i++){
                DatabaseObject.execSQL(insertData[i]);
            }

            // データベースオブジェクトをクローズ
//        DatabaseObject.close();
    }

    //▼database method終了▼





    //▼DatabaseHelper class設定▼
    private static class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context) {

           super(
                context,DB_NAME,null,DB_VERSION
           );
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS " +
                        DB_TABLE +
                        "(id integer primary key autoincrement, ylang text, tlang text, level text, wclass text, type text, tlang_ex text, ylang_ex text, tlang_exf, furigana text, chikugoyaku text)"
        );
        Log.d("Database","Create Table");
        }
        @Override

        public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion
        ) {
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
        onCreate(db);
        }
    }
    //▼DatabaseHelper class終了▼

}
// ▼MainActivity class終了▼
