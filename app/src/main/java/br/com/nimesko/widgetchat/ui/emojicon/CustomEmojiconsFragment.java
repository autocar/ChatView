package br.com.nimesko.widgetchat.ui.emojicon;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.rockerhieu.emojicon.EmojiconRecents;
import com.rockerhieu.emojicon.EmojiconRecentsManager;
import com.rockerhieu.emojicon.emoji.Emojicon;

import java.util.List;

import br.com.nimesko.widgetchat.ChatViewApplication;

public class CustomEmojiconsFragment extends Fragment implements ViewPager.OnPageChangeListener, EmojiconRecents {

    private CustomEmojiconsFragment.OnEmojiconBackspaceClickedListener mOnEmojiconBackspaceClickedListener;
    private int mEmojiTabLastSelectedIndex = -1;
    private View[] mEmojiTabs;
    private PagerAdapter mEmojisAdapter;
    private EmojiconRecentsManager mRecentsManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(com.rockerhieu.emojicon.R.layout.emojicons, container, false);
        final ViewPager emojisPager = (ViewPager)view.findViewById(com.rockerhieu.emojicon.R.id.emojis_pager);
        emojisPager.addOnPageChangeListener(this);
        this.mEmojisAdapter = new CustomEmojiconsFragment.EmojisPagerAdapter(this.getFragmentManager(), ChatViewApplication.loadListEmojiconGridFragment(this));
        emojisPager.setAdapter(this.mEmojisAdapter);

        this.mEmojiTabs = new View[6];
        this.mEmojiTabs[0] = view.findViewById(com.rockerhieu.emojicon.R.id.emojis_tab_0_recents);
        this.mEmojiTabs[1] = view.findViewById(com.rockerhieu.emojicon.R.id.emojis_tab_1_people);
        this.mEmojiTabs[2] = view.findViewById(com.rockerhieu.emojicon.R.id.emojis_tab_2_nature);
        this.mEmojiTabs[3] = view.findViewById(com.rockerhieu.emojicon.R.id.emojis_tab_3_objects);
        this.mEmojiTabs[4] = view.findViewById(com.rockerhieu.emojicon.R.id.emojis_tab_4_cars);
        this.mEmojiTabs[5] = view.findViewById(com.rockerhieu.emojicon.R.id.emojis_tab_5_punctuation);

