package tridoo.sylabus;


import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class Slowniki {


    public List<String> podajListeSlow(Context context, int level, int rozdzial) {

        String pSciezka = "rozdzial_" + rozdzial + "/level_" + level+".txt";
        List<String> pLista = new ArrayList<>();

        try {
            InputStream inputStream = context.getAssets().open(pSciezka);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                pLista.add(line);
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return pLista;
    }
}
