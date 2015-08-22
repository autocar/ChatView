package br.com.nimesko.widgetchat.ui.emojicon;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.rockerhieu.emojicon.EmojiconRecents;
import com.rockerhieu.emojicon.emoji.Emojicon;
import com.rockerhieu.emojicon.emoji.People;

import java.util.Arrays;

public class CustomEmojiconGridFragment extends Fragment implements AdapterView.OnItemClickListener {

    private static final String USE_SYSTEM_DEFAULT_KEY = "useSystemDefaults";
    private CustomEmojiconGridFragment.OnEmojiconClickedListener mOnEmojiconClickedListener;
    private EmojiconRecents mRecents;
    private Emojicon[] mData;
    private boolean mUseSystemDefault = false;

    public static CustomEmojiconGridFragment newInstance(Emojicon[] emojicons, EmojiconRecents recents) {
        return newInstance(emojicons, recents, false);
    }

    public static CustomEmojiconGridFragment newInstance(Emojicon[] emojicons, EmojiconRecents recents, boolean useSystemDefault) {
        CustomEmojiconGridFragment emojiGridFragment = new CustomEmojiconGridFragment();
        Bundle args = new Bundle();
        args.putSerializable("emojicons", emojicons);
        args.putBoolean(USE_SYSTEM_DEFAULT_KEY, useSystemDefault);
        emojiGridFragment.setArguments(args);
        emojiGridFragment.setRecents(recents);
        return emojiGridFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(com.rockerhieu.emojicon.R.layout.emojicon_grid, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        GridView gridView = (GridView)view.findViewById(com.rockerhieu.emojicon.R.id.Emoji_GridView);
        Bundle bundle = this.getArguments();
        if(bundle == null) {
            this.mData = People.DATA;
            this.mUseSystemDefault = false;
        } else {
            Object[] o = (Object[])(this.getArguments().getSerializable("emojicons"));
            this.mData = Arrays.asList(o).toArray(new Emojicon[o.length]);
            this.mUseSystemDefault = bundle.getBoolean("useSystemDefaults");
        }

        gridView.setAdapter(new CustomEmojiAdapter(view.getContext(), this.mData, this.mUseSystemDefault));
        gridView.setOnItemClickListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("emojicons", this.mData);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof CustomEmojiconGridFragment.OnEmojiconClickedListener) {
            this.mOnEmojiconClickedListener = (CustomEmojiconGridFragment.OnEmojiconClickedListener)activity;
        } else {
            if(!(this.getParentFragment() instanceof CustomEmojiconGridFragment.OnEmojiconClickedListener)) {
                throw new IllegalArgumentException(activity + " must implement interface " + CustomEmojiconGridFragment.OnEmojiconClickedListener.class.getSimpleName());
            }
            this.mOnEmojiconClickedListener = (CustomEmojiconGridFragment.OnEmojiconClickedListener)this.getParentFragment();
        }
    }

    @Override
    public void onDetach() {
        this.mOnEmojiconClickedListener = null;
        super.onDetach();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(this.mOnEmojiconClickedListener != null) {
            this.mOnEmojiconClickedListener.onEmojiconClicked((Emojicon)parent.getItemAtPosition(position));
        }
        if(this.mRecents != null) {
            this.mRecents.addRecentEmoji(view.getContext(), (Emojicon)parent.getItemAtPosition(position));
        }
    }

    private void setRecents(EmojiconRecents recents) {
        this.mRecents = recents;
    }

    public interface OnEmojiconClickedListener {
        void onEmojiconClicked(Emojicon var1);
    }

}
