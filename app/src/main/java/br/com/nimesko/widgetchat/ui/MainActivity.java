package br.com.nimesko.widgetchat.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.rockerhieu.emojicon.EmojiconTextView;

import br.com.nimesko.widgetchat.R;
import br.com.nimesko.widgetchat.ui.widget.ChatView;

public class MainActivity extends AppCompatActivity implements ChatView.OnSendTextListener, ChatView.OnVoiceRecordListener {

    private ChatView chatView;
    private EmojiconTextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chatView = (ChatView) findViewById(R.id.chatView);
        textView = (EmojiconTextView) findViewById(R.id.textView);
        chatView.setOnSendTextListener(this);
        chatView.setOnVoiceRecordListener(this);
    }

    @Override
    public void sendText(String text) {
        textView.setText(textView.getText() + "\n" + text);
    }

    @Override
    public void beforeRecord() {

    }

    @Override
    public void afterRecord(String pathLastAudio) {

    }

}
