package br.com.nimesko.widgetchat.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.emoji.Emojicon;

import java.io.IOException;
import java.util.Stack;

import br.com.nimesko.widgetchat.R;
import br.com.nimesko.widgetchat.animation.types.ImageAnimation;
import br.com.nimesko.widgetchat.ui.emojicon.CustomEmojiconsFragment;

public class ChatView extends RelativeLayout {

    private LinearLayout containerChatView;
    private EmojiconEditText editTextMessage;
    private ImageButton imageButtonSendSpeakMessage;
    private ImageButton imageButtonShowEmoticon;
    private FrameLayout containerEditTextKeyboardEmoticon;
    private FrameLayout containerKeyboard;
    private FrameLayout containerImgBtnSendSpeak;

    private MediaRecorder mediaRecorder;
    private InputMethodManager inputMethodManager;
    private FragmentManager fragmentManager;
    private boolean isKeyboardEmoticonShowed;
    private boolean isRecording;
    private int heigthView;
    private int originalSize;

    private String pathLastAudio;
    private CustomEmojiconsFragment customEmojiconsFragment;
    private Stack<String> stateText;
    private Stack<Integer> stateSelection;

    private OnVoiceRecordListener onVoiceRecordListener;
    private OnSendTextListener onSendTextListener;

    public ChatView(Context context) {
        super(context);
        init(context, null);
    }

