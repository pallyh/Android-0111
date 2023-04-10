package step.learning.course;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class CalcActivity extends AppCompatActivity {
private TextView tvHistory;
private TextView tvResult;
private int maxSymbol;
    private String minusSign;
    private String zeroSymbol;
    private Boolean pointCreate;
    private String pointSymbol;
    private int pointSymbolAnInt;
    private boolean needClear;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calc);

        pointCreate = false;
        maxSymbol = 10;

        minusSign = getString(R.string.calc_btn_minus_text);
        zeroSymbol = getString(R.string.calc_btn_num_zero_text);
        pointSymbol = getString(R.string.calc_point_symbol);
        pointSymbolAnInt = 0;

        tvHistory = findViewById(R.id.tv_history);
        tvResult = findViewById(R.id.tv_result);

        clearClick(null);

        String[] suffixes ={"zero","one","two","three","four","five","six","seven","eight","nine"} ;

        for (int i = 0 ; i<10 ; i++){
            @SuppressLint("DiscouragedApi")
            int buttonId = getResources().getIdentifier(
                    "calc_btn_num_" + suffixes[i],
                    "id",
                    getPackageName()
            );
            findViewById(buttonId).setOnClickListener(this::digitClick);
        }

        findViewById(R.id.calc_btn_backspace).setOnClickListener(this::backspaceClick);
        findViewById(R.id.calc_btn_plus_minus).setOnClickListener(this::plusMinusClick);
        findViewById(R.id.calc_btn_point).setOnClickListener(this::pointClick);
        findViewById(R.id.calc_btn_clear).setOnClickListener(this::clearClick);
        findViewById(R.id.calc_btn_ce).setOnClickListener(this::clearEditClick);
        findViewById(R.id.calc_btn_x_square).setOnClickListener(this::squareClick);
        findViewById(R.id.calc_btn_divide_by_x).setOnClickListener(this::divineByXClick);
        findViewById(R.id.calc_btn_square_root_of_x).setOnClickListener(this::sqrtByClick);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedState) {
        super.onRestoreInstanceState(savedState);
        Log.d("CalcActivity","onRestoreInstanceState");
    
        tvHistory.setText(savedState.getCharSequence("history"));
        tvResult.setText(savedState.getCharSequence("result"));
        needClear = savedState.getBoolean("neededClear");
        pointCreate = savedState.getBoolean("pointCreated");
        pointSymbolAnInt = savedState.getInt("numberPointSymbol");

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle savingState) {
        super.onSaveInstanceState(savingState);
        Log.d("CalcActivity","onSaveInstanceState");
        savingState.putCharSequence("history",tvHistory.getText());
        savingState.putCharSequence("result",tvResult.getText());
        savingState.putBoolean("neededClear",needClear);
        savingState.putBoolean("pointCreated",pointCreate);
        savingState.putInt("numberPointSymbol",pointSymbolAnInt);
    }


    private void sqrtByClick(View view){
        String result = tvResult.getText().toString();
        double arg;
        try {

            arg = Double.parseDouble(
                    result
                            .replace(minusSign,"-")
                            .replaceAll(zeroSymbol , "0")
                            .replace(pointSymbol,"."));
        }
        catch (NumberFormatException | NullPointerException ignored){
            Toast.makeText(this, R.string.calc_error_parse, Toast.LENGTH_SHORT).show();
            return;
        }
        if( arg < 0 ) {

           Vibrator vibrator;

           if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
                VibratorManager vibratorManager = (VibratorManager)
                        getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
                vibrator = vibratorManager.getDefaultVibrator();
            }else {
                vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            }

            long[] vibratePattern = {0,400,200,400};

           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
             vibrator.vibrate( VibrationEffect.createOneShot(
                    250,VibrationEffect.DEFAULT_AMPLITUDE
                     )
                );
            vibrator.vibrate(
                    VibrationEffect.createWaveform(vibratePattern,-1)
                );
           } else {
           /* Самый простой подход -deprecate from 0 (API 26)*/
            /*Vibrator vibrator ;*/
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(250);
           }
        }
        else{
            tvHistory.setText(getString( R.string.calc_inverse_history ,result));
            arg = Math.sqrt(arg);
            displayResult(arg);
            needClear = true;
        }
    }

    private void divineByXClick(View view){
        String result = tvResult.getText().toString();
        double arg;
        try {

            arg = Double.parseDouble(
                    result
                            .replace(minusSign,"-")
                            .replaceAll(zeroSymbol , "0")
                            .replace(pointSymbol,"."));
        }
        catch (NumberFormatException | NullPointerException ignored){
            Toast.makeText(this, R.string.calc_error_parse, Toast.LENGTH_SHORT).show();
            return;
        }
        tvHistory.setText(getString( R.string.calc_inverse_history ,result));
        arg = 1 / arg;
        displayResult(arg);
        needClear = true;

    }
    private void squareClick(View view){
        String result = tvResult.getText().toString();
        double arg;
        try {

        arg = Double.parseDouble(
                result
                        .replace(minusSign,"-")
                        .replaceAll(zeroSymbol , "0")
                        .replace(pointSymbol,"."));
        }
        catch (NumberFormatException | NullPointerException ignored){
            Toast.makeText(this, R.string.calc_error_parse, Toast.LENGTH_SHORT).show();
            return;
        }
        tvHistory.setText(getString( R.string.calc_square_history ,result));
        arg *= arg;
        displayResult(arg);
        needClear = true;
    }
    private void clearClick(View view){ //C
        tvHistory.setText("");
        displayResult("");
    }
    private void clearEditClick(View view){ //CE
        displayResult("");
    }
    private void plusMinusClick(View view){
        String result = tvResult.getText().toString();

        if (result.equals(zeroSymbol)){
            return;
        }
        if (result.startsWith(minusSign)){
            maxSymbol = maxSymbol - 1;
            result = result.substring(1);
        }else {
            maxSymbol = maxSymbol + 1;
            result = minusSign + result;
        }
        displayResult(result);
    }

    private void pointClick(View view){
        String result = tvResult.getText().toString();

        if (pointCreate == true){
            result = result.substring(0 ,pointSymbolAnInt-1);
            maxSymbol = maxSymbol - 1;
            pointCreate = false;
        }
        else {
            pointSymbolAnInt = result.length() + 1;
            result = result + pointSymbol;
            maxSymbol = maxSymbol+1;
            pointCreate = true;
        }
        displayResult(result);
    }

    private void backspaceClick(View view){
        String result = tvResult.getText().toString();
        result = result.substring(0,result.length() - 1);

        if (result.equals(zeroSymbol) || needClear) {
            result = "";
            tvHistory.setText("");
            needClear = false;
        }
        displayResult(result);

    }
    private void digitClick(View view){
        String result = tvResult.getText().toString();
        if (result.length() >= maxSymbol){
            return;
        }
        String digit = ((Button) view).getText().toString();
        if (result.equals(zeroSymbol) || needClear) {
            result = "";
            tvHistory.setText("");
            needClear = false;
        }

        result += digit;
        displayResult(result);
    }
    private void displayResult(String result){
        if ("".equals(result) || minusSign.equals(result) ){
            result = zeroSymbol;

        }

        tvResult.setText(result);
    }
    private void displayResult(double arg){
        long argInt = (long)arg;
        String result = (argInt == arg ? "" + argInt : "" + arg);

        result = result.replace("-" ,minusSign)
                .replace("0",zeroSymbol)
                .replace(".",pointSymbol);

        displayResult(result);
    }
}