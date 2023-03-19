package personalprojects.seakyluo.randommenu.database.services;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import personalprojects.seakyluo.randommenu.database.AppDatabase;
import personalprojects.seakyluo.randommenu.database.dao.TagMapEntryDAO;
import personalprojects.seakyluo.randommenu.database.mappers.TagMapEntryMapper;
import personalprojects.seakyluo.randommenu.models.Tag;
import personalprojects.seakyluo.randommenu.models.TagMapEntry;

public class AutoTagMapperDaoService {

    private static final String TAG_JOINER = ",";

    public static void insert(TagMapEntry tm){
        TagMapEntryMapper mapper = AppDatabase.instance.tagMapEntryMapper();
        Long id = mapper.insert(convert(tm));
        tm.setId(id);
    }

    public static void update(TagMapEntry tm){
        TagMapEntryMapper mapper = AppDatabase.instance.tagMapEntryMapper();
        mapper.update(convert(tm));
    }

    public static void delete(TagMapEntry tm){
        TagMapEntryMapper mapper = AppDatabase.instance.tagMapEntryMapper();
        mapper.delete(convert(tm));
    }

    public static List<TagMapEntry> selectAll(){
        TagMapEntryMapper mapper = AppDatabase.instance.tagMapEntryMapper();
        return mapper.selectAll().stream().map(AutoTagMapperDaoService::convert).collect(Collectors.toList());
    }

    public static TagMapEntry selectByKeyword(String keyword){
        TagMapEntryMapper mapper = AppDatabase.instance.tagMapEntryMapper();
        return convert(mapper.selectByKeyword(keyword));
    }

    private static TagMapEntry convert(TagMapEntryDAO src){
        if (src == null){
            return null;
        }
        TagMapEntry dst = new TagMapEntry();
        dst.setId(src.getId());
        dst.setKeyword(src.getKeyword());
        dst.setTags(Arrays.stream(src.getTags().split(TAG_JOINER)).map(Tag::new).collect(Collectors.toList()));
        return dst;
    }

    private static TagMapEntryDAO convert(TagMapEntry src){
        if (src == null){
            return null;
        }
        TagMapEntryDAO dst = new TagMapEntryDAO();
        dst.setId(src.getId());
        dst.setKeyword(src.getKeyword());
        dst.setTags(src.getTags().stream().map(Tag::getName).collect(Collectors.joining(TAG_JOINER)));
        return dst;
    }
}
