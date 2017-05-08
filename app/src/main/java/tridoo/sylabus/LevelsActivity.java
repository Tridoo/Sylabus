package tridoo.sylabus;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

public class LevelsActivity extends Activity {
    ArrayList <Button> listaPrzyciskow;
    Context context;
    DAO dao;
    int levelZaliczony;
    int rozdzial=0;
    int ILE_LEVELI=20;
    HashMap<Integer, Integer> listaWynikow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels);
        context = getApplicationContext();
        rozdzial=getIntent().getIntExtra("rozdzial",1);
        listaWynikow = (HashMap<Integer, Integer>) getIntent().getSerializableExtra("wyniki");

    }

    private void dodajPrzyciski() {
        LinearLayout layGlowny = (LinearLayout) findViewById(R.id.layPrzyciski);
        if(layGlowny.getChildCount()>0) layGlowny.removeAllViews();

        LinearLayout[] newLay = new LinearLayout[(int)Math.ceil(ILE_LEVELI/5f)];
        int licznikWierszy=-1;
        for (int i = 1; i <= ILE_LEVELI; i++) {
            if (i%5==1){
                licznikWierszy++;
                newLay[licznikWierszy]=new LinearLayout(context);
                newLay[licznikWierszy].setOrientation(LinearLayout.HORIZONTAL);
                newLay[licznikWierszy].setGravity(Gravity.CENTER_HORIZONTAL);
            }

            Button button = new Button(context);
            button.setText(String.valueOf(i));
            button.setTextSize(40);
            button.setTextColor(Color.BLACK);
            button.setBackground(podajTloPrzycisku(i));

            newLay[licznikWierszy].addView(button);
            listaPrzyciskow.add(button);
            ustawCzujke(i);

            ImageView gwiazdki=new ImageView(context);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(-90, 0, 50, 0);
            gwiazdki.setLayoutParams(lp);
            gwiazdki.setBackground(podajGwiazdki(i));
            newLay[licznikWierszy].addView(gwiazdki);


        }
        layGlowny.addView(newLay[0]);
        layGlowny.addView(newLay[1]);
        layGlowny.addView(newLay[2]);
        layGlowny.addView(newLay[3]);


        Button buttonExit = (Button) findViewById(R.id.btn_exit);
        buttonExit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                LevelsActivity.this.finish();
                return false;
            }
        });
    }

    private int wyliczLevelZaliczony(HashMap listaWynikow){
        if (listaWynikow.isEmpty()) return 0;
        return(int) Collections.max(listaWynikow.keySet())+1;
    }

    private Drawable podajTloPrzycisku(int poziom){
        if (poziom<=levelZaliczony) return getResources().getDrawable(R.drawable.button_ok);
        if (poziom==levelZaliczony+1) return getResources().getDrawable(R.drawable.button_play);
        else return getResources().getDrawable(R.drawable.button_off);
    }

    private void ustawCzujke(final int poziom) {
        if (poziom>levelZaliczony+1) return;
        listaPrzyciskow.get(poziom-1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Level.class);
                intent.putExtra("level", poziom);
                intent.putExtra("rozdzial", rozdzial);
                intent.putExtra("aktualnyWynik", listaWynikow.get(poziom-1));
                startActivityForResult(intent,1);
            }
        });
    }

    private Drawable podajGwiazdki(int aLevel) {
        try {
            int ile = listaWynikow.get(aLevel-1);
            switch (ile) {
                case 1:
                    return getResources().getDrawable(R.drawable.star1);
                case 2:
                    return getResources().getDrawable(R.drawable.star2);
                case 3:
                    return getResources().getDrawable(R.drawable.star3);
            }
        } catch (NullPointerException e) {
            return getResources().getDrawable(R.drawable.star0);
        }
        return getResources().getDrawable(R.drawable.star0);
    }


    @Override
    protected void onResume() {
        super.onResume();
        levelZaliczony=wyliczLevelZaliczony(listaWynikow);
        listaPrzyciskow=new ArrayList<>();
        dodajPrzyciski();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if(resultCode >0 ){
                listaWynikow=podajDAO().odczytajWynikiRozdzialu(rozdzial);
            }
        }
    }

    private DAO podajDAO(){
        if (dao==null){
            dao = new DAO(context);
        }
        return dao;
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }
}

