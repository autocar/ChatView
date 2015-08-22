package br.com.nimesko.widgetchat.ui.emojicon;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;

import com.rockerhieu.emojicon.EmojiconRecents;
import com.rockerhieu.emojicon.EmojiconRecentsManager;
import com.rockerhieu.emojicon.emoji.Emojicon;

public class CustomEmojiconRecentsGridFragment extends CustomEmojiconGridFragment implements EmojiconRecents {

    private CustomEmojiAdapter customEmojiAdapter;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        EmojiconRecentsManager recents = EmojiconRecentsManager.getInstance(view.getContext());
        this.customEmojiAdapter = new CustomEmojiAdapter(view.getContext(), recents, false);
        GridView gridView = (GridView)view.findViewById(com.rockerhieu.emojicon.R.id.Emoji_GridView);
        gridView.setAdapter(this.customEmojiAdapter);
        gridView.setOnItemClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.customEmojiAdapter = null;
    }

    @Override
    public void addRecentEmoji(Context context, Emojicon emojicon) {
        EmojiconRecentsManager recents = EmojiconRecentsManager.getInstance(context);
        recents.push(emojicon);
        if (this.customEmojiAdapter != null) {
            this.customEmojiAdapter.notifyDataSetChanged();
        }
    }

}
