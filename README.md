# WidgetChat
Project that has visual resemblance to the WhatsApp regarding the visual component ( EditText ) that captures the emojicons and the text typed by the user (and the button to send / record audio) and send to contacts.

What this project have?
* Provide emojicon and text to send messages
* Smooth animation when start typing an text
* Abstraction to recording audio (providing URL of file location)

This is a library and a sample project that uses the widget that looks like WhatsApp.

After added the view in your layout file, you will able to implement two interfaces that will be described below.
Exists another two interfaces (ChatView.OnSendTextListener and ChatView.OnVoiceRecordListener), your use, is describe below:

ChatView.OnSendTextListener:
* When user send a text with/without emojicon, this events is called

ChatView.OnVoiceRecordListener:
* When user starts a audio record, the method <b>beforeRecord</b> is called
* When user stops, the method <b>afterRecord</b> is called, passing the location of file created.

