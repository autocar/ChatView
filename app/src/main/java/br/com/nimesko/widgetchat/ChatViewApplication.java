package br.com.nimesko.widgetchat;

import android.app.Application;

import com.rockerhieu.emojicon.EmojiconRecents;
import com.rockerhieu.emojicon.emoji.Nature;
import com.rockerhieu.emojicon.emoji.Objects;
import com.rockerhieu.emojicon.emoji.People;
import com.rockerhieu.emojicon.emoji.Places;
import com.rockerhieu.emojicon.emoji.Symbols;

import java.util.Arrays;
import java.util.List;

import br.com.nimesko.widgetchat.ui.emojicon.CustomEmojiconGridFragment;
import br.com.nimesko.widgetchat.ui.emojicon.CustomEmojiconRecentsGridFragment;

public class ChatViewApplication extends Application {

    private static List<CustomEmojiconGridFragment> customEmojiconGridFragments;

    public static List<CustomEmojiconGridFragment> loadListEmojiconGridFragment(EmojiconRecents emojiconRecents) {
        if(customEmojiconGridFragments == null) {
            customEmojiconGridFragments = Arrays.asList(
                    new CustomEmojiconRecentsGridFragment(),
                    CustomEmojiconGridFragment.newInstance(People.DATA, emojiconRecents),
                    CustomEmojiconGridFragment.newInstance(Nature.DATA, emojiconRecents),
                    CustomEmojiconGridFragment.newInstance(Objects.DATA, emojiconRecents),
                    CustomEmojiconGridFragment.newInstance(Places.DATA, emojiconRecents),
                    CustomEmojiconGridFragment.newInstance(Symbols.DATA, emojiconRecents)
            );
        }
        return customEmojiconGridFragments;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

}
