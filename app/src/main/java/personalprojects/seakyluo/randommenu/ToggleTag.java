package personalprojects.seakyluo.randommenu;

import android.support.annotation.Nullable;

public class ToggleTag extends Tag{
    public String name;
    public boolean visible = false;

    public ToggleTag(Tag tag, boolean visible){
        this.name = tag.getName();
        this.visible = visible;
    }

    public Tag ToTag() { return new Tag(this.name); }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof ToggleTag)) return false;
        return ToTag().equals(((ToggleTag)obj).ToTag());
    }
}
