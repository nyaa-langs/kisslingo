package com.minapikke.kisslingo;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

enum DbObjectType{
    Y_LANG      ,
    T_LANG      ,
    LEVEL       ,
    CLASS       ,
    TYPE        ,
    T_LANG_EX   ,
    Y_LANG_EX   ,
    T_LANG_EXF  ,
    FURIGANA    ,
    CHIKUGOYAKU
}

public class DatabaseManager {
    private static DatabaseManager Instance = new DatabaseManager();

    public static DatabaseManager getInstance() {
        return Instance;
    }

    private final static String DB_NAME="example_sentences.db";
    private final static String DB_TABLE="ExampleSentences";
    private final static int DB_VERSION=1;
    private ArrayList<String> yLangArray       = new ArrayList<>();
    private ArrayList<String> tLangArray       = new ArrayList<>();
    private ArrayList<String> levelArray       = new ArrayList<>();
    private ArrayList<String> wClassArray      = new ArrayList<>();
    private ArrayList<String> typeArray        = new ArrayList<>();
    private ArrayList<String> tLangExArray     = new ArrayList<>();
    private ArrayList<String> yLangExArray     = new ArrayList<>();
    private ArrayList<String> tLangExFArray    = new ArrayList<>();
    private ArrayList<String> furiganaArray    = new ArrayList<>();
    private ArrayList<String> chikugoyakuArray = new ArrayList<>();

    private SQLiteDatabase DatabaseObject;

    private DatabaseManager() {
        //▼csvファイルからDatabaseへの落とし込み
        DatabaseHelper DbHelperObject = new DatabaseHelper(GlobalApplication.getAppContext());
        DatabaseObject = DbHelperObject.getWritableDatabase();

        String dropTable = "DROP TABLE IF EXISTS " + DB_TABLE;

        String createTable = "CREATE TABLE " + DB_TABLE +
                "(id integer primary key autoincrement, ylang text, tlang text, level text, wclass text, type text, tlang_ex text, ylang_ex text, tlang_exf, furigana text, chikugoyaku text)";

        // 古いテーブルを破棄
        DatabaseObject.execSQL(dropTable);
        // テーブルを作成
        DatabaseObject.execSQL(createTable);

        // csvからのデータの書き込み
        writeToDatabase("jap_verb_b1.csv");
        //writeToDatabase("jap_adjective_b1.csv");
        writeToDatabase("eng_verb_b1.csv");
        writeToDatabase("jap_frag_b1.csv");
        writeToDatabase("jap_sentence_ad.csv");
        writeToDatabase("jap_noun_b1.csv");
        initializeArrays();
    }

    private void initializeArrays(){
        try {
            String selectsql = "SELECT id,ylang,tlang,level,wclass,type FROM ExampleSentences WHERE ylang NOT LIKE 'ylang'";

            Cursor cursor = DatabaseObject.rawQuery(selectsql, null);
            clearArrays();
            if (cursor.moveToFirst()) {
                do {
                    String yLang = cursor.getString(cursor.getColumnIndex("ylang"));
                    String tLang = cursor.getString(cursor.getColumnIndex("tlang"));
                    String level = cursor.getString(cursor.getColumnIndex("level"));
                    String wClass = cursor.getString(cursor.getColumnIndex("wclass"));
                    String type = cursor.getString(cursor.getColumnIndex("type"));
                    //System.out.println( id+"\n"+ yLang+"\n"+ tLang +"\n"+level +"\n"+ wClass +"\n"+ type);

                    if (!yLangArray.contains(yLang))
                        yLangArray.add(yLang);
                    if (!tLangArray.contains(tLang))
                        tLangArray.add(tLang);
                    if (!levelArray.contains(level))
                        levelArray.add(level);
                    if (!wClassArray.contains(wClass))
                        wClassArray.add(wClass);
                    if (!typeArray.contains(type))
                        typeArray.add(type);
                } while (cursor.moveToNext());
            }
        } catch(Exception e) {
            // データベースオブジェクトをクローズ
            DatabaseObject.close();
        }

        UpdateYourLanguage(yLangArray.get(0));
        UpdateLevelType(levelArray.get(0));
        UpdateClassType(wClassArray.get(0));
        UpdateFlashcards(typeArray.get(0));
    }

