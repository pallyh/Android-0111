package step.learning.course;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {

    private final String CHAT_URL = "https://diorama-chat.ew.r.appspot.com/story" ;
    private EditText etAuthor ;
    private EditText etMessage ;
    private TextView tvChat ;
    private List<ChatMessage> chatMessages = new ArrayList<>() ;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_chat );

        etAuthor = findViewById( R.id.chat_et_author ) ;
        etMessage = findViewById( R.id.chat_et_message ) ;
        tvChat = findViewById( R.id.tv_chat ) ;
        findViewById( R.id.chat_button_send ).setOnClickListener( this::sendMessageClick ) ;

        new Thread( this::getChatMessages ).start() ;
    }

    private void getChatMessages() {
        try( InputStream chatStream = new URL( CHAT_URL ).openStream() ) {
            ByteArrayOutputStream byteBuilder = new ByteArrayOutputStream() ;
            byte[] chunk = new byte[ 4096 ] ;
            int len ;
            while( ( len = chatStream.read( chunk ) ) != -1 ) {
                byteBuilder.write( chunk, 0, len ) ;
            }
            parseChatMessages( byteBuilder.toString() ) ;
            byteBuilder.close() ;
        }
        catch( android.os.NetworkOnMainThreadException ignored ) {
            Log.d( "getChatMessages", "NetworkOnMainThreadException" ) ;
        }
        catch( MalformedURLException ex ) {
            Log.d( "getChatMessages", "MalformedURLException " + ex.getMessage() );
        }
        catch( IOException ex ) {
            Log.d( "getChatMessages", "IOException " + ex.getMessage() );
        }
    }
    private void parseChatMessages( String loadedContent ) {
        /* loadedContent =
            {
              "status": "success",
              "data": [ {}, {}, ... ]
            }
         */
        try {
            JSONObject content = new JSONObject( loadedContent ) ;
            // TODO: check 'status' field for 'success' value
            if( content.has( "data" ) ) {
                JSONArray data = content.getJSONArray( "data" ) ;
                int len = data.length() ;
                for( int i = 0; i < len; i++ ) {
                    chatMessages.add(
                            new ChatMessage( data.getJSONObject( i ) )
                    ) ;
                }
                /* Д.З. Реализовать сортировку сообщений - последние (по времени) - идут снизу
                    Реализовать отображение даты-времени сообщения
                */
            }
            else {
                Log.d( "parseChatMessages", "Content has no 'data' " + loadedContent ) ;
            }
        }
        catch( JSONException ex ) {
            Log.d( "parseChatMessages", ex.getMessage() ) ;
        }
        runOnUiThread( this::showChatMessages ) ;
    }
    private void showChatMessages() {
        StringBuilder sb = new StringBuilder() ;
        for( ChatMessage message : chatMessages ) {
            sb.append( message.getAuthor() ).append( ':' ).append( message.getTxt() ).append( '\n' ) ;
        }
        tvChat.setText( sb.toString() ) ;
    }
    private void sendMessageClick( View view ) {

    }

    /**
     * ORM for Chat API
     */
    private static class ChatMessage {
        private UUID id;
        private String author;
        private String txt;
        private Date moment;
        private UUID idReply;
        private String replyPreview;

        private static final SimpleDateFormat chatMomentFormat =   // "Apr 19, 2023 4:41:35 PM"
                new SimpleDateFormat( "MMM dd, yyyy KK:mm:ss a", Locale.US ) ;
        public ChatMessage() {
        }

        public ChatMessage( JSONObject jsonObject ) throws JSONException {
            this.setId( UUID.fromString( jsonObject.getString( "id" ) ) ) ;
            this.setAuthor( jsonObject.getString( "author" ) ) ;
            this.setTxt( jsonObject.getString( "txt" ) ) ;
            try {
                this.setMoment( chatMomentFormat.parse( jsonObject.getString( "moment" ) ) ) ;
            }
            catch( ParseException ex ) {
                throw new JSONException( "Moment parse error: " + ex.getMessage() ) ;
            }
            // Optional fields
            if( jsonObject.has( "idReply" ) ) {
                this.setIdReply( UUID.fromString( jsonObject.getString( "idReply" ) ) ); ;
            }
            if( jsonObject.has( "replyPreview" ) ) {
                this.setReplyPreview( jsonObject.getString( "replyPreview" ) ) ;
            }
        }

        // region Accessors
        public UUID getId() {
            return id;
        }

        public void setId( UUID id ) {
            this.id = id;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor( String author ) {
            this.author = author;
        }

        public String getTxt() {
            return txt;
        }

        public void setTxt( String txt ) {
            this.txt = txt;
        }

        public Date getMoment() {
            return moment;
        }

        public void setMoment( Date moment ) {
            this.moment = moment;
        }

        public UUID getIdReply() {
            return idReply;
        }

        public void setIdReply( UUID idReply ) {
            this.idReply = idReply;
        }

        public String getReplyPreview() {
            return replyPreview;
        }

        public void setReplyPreview( String replyPreview ) {
            this.replyPreview = replyPreview;
        }
        // endregion

        /*
      "id": "0c9d3a4c-ded1-11ed-a079-3a2520158311",
      "author": "John Smith",
      "txt": "Классный ник",
      "moment": "Apr 19, 2023 4:41:35 PM",
      "idReply": "4526cc3e-dc49-11ed-88bb-4ad81b26d4d9",
      "replyPreview": "azaza"
         */
    }
}
/*
Работа с Интернет
java.net.URL - основной класс (аналог File для файлов)
URL chatUrl = new URL( CHAT_URL ) ; - создание объекта, подключений не устанавливается
chatUrl.openStream() - подключение + поток данных (stream)
android.os.NetworkOnMainThreadException - исключение (без текста сообщения)
выбрасывается если соединение открывается из основного (UI) потока
Работа с сетью должна быть асинхронной
Стартуем в отдельном потоке -
java.lang.SecurityException: Permission denied (missing INTERNET permission?)
Добавляем в манифест
<uses-permission android:name="android.permission.INTERNET"/>
Следствие того что работа с сетью должна быть в отдельном потоке,
является ошибка доступа к UI элементам из другого потока (setText)
Поэтому финальные задачи по отображению желательно переместить в
отдельный метод, который вызывать методом  runOnUiThread( this::showChatMessages ) ;
runOnUiThread( () -> setText )
UI -X- URL             access UI from other thread
                       |
     URL - GET - JSON -X- show(setText)
UI /
                       runOnUiThread
     URL - GET - JSON  |
UI /                  \  show(setText)
                / JSON
     URL - GET          \ runOnUiThread
UI /                     \  show(setText)
 */