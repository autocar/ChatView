package br.com.nimesko.widgetchat.ui.emojicon;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.rockerhieu.emojicon.EmojiconRecents;
import com.rockerhieu.emojicon.EmojiconRecentsManager;
import com.rockerhieu.emojicon.emoji.Emojicon;

import java.util.List;

import br.com.nimesko.widgetchat.ChatViewApplication;

public class CustomEmojiconsFragment extends Fragment implements ViewPager.OnPageChangeListener, EmojiconRecents {

    private CustomEmojiconGridFragment.OnEmojiconClickedListener onEmojiconClickedListener;
    private CustomEmojiconsFragment.OnEmojiconBackspaceClickedListener onEmojiconBackspaceClickedListener;
    private int mEmojiTabLastSelectedIndex = -1;
    private View[] mEmojiTabs;
    private PagerAdapter mEmojisAdapter;
    private EmojiconRecentsManager mRecentsManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(com.rockerhieu.emojicon.R.layout.emojicons, container, false);
        final ViewPager emojisPager = (ViewPager)view.findViewById(com.rockerhieu.emojicon.R.id.emojis_pager);
        emojisPager.addOnPageChangeListener(this);
        this.mEmojisAdapter = new CustomEmojiconsFragment.EmojisPagerAdapter(this.getFragmentManager(), ChatViewApplication.loadListEmojiconGridFragment(this), onEmojiconClickedListener);
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
                if (CustomEmojiconsFragment.this.onEmojiconBackspaceClickedListener != null) {
                    CustomEmojiconsFragment.this.onEmojiconBackspaceClickedListener.onEmojiconBackspaceClicked(v);
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
    public void onDetach() {
        this.onEmojiconBackspaceClickedListener = null;
        super.onDetach();
    }

    public void setOnEmojiconBackspaceClickedListener(OnEmojiconBackspaceClickedListener onEmojiconBackspaceClickedListener) {
        this.onEmojiconBackspaceClickedListener = onEmojiconBackspaceClickedListener;
    }

    @Override
    public void addRecentEmoji(Context context, Emojicon emojicon) {
        ViewPager emojisPager = (ViewPager) getView().findViewById(com.rockerhieu.emojicon.R.id.emojis_pager);
        CustomEmojiconRecentsGridFragment fragment = (CustomEmojiconRecentsGridFragment) mEmojisAdapter.instantiateItem(emojisPager, 0);
        fragment.addRecentEmoji(context, emojicon);
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

    public void setOnEmojiconClickedListener(CustomEmojiconGridFragment.OnEmojiconClickedListener onEmojiconClickedListener) {
        this.onEmojiconClickedListener = onEmojiconClickedListener;
    }

    public interface OnEmojiconBackspaceClickedListener {
        void onEmojiconBackspaceClicked(View var1);
    }

    public static class RepeatListener implements View.OnTouchListener {

        private final int normalInterval;
        private final View.OnClickListener clickListener;
        private Handler handler;
        private int initialInterval;
        private View downView;

        private Runnable handlerRunnable;

        public RepeatListener(int initialInterval, int normalInterval, View.OnClickListener clickListener) {
            if(clickListener == null) {
                throw new IllegalArgumentException("Click Listener is null!");
            } else if(initialInterval >= 0 && normalInterval >= 0) {
                this.initialInterval = initialInterval;
                this.normalInterval = normalInterval;
                this.clickListener = clickListener;
                handler = new Handler();
                handlerRunnable = new Runnable() {
                    public void run() {
                        if (RepeatListener.this.downView != null) {
                            RepeatListener.this.handler.removeCallbacksAndMessages(RepeatListener.this.downView);
                            RepeatListener.this.handler.postAtTime(this, RepeatListener.this.downView, SystemClock.uptimeMillis() + (long) RepeatListener.this.normalInterval);
                            RepeatListener.this.clickListener.onClick(RepeatListener.this.downView);
                        }
                    }
                };
            } else {
                throw new IllegalArgumentException("Negative interval!");
            }
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
        private CustomEmojiconGridFragment.OnEmojiconClickedListener onEmojiconClickedListener;

        public EmojisPagerAdapter(FragmentManager fragmentManager, List<CustomEmojiconGridFragment> fragments, CustomEmojiconGridFragment.OnEmojiconClickedListener onEmojiconClickedListener) {
            super(fragmentManager);
            this.fragments = fragments;
            this.onEmojiconClickedListener = onEmojiconClickedListener;
        }

        @Override
        public Fragment getItem(int position) {
            CustomEmojiconGridFragment customEmojiconGridFragment = this.fragments.get(position);
            customEmojiconGridFragment.setOnEmojiconClickedListener(onEmojiconClickedListener);
            return customEmojiconGridFragment;
        }

        @Override
        public int getCount() {
            return this.fragments.size();
        }

    }

}
