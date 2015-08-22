package br.com.nimesko.widgetchat.ui.emojicon;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;

import com.rockerhieu.emojicon.EmojiconRecents;
import com.rockerhieu.emojicon.EmojiconRecentsManager;
import com.rockerhieu.emojicon.emoji.Emojicon;

public class CustomEmojiconRecentsGridFragment extends CustomEmojiconGridFragment implements EmojiconRecents {

    private CustomEmojiAdapter mAdapter;
    private boolean mUseSystemDefault = false;
    private static final String USE_SYSTEM_DEFAULT_KEY = "useSystemDefaults";

    public static CustomEmojiconRecentsGridFragment newInstance() {
        return newInstance(false);
    }

    public static CustomEmojiconRecentsGridFragment newInstance(boolean useSystemDefault) {
        CustomEmojiconRecentsGridFragment fragment = new CustomEmojiconRecentsGridFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(USE_SYSTEM_DEFAULT_KEY, useSystemDefault);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mUseSystemDefault = this.getArguments() != null && this.getArguments().getBoolean("useSystemDefaults");
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        EmojiconRecentsManager recents = EmojiconRecentsManager.getInstance(view.getContext());
        this.mAdapter = new CustomEmojiAdapter(view.getContext(), recents, this.mUseSystemDefault);
        GridView gridView = (GridView)view.findViewById(com.rockerhieu.emojicon.R.id.Emoji_GridView);
        gridView.setAdapter(this.mAdapter);
        gridView.setOnItemClickListener(this);
    }

    public void onDestroyView() {
        super.onDestroyView();
        this.mAdapter = null;
    }

    public void addRecentEmoji(Context context, Emojicon emojicon) {
        EmojiconRecentsManager recents = EmojiconRecentsManager.getInstance(context);
        recents.push(emojicon);
        if(this.mAdapter != null) {
            this.mAdapter.notifyDataSetChanged();
        }
    }

}
