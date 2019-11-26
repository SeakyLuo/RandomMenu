package personalprojects.seakyluo.randommenu.Models;

import android.support.annotation.Nullable;

import personalprojects.seakyluo.randommenu.Models.Tag;

public class ToggleTag extends Tag {
    public boolean visible = false;

    public ToggleTag(Tag tag, boolean visible){
        this.Name = tag.Name;
        this.Counter = tag.Counter;
        this.visible = visible;
    }

    public boolean Toggle() { return visible = !visible; }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof Tag && super.equals(obj);
    }
}
