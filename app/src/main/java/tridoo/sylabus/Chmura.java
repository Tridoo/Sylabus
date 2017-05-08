package tridoo.sylabus;


import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class Chmura {
    private ImageView bitmapa;
    private Animation animacja;
    private Context context;
    private int idAnimacji;

    public Chmura(Context context) {
        this.context = context;
    }

    public void startAnimacji() {
        bitmapa.startAnimation(animacja);

        animacja.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {    }

            @Override
            public void onAnimationEnd(Animation a) {
                a.setAnimationListener(null);
                a = AnimationUtils.loadAnimation(context,idAnimacji);
                a.setAnimationListener(this);

                bitmapa.clearAnimation();
                bitmapa.setAnimation(a);
                bitmapa.startAnimation(a);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {   }
        });
    }


    public ImageView getBitmapa() {
        return bitmapa;
    }

    public void setBitmapa(ImageView bitmapa) {
        this.bitmapa = bitmapa;
    }

    public Animation getAnimacja() {
        return animacja;
    }

    public void setAnimacja(Animation animacja) {
        this.animacja = animacja;
    }


    public int getIdAnimacji() {
        return idAnimacji;
    }

    public void setIdAnimacji(int idAnimacji) {
        this.idAnimacji = idAnimacji;
    }
}
