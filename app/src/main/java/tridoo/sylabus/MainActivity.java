package tridoo.sylabus;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.app.Activity;
import android.content.ClipData;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextSwitcher;
import android.widget.TextView;

import android.media.AudioManager;
import android.media.SoundPool;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.widget.ToggleButton;
import android.widget.ViewSwitcher;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends Activity implements ViewSwitcher.ViewFactory {
    boolean czyDemo = false;
    Map slownikMapa = new Hashtable();
    Map slownikMapaPRV = new Hashtable();

    List<Integer> tabPoprawnosci = new ArrayList<Integer>();
    RelativeLayout layZrodlo;
    RelativeLayout layCel;
    RelativeLayout layAdd;
    LinearLayout layPkt;
    LinearLayout layPkt2;
    AdView mAdView;
    ImageButton btnPodp;
    ImageView usmiech;
    ImageView smutek;
    TextView komunikat;
    TextView podpowiedz;
    TextView odpowiedz;
    TextView migawka;
    EditText editS1;
    EditText editS2;
    EditText editS3;
    EditText editS4;
    EditText editS5;
    Spinner spiner;
    ArrayAdapter<String> adapter;
    ImageButton btnRestart;
    ImageButton btnAdd;
    ImageButton btnCancel;
    ImageButton btnOK;
    ToggleButton btnPoziom3;
    ToggleButton btnPoziom4;
    ToggleButton btnPoziom5;

    SoundPool sounds;
    ArrayList tada = new ArrayList();
    ArrayList err = new ArrayList();
    String kategoria;
    String odpTXT = "";
    Boolean czyPoziom3 = true;
    Boolean czyPoziom4 = true;
    Boolean czyPoziom5 = true;
    String[] listaKategorii;
    String[] slowo;
    Context context;

    ImageView chmura1;
    ImageView chmura2;
    ImageView chmura3;
    ImageView chmura4;

    Animation animChmura1;
    Animation animChmura2;
    Animation animChmura3;
    Animation animChmura4;
    Animation animFadeIn;

    final int KAT_ZWIERZ = 1;
    final int KAT_ROSLINA = 2;
    final int KAT_OSOBA = 3;
    final int KAT_RZECZ = 4;

    int ILE_SYLAB = 0;
    int TEXT_SIZE = 0;
    int TEXT_WYSOKOSC_POLA = 0;
    int SYLABA_SZEROKOSC = 0;
    int MIGAWKA_SZEROKOSC = 0;
    int MIGAWKA_MARGINES_TOP = 0;
    int MARGINES_DOL = 0;
    int MIGAWKA_X = 0;
    int CHMURA1_TOP = 0;
    int CHMURA2_TOP = 0;
    int CHMURA3_TOP = 0;
    int CHMURA4_TOP = 0;
    int[] SYLABA_X = new int[5];

    int MIGAWKA_CZAS = 2500;
    int SYLABA_OFFSET = 1200;
    int SYLABA_ALFA = 1500;
    int SYLABA_RUCH = 1500;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();
        ustawWidocznoscStart();
        ustawPrzyciski();
        wczytajDzwieki();
        wczytajSlowniki();
        slowo = losujSlowo(slownikMapa);
        wyliczRozmiary();
        animacjaChmur();
        wypelnijLay(slowo);
        dodajAnimacje();
        if (czyDemo) pokazReklamy();
    }

    private void initComponents() {
        context = this;
        layZrodlo = (RelativeLayout) findViewById(R.id.layZrodlo);
        layCel = (RelativeLayout) findViewById(R.id.layCel);
        layPkt = (LinearLayout) findViewById(R.id.layPkt);
        layPkt2 = (LinearLayout) findViewById(R.id.layPkt2);
        layAdd = (RelativeLayout) findViewById(R.id.layAdd);

        chmura1 = (ImageView) findViewById(R.id.chmura1);
        chmura2 = (ImageView) findViewById(R.id.chmura2);
        chmura3 = (ImageView) findViewById(R.id.chmura3);
        chmura4 = (ImageView) findViewById(R.id.chmura4);

        animChmura1 = AnimationUtils.loadAnimation(this, R.anim.anim_chmura1);
        animChmura2 = AnimationUtils.loadAnimation(this, R.anim.anim_chmura2);
        animChmura3 = AnimationUtils.loadAnimation(this, R.anim.anim_chmura3);
        animChmura4 = AnimationUtils.loadAnimation(this, R.anim.anim_chmura4);
        animFadeIn = AnimationUtils.loadAnimation(this, R.anim.fadein);

        usmiech = (ImageView) findViewById(R.id.usmiech);
        smutek = (ImageView) findViewById(R.id.smutek);

        komunikat = (TextView) findViewById(R.id.komunikat);
        podpowiedz = (TextView) findViewById(R.id.podpowiedz);
        odpowiedz = (TextView) findViewById(R.id.odpowiedz);
        migawka = (TextView) findViewById(R.id.migawka);

        editS1 = (EditText) findViewById(R.id.sylaba1);
        editS2 = (EditText) findViewById(R.id.sylaba2);
        editS3 = (EditText) findViewById(R.id.sylaba3);
        editS4 = (EditText) findViewById(R.id.sylaba4);
        editS5 = (EditText) findViewById(R.id.sylaba5);
        spiner = (Spinner) findViewById(R.id.spKategoria);

        btnRestart = (ImageButton) findViewById(R.id.btnRestart);
        btnPodp = (ImageButton) findViewById(R.id.btnPodpowiedz);
        btnAdd = (ImageButton) findViewById(R.id.btnAdd);
        btnCancel = (ImageButton) findViewById(R.id.btnCacnel);
        btnOK = (ImageButton) findViewById(R.id.btnOk);
        btnPoziom3 = (ToggleButton) findViewById(R.id.btnPoziom3);
        btnPoziom4 = (ToggleButton) findViewById(R.id.btnPoziom4);
        btnPoziom5 = (ToggleButton) findViewById(R.id.btnPoziom5);

        listaKategorii = getResources().getStringArray(R.array.lista_kategorii);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listaKategorii);
        spiner.setAdapter(adapter);
    }

    private void wyliczRozmiary() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int procentW = size.x / 100;
        int procentH = size.y / 100;
        TEXT_SIZE = getResources().getInteger(R.integer.textSize) * procentH;
        TEXT_WYSOKOSC_POLA = getResources().getInteger(R.integer.wysokoscPola) * procentH;
        SYLABA_SZEROKOSC = getResources().getInteger(R.integer.sylabaSzerokosc) * procentW;
        MIGAWKA_SZEROKOSC = getResources().getInteger(R.integer.migawkaSzerokosc) * procentW;
        MIGAWKA_MARGINES_TOP = getResources().getInteger(R.integer.migawkaMarginesTop) * procentH;
        MARGINES_DOL = size.y - MIGAWKA_MARGINES_TOP - (int) (TEXT_WYSOKOSC_POLA * 1.5);
        CHMURA1_TOP = getResources().getInteger(R.integer.chmura1Top) * procentH;
        CHMURA2_TOP = getResources().getInteger(R.integer.chmura2Top) * procentH;
        CHMURA3_TOP = getResources().getInteger(R.integer.chmura3Top) * procentH;
        CHMURA4_TOP = getResources().getInteger(R.integer.chmura4Top) * procentH;

        int x = (size.x - ILE_SYLAB * SYLABA_SZEROKOSC) / (ILE_SYLAB + 1);
        for (int i = 0; i < 5; i++) {
            SYLABA_X[i] = x * (i + 1) + SYLABA_SZEROKOSC * i;
        }
        MIGAWKA_X = size.x / 2 - MIGAWKA_SZEROKOSC / 2;
    }

    private void ustawWidocznoscStart() {
        usmiech.setVisibility(View.INVISIBLE);
        smutek.setVisibility(View.INVISIBLE);
        komunikat.setVisibility(View.INVISIBLE);
        podpowiedz.setVisibility(View.INVISIBLE);
        odpowiedz.setVisibility(View.INVISIBLE);
        layAdd.setVisibility(View.INVISIBLE);
        if (czyDemo) btnAdd.setVisibility(View.INVISIBLE);
    }

    private void ustawPrzyciski() {
        btnRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nowaGra();
            }
        });

        btnPodp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                wyswietlPodpowiedz();
            }
        });

        btnPoziom3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (((ToggleButton) v).isChecked()) czyPoziom3 = true;
                else czyPoziom3 = false;
            }
        });

        btnPoziom4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (((ToggleButton) v).isChecked()) czyPoziom4 = true;
                else czyPoziom4 = false;
            }
        });

        btnPoziom5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (((ToggleButton) v).isChecked()) czyPoziom5 = true;
                else czyPoziom5 = false;
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                layAdd.setVisibility(View.VISIBLE);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                wyczyscFormatke();
                layAdd.setVisibility(View.INVISIBLE);
            }
        });

        btnOK.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String pNoweslowo = noweSlowo();
                int pNrKategorii = (int) spiner.getSelectedItemId();
                if (pNoweslowo.length() > 0 && pNrKategorii > 0) {
                    slownikMapaPRV.put(pNoweslowo, pNrKategorii);
                    slownikMapa.put(pNoweslowo, pNrKategorii);
                    wyczyscFormatke();
                    layAdd.setVisibility(View.INVISIBLE);
                    zapiszSlownikPRV();
                } else {
                    new AlertDialog.Builder(context)
                            .setTitle("BŁĄD")
                            .setMessage("Błędne dane")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }
        });
    }

    private void animacjaChmur() {
        chmura1.startAnimation(animChmura1);
        chmura2.startAnimation(animChmura2);
        chmura3.startAnimation(animChmura3);
        chmura4.startAnimation(animChmura4);

        animChmura1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation a) {
                a.setAnimationListener(null);
                a = AnimationUtils.loadAnimation(context, R.anim.anim_chmura1);
                a.setAnimationListener(this);

                chmura1.clearAnimation();
                chmura1.setAnimation(a);
                chmura1.startAnimation(a);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        animChmura2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation a) {
                a.setAnimationListener(null);
                a = AnimationUtils.loadAnimation(context, R.anim.anim_chmura2);
                a.setAnimationListener(this);

                chmura2.clearAnimation();
                chmura2.setAnimation(a);
                chmura2.startAnimation(a);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        animChmura3.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation a) {
                a.setAnimationListener(null);
                a = AnimationUtils.loadAnimation(context, R.anim.anim_chmura3);
                a.setAnimationListener(this);

                chmura3.clearAnimation();
                chmura3.setAnimation(a);
                chmura3.startAnimation(a);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        animChmura4.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation a) {
                a.setAnimationListener(null);
                a = AnimationUtils.loadAnimation(context, R.anim.anim_chmura4);
                a.setAnimationListener(this);

                chmura4.clearAnimation();
                chmura4.setAnimation(a);
                chmura4.startAnimation(a);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

    }

    private String noweSlowo() {
        int pIleSylab = 0;
        String pWyraz = "";

        String pSylaba1 = editS1.getText().toString();
        String pSylaba2 = editS2.getText().toString();
        String pSylaba3 = editS3.getText().toString();
        String pSylaba4 = editS4.getText().toString();
        String pSylaba5 = editS5.getText().toString();

        if (pSylaba1.length() > 0) pIleSylab++;
        if (pSylaba2.length() > 0) pIleSylab++;
        if (pSylaba3.length() > 0) pIleSylab++;
        if (pSylaba4.length() > 0) pIleSylab++;
        if (pSylaba5.length() > 0) pIleSylab++;

        if (pIleSylab < 3) return pWyraz;
        else {
            pWyraz = pSylaba1 + ',' + pSylaba2 + ',' + pSylaba3 + ',' + pSylaba4 + ',' + pSylaba5;
            pWyraz = pWyraz.replace(",,", ",");
            if (pWyraz.startsWith(",")) pWyraz = pWyraz.substring(1);
            if (pWyraz.endsWith(",")) pWyraz = pWyraz.substring(0, pWyraz.length() - 1);
            return pWyraz.trim().toUpperCase();
        }
    }

    private void wyczyscFormatke() {
        editS1.setText("");
        editS2.setText("");
        editS3.setText("");
        editS4.setText("");
        editS5.setText("");
        spiner.setSelection(0);
    }

    private void wczytajDzwieki() {
        sounds = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);

