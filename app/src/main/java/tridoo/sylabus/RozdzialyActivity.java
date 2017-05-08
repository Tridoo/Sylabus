package tridoo.sylabus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class RozdzialyActivity  extends Activity {
    static int ILE_LEVELI=20;
    Context context;
    DAO dao;
    HashMap<Integer,Integer> wynikiR1;
    HashMap<Integer,Integer> wynikiR2;
    HashMap<Integer,Integer> wynikiR3;
    HashMap<Integer,Integer> wynikiR4;
    HashMap<Integer,Integer> wynikiR5;
    List<Integer> listaIdPrzyciskow;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rozdzialy);
        context = getApplicationContext();
        dao = new DAO(context);
        listaIdPrzyciskow=wczytajID();
        wczytajWyniki();
        ustawPrzyciski();
        ustawWyjscie();
    }

    private List<Integer> wczytajID(){
        List<Integer> listaPrzyciskow= new ArrayList<>();
        listaPrzyciskow.add(R.id.iv_r1);
        listaPrzyciskow.add(R.id.iv_r2);
        listaPrzyciskow.add(R.id.iv_r3);
        listaPrzyciskow.add(R.id.iv_r4);
        listaPrzyciskow.add(R.id.iv_r5);
        return listaPrzyciskow;
    }

    private  void wczytajWyniki(){
        List<HashMap<Integer,Integer>> listaWynikow=dao.odczytajWyniki();
        wynikiR1=listaWynikow.get(0);
        wynikiR2=listaWynikow.get(1);
        wynikiR3=listaWynikow.get(2);
        wynikiR4=listaWynikow.get(3);
        wynikiR5=listaWynikow.get(4);
    }

    private void ustawPrzyciski(){
        for (Integer pID: listaIdPrzyciskow){
            ImageView pIV=(ImageView) findViewById(pID);
            if (czyRozdzialDostepny(pIV)) {
                dodajMedal(pIV);
                dodajClickListenera(pIV);
            } else{
                dodajKlodke(pIV);
            }
        }
    }

    private boolean czyRozdzialDostepny(ImageView iv){
        if (iv.getId()== listaIdPrzyciskow.get(0)) return true;
        if (iv.getId()== listaIdPrzyciskow.get(1) && wynikiR1.size()==ILE_LEVELI) return true;
        if (iv.getId()== listaIdPrzyciskow.get(2) && wynikiR2.size()==ILE_LEVELI) return true;
        if (iv.getId()== listaIdPrzyciskow.get(3) && wynikiR4.size()==ILE_LEVELI) return true;
        if (iv.getId()== listaIdPrzyciskow.get(4) && wynikiR4.size()==ILE_LEVELI) return true;
        return false;
    }

    private void dodajMedal(ImageView iv){
        int nr=jakiMedal(iv);
        if (nr==0) return;
        ImageView medal=new ImageView(this);
        int idObrazka=0;
        switch (nr){
            case 1:
                idObrazka=R.drawable.medal_1;
                break;
            case 2:
                idObrazka=R.drawable.medal_2;
                break;
            case 3:
                idObrazka=R.drawable.medal_3;
                break;
        }

        RelativeLayout lay=(RelativeLayout)findViewById(R.id.lay_rozdzialy);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ABOVE, iv.getId());
        params.addRule(RelativeLayout.ALIGN_LEFT, iv.getId());

        medal.setImageResource(idObrazka);
        medal.setLayoutParams(params);
        lay.addView(medal);
    }

    private void dodajKlodke(ImageView iv){
        ImageView klodka=new ImageView(this);
        klodka.setImageResource(R.drawable.lock);

        RelativeLayout lay=(RelativeLayout)findViewById(R.id.lay_rozdzialy);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_LEFT, iv.getId());
        params.addRule(RelativeLayout.CENTER_VERTICAL, iv.getId());
        params.setMarginStart(50);

        klodka.setLayoutParams(params);
        lay.addView(klodka);

    }

    private void dodajClickListenera(ImageView iv){
        int rozdzial=1;
        HashMap<Integer,Integer> pWyniki=new HashMap<>();
        if (iv.getId()== listaIdPrzyciskow.get(0)) {
            pWyniki = wynikiR1;
            rozdzial=1;
        }
        if (iv.getId()== listaIdPrzyciskow.get(1)) {
            pWyniki = wynikiR2;
            rozdzial=2;
        }
        if (iv.getId()== listaIdPrzyciskow.get(2)) {
            pWyniki = wynikiR3;
            rozdzial=3;
        }
        if (iv.getId()== listaIdPrzyciskow.get(3)) {
            pWyniki = wynikiR4;
            rozdzial=4;
        }
        if (iv.getId()== listaIdPrzyciskow.get(4)) {
            pWyniki = wynikiR5;
            rozdzial=5;
        }



        final HashMap<Integer, Integer> finalPWyniki = pWyniki;
        final int finalRozdzial = rozdzial;

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),LevelsActivity.class);
                intent.putExtra("wyniki", finalPWyniki);
                intent.putExtra("rozdzial", finalRozdzial);
                startActivity(intent);
            }
        });

    }

    private int jakiMedal(ImageView iv){
        if (iv.getId() ==listaIdPrzyciskow.get(0)) {
            if (wynikiR1.size() == ILE_LEVELI) return wyliczMedal(wynikiR1);
        } else if (iv.getId() == listaIdPrzyciskow.get(1)) {
            if (wynikiR2.size() == ILE_LEVELI) return wyliczMedal(wynikiR2);
        } else if (iv.getId() == listaIdPrzyciskow.get(2)) {
            if (wynikiR3.size() == ILE_LEVELI) return wyliczMedal(wynikiR3);
        } else if (iv.getId() == listaIdPrzyciskow.get(3)) {
            if (wynikiR4.size() == ILE_LEVELI) return wyliczMedal(wynikiR4);
        } else if (iv.getId() == listaIdPrzyciskow.get(4)) {
            if (wynikiR5.size() == ILE_LEVELI) return wyliczMedal(wynikiR5);
        }
        return 0;
    }

    private int wyliczMedal(HashMap<Integer,Integer> listaWynikow){
        int sumaPKT=0;
        for (Integer pkt : listaWynikow.values()) {
            sumaPKT += pkt;
        }
        if (sumaPKT<33) return 1;
        if (sumaPKT<46) return 2;
        return 3;
    }

    private void ustawWyjscie(){
        ImageView btn=(ImageView) findViewById(R.id.iv_exit);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
