package com.kukdudelivery.util;


import android.text.TextUtils;
import android.widget.TextView;

public class Formulas {

    public static void CalculatePastureIntakes(double effective_hectares, double pregraze, double postgraze, double hectares_offer, double days, double cows, boolean HC_ON, boolean COWS_ON, TextView t1, TextView t2, TextView t3, TextView t4)
    {
            if(TextUtils.isEmpty(effective_hectares+"")){
                effective_hectares=0;
            }
            if(TextUtils.isEmpty(cows+"")){
            cows=0;
            }
            double rotation_length= effective_hectares/hectares_offer;
            double stocking_rate=COWS_ON?(cows/effective_hectares):(cows*effective_hectares);
            double m2_cow=(((HC_ON?hectares_offer:rotation_length)*10000)/days)/((COWS_ON?cows:stocking_rate));
            double kgdm_cow_day=(((pregraze-postgraze)*(HC_ON?hectares_offer:rotation_length)/days)/(COWS_ON?cows:stocking_rate));
            t1.setText(String.format("%.2f",rotation_length));
            t2.setText(String.format("%.2f",stocking_rate));
            t3.setText(String.format("%.2f",m2_cow));
            t4.setText(String.format("%.2f",kgdm_cow_day));
    }
    public static void CalculatePregrazeCover(double stokingrate, double intake, double roundlength, double optimalresidual, TextView t1, TextView t2)
    {
        double pc=(stokingrate*intake*roundlength)+optimalresidual;
        double average=(optimalresidual+pc)/2;
        t1.setText(String.format("%.2f",pc));
        t2.setText(String.format("%.2f",average));
    }
    public static void CalculateAveragePastureCover(double pregrazingcover, double optimalresidual, TextView t1)
    {
        double average=(pregrazingcover+optimalresidual)/2;
        t1.setText(String.format("%.2f",average));
    }
    public static void CalculateMixRation(double product, double ratio, TextView t1, TextView t2)
    {
        double mililiter=(product*1000)/ratio;
        double liter=(product/ratio);
        t1.setText(String.format("%.2f",mililiter));
        t2.setText(String.format("%.2f",liter));
    }
    public static void CalculateKGDMAllocation(double cover, double residual, double hectares, double cows, double kgdm, TextView t1)
    {
        double kgdm_data=(((cover-residual)*hectares)/(cows*kgdm));
        t1.setText(String.format("%.2f",kgdm_data));
    }
    public static void CalculateM2Allocation(double cows, double m2, double hectares, TextView t1)
    {
        double kgdm_data=hectares/((m2*cows)/10000);
        t1.setText(String.format("%.2f",kgdm_data));
    }
    public static void CalculateCropAllocation(double yield, double cows, double kgdm, double size, TextView t1, TextView t2, TextView t3)
    {
        double m2cows=(kgdm/yield)*10;
        double dialyarea=(m2cows*cows)/10000;
        double find_days=(size*10000)/(m2cows*cows);
        t1.setText(String.format("%.2f",m2cows));
        t2.setText(String.format("%.2f",dialyarea));
        t3.setText(String.format("%.2f",find_days));
    }
    public static void CalculateBreakFence(double m2cows, double cows, double fence, TextView t1)
    {
        double total=(m2cows*cows)/fence;
        t1.setText(String.format("%.2f",total));
    }

}
//=E5/((E4*E3)/10000)