package br.com.nimesko.widgetchat.ui.emojicon;

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

public class CustomEmojiconGridFragment extends Fragment implements AdapterView.OnItemClickListener {

    private CustomEmojiconGridFragment.OnEmojiconClickedListener onEmojiconClickedListener;
    private EmojiconRecents emojiconRecents;
    private Emojicon[] emojicons;

    public static CustomEmojiconGridFragment newInstance(Emojicon[] emojicons, EmojiconRecents recents) {
        CustomEmojiconGridFragment emojiGridFragment = new CustomEmojiconGridFragment();
        Bundle args = new Bundle();
        args.putSerializable("emojicons", emojicons);
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
        Bundle bundle = getArguments();
        if(bundle == null) {
            this.emojicons = People.DATA;
        } else {
            Object[] o = (Object[]) (bundle.getSerializable("emojicons"));
            this.emojicons = (Emojicon[]) o;
        }
        gridView.setAdapter(new CustomEmojiAdapter(view.getContext(), this.emojicons));
        gridView.setOnItemClickListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("emojicons", this.emojicons);
    }

    @Override
    public void onDetach() {
        this.onEmojiconClickedListener = null;
        super.onDetach();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (this.onEmojiconClickedListener != null) {
            this.onEmojiconClickedListener.onEmojiconClicked((Emojicon) parent.getItemAtPosition(position));
        }
        if (this.emojiconRecents != null) {
            this.emojiconRecents.addRecentEmoji(view.getContext(), (Emojicon) parent.getItemAtPosition(position));
        }
    }

    private void setRecents(EmojiconRecents recents) {
        this.emojiconRecents = recents;
    }

    public void setOnEmojiconClickedListener(OnEmojiconClickedListener onEmojiconClickedListener) {
        this.onEmojiconClickedListener = onEmojiconClickedListener;
    }

    public interface OnEmojiconClickedListener {
        void onEmojiconClicked(Emojicon var1);
    }

}
