package tridoo.sylabus;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class DAO {
    Context context;
    String jStringName = "sylaby";

    public DAO(Context aContext) {
        context = aContext;
    }


    public void zapiszZaliczonyPoziom(int rozdzial, int poziom, int pkt) {
        List<HashMap<Integer, Integer>> listaWynikow=odczytajWyniki();
        HashMap<Integer,Integer> wynikiRozdzialu=listaWynikow.get(rozdzial-1);
        wynikiRozdzialu.put(poziom-1, pkt);

        zapiszWyniki(listaWynikow);
    }

    public List<HashMap<Integer,Integer>> podajPustaTabliceWynikow(){
        List<HashMap<Integer,Integer>> listaWynikow=new ArrayList<>();
        HashMap<Integer,Integer> wynikiR1=new HashMap<>();
        HashMap<Integer,Integer> wynikiR2=new HashMap<>();
        HashMap<Integer,Integer> wynikiR3=new HashMap<>();
        HashMap<Integer,Integer> wynikiR4=new HashMap<>();
        HashMap<Integer,Integer> wynikiR5=new HashMap<>();

        listaWynikow.add(wynikiR1);
        listaWynikow.add(wynikiR2);
        listaWynikow.add(wynikiR3);
        listaWynikow.add(wynikiR4);
        listaWynikow.add(wynikiR5);
        return listaWynikow;
    }

    public List<HashMap<Integer, Integer>> odczytajWyniki() {

        List<HashMap<Integer, Integer>> listaWynikow = new ArrayList<>();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String jString = prefs.getString(jStringName, "");
        try {
            JSONArray jWszystkieWyniki = new JSONArray(jString);
            for (int i = 0; i < jWszystkieWyniki.length(); i++) {
                JSONArray jWynikiRozdzialu = jWszystkieWyniki.getJSONArray(i);

                HashMap<Integer, Integer> wynikiRozdzialu = new HashMap<>();
                for (int j = 0; j < jWynikiRozdzialu.length(); j++) {
                    int level = Integer.valueOf(jWynikiRozdzialu.get(j).toString().split(":")[0].replaceAll("\\D+", ""));
                    int pkt = Integer.valueOf(jWynikiRozdzialu.get(j).toString().split(":")[1].replaceAll("\\D+", ""));
                    wynikiRozdzialu.put(level, pkt);
                }
                listaWynikow.add(wynikiRozdzialu);

            }

        } catch (JSONException e) {
            return podajPustaTabliceWynikow();
        }
        return listaWynikow;
    }

    public HashMap<Integer,Integer> odczytajWynikiRozdzialu(int rozdzial){
        List<HashMap<Integer, Integer>> pWszystkieWyniki=odczytajWyniki();
        return pWszystkieWyniki.get(rozdzial-1);
    }

    public void zapiszWyniki(List<HashMap<Integer,Integer>> listaWynikow){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray jWYnikiWszystkie = new JSONArray();

        for (HashMap wynikiRozdzialu : listaWynikow){
            JSONArray jWYnikiRozdzialu = new JSONArray();

            Iterator it = wynikiRozdzialu.entrySet().iterator();
            while (it.hasNext()) {
                JSONObject jWynik = new JSONObject();
                Map.Entry pair = (Map.Entry)it.next();
                try {
                    jWynik.put(String.valueOf(pair.getKey()),(Integer)pair.getValue());
                    jWYnikiRozdzialu.put(jWynik);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            jWYnikiWszystkie.put(jWYnikiRozdzialu);
        }
        editor.putString(jStringName, jWYnikiWszystkie.toString());
        editor.apply();
    }

}
