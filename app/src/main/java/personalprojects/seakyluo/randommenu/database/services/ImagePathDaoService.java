package personalprojects.seakyluo.randommenu.database.services;

import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import personalprojects.seakyluo.randommenu.database.AppDatabase;
import personalprojects.seakyluo.randommenu.database.dao.ImagePathDAO;
import personalprojects.seakyluo.randommenu.database.mappers.ImagePathMapper;
import personalprojects.seakyluo.randommenu.models.SelfMadeFood;

public class ImagePathDaoService {

    public static void insert(long itemId, String itemType, List<String> images){
        if (CollectionUtils.isEmpty(images)){
            return;
        }
        ImagePathMapper mapper = AppDatabase.instance.imagePathMapper();
        List<ImagePathDAO> daoList = new ArrayList<>();
        for (int i = 0; i < images.size(); i++){
            String image = images.get(i);
            ImagePathDAO dao = convert(itemId, itemType, image, i);
            daoList.add(dao);
        }
        mapper.insert(daoList);
    }

    public static void deleteByItem(long itemId, String itemType){
        ImagePathMapper mapper = AppDatabase.instance.imagePathMapper();
        mapper.deleteByItem(itemId, itemType);
    }

    public static void deleteByItems(List<Long> itemIds, String itemType){
        ImagePathMapper mapper = AppDatabase.instance.imagePathMapper();
        mapper.deleteByItems(itemIds, itemType);
    }

    public static List<String> selectByItem(long itemId, String itemType){
        ImagePathMapper mapper = AppDatabase.instance.imagePathMapper();
        return mapper.selectByItem(itemId, itemType);
    }

    public static Map<Long, List<String>> selectByItems(List<Long> itemIds, String itemType){
        ImagePathMapper mapper = AppDatabase.instance.imagePathMapper();
        List<ImagePathDAO> daoList = mapper.selectByItems(itemIds, itemType);
        Map<Long, List<ImagePathDAO>> map = daoList.stream().collect(Collectors.groupingBy(ImagePathDAO::getItemId));
        return map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                e -> e.getValue().stream()
                        .sorted(Comparator.comparing(ImagePathDAO::getOrderNum))
                        .map(ImagePathDAO::getPath)
                        .collect(Collectors.toList())));
    }

    public static void clearNonExistent(List<String> existing){
        ImagePathMapper mapper = AppDatabase.instance.imagePathMapper();
        mapper.clearNonExistent(existing);
    }

    public static List<String> selectPaths(){
        ImagePathMapper mapper = AppDatabase.instance.imagePathMapper();
        return mapper.selectPaths();
    }

    private static ImagePathDAO convert(long itemId, String itemType, String path, int order){
        ImagePathDAO dao = new ImagePathDAO();
        dao.setItemId(itemId);
        dao.setItemType(itemType);
        dao.setPath(path);
        dao.setOrderNum(order);
        return dao;
    }
}