    public ChatView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ChatView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ChatView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View.inflate(context, R.layout.view_chat, this);
        if(!isInEditMode()) {
            containerKeyboard = (FrameLayout) findViewById(R.id.containerKeyboard);
            containerImgBtnSendSpeak = (FrameLayout) findViewById(R.id.containerImgBtnSendSpeak);
            containerEditTextKeyboardEmoticon = (FrameLayout) findViewById(R.id.containerEditTextKeyboardEmoticon);
            containerChatView = (LinearLayout) findViewById(R.id.containerChatView);
            editTextMessage = (EmojiconEditText) findViewById(R.id.edtMessage);
            imageButtonSendSpeakMessage = (ImageButton) findViewById(R.id.imgBtnSendSpeak);
            imageButtonShowEmoticon = (ImageButton) findViewById(R.id.imgBtnShowKeyboardOrEmoticon);

            setupEditTextMessage();
            setupImageButtonSendSpeakMessage();
            setupImageButtonShowEmoticon(context);

            inputMethodManager = ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE));

            mediaRecorder = new MediaRecorder();
            stateText = new Stack<>();
            stateSelection = new Stack<>();
            fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            customEmojiconsFragment = new CustomEmojiconsFragment();
            fragmentTransaction.add(R.id.containerKeyboard, customEmojiconsFragment);
            fragmentTransaction.hide(customEmojiconsFragment);
            fragmentTransaction.commit();

            heigthView = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 258, getResources().getDisplayMetrics());

        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        originalSize = getHeight();
    }

    private void setupImageButtonShowEmoticon(final Context context) {
        imageButtonShowEmoticon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isKeyboardEmoticonShowed) {
                    ImageAnimation.imageAnimation(imageButtonShowEmoticon, R.drawable.ic_keyboard);
                    inputMethodManager.hideSoftInputFromWindow(editTextMessage.getWindowToken(), 0);
                    containerKeyboard.getLayoutParams().height = heigthView;
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while(editTextMessage.isInEditMode());
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {

                            } finally {
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.show(customEmojiconsFragment);
                                fragmentTransaction.commit();
                            }
                        }
                    });
                    thread.start();
                    isKeyboardEmoticonShowed = true;
                } else {
                    ImageAnimation.imageAnimation(imageButtonShowEmoticon, R.drawable.ic_emoticon);
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.hide(customEmojiconsFragment);
                    fragmentTransaction.commit();
                    inputMethodManager.showSoftInput(editTextMessage, 0);
                    isKeyboardEmoticonShowed = false;
                }
            }
        });
    }

    private void setupImageButtonSendSpeakMessage() {
        ViewCompat.setElevation(containerImgBtnSendSpeak, 12);
        imageButtonSendSpeakMessage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("".equals(editTextMessage.getText().toString())) {
                    if (isRecording) {
                        containerImgBtnSendSpeak.setBackgroundResource(R.drawable.background_send_speak_button_default);
                        mediaRecorder.stop();
                        mediaRecorder.reset();
                        mediaRecorder.release();
                        isRecording = false;
                        if(onVoiceRecordListener != null) {
                            onVoiceRecordListener.afterRecord(pathLastAudio);
                        }
                    } else {
                        try {
                            if(onVoiceRecordListener != null) {
                                onVoiceRecordListener.beforeRecord();
                            }
                            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                            mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
                            containerImgBtnSendSpeak.setBackgroundResource(R.drawable.background_send_speak_button_recording);
                            isRecording = true;
                            pathLastAudio = Environment.getExternalStorageDirectory()
                                    .getAbsolutePath() + "/recording_" + System.currentTimeMillis
                                    () + ".3gp";
                            mediaRecorder.setOutputFile(pathLastAudio);
                            mediaRecorder.prepare();
                            mediaRecorder.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    if(onSendTextListener != null) {
                        onSendTextListener.sendText(editTextMessage.getText().toString());
                    }
                    editTextMessage.setText("");
                }
            }
        });
    }

    private void setupEditTextMessage() {
        ViewCompat.setElevation(containerEditTextKeyboardEmoticon, 12);
        editTextMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (before == 0 && start == 0) {
                    ImageAnimation.imageAnimation(imageButtonSendSpeakMessage, R.drawable.ic_sent);
                } else {
                    if (s.toString().length() == 0) {
                        ImageAnimation.imageAnimation(imageButtonSendSpeakMessage, R.drawable.ic_microphone);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        editTextMessage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isKeyboardEmoticonShowed) {
                    isKeyboardEmoticonShowed = false;
                    ImageAnimation.imageAnimation(imageButtonShowEmoticon, R.drawable.ic_emoticon);
                    fragmentManager.beginTransaction().hide(customEmojiconsFragment).commit();
                    containerKeyboard.getLayoutParams().height = heigthView;
                } else {
                    containerKeyboard.getLayoutParams().height = heigthView;
                }
            }
        });
    }

    public boolean onBackPressed() {
        if(isKeyboardEmoticonShowed) {
            fragmentManager.beginTransaction().hide(customEmojiconsFragment).commit();
            containerKeyboard.getLayoutParams().height = 0;
            ImageAnimation.imageAnimation(imageButtonShowEmoticon, R.drawable.ic_emoticon);
            isKeyboardEmoticonShowed = false;
            return false;
        } else {
            return true;
        }
    }

    public void handleEmojiconClicked(Emojicon emojicon) {
        stateText.push(editTextMessage.getText().toString());
        stateSelection.push(editTextMessage.getSelectionEnd());
        editTextMessage.setText(editTextMessage.getText() + emojicon.getEmoji());
        editTextMessage.setSelection(editTextMessage.getText().toString().length() - 1);
    }

    public void handleEmojiconBackspace() {
        if(!stateText.isEmpty()) {
            editTextMessage.setText(stateText.pop());
            editTextMessage.setSelection(stateSelection.pop());
        }
    }

    @Override
    public boolean dispatchKeyEventPreIme(@NonNull KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            containerKeyboard.getLayoutParams().height = 0;
        }
        return super.dispatchKeyEventPreIme(event);
    }

    public OnVoiceRecordListener getOnVoiceRecordListener() {
        return onVoiceRecordListener;
    }

    public void setOnVoiceRecordListener(OnVoiceRecordListener onVoiceRecordListener) {
        this.onVoiceRecordListener = onVoiceRecordListener;
    }

    public void setOnSendTextListener(OnSendTextListener onSendTextListener) {
        this.onSendTextListener = onSendTextListener;
    }

    public OnSendTextListener getOnSendTextListener() {
        return onSendTextListener;
    }

    public interface OnVoiceRecordListener {

        void beforeRecord();

        void afterRecord(String pathLastAudio);

    }

    public interface OnSendTextListener {

        void sendText(String text);

    }

}
