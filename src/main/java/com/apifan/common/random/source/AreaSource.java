package com.apifan.common.random.source;

import com.apifan.common.random.entity.Area;
import com.apifan.common.random.util.ResourceUtils;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 地区数据源
 *
 * @author yin
 */
public class AreaSource {
    private static final Logger logger = LoggerFactory.getLogger(AreaSource.class);

    /**
     * 地区列表
     */
    private List<Area> areaList = new ArrayList<>();

    /**
     * 道路名称中常见的方向
     */
    private List<String> directionList = Lists.newArrayList("东", "西", "南", "北", "中");

    /**
     * 中国大陆常见小区名称
     */
    private List<String> communityNameList = new ArrayList<>();

    /**
     * 中国大陆常见小区名称后缀
     */
    private List<String> communitySuffixList = new ArrayList<>();

    /**
     * 地名常用词
     */
    private List<String> addressWordList = new ArrayList<>();

    /**
     * 乡镇
     */
    private List<String> townSuffixList = Lists.newArrayList("乡", "镇");

    private static final AreaSource instance = new AreaSource();

    private AreaSource() {
        try {
            List<String> areaLines = ResourceUtils.readLines("area.csv");
            if (CollectionUtils.isNotEmpty(areaLines)) {
                areaLines.forEach(i -> {
                    if (StringUtils.isEmpty(i)) {
                        return;
                    }
                    List<String> row = Splitter.on(",").splitToList(i);
                    Area area = new Area();
                    area.setProvince(row.get(0));
                    area.setCity(row.get(1));
                    area.setCounty(row.get(2));
                    area.setZipCode(row.get(3));
                    areaList.add(area);
                });
            }
            communityNameList = ResourceUtils.readLines("community-name.txt");
            communitySuffixList = ResourceUtils.readLines("community-suffix.txt");
            addressWordList = ResourceUtils.readLines("address-word-cn.txt");
        } catch (Exception e) {
            logger.error("初始化数据异常", e);
        }
    }

    /**
     * 获取唯一实例
     *
     * @return 实例
     */
    public static AreaSource getInstance() {
        return instance;
    }

    /**
     * 获取随机的地区信息
     *
     * @return 随机的地区信息
     */
    public Area nextArea() {
        return ResourceUtils.getRandomElement(areaList);
    }

    /**
     * 随机省级行政区名称
     *
     * @return 随机省级行政区名称
     */
    public String randomProvince() {
        return nextArea().getProvince();
    }

    /**
     * 随机城市名称
     *
     * @param separator 分隔符
     * @return 随机城市名称
     */
    public String randomCity(String separator) {
        Area area = nextArea();
        return area.getProvince() + Objects.toString(separator, "") + area.getCity();
    }

    /**
     * 随机邮编
     *
     * @return 随机邮编
     */
    public String randomZipCode() {
        return nextArea().getZipCode();
    }

    /**
     * 获取随机的详细地址
     *
     * @return 随机的详细地址
     */
    public String randomAddress() {
        Area area = nextArea();
        String prefix = area.getProvince() + area.getCity() + Objects.toString(area.getCounty(), "");
        if (prefix.endsWith("县") || prefix.endsWith("旗")) {
            //乡村地址
            String town = ResourceUtils.getRandomString(addressWordList, 2) + ResourceUtils.getRandomElement(townSuffixList);
            String village = ResourceUtils.getRandomString(addressWordList, 2) + "村";
            String group = ResourceUtils.getRandomString(addressWordList, 2) + "组";
            return prefix + town + village + group + RandomUtils.nextInt(1, 100) + "号";
        } else {
            //城镇地址
            String road = ResourceUtils.getRandomString(addressWordList, 2) + ResourceUtils.getRandomElement(directionList);
            String community = ResourceUtils.getRandomElement(communityNameList) + ResourceUtils.getRandomElement(communitySuffixList);
            String building = RandomUtils.nextInt(1, 20) + "栋";
            String unit = RandomUtils.nextInt(1, 5) + "单元";
            String room = String.format("%02d", RandomUtils.nextInt(1, 31)) + String.format("%02d", RandomUtils.nextInt(1, 5)) + "房";
            return prefix + road + "路" + RandomUtils.nextInt(1, 1000) + "号" + community + building + unit + room;
        }
    }

    /**
     * 随机纬度(中国)
     *
     * @return 随机纬度
     */
    public double randomLatitude() {
        return NumberSource.getInstance().randomDouble(3.86D, 53.55D);
    }

    /**
     * 随机经度(中国)
     *
     * @return 随机经度
     */
    public double randomLongitude() {
        return NumberSource.getInstance().randomDouble(73.66D, 135.05D);
    }
}