        for(int page = 0, size = this.mEmojiTabs.length; page < size; ++page) {
            final int finalPage = page;
            this.mEmojiTabs[page].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    emojisPager.setCurrentItem(finalPage);
                }
            });
        }

        view.findViewById(com.rockerhieu.emojicon.R.id.emojis_backspace).setOnTouchListener(new CustomEmojiconsFragment.RepeatListener(1000, 50, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CustomEmojiconsFragment.this.mOnEmojiconBackspaceClickedListener != null) {
                    CustomEmojiconsFragment.this.mOnEmojiconBackspaceClickedListener.onEmojiconBackspaceClicked(v);
                }
            }
        }));

        this.mRecentsManager = EmojiconRecentsManager.getInstance(view.getContext());
        int page = this.mRecentsManager.getRecentPage();

        if(page == 0 && this.mRecentsManager.size() == 0) {
            page = 1;
        }

        if(page == 0) {
            this.onPageSelected(page);
        } else {
            emojisPager.setCurrentItem(page, false);
        }

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(this.getActivity() instanceof CustomEmojiconsFragment.OnEmojiconBackspaceClickedListener) {
            this.mOnEmojiconBackspaceClickedListener = (CustomEmojiconsFragment.OnEmojiconBackspaceClickedListener)this.getActivity();
        } else {
            if(!(this.getParentFragment() instanceof CustomEmojiconsFragment.OnEmojiconBackspaceClickedListener)) {
                throw new IllegalArgumentException(activity + " must implement interface " + CustomEmojiconsFragment.OnEmojiconBackspaceClickedListener.class.getSimpleName());
            }
            this.mOnEmojiconBackspaceClickedListener = (CustomEmojiconsFragment.OnEmojiconBackspaceClickedListener)this.getParentFragment();
        }
    }

    @Override
    public void onDetach() {
        this.mOnEmojiconBackspaceClickedListener = null;
        super.onDetach();
    }

    public static void input(EditText editText, Emojicon emojicon) {
        if(editText != null && emojicon != null) {
            int start = editText.getSelectionStart();
            int end = editText.getSelectionEnd();
            if(start < 0) {
                editText.append(emojicon.getEmoji());
            } else {
                editText.getText().replace(Math.min(start, end), Math.max(start, end), emojicon.getEmoji(), 0, emojicon.getEmoji().length());
            }
        }
    }

    @Override
    public void addRecentEmoji(Context context, Emojicon emojicon) {
        ViewPager emojisPager = (ViewPager) getView().findViewById(com.rockerhieu.emojicon.R.id.emojis_pager);
        CustomEmojiconRecentsGridFragment fragment = (CustomEmojiconRecentsGridFragment)this.mEmojisAdapter.instantiateItem
                (emojisPager, 0);
        fragment.addRecentEmoji(context, emojicon);
    }

    public static void backspace(EditText editText) {
        KeyEvent event = new KeyEvent(0L, 0L, 0, 67, 0, 0, 0, 0, 6);
        editText.dispatchKeyEvent(event);
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {

    }

    @Override
    public void onPageSelected(int i) {
        if(this.mEmojiTabLastSelectedIndex != i) {
            switch(i) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                    if(this.mEmojiTabLastSelectedIndex >= 0 && this.mEmojiTabLastSelectedIndex < this.mEmojiTabs.length) {
                        this.mEmojiTabs[this.mEmojiTabLastSelectedIndex].setSelected(false);
                    }
                    this.mEmojiTabs[i].setSelected(true);
                    this.mEmojiTabLastSelectedIndex = i;
                    this.mRecentsManager.setRecentPage(i);
                default:
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    public interface OnEmojiconBackspaceClickedListener {
        void onEmojiconBackspaceClicked(View var1);
    }

    public static class RepeatListener implements View.OnTouchListener {

        private Handler handler = new Handler();
        private int initialInterval;
        private final int normalInterval;
        private final View.OnClickListener clickListener;
        private View downView;

        private Runnable handlerRunnable;

        public RepeatListener(int initialInterval, int normalInterval, View.OnClickListener clickListener) {
            if(clickListener == null) {
                throw new IllegalArgumentException("null runnable");
            } else if(initialInterval >= 0 && normalInterval >= 0) {
                this.initialInterval = initialInterval;
                this.normalInterval = normalInterval;
                this.clickListener = clickListener;
            } else {
                throw new IllegalArgumentException("negative interval");
            }
            handlerRunnable = new Runnable() {
                public void run() {
                    if(RepeatListener.this.downView != null) {
                        RepeatListener.this.handler.removeCallbacksAndMessages(RepeatListener.this.downView);
                        RepeatListener.this.handler.postAtTime(this, RepeatListener.this.downView, SystemClock.uptimeMillis() + (long)RepeatListener.this.normalInterval);
                        RepeatListener.this.clickListener.onClick(RepeatListener.this.downView);
                    }
                }
            };
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch(motionEvent.getAction()) {
                case 0:
                    this.downView = view;
                    this.handler.removeCallbacks(this.handlerRunnable);
                    this.handler.postAtTime(this.handlerRunnable, this.downView, SystemClock.uptimeMillis() + (long)this.initialInterval);
                    this.clickListener.onClick(view);
                    return true;
                case 1:
                case 3:
                case 4:
                    this.handler.removeCallbacksAndMessages(this.downView);
                    this.downView = null;
                    return true;
                case 2:
                default:
                    return false;
            }
        }
    }

    private static class EmojisPagerAdapter extends FragmentStatePagerAdapter {

        private List<CustomEmojiconGridFragment> fragments;

        public EmojisPagerAdapter(FragmentManager fm, List<CustomEmojiconGridFragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int i) {
            return this.fragments.get(i);
        }

        @Override
        public int getCount() {
            return this.fragments.size();
        }

    }

}