    public void UpdateYourLanguage(String pYoLang) {
        try {
            String selectsql = "SELECT ylang,tlang,level,wclass FROM ExampleSentences WHERE ylang = '" + pYoLang + "'";
            Cursor cursor = DatabaseObject.rawQuery(selectsql, null);
            tLangArray.clear();
            levelArray.clear();
            wClassArray.clear();

            if (cursor.moveToFirst()) {
                do {
                    String tLang = cursor.getString(cursor.getColumnIndex("tlang"));
                    String level = cursor.getString(cursor.getColumnIndex("level"));
                    String wClass = cursor.getString(cursor.getColumnIndex("wclass"));

                    if (!tLangArray.contains(tLang))
                        tLangArray.add(tLang);
                    if (!levelArray.contains(level))
                        levelArray.add(level);
                    if (!wClassArray.contains(wClass))
                        wClassArray.add(wClass);
                } while (cursor.moveToNext());

                cursor.close();
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    public void UpdateLevelType(String pLevel){
        try {
            String selectsql = "SELECT level,wclass FROM ExampleSentences WHERE "+" level = '"+pLevel+"'";
            Cursor cursor = DatabaseObject.rawQuery(selectsql, null);
            wClassArray.clear();

            if (cursor.moveToFirst()) {
                do {
                    String mClass = cursor.getString(cursor.getColumnIndex("wclass"));
                    //System.out.println( id+"\n"+ yLang+"\n"+ tLang +"\n"+level +"\n"+ wClass +"\n"+ type);
                    if (!wClassArray.contains(mClass))
                        wClassArray.add(mClass);

                } while (cursor.moveToNext());

                cursor.close();
            }

            UpdateClassType(wClassArray.get(0));
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    public void UpdateClassType(String pClass){
        try {
            String selectsql = "SELECT wclass,type FROM ExampleSentences WHERE "+" wclass = '"+pClass+"'";
            Cursor cursor = DatabaseObject.rawQuery(selectsql, null);
            typeArray.clear();

            if (cursor.moveToFirst()) {
                do {
                    String type = cursor.getString(cursor.getColumnIndex("type"));
                    //System.out.println( id+"\n"+ yLang+"\n"+ tLang +"\n"+level +"\n"+ wClass +"\n"+ type);
                    if (!typeArray.contains(type))
                        typeArray.add(type);

                } while (cursor.moveToNext());

                cursor.close();
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    public void UpdateFlashcards(String pType){
        try {
            String selectsql = "SELECT type,tlang_ex,ylang_ex,tlang_exf,furigana,chikugoyaku FROM ExampleSentences WHERE type = '" + pType + "'";
            Cursor cursor = DatabaseObject.rawQuery(selectsql, null);
            tLangExArray.clear();
            yLangExArray.clear();
            tLangExFArray.clear();
            furiganaArray.clear();
            chikugoyakuArray.clear();

            if (cursor.moveToFirst()) {
                do {
                    String tlang_ex = cursor.getString(cursor.getColumnIndex("tlang_ex"));
                    String ylang_ex = cursor.getString(cursor.getColumnIndex("ylang_ex"));
                    String tlang_exf = cursor.getString(cursor.getColumnIndex("tlang_exf"));
                    String furigana = cursor.getString(cursor.getColumnIndex("furigana"));
                    String chikugoyaku = cursor.getString(cursor.getColumnIndex("chikugoyaku"));
                    //System.out.println( id+"\n"+ yLang+"\n"+ tLang +"\n"+level +"\n"+ wClass +"\n"+ type);
                    if (!tLangExArray.contains(tlang_ex))
                        tLangExArray.add(tlang_ex);
                    if (!yLangExArray.contains(ylang_ex))
                        yLangExArray.add(ylang_ex);
                    if (!tLangExFArray.contains(tlang_exf))
                        tLangExFArray.add(tlang_exf);
                    if (!furiganaArray.contains(furigana))
                        furiganaArray.add(furigana);
                    if (!chikugoyakuArray.contains(chikugoyaku))
                        chikugoyakuArray.add(chikugoyaku);

                } while (cursor.moveToNext());

                cursor.close();
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private void clearArrays(){
        yLangArray.clear();
        tLangArray.clear();
        levelArray.clear();
        wClassArray.clear();
        typeArray.clear();
        tLangExArray.clear();
        yLangExArray.clear();
        tLangExFArray.clear();
        furiganaArray.clear();
        chikugoyakuArray.clear();
    }

    public String[] GetDbArray(DbObjectType pObjectType){
        switch (pObjectType){
            case Y_LANG:
                return yLangArray.toArray(new String[yLangArray.size()]);
            case T_LANG:
                return tLangArray.toArray(new String[tLangArray.size()]);
            case LEVEL:
                return levelArray.toArray(new String[levelArray.size()]);
            case CLASS:
                return wClassArray.toArray(new String[wClassArray.size()]);
            case TYPE:
                return typeArray.toArray(new String[typeArray.size()]);
            case T_LANG_EX:
                return tLangExArray.toArray(new String[tLangExArray.size()]);
            case Y_LANG_EX:
                return yLangExArray.toArray(new String[yLangExArray.size()]);
            case T_LANG_EXF:
                return tLangExFArray.toArray(new String[tLangExFArray.size()]);
            case FURIGANA:
                return furiganaArray.toArray(new String[furiganaArray.size()]);
            case CHIKUGOYAKU:
                return chikugoyakuArray.toArray(new String[chikugoyakuArray.size()]);
            default:
                return null;
        }
    }

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

    // writetoDatabaseの定義
    private void writeToDatabase(String filename) {
        try{
            AssetManager assetManager = GlobalApplication.getAppContext().getAssets();
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
}
