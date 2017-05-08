package tridoo.sylabus;

import android.media.AudioManager;
import android.media.SoundPool;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


public class Dzwieki {
    SoundPool sounds;
    ArrayList<Integer> tada = new ArrayList<>();
    ArrayList<Integer> err = new ArrayList<>();


    private void wczytajDzwieki() {
        SoundPool.Builder builder=new SoundPool.Builder();
        sounds=builder.build();

        tada.add(R.raw.tada_1);
        tada.add(R.raw.tada_2);
        tada.add(R.raw.tada_3);
        tada.add(R.raw.tada_4);
        tada.add(R.raw.tada_5);

        err.add(R.raw.err_1);
        err.add(R.raw.err_2);
        err.add(R.raw.err_3);
        err.add(R.raw.err_4);
        err.add(R.raw.err_5);

    }

    public static int podajIdTada(){
        int x=ThreadLocalRandom.current().nextInt(1, 5 + 1);

        switch (x){
            case 1:
                return R.raw.tada_1;
            case 2:
                return R.raw.tada_2;
            case 3:
                return R.raw.tada_3;
            case 4:
                return R.raw.tada_4;
            case 5:
                return R.raw.tada_5;
        }
        return 0;
    }
    public static int podajIdErr(){
        int x=ThreadLocalRandom.current().nextInt(1, 5 + 1);

        switch (x){
            case 1:
                return R.raw.err_1;
            case 2:
                return R.raw.err_2;
            case 3:
                return R.raw.err_3;
            case 4:
                return R.raw.err_4;
            case 5:
                return R.raw.err_5;
        }
        return 0;
    }



}
