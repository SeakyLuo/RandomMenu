package personalprojects.seakyluo.randommenu.models;

public class TempFood {

    public String Name;
    public AList<String> Images = new AList<>();
    private AList<Tag> Tags = new AList<>();
    public String Note = "";
    public boolean Favorite = false;
    private String Cover = "";
    private Long DateAdded = 0L;
    public int HideCount = 0;

    public SelfFood convert(){
        SelfFood dst = new SelfFood();
        dst.setName(Name);
        dst.setImages(Images);
        dst.setTags(Tags);
        dst.setNote(Note);
        dst.setFavorite(Favorite);
        dst.setCover(Cover);
        dst.setDateAdded(DateAdded);
        dst.setHideCount(HideCount);
        return dst;
    }

}
