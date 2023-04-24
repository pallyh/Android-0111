package step.learning.course;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button_calc).setOnClickListener( this::buttonClick);

        findViewById(R.id.button2).setOnClickListener(this::buttonClickDell);
        findViewById(R.id.button3).setOnClickListener(this::buttonGameClick);
        findViewById(R.id.button_chat).setOnClickListener(this::buttonChatClick);
    }

    private void buttonClick(View view){
/*        TextView textHello = findViewById( R.id.text_hello);
        String txt = textHello.getText().toString();
        txt+="!";
        textHello.setText(txt);*/

        Intent activityIntent = new Intent(MainActivity.this, CalcActivity.class);
        startActivity(activityIntent);

    }
    private void buttonGameClick(View view){
        Intent activityIntent = new Intent(MainActivity.this, GameActivity.class);
        startActivity(activityIntent);

    }
    private void buttonClickDell(View view){
        TextView textHello = findViewById( R.id.text_hello);
        String txt = textHello.getText().toString();
        String tmp = txt.substring(txt.length()-1 , txt.length());
        String checkSymbol = "!";
        if(tmp.equals(checkSymbol)){
            txt = txt.substring(0,txt.length()-1);
            textHello.setText(txt);
        }
    }
    private void buttonChatClick( View view ) {
        Intent activityIntent = new Intent( MainActivity.this, ChatActivity.class ) ;
        startActivity( activityIntent ) ;
    }
}