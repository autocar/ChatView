# ChatView
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

# License
The MIT License (MIT)

Copyright (c) 2015 Rogerio Tristao Junior

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
