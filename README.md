# WidgetChat
Project that has visual resemblance to the WhatsApp regarding the visual component ( EditText ) that captures the emojicons and the text typed by the user (and the button to send / record audio) and send to contacts.

What this project have?
* Provide emojicon and text to send messages
* Smooth animation when start typing an text
* Abstraction to recording audio (providing URL of file location)

This is a library and a sample project that uses the widget that looks like WhatsApp.

After added the view in your layout file, you need to implement 3 pieces of code to work property.

<b>Overriding the onBackPressed to handle events when the back button is pressed
```java
...
    @Override
    public void onBackPressed() {
        if(chatView.onBackPressed()) {
            super.onBackPressed();
        }
    }
...
```

<b>Implements this interfaces, to handle click event on each of emojicon in keyboard emojicon</b>
```java
...
extends AppCompatActivity implements CustomEmojiconsFragment.OnEmojiconBackspaceClickedListener, CustomEmojiconGridFragment.OnEmojiconClickedListener
...
```

<b>And finally, implements the interface listed above with this pice of code</b>
```java
...
    @Override
    public void onEmojiconBackspaceClicked(View view) {
        chatView.handleEmojiconBackspace();
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        chatView.handleEmojiconClicked(emojicon);
    }
...
```

Exists another two interfaces (ChatView.OnSendTextListener and ChatView.OnVoiceRecordListener), your use, is describe below:

ChatView.OnSendTextListener:
* When user send a text with/without emojicon, this events is called

ChatView.OnVoiceRecordListener:
* When user starts a audio record, the method <b>beforeRecord</b> is called
* When user stops, the method <b>afterRecord</b> is called, passing the location of file created.

