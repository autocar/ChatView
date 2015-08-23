package br.com.nimesko.widgetchat.ui.widget;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.emoji.Emojicon;

import java.io.IOException;

import br.com.nimesko.widgetchat.R;
import br.com.nimesko.widgetchat.animation.types.ImageAnimation;
import br.com.nimesko.widgetchat.ui.emojicon.CustomEmojiconGridFragment;
import br.com.nimesko.widgetchat.ui.emojicon.CustomEmojiconsFragment;

public class ChatView extends RelativeLayout implements CustomEmojiconsFragment.OnEmojiconBackspaceClickedListener, CustomEmojiconGridFragment.OnEmojiconClickedListener {

    private EmojiconEditText editTextMessage;
    private ImageButton imageButtonSendSpeakMessage;
    private ImageButton imageButtonShowEmoticon;
    private FrameLayout containerEditTextKeyboardEmoticon;
    private FrameLayout containerKeyboard;
    private FrameLayout containerImgBtnSendSpeak;

    private MediaRecorder mediaRecorder;
    private InputMethodManager inputMethodManager;
    private FragmentManager fragmentManager;
    private Runnable runnable;
    private boolean isKeyboardEmoticonShowed;
    private boolean isRecording;
    private int heigthView;

    private String pathLastAudio;
    private CustomEmojiconsFragment customEmojiconsFragment;

    private OnVoiceRecordListener onVoiceRecordListener;
    private OnSendTextListener onSendTextListener;

    public ChatView(Context context) {
        super(context);
        init(context);
    }

    public ChatView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ChatView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    public boolean dispatchKeyEventPreIme(@NonNull KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (isKeyboardEmoticonShowed) {
                fragmentManager.beginTransaction().hide(customEmojiconsFragment).commit();
                containerKeyboard.getLayoutParams().height = 0;
                ImageAnimation.imageAnimation(imageButtonShowEmoticon, R.drawable.ic_emoticon);
                isKeyboardEmoticonShowed = false;
                return true;
            } else {
                containerKeyboard.getLayoutParams().height = 0;
                return super.dispatchKeyEventPreIme(event);
            }
        } else {
            return super.dispatchKeyEventPreIme(event);
        }
    }

    @Override
    public void onEmojiconBackspaceClicked(View view) {
        if (!"".equals(editTextMessage.getText().toString())) {
            char[] text = editTextMessage.getText().toString().toCharArray();
            if (text[text.length - 1] > 1000 && text[text.length - 2] > 1000) {
                editTextMessage.setText(editTextMessage.getText().toString().substring(0, text.length - 2));
                editTextMessage.setSelection(text.length - 2);
            } else {
                editTextMessage.setText(editTextMessage.getText().toString().substring(0, text.length - 1));
                editTextMessage.setSelection(text.length - 1);
            }
        }
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        editTextMessage.setText(editTextMessage.getText() + emojicon.getEmoji());
        editTextMessage.setSelection(editTextMessage.getText().toString().length() - 1);
    }

    private void init(Context context) {
        View.inflate(context, R.layout.view_chat, this);
        if(!isInEditMode()) {
            containerKeyboard = (FrameLayout) findViewById(R.id.container_keyboard);
            containerImgBtnSendSpeak = (FrameLayout) findViewById(R.id.container_img_btn_send_speak);
            containerEditTextKeyboardEmoticon = (FrameLayout) findViewById(R.id.container_edit_text_keyboard_emoticon);
            editTextMessage = (EmojiconEditText) findViewById(R.id.edt_message);
            imageButtonSendSpeakMessage = (ImageButton) findViewById(R.id.img_btn_send_speak);
            imageButtonShowEmoticon = (ImageButton) findViewById(R.id.img_btn_show_keyboard_or_emoticon);

            setupEditTextMessage();
            setupImageButtonSendSpeakMessage();
            setupImageButtonShowEmoticon();

            inputMethodManager = ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE));

            mediaRecorder = new MediaRecorder();
            fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            customEmojiconsFragment = new CustomEmojiconsFragment();
            customEmojiconsFragment.setOnEmojiconBackspaceClickedListener(this);
            customEmojiconsFragment.setOnEmojiconClickedListener(this);
            fragmentTransaction.add(R.id.container_keyboard, customEmojiconsFragment);
            fragmentTransaction.hide(customEmojiconsFragment);
            fragmentTransaction.commit();

            heigthView = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 258, getResources().getDisplayMetrics());
            runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Log.e("ChatView", "", e);
                    } finally {
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.show(customEmojiconsFragment);
                        fragmentTransaction.commit();
                    }
                }
            };
        }
    }

    private void setupImageButtonShowEmoticon() {
        imageButtonShowEmoticon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isKeyboardEmoticonShowed) {
                    ImageAnimation.imageAnimation(imageButtonShowEmoticon, R.drawable.ic_keyboard);
                    inputMethodManager.hideSoftInputFromWindow(editTextMessage.getWindowToken(), 0);
                    containerKeyboard.getLayoutParams().height = heigthView;
                    new Thread(runnable).start();
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
                        if (onVoiceRecordListener != null) {
                            onVoiceRecordListener.afterRecord(pathLastAudio);
                        }
                    } else {
                        try {
                            if (onVoiceRecordListener != null) {
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
                    if (onSendTextListener != null) {
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
            public void onClick(View view) {
                if (isKeyboardEmoticonShowed) {
                    isKeyboardEmoticonShowed = false;
                    ImageAnimation.imageAnimation(imageButtonShowEmoticon, R.drawable.ic_emoticon);
                    fragmentManager.beginTransaction().hide(customEmojiconsFragment).commit();
                }
                containerKeyboard.getLayoutParams().height = heigthView;
            }
        });
    }

    public void setOnVoiceRecordListener(OnVoiceRecordListener onVoiceRecordListener) {
        this.onVoiceRecordListener = onVoiceRecordListener;
    }

    public void setOnSendTextListener(OnSendTextListener onSendTextListener) {
        this.onSendTextListener = onSendTextListener;
    }

    public interface OnVoiceRecordListener {
        void beforeRecord();
        void afterRecord(String pathLastAudio);
    }

    public interface OnSendTextListener {
        void sendText(String text);
    }

}