//        tada.add(sounds.load(this, R.raw.tada01, 1));
//        tada.add(sounds.load(this, R.raw.tada02, 1));
//        tada.add(sounds.load(this, R.raw.tada03, 1));
//        tada.add(sounds.load(this, R.raw.tada04, 1));
//        tada.add(sounds.load(this, R.raw.tada05, 1));
//        tada.add(sounds.load(this, R.raw.tada06, 1));
//        tada.add(sounds.load(this, R.raw.tada07, 1));
//        tada.add(sounds.load(this, R.raw.tada08, 1));
//        tada.add(sounds.load(this, R.raw.tada09, 1));
//        tada.add(sounds.load(this, R.raw.tada10, 1));
//        tada.add(sounds.load(this, R.raw.tada11, 1));
//        tada.add(sounds.load(this, R.raw.tada12, 1));
//        tada.add(sounds.load(this, R.raw.tada13, 1));
//
//        err.add(sounds.load(this, R.raw.err01, 1));
//        err.add(sounds.load(this, R.raw.err02, 1));
//        err.add(sounds.load(this, R.raw.err03, 1));
//        err.add(sounds.load(this, R.raw.err04, 1));
//        err.add(sounds.load(this, R.raw.err05, 1));
//        err.add(sounds.load(this, R.raw.err06, 1));
//        err.add(sounds.load(this, R.raw.err07, 1));
//        err.add(sounds.load(this, R.raw.err08, 1));
//        err.add(sounds.load(this, R.raw.err09, 1));
//        err.add(sounds.load(this, R.raw.err10, 1));
//        err.add(sounds.load(this, R.raw.err11, 1));
//        err.add(sounds.load(this, R.raw.err12, 1));
    }

    private void wczytajSlowniki() {
        wypelnijSlownikDemo();
        if (!czyDemo) {
            dodajSlowaPremium();
            dodajSlowaPRV();
        }
    }

    private void pokazReklamy() {
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void wypelnijLay(String[] aSlowo) {

        RelativeLayout.LayoutParams parametryMigawki = new RelativeLayout.LayoutParams(MIGAWKA_SZEROKOSC, TEXT_WYSOKOSC_POLA);
        RelativeLayout.LayoutParams rparams = new RelativeLayout.LayoutParams(SYLABA_SZEROKOSC, TEXT_WYSOKOSC_POLA);

        TextView[] tvCel = new TextView[ILE_SYLAB];
        TextSwitcher[] tvZrodlo = new TextSwitcher[ILE_SYLAB];

        int[] tabPozycji = new int[ILE_SYLAB];

        layZrodlo.setGravity(Gravity.START);
        layCel.setGravity(Gravity.START);

        migawka.setX(MIGAWKA_X);
        migawka.setY(MIGAWKA_MARGINES_TOP);
        migawka.setLayoutParams(parametryMigawki);
        migawka.setTextSize(TEXT_SIZE);
        migawka.setText(odpTXT.toUpperCase());

        for (int i = 0; i < ILE_SYLAB; i++) {
            tabPoprawnosci.add(0);
            tabPozycji[i] = i;
        }
        tabPozycji = RandomizeArray(tabPozycji);

        for (int i = 0; i < ILE_SYLAB; i++) {
            tvZrodlo[i] = new TextSwitcher(this);
            tvZrodlo[i].setFactory(this);
            tvZrodlo[i].setId(1000 + i);
            tvZrodlo[i].setBackgroundResource(R.drawable.rounded_corner);
            tvZrodlo[i].setCurrentText("");
            tvZrodlo[i].setContentDescription(aSlowo[i]);
            tvZrodlo[i].setOnTouchListener(new ChoiceTouchListener());
        }

        for (int i = 0; i < ILE_SYLAB; i++) {
            TextSwitcher ts = tvZrodlo[tabPozycji[i]];
            layZrodlo.addView(ts);
            ts.setLayoutParams(rparams);
            ts.setY(MIGAWKA_MARGINES_TOP);
            ts.setX(SYLABA_X[i]);
        }

///////// wypelnienie celu
        for (int i = 0; i < ILE_SYLAB; i++) {
            tvCel[i] = new TextView(this);
            tvCel[i].setLayoutParams(rparams);
            tvCel[i].setId(2000 + i);
            tvCel[i].setGravity(Gravity.CENTER);
            tvCel[i].setBackgroundResource(R.drawable.rounded_corner_dark);
            tvCel[i].setTextSize(TEXT_SIZE);
            tvCel[i].setContentDescription(aSlowo[i]);
            tvCel[i].setX(SYLABA_X[i]);
            tvCel[i].setY(MARGINES_DOL);
            layCel.addView(tvCel[i]);
            tvCel[i].setOnDragListener(new ChoiceDragListener());
            tvCel[i].setOnTouchListener(new ChoiceTouchListener2());
        }
    }

    private void dodajAnimacje() {

        float SYLABA_X_START = MIGAWKA_X + MIGAWKA_SZEROKOSC / 2 - SYLABA_SZEROKOSC / 2;

        ObjectAnimator fadeIn;
        ObjectAnimator[] objectAnimator = new ObjectAnimator[ILE_SYLAB];

        AnimatorSet[] animatorSet = new AnimatorSet[ILE_SYLAB];

        ObjectAnimator.ofFloat(migawka, "alpha", 0f, 1f, 0f)
                .setDuration(MIGAWKA_CZAS)
                .start();

        for (int i = 0; i < ILE_SYLAB; i++) {
            TextSwitcher ts = (TextSwitcher) layZrodlo.getChildAt(i);
            ts.setAlpha(0);

            fadeIn = ObjectAnimator.ofFloat(ts, "alpha", 0f, 1f);
            fadeIn.setDuration(SYLABA_ALFA);

            objectAnimator[i] = ObjectAnimator.ofFloat(ts, "x", SYLABA_X_START, SYLABA_X[i]);
            objectAnimator[i].setDuration(SYLABA_RUCH);

            animatorSet[i] = new AnimatorSet();
            animatorSet[i].playTogether(fadeIn, objectAnimator[i]);
            animatorSet[i].setStartDelay(SYLABA_OFFSET);
            animatorSet[i].start();
        }

        animatorSet[0].addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                pokazLitery();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    private String[] losujSlowo(Map aSlownikMapa) {
        boolean pCzySlowoDozwolone = false;
        String[] pSlowo;
        String randomKey;
        int aKategoria;

        do {
            do {
                Random random = new Random();
                List<String> keys = new ArrayList<String>(aSlownikMapa.keySet());
                randomKey = keys.get(random.nextInt(keys.size()));
                aKategoria = (int) aSlownikMapa.get(randomKey);
            } while (odpTXT.equals(randomKey.replace(",", "")));

            pSlowo = randomKey.toUpperCase().split(",");
            switch (pSlowo.length) {
                case 3:
                    if (czyPoziom3) pCzySlowoDozwolone = true;
                    else pCzySlowoDozwolone = false;
                    break;
                case 4:
                    if (czyPoziom4) pCzySlowoDozwolone = true;
                    else pCzySlowoDozwolone = false;
                    break;
                case 5:
                    if (czyPoziom5) pCzySlowoDozwolone = true;
                    else pCzySlowoDozwolone = false;
                    break;
            }
        } while (!pCzySlowoDozwolone);

        ILE_SYLAB = pSlowo.length;

        odpTXT = randomKey.replace(",", "");

        switch (aKategoria) {
            case KAT_ROSLINA:
                kategoria = "ROŚLINA";
                break;
            case KAT_ZWIERZ:
                kategoria = "ZWIERZĘ";
                break;
            case KAT_RZECZ:
                kategoria = "RZECZ";
                break;
            case KAT_OSOBA:
                kategoria = "OSOBA";
                break;
        }
        return pSlowo;
    }

    private void nowaGra() {
        if (czyPoziom3 || czyPoziom4 || czyPoziom5) {
            layZrodlo.removeAllViews();
            layCel.removeAllViews();
            usmiech.setVisibility(View.INVISIBLE);
            smutek.setVisibility(View.INVISIBLE);
            komunikat.setVisibility(View.INVISIBLE);
            podpowiedz.setVisibility(View.INVISIBLE);
            odpowiedz.setVisibility(View.INVISIBLE);
            tabPoprawnosci.removeAll(tabPoprawnosci);
            slowo = losujSlowo(slownikMapa);
            wyliczRozmiary();
            wypelnijLay(slowo);
            dodajAnimacje();
            btnPodp.setEnabled(true);
        }
    }

    private void zapiszSlownikPRV() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        JSONObject pSlownikJS = new JSONObject(slownikMapaPRV);
        editor.putString("json", pSlownikJS.toString());
        editor.apply();
        //editor.clear().commit();//czyszczenie
    }

    private void dodajSlowaPRV() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Map pMapa = new Hashtable();
        String jString = prefs.getString("json", null);

        if (jString == null || jString.isEmpty()) {
            return;
        }
        try {
            JSONObject pSlownikJS = new JSONObject(jString);
            Iterator keys = pSlownikJS.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                pMapa.put(key, pSlownikJS.get(key));
            }
            slownikMapaPRV.putAll(pMapa);
            slownikMapa.putAll(pMapa);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void wypelnijSlownikDemo() {
        slownikMapa.put("pa,pu,ga", KAT_ZWIERZ);
        slownikMapa.put("ja,gu,ar", KAT_ZWIERZ);
        slownikMapa.put("ko,zi,ca", KAT_ZWIERZ);
        slownikMapa.put("kro,ko,dyl", KAT_ZWIERZ);
        slownikMapa.put("pan,te,ra", KAT_ZWIERZ);
        slownikMapa.put("no,so,ro,żec", KAT_ZWIERZ);

        slownikMapa.put("to,po,la", KAT_ROSLINA);
        slownikMapa.put("cyt,ry,na", KAT_ROSLINA);
        slownikMapa.put("a,wo,ka,do", KAT_ROSLINA);
        slownikMapa.put("po,rzecz,ka", KAT_ROSLINA);
        slownikMapa.put("ja,go,da", KAT_ROSLINA);

        slownikMapa.put("in,for,ma,ty,ka", KAT_RZECZ);
        slownikMapa.put("ko,ro,na", KAT_RZECZ);
        slownikMapa.put("za,baw,ka", KAT_RZECZ);
        slownikMapa.put("he,li,kop,ter", KAT_RZECZ);

        slownikMapa.put("ko,le,ga", KAT_OSOBA);
        slownikMapa.put("ad,wo,kat", KAT_OSOBA);
        slownikMapa.put("ak,ro,ba,ta", KAT_OSOBA);
        slownikMapa.put("de,tek,tyw", KAT_OSOBA);
        slownikMapa.put("e,ko,no,mi,sta", KAT_OSOBA);
    }

    private void dodajSlowaPremium() {
        slownikMapa.put("ce,bu,la", KAT_ROSLINA);
        slownikMapa.put("cze,reś,nia", KAT_ROSLINA);
        slownikMapa.put("je,mio,ła", KAT_ROSLINA);
        slownikMapa.put("je,ży,na", KAT_ROSLINA);
        slownikMapa.put("ko,ni,czy,na", KAT_ROSLINA);
        slownikMapa.put("ma,li,na", KAT_ROSLINA);
        slownikMapa.put("po,ziom,ka", KAT_ROSLINA);
        slownikMapa.put("ru,mia,nek", KAT_ROSLINA);

        slownikMapa.put("me,du,za", KAT_ZWIERZ);
        slownikMapa.put("a,na,kon,da", KAT_ZWIERZ);
        slownikMapa.put("an,ty,lo,pa", KAT_ZWIERZ);
        slownikMapa.put("ga,ze,la", KAT_ZWIERZ);
        slownikMapa.put("ka,me,le,on", KAT_ZWIERZ);
        slownikMapa.put("le,ni,wiec", KAT_ZWIERZ);
        slownikMapa.put("ła,si,ca", KAT_ZWIERZ);
        slownikMapa.put("mrów,ko,jad", KAT_ZWIERZ);
        slownikMapa.put("o,ce,lot", KAT_ZWIERZ);
        slownikMapa.put("pan,cer,nik", KAT_ZWIERZ);
        slownikMapa.put("pe,li,kan", KAT_ZWIERZ);
        slownikMapa.put("pi,ra,nia", KAT_ZWIERZ);
        slownikMapa.put("re,ni,fer", KAT_ZWIERZ);
        slownikMapa.put("ro,pu,cha", KAT_ZWIERZ);
        slownikMapa.put("su,ry,kat,ka", KAT_ZWIERZ);
        slownikMapa.put("szyn,szy,la", KAT_ZWIERZ);
        slownikMapa.put("ta,ran,tu,la", KAT_ZWIERZ);
        slownikMapa.put("wie,wiór,ka", KAT_ZWIERZ);
        slownikMapa.put("ży,ra,fa", KAT_ZWIERZ);

        slownikMapa.put("a,ba,żur", KAT_RZECZ);
        slownikMapa.put("dra,bi,na", KAT_RZECZ);
        slownikMapa.put("dy,na,mit", KAT_RZECZ);
        slownikMapa.put("dłu,go,pis", KAT_RZECZ);
        slownikMapa.put("fla,mas,ter", KAT_RZECZ);
        slownikMapa.put("gaś,ni,ca", KAT_RZECZ);
        slownikMapa.put("kaj,zer,ka", KAT_RZECZ);
        slownikMapa.put("ka,lo,ry,fer", KAT_RZECZ);
        slownikMapa.put("ka,na,pa", KAT_RZECZ);
        slownikMapa.put("ka,ta,log", KAT_RZECZ);
        slownikMapa.put("kier,ow,ni,ca", KAT_RZECZ);
        slownikMapa.put("kla,wia,tu,ra", KAT_RZECZ);
        slownikMapa.put("na,leś,nik", KAT_RZECZ);
        slownikMapa.put("rę,ka,wicz,ka", KAT_RZECZ);
        slownikMapa.put("su,kien,ka", KAT_RZECZ);
        slownikMapa.put("ła,do,war,ka", KAT_RZECZ);
        slownikMapa.put("lo,ko,mo,ty,wa", KAT_RZECZ);
        slownikMapa.put("ma,ku,la,tu,ra", KAT_RZECZ);
        slownikMapa.put("mag,ne,to,fon", KAT_RZECZ);
        slownikMapa.put("ag,lo,me,rac,ja", KAT_RZECZ);
        slownikMapa.put("cza,so,pis,mo", KAT_RZECZ);
        slownikMapa.put("cze,ko,la,da", KAT_RZECZ);
        slownikMapa.put("kli,ma,ty,za,cja", KAT_RZECZ);

        slownikMapa.put("a,sys,tent", KAT_OSOBA);
        slownikMapa.put("cu,kier,nik", KAT_OSOBA);
        slownikMapa.put("e,lek,tryk", KAT_OSOBA);
        slownikMapa.put("fo,to,graf", KAT_OSOBA);
        slownikMapa.put("ko,mi,niarz", KAT_OSOBA);
        slownikMapa.put("kon,duk,tor", KAT_OSOBA);
        slownikMapa.put("la,kier,nik", KAT_OSOBA);
        slownikMapa.put("lis,to,nosz", KAT_OSOBA);
        slownikMapa.put("me,cha,nik", KAT_OSOBA);
        slownikMapa.put("og,rod,nik", KAT_OSOBA);
        slownikMapa.put("o,pe,ra,tor", KAT_OSOBA);
        slownikMapa.put("o,pie,kun,ka", KAT_OSOBA);
        slownikMapa.put("ra,tow,nik", KAT_OSOBA);
        slownikMapa.put("ta,pi,cer", KAT_OSOBA);
        slownikMapa.put("tak,sów,karz", KAT_OSOBA);
    }

    @Override
    public View makeView() {
        TextView t = new TextView(this);
        t.setTextSize(TEXT_SIZE);
        t.setLayoutParams(new FrameLayout.LayoutParams(SYLABA_SZEROKOSC, TEXT_WYSOKOSC_POLA));
        t.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        return t;
    }

    private final class ChoiceTouchListener implements OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                //setup drag
                ClipData data = ClipData.newPlainText("", "");
                DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                //start dragging the item touched
                view.startDrag(data, shadowBuilder, view, 0);
                return true;
            } else {
                return false;
            }
        }
    }

    private final class ChoiceTouchListener2 implements OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                if (view.getTag() != null) {
                    TextSwitcher tv = (TextSwitcher) findViewById((int) view.getTag());
                    tv.setVisibility(View.VISIBLE);

                    ((TextView) view).setText("");
                    view.setTag(null);
                    view.setBackgroundResource(R.drawable.rounded_corner_dark);
                    tabPoprawnosci.set(view.getId() - 2000, 0);
                }
                return true;
            } else {
                return false;
            }
        }
    }

    private class ChoiceDragListener implements OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            //handle drag events
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    //no action necessary
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    //no action necessary
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    //no action necessary
                    break;
                case DragEvent.ACTION_DROP:
                    //handle the dragged view being dropped over a drop view
                    //handle the dragged view being dropped over a target view
                    View view = (View) event.getLocalState();
                    //stop displaying the view where it was before it was dragged
                    view.setVisibility(View.INVISIBLE);
                    //view dragged item is being dropped on
                    TextView dropTarget = (TextView) v;
                    //view being dragged and dropped
                    TextSwitcher dropped = (TextSwitcher) view;
                    //update the text in the target view to reflect the data being dropped
                    TextView textView = (TextView) dropped.getCurrentView();

                    //make it bold to highlight the fact that an item has been dropped
                    //dropTarget.setTypeface(Typeface.DEFAULT_BOLD);
                    //if an item has already been dropped here, there will be a tag
                    Object tag = dropTarget.getTag();
                    //if there is already an item here, set it back visible in its original place
                    if (tag != null) {
                        //the tag is the view id already dropped here
                        int existingID = (Integer) tag;
                        //set the original view visible again
                        findViewById(existingID).setVisibility(View.VISIBLE);
                    }

                    dropTarget.setText(textView.getText());
                    //set the tag in the target view to the ID of the view being dropped
                    dropTarget.setTag(dropped.getId());
                    //dropTarget.setBackgroundResource(R.drawable.rounded_corner);

                    //if (dropTarget.getId()==(int)dropTarget.getTag()+1000){
                    String sylabaZrodlo = textView.getText().toString();
                    String sylabaCel = dropTarget.getContentDescription().toString();
                    if (sylabaZrodlo.equals(sylabaCel)) {
                        tabPoprawnosci.set(dropTarget.getId() - 2000, 1);
                    } else {
                        tabPoprawnosci.set(dropTarget.getId() - 2000, 0);
                    }

                    if (czyWszystkoSchowane((RelativeLayout) findViewById(R.id.layZrodlo))) {
                        if (tabPoprawnosci.contains(0)) {
                            przegrana();
                        } else {
                            wygrana();
                        }
                    }

                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    //no action necessary
                    break;
                default:
                    break;
            }
            return true;
        }
    }

    private boolean czyWszystkoSchowane(RelativeLayout aLay) {
        boolean czySchowane = true;

        for (int i = 0; i < aLay.getChildCount(); i++) {
            View child = aLay.getChildAt(i);
            int vv = child.getVisibility();
            if (vv == View.VISIBLE) {
                czySchowane = false;
                break;
            }
        }
        return czySchowane;
    }

    private void wygrana() {
        Random rand = new Random();
        int x = rand.nextInt(tada.size());
        sounds.play((int) tada.get(x), 1f, 1f, 0, 0, 1.5f); //dzwiek sukcesu

        TextView tv = (TextView) findViewById(R.id.komunikat);
        tv.setTextSize(TEXT_SIZE);
        tv.setText("BRAWO");
        komunikat.setVisibility(View.VISIBLE);
        usmiech.setVisibility(View.VISIBLE);
        podpowiedz.setVisibility(View.INVISIBLE);
        btnPodp.setEnabled(false);

        ImageView kwiatek = new ImageView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (layPkt.getChildCount() > 13) {
            params.setMargins(0, 0, 0, 300);
            params.gravity = Gravity.TOP;
            kwiatek.setImageResource(R.drawable.flower2);
            kwiatek.setLayoutParams(params);
            layPkt2.addView(kwiatek);
        } else {
            kwiatek.setImageResource(R.drawable.flower);
            layPkt.addView(kwiatek);
        }
    }

    private void przegrana() {
        Random rand = new Random();
        int x = rand.nextInt(err.size());
        sounds.play((int) err.get(x), 1f, 1f, 0, 0, 1.5f); //dzwiek przegranej

        TextView tv = (TextView) findViewById(R.id.komunikat);
        tv.setTextSize(TEXT_SIZE);
        tv.setText("SPRÓBUJ PONOWNIE");
        komunikat.setVisibility(View.VISIBLE);
        smutek.setVisibility(View.VISIBLE);
        podpowiedz.setVisibility(View.INVISIBLE);
        wyswietlOpowiedz();
        btnPodp.setEnabled(false);
        for (int i = 0; i < layCel.getChildCount(); i++) {
            layCel.getChildAt(i).setOnTouchListener(null);
        }
        layPkt.removeAllViewsInLayout();
        layPkt2.removeAllViewsInLayout();
    }

    private void wyswietlOpowiedz() {
        odpowiedz.setText(odpTXT.toUpperCase());
        odpowiedz.setTextSize(TEXT_SIZE);
        odpowiedz.setVisibility(View.VISIBLE);
    }

    private void wyswietlPodpowiedz() {
        podpowiedz.setText(kategoria.toUpperCase());
        podpowiedz.setVisibility(View.VISIBLE);
    }

    private void pokazLitery() {
        animFadeIn.setStartOffset(0);
        for (int i = 0; i < ILE_SYLAB; i++) {
            layZrodlo.getChildAt(i).clearAnimation();
            TextSwitcher ts = (TextSwitcher) layZrodlo.getChildAt(i);
            ts.setInAnimation(animFadeIn);
            ts.setText(ts.getContentDescription());
        }
    }

    private static int[] RandomizeArray(int[] array) {
        do {
            Random rgen = new Random();  // Random number generator
            for (int i = 0; i < array.length; i++) {
                int randomPosition = rgen.nextInt(array.length);
                int temp = array[i];
                array[i] = array[randomPosition];
                array[randomPosition] = temp;
            }
        } while (array[0] == 0 && array[1] == 1 && array[2] == 2);
        return array;
    }

    @Override
    public void onResume() {
        super.onResume();
        animacjaChmur();
    }
}