package tridoo.sylabus;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.SoundPool;
import android.os.Bundle;
import android.renderscript.RenderScript;
import android.speech.tts.TextToSpeech;
import android.view.Display;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Level extends Activity implements ViewSwitcher.ViewFactory {
    List<String> wyrazy;
    Slowniki slowniki;
    String wyraz;

    int procentW;
    int procentH;
    int sylabaStartX;
    int ileBledow;
    int level;
    int rozdzial;
    int zapisanyWynik=0;
    int rezultat=0;

    Context context;
    List<Chmura> listaChmur;
    List<TextSwitcher> listaKomorekZrodel;
    List<TextView> listaKomorekCeli;
    List<Integer> listaPozycjiSylab;
    List<ImageView> listaKwiatkow;
    private TextToSpeech tts;
    DAO dao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);
        context = getApplicationContext();
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        procentW = (int) (size.x * 0.01);
        procentH = (int) (size.y * 0.01);

        level = getIntent().getIntExtra("level", 0);
        rozdzial = getIntent().getIntExtra("rozdzial", 0);
        zapisanyWynik = getIntent().getIntExtra("aktualnyWynik", 0);

        slowniki=new Slowniki();
        wyrazy=  slowniki.podajListeSlow(context, level, rozdzial);
        ustawChmury();
        ustawKwiatki();
        generujWyjscie();

        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                tts.setLanguage(Locale.getDefault());
                if (status==TextToSpeech.SUCCESS) nowaGra();
            }
        });
    }

    private void nowaGra(){
        if (ileBledow == 3) koniecGry(false);
        else if (wyrazy.isEmpty()) {
            koniecGry(true);
        } else {
            listaKomorekZrodel = new ArrayList<>();
            listaKomorekCeli = new ArrayList<>();
            Collections.shuffle(wyrazy);
            wyraz = wyrazy.get(0);
            RelativeLayout laySylaby = (RelativeLayout) findViewById(R.id.laySylaby);
            laySylaby.removeAllViews();
            wypelnijLay(wyraz);
        }
    }

    public void ustawChmury() {
        generujChmury();
        startAnimacjiChmur();
    }

    public void wypelnijLay(String slowo) {
        List<String> sylaby = podajSylaby(slowo);
        int ileSylab = sylaby.size();
        generujMigawke(slowo);
        generujKomorkiSylab(slowo);
        listaPozycjiSylab = generujPozycjeSylab(ileSylab);
        generujAnimacjeSylab(ileSylab);
        dodajCzujkiSylab();
        dodajGlosnik();
    }


    public void przeczytaj(final String wyraz) {
        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(2000);
                    if (!tts.isSpeaking()) {
                        tts.speak(wyraz, TextToSpeech.QUEUE_FLUSH, null, null);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        timerThread.start();
    }

    private void ustawKwiatki(){
        final RelativeLayout layKwiatki = (RelativeLayout) findViewById(R.id.layKwiatki);
        listaKwiatkow = new ArrayList<>();

        for (int i=0;i<3;i++) {
            ImageView kwiatek = new ImageView(context);

            kwiatek.setImageResource(R.drawable.flower);
            kwiatek.setY(85 * procentH);
            kwiatek.setX((33+20 * i) * procentW);

            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            kwiatek.setLayoutParams(lp);
            layKwiatki.addView(kwiatek);
            listaKwiatkow.add(kwiatek);
        }

    }

    private void generujChmury() {
        TypedArray listaAnimacji = podajListeIDAnimacjiChmur();
        TypedArray listaBitmap = podajListeBitmapChmur();
        int[] marginesy = getResources().getIntArray(R.array.chmuraMargines);
        final RelativeLayout layChmury = (RelativeLayout) findViewById(R.id.layAnimacjeChmur);

        listaChmur = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            ImageView iv = new ImageView(context);

            iv.setImageResource(listaBitmap.getResourceId(i, -1));
            iv.setY(marginesy[i] * procentH);
            iv.setX(0);

            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(10 * procentW, 10 * procentH);
            iv.setLayoutParams(lp);
            layChmury.addView(iv);

            Chmura chmura = new Chmura(context);
            chmura.setBitmapa(iv);
            chmura.setAnimacja(AnimationUtils.loadAnimation(this, listaAnimacji.getResourceId(i, -1)));
            chmura.setIdAnimacji(listaAnimacji.getResourceId(i, -1));
            listaChmur.add(chmura);
        }
        listaBitmap.recycle();
        listaAnimacji.recycle();
    }

    private void startAnimacjiChmur() {
        for (Chmura chmura : listaChmur) {
            chmura.startAnimacji();
        }
    }

    private TypedArray podajListeIDAnimacjiChmur() {
        return getResources().obtainTypedArray(R.array.chmuraAnim);
    }

    private TypedArray podajListeBitmapChmur() {
        return getResources().obtainTypedArray(R.array.chmuraImg);
    }

    private List<String> podajSylaby(String slowo) {
        return Arrays.asList(slowo.toUpperCase().split(","));
    }

    private void generujKomorkiSylab(String slowo) {
        int MIGAWKA_SZEROKOSC = getResources().getInteger(R.integer.migawkaSzerokosc) * procentW;
        int MIGAWKA_X = procentW * 50 - MIGAWKA_SZEROKOSC / 2;
        sylabaStartX = MIGAWKA_X + MIGAWKA_SZEROKOSC / 2 - getResources().getInteger(R.integer.sylabaSzerokosc) / 2;

        String sylaby[] = slowo.split(",");
        final RelativeLayout laySylaby = (RelativeLayout) findViewById(R.id.laySylaby);
        for (int i = 0; i < sylaby.length; i++) {
            RelativeLayout.LayoutParams parametrySylab = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            TextSwitcher zrodlo = new TextSwitcher(context);
            zrodlo.setFactory(this);
            zrodlo.setId(1000 + i);
            zrodlo.setBackgroundResource(R.drawable.rounded_corner);
            zrodlo.setCurrentText("");
            zrodlo.setContentDescription(sylaby[i]);
            zrodlo.setY(getResources().getInteger(R.integer.migawkaMarginesTop) * procentH);
            zrodlo.setX(sylabaStartX);
            zrodlo.setLayoutParams(parametrySylab);
            zrodlo.setAlpha(0);
            laySylaby.addView(zrodlo);
            listaKomorekZrodel.add(zrodlo);

            TextView cel = new TextView(context);
            cel.setLayoutParams(parametrySylab);
            cel.setId(2000 + i);
            cel.setGravity(Gravity.CENTER);
            cel.setBackgroundResource(R.drawable.rounded_corner_dark);
            cel.setTextSize(getResources().getInteger(R.integer.textSize) * procentH);
            //Typeface font = Typeface.createFromAsset(getAssets(), "fonts/mvboli.ttf"); //todo
            //cel.setTypeface(font);
            cel.setContentDescription(sylaby[i]);
            cel.setX(sylabaStartX);
            cel.setY(getResources().getInteger(R.integer.migawkaMarginesTop) * procentH);
            cel.setAlpha(0);
            laySylaby.addView(cel);
            listaKomorekCeli.add(cel);
        }
    }

    private List<Integer> generujPozycjeSylab(int ile) {
        List<Integer> listaPozycji = new ArrayList<>();
        int szerokoscSylaby = getResources().getInteger(R.integer.sylabaSzerokosc);
        int odstep = 100 / ile - szerokoscSylaby;

        for (int i = 0; i < ile ; i++) {
            listaPozycji.add((int) (procentW * (odstep * 0.5f + i * (szerokoscSylaby + odstep) + szerokoscSylaby * 0.4f)));
        }
        return listaPozycji;
    }

    private void generujAnimacjeSylab(int ile) {
        int czas = getResources().getInteger(R.integer.czasFadeIn);

        List<Integer> listaLosowa = wymieszaj(listaPozycjiSylab);

        for (int i = 0; i < ile; i++) {
            //zrodlo
            ObjectAnimator fadeIn;
            ObjectAnimator objectAnimator;
            AnimatorSet animatorSet = new AnimatorSet();

            TextSwitcher ts = listaKomorekZrodel.get(i);

            fadeIn = ObjectAnimator.ofFloat(ts, "alpha", 0f, 1f);
            fadeIn.setDuration(czas);

            objectAnimator = ObjectAnimator.ofFloat(ts, "x", sylabaStartX, listaLosowa.get(i));
            objectAnimator.setDuration(czas * 2);

            animatorSet.playTogether(fadeIn, objectAnimator);
            animatorSet.setStartDelay(czas * 3);
            animatorSet.start();
            if (i == 0) animatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    pokazLitery();
                }

                @Override
                public void onAnimationCancel(Animator animation) {                }

                @Override
                public void onAnimationRepeat(Animator animation) {                }
            });


            //cel
            float yStart = getResources().getInteger(R.integer.migawkaMarginesTop) * procentH;
            float yKoniec = (100 - getResources().getInteger(R.integer.migawkaMarginesTop) - getResources().getInteger(R.integer.wysokoscPola)) * procentH;

            ObjectAnimator fadeIn2;
            ObjectAnimator objectAnimator2, objectAnimator3;
            AnimatorSet animatorSet2 = new AnimatorSet();
            TextView tv = listaKomorekCeli.get(i);

            fadeIn2 = ObjectAnimator.ofFloat(tv, "alpha", 0f, 1f);
            fadeIn2.setDuration(czas);

            objectAnimator2 = ObjectAnimator.ofFloat(tv, "x", sylabaStartX, listaPozycjiSylab.get(i));
            objectAnimator2.setDuration(czas * 2);
            objectAnimator3 = ObjectAnimator.ofFloat(tv, "y", yStart, yKoniec);
            objectAnimator3.setDuration(czas * 2);

            animatorSet2.playTogether(fadeIn2, objectAnimator2, objectAnimator3);
            animatorSet2.setStartDelay(czas * 3);
            animatorSet2.start();
        }
    }

    private void pokazLitery() {
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.fadein);
        anim.setStartOffset(0);
        for (TextSwitcher ts : listaKomorekZrodel) {
            ts.setInAnimation(anim);
            ts.setText(ts.getContentDescription());
        }
    }


    private void generujMigawke(final String slowo) {
        RelativeLayout.LayoutParams parametryMigawki = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        parametryMigawki.addRule(RelativeLayout.CENTER_HORIZONTAL);
        final TextView pMigawka = new TextView(context);
        pMigawka.setY(getResources().getInteger(R.integer.migawkaMarginesTop) * procentH);
        pMigawka.setHeight(getResources().getInteger(R.integer.wysokoscPola) * procentH);
        pMigawka.setLayoutParams(parametryMigawki);
        pMigawka.setTextSize(getResources().getInteger(R.integer.textSize) * procentH);
        pMigawka.setText(slowo.toUpperCase().replace(",", ""));
        pMigawka.setTextColor(Color.BLACK);
        pMigawka.setBackground(getResources().getDrawable(R.drawable.rounded_corner));
        pMigawka.setPadding(3 * procentW, 0, 3 * procentW, 0);
        pMigawka.setGravity(Gravity.CENTER);

        Animation animMigawka = AnimationUtils.loadAnimation(context, R.anim.anim_migawka);
        animMigawka.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                przeczytaj(slowo.replace(",", ""));
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                pMigawka.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {            }
        });
        pMigawka.startAnimation(animMigawka);
        RelativeLayout migawkaLayout = (RelativeLayout) findViewById(R.id.layMigawka);
        migawkaLayout.addView(pMigawka);
    }


    private void dodajCzujkiSylab() {
        for (TextSwitcher ts : listaKomorekZrodel) {
            ts.setOnTouchListener(new ChoiceTouchListenerZrodlo());
        }

        for (TextView tv : listaKomorekCeli) {
            tv.setOnTouchListener(new ChoiceTouchListenerCel());
            tv.setOnDragListener(new ChoiceDragListener());
        }
    }

    private void dodajGlosnik(){
        ImageView glosnik=(ImageView) findViewById(R.id.iv_glosnik);
        glosnik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                przeczytaj(wyraz.replace(",", ""));
            }
        });
    }

    private List<Integer> wymieszaj(List<Integer> lista) {
        List<Integer> listaLosowa = new ArrayList<>(lista);
        do {
            long seed = System.nanoTime();
            Collections.shuffle(listaLosowa, new Random(seed));
        } while (listaLosowa.equals(listaPozycjiSylab));
        return listaLosowa;
    }

    private boolean czyWszystkieSchowane(){
        for (TextSwitcher ts : listaKomorekZrodel) {
            if (ts.getVisibility()==View.VISIBLE) return false;
        }
        return true;
    }

    private List<TextView> listaZlychSylab(){
        List<TextView> lista=new ArrayList<>();
        for (TextView tv:listaKomorekCeli){
            if (!tv.getText().equals(tv.getContentDescription())) {
                lista.add(tv);
            }
        }
        return lista;
    }

    private void wylaczDotykSylab(){
        for (TextView tv : listaKomorekCeli) {
            tv.setOnTouchListener(null);
            tv.setOnDragListener(null);
        }
    }

    private void blad(List<TextView> textViews){
        ustawSzaryKwiatek();
        ileBledow++;

        int ileSylab=textViews.size();
//        ObjectAnimator animaGora;
//        ObjectAnimator animaDol;
//        ObjectAnimator pause;
//        ObjectAnimator[] animaPrzesuniecie= new ObjectAnimator[ileSylab];
        //AnimatorSet[] animatorSet = new AnimatorSet[ileSylab];

        RelativeLayout lay = (RelativeLayout) findViewById(R.id.layMigawka);
        final ImageView iv = new ImageView(context);
        iv.setImageResource(R.drawable.sad);
        iv.setY(getResources().getInteger(R.integer.migawkaMarginesTop) * procentH);
        iv.setX(45 * procentW);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(20 * procentW, 20 * procentH);
        iv.setLayoutParams(lp);
        Animation animMigawka = AnimationUtils.loadAnimation(context, R.anim.anim_migawka);
        animMigawka.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {            }

            @Override
            public void onAnimationEnd(Animation animation) {
                iv.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {            }
        });
        iv.setAnimation(animMigawka);
        iv.startAnimation(animMigawka);
        lay.addView(iv);


        for (int i=0; i< ileSylab;i++){//TextView tv: textViews){
            TextView tv= textViews.get(i);
            tv.setBackground(getResources().getDrawable(R.drawable.rounded_corner_red));
//            animaGora= ObjectAnimator.ofFloat(tv, "y", tv.getY() - 15 * procentH);
//            animaGora.setDuration(500);
//
//            animaDol= ObjectAnimator.ofFloat(tv, "y", tv.getY());
//            animaDol.setDuration(1000);

//            pause=ObjectAnimator.ofInt(tv,"alpha",255);
//            pause.setDuration(1500);

            int pozX=wlasciwaPozycjaSylaby(tv);
//            animaPrzesuniecie[i] = ObjectAnimator.ofFloat(tv, "x", pozX);
//            animaPrzesuniecie[i].setStartDelay(800*i);
//            animaPrzesuniecie[i].setDuration(1000);
//
//            animatorSet[i]= new AnimatorSet();
//            animatorSet[i].play(animaPrzesuniecie[i]).after(animaGora);
//            animatorSet[i].play(animaPrzesuniecie[i]).with(animaDol);
//            animatorSet[i].play(animaPrzesuniecie[i]).before(pause);
            //animatorSet[i].start();

            final Path path = new Path();
            path.moveTo(tv.getX(),tv.getY());
            path.quadTo(tv.getX(), tv.getY()- 40 * procentH, pozX, tv.getY());
            ObjectAnimator animacja=ObjectAnimator.ofFloat(tv, View.X, View.Y, path);
            animacja.setStartDelay(1000*i);
            animacja.setDuration(3000).start();
        }
        ObjectAnimator animackaKoncowa = ObjectAnimator.ofArgb(iv,"alpha",255);
        animackaKoncowa.setDuration(1);
        animackaKoncowa.setStartDelay(ileSylab*1000+3000);
        animackaKoncowa.start();

        animackaKoncowa.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                schowajSylaby();
                pokazOdpowiedz();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    private int wlasciwaPozycjaSylaby(TextView textView){
        int x= (int) textView.getTag()-1000;
        return listaPozycjiSylab.get(x);
    }

    private void wygrana(){
        wyrazy.remove(wyraz);


            RelativeLayout lay = (RelativeLayout) findViewById(R.id.layMigawka);
            final ImageView iv = new ImageView(context);
            iv.setImageResource(R.drawable.smile);
            iv.setY(getResources().getInteger(R.integer.migawkaMarginesTop) * procentH);
            iv.setX(45 * procentW);

            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(20 * procentW, 20 * procentH);
            iv.setLayoutParams(lp);
            Animation animMigawka = AnimationUtils.loadAnimation(context, R.anim.anim_migawka);
            animMigawka.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {            }

                @Override
                public void onAnimationEnd(Animation animation) {
                    iv.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {            }
            });
            iv.setAnimation(animMigawka);
            iv.startAnimation(animMigawka);
            lay.addView(iv);


            int ileSylab=listaKomorekCeli.size();
            AnimatorSet[] animatorSet = new AnimatorSet[ileSylab];
            ObjectAnimator[] animaPrzesuniecie= new ObjectAnimator[ileSylab];
            ObjectAnimator fadeOut;

            for (int i=0; i<ileSylab;i++){
                TextView tv=listaKomorekCeli.get(i);
                fadeOut= ObjectAnimator.ofFloat(tv, "alpha", 0);
                fadeOut.setDuration(2000);

                animaPrzesuniecie[i] = ObjectAnimator.ofFloat(tv, "x", tv.getX()/2+25*procentW);//todo poprawic
                animaPrzesuniecie[i].setDuration(2000);

                animatorSet[i]= new AnimatorSet();
                animatorSet[i].playSequentially(animaPrzesuniecie[i],fadeOut);
                animatorSet[i].start();

                if (i==0){
                    animatorSet[i].addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {               }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (wyrazy.isEmpty()) koniecGry(true);
                            else nowaGra();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {                        }
                    });
                }
            }


    }

    private void koniecGry(boolean wygrana){
        int ilePkt=3-ileBledow;
        if (wygrana && zapisanyWynik<ilePkt) {
            podajDAO().zapiszZaliczonyPoziom(rozdzial,level,ilePkt );
            rezultat=ilePkt;
        }

        RelativeLayout lay = (RelativeLayout) findViewById(R.id.layMigawka);
        ImageView iv = new ImageView(context);
        iv.setImageResource(wygrana ? R.drawable.smile : R.drawable.sad);
        int pID=99;
        iv.setId(pID);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        //lp.setMargins(0,-50*procentH,0,2*procentH);
        iv.setLayoutParams(lp);
        Animation animacjaFadeIn = AnimationUtils.loadAnimation(context, R.anim.fadein);
        iv.setAnimation(animacjaFadeIn);
        iv.startAnimation(animacjaFadeIn);
        lay.addView(iv);

        ImageView przyciskWyjscia=generujPrzyciskWyjscia();
        przyciskWyjscia.setAnimation(animacjaFadeIn);
        przyciskWyjscia.startAnimation(animacjaFadeIn);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, iv.getId());
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);

        String pKomunikat="";
        pKomunikat = wygrana ? " BRAWO " : " SPRÓBUJ PONOWNIE ";
        TextView txt=new TextView(context);
        txt.setText(pKomunikat);
        txt.setTextColor(Color.BLACK);
        txt.setTextSize(getResources().getInteger(R.integer.textSize) * procentH);
        txt.setGravity(Gravity.CENTER);
        txt.setBackground(getResources().getDrawable(R.drawable.rounded_corner));
        txt.setHeight(getResources().getInteger(R.integer.wysokoscPola) * procentH);
        txt.setAnimation(animacjaFadeIn);
        txt.startAnimation(animacjaFadeIn);
        lay.addView(txt,params);

        schowajSylaby();
        grajDzwiek(wygrana);
    }

    private void generujWyjscie(){
        ImageView przyciskWyjscia = new ImageView(context);
        przyciskWyjscia.setImageResource(R.drawable.back);
        przyciskWyjscia.setY(90* procentH);
        przyciskWyjscia.setX( 0);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        przyciskWyjscia.setLayoutParams(lp);
        RelativeLayout lay = (RelativeLayout) findViewById(R.id.layMigawka);
        lay.addView(przyciskWyjscia);

        przyciskWyjscia.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Intent returnIntent = new Intent();
                setResult(rezultat, returnIntent);
                finish();
                return false;
            }
        });
    }


    private ImageView generujPrzyciskWyjscia(){
        ImageView iv = new ImageView(context);
        iv.setImageResource(R.drawable.back);
        iv.setY(getResources().getInteger(R.integer.migawkaMarginesTop) * procentH*2f);
        iv.setX(50 * procentW);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        iv.setLayoutParams(lp);

        iv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Level.this.finish();
                return false;
            }
        });
        return iv;
    }

    private void ustawSzaryKwiatek(){
        listaKwiatkow.get(ileBledow).setImageResource(R.drawable.flower_bw);
    }

    private void pokazOdpowiedz(){
        RelativeLayout.LayoutParams parametryMigawki = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        parametryMigawki.addRule(RelativeLayout.CENTER_HORIZONTAL);
        parametryMigawki.addRule(RelativeLayout.CENTER_VERTICAL);
        final TextView pKomunikat = new TextView(context);

        pKomunikat.setHeight(getResources().getInteger(R.integer.wysokoscPola) * procentH);
        pKomunikat.setLayoutParams(parametryMigawki);
        pKomunikat.setTextSize(getResources().getInteger(R.integer.textSize) * procentH);
        pKomunikat.setText(wyraz.toUpperCase().replace(",", ""));
        pKomunikat.setTextColor(Color.BLACK);
        pKomunikat.setBackground(getResources().getDrawable(R.drawable.rounded_corner));
        pKomunikat.setPadding(3 * procentW, 0, 3 * procentW, 0);
        pKomunikat.setGravity(Gravity.CENTER);

        Animation animMigawka = AnimationUtils.loadAnimation(context, R.anim.anim_migawka);
        animMigawka.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {            }

            @Override
            public void onAnimationEnd(Animation animation) {
                pKomunikat.setVisibility(View.GONE);
//                schowajSylaby();
                nowaGra();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {            }
        });
        pKomunikat.startAnimation(animMigawka);

        RelativeLayout migawkaLayout = (RelativeLayout) findViewById(R.id.layMigawka);
        migawkaLayout.addView(pKomunikat);
    }

    private void schowajSylaby(){
        Animation animacjaFadeOut = AnimationUtils.loadAnimation(context, R.anim.fadeout);
        for(TextView tv: listaKomorekCeli){
            tv.setAnimation(animacjaFadeOut);
            tv.startAnimation(animacjaFadeOut);
        }
        animacjaFadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                RelativeLayout laySylaby = (RelativeLayout) findViewById(R.id.laySylaby);
                laySylaby.removeAllViews();
                listaKomorekCeli.clear();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private DAO podajDAO(){
        return dao == null ? (dao = new DAO(context)) : dao;
    }

    private void grajDzwiek(boolean czyWygrana){
        SoundPool sounds=new SoundPool.Builder().build();
        int idDzwieku = czyWygrana ? Dzwieki.podajIdTada() : Dzwieki.podajIdErr();
        sounds.load(context,idDzwieku, 1);
        sounds.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                soundPool.play(sampleId,1,1,1,0,1);
            }
        });
    }

    @Override
    protected void onPause() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onPause();
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

    @Override
    public View makeView() {
        TextView t = new TextView(this);
        t.setTextSize(getResources().getInteger(R.integer.textSize) * procentH);
        t.setLayoutParams(new FrameLayout.LayoutParams(procentW * getResources().getInteger(R.integer.sylabaSzerokosc), getResources().getInteger(R.integer.wysokoscPola) * procentH));
        t.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        return t;
    }

    private final class ChoiceTouchListenerZrodlo implements View.OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                view.startDrag(data, shadowBuilder, view, 0);
                return true;
            } else {
                return false;
            }
        }
    }

    private final class ChoiceTouchListenerCel implements View.OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                if (view.getTag() != null) {
                    TextSwitcher tv = (TextSwitcher) findViewById((int) view.getTag());
                    tv.setVisibility(View.VISIBLE);
                    ((TextView) view).setText("");
                    view.setTag(null);
                    view.setBackgroundResource(R.drawable.rounded_corner_dark);
                }
                return true;
            } else {
                return false;
            }
        }
    }

    private class ChoiceDragListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    break;
                case DragEvent.ACTION_DROP:
                    View view = (View) event.getLocalState();
                    TextView dropTarget = (TextView) v;
                    Object tag = dropTarget.getTag();

                    if (tag != null) {
                        return false;
                        //dropped.setVisibility(View.VISIBLE);
                    }
                    else {
                        view.setVisibility(View.INVISIBLE);
                        TextSwitcher dropped = (TextSwitcher) view;
                        TextView textView = (TextView) dropped.getCurrentView();
                        //int existingID = (Integer) tag;
                        //findViewById(existingID).setVisibility(View.VISIBLE);


                        dropTarget.setText(textView.getText());
                        dropTarget.setTag(dropped.getId());
                    }
                    if (czyWszystkieSchowane()) {
                        wylaczDotykSylab();
                        if (listaZlychSylab().isEmpty()) wygrana();
                        else blad(listaZlychSylab());
                    }
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    break;
                default:
                    break;
            }
            return true;
        }
    }
}