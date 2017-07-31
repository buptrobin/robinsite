package com.robinwang.site.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by robin on 2017/7/8.
 */
@Service
public class StatisticCSV {
    private static Logger LOG = LoggerFactory.getLogger(StatisticCSV.class);
    private static String newLine = System.getProperty("line.separator");

    private static final int GONG_SI_AN_HAO = 0; //公司案号
    private static final int KE_HU_AN_HAO = 2; //客户案号
    private static final int JI_BEN_FA_LV_ZHUANG_TAI = 3; //基本法律状态
    private static final int ZI_SHU = 4; //字数
    private static final int FAN_YI_REN = 7; //翻译人
    private static final int DI_YI_JIAO_DUI_REN = 8; //第一校对人
    private static final int DI_ER_JIAO_DUI_REN = 9; //第二校对人
    private static final int JIAN_CHA = 10; //检查

    /**
     * May:
     * 0: 公司案号
     * 2: 客户案号
     * 3: 基本法律状态
     * 7: 字数
     * 8: 翻译人
     * 9: 第一校对人
     * 10: 第二校对人
     * 11: 检查
     *
     * June:
     * 0: 公司案号
     * 2: 客户案号
     * 3: 基本法律状态
     * 4: 字数
     * 7: 翻译人
     * 8: 第一校对人
     * 9: 第二校对人
     * 10: 检查
     */
    public void readCSV() throws Exception {
        LOG.info("enter readCSV");

        Path path = new File("/Users/robin/wangxp/6/6.csv").toPath();
        List<String> content = Files.readAllLines(path);

        ArrayList<String[]> table = new ArrayList<>(100);
        for (String line : content) {
            String[] k = line.split(",", -1);
            table.add(k);
        }

        table.remove(0);

        Set<String> people = new HashSet<>();
        people.addAll(table.stream().map(line -> line[FAN_YI_REN]).flatMap(x -> Arrays.stream(x.split("、"))).collect(Collectors.toSet()));
        people.addAll(table.stream().map(line -> line[DI_YI_JIAO_DUI_REN]).flatMap(x -> Arrays.stream(x.split("、"))).collect(Collectors.toSet()));
        people.addAll(table.stream().map(line -> line[DI_ER_JIAO_DUI_REN]).flatMap(x -> Arrays.stream(x.split("、"))).collect(Collectors.toSet()));
        people.addAll(table.stream().map(line -> line[JIAN_CHA]).flatMap(x -> Arrays.stream(x.split("、"))).collect(Collectors.toSet()));

        people = people.stream().filter(x -> x.trim().length() > 0).collect(Collectors.toSet());
        people.forEach(System.out::println);

        String header = "公司案号,客户案号,基本法律状态,字数,翻译人,第一校对人,第二校对人,检查";
        for (String name : people) {

            List<String> personData = table.stream().filter(line -> line[FAN_YI_REN].contains(name)).map(line -> line[GONG_SI_AN_HAO] + "," + line[KE_HU_AN_HAO] + "," + line[JI_BEN_FA_LV_ZHUANG_TAI] + "," + line[ZI_SHU] + "," + name + ",,,").collect(Collectors.toList());
            personData.addAll(table.stream().filter(line -> line[DI_YI_JIAO_DUI_REN].contains(name)).map(line -> line[GONG_SI_AN_HAO] + "," + line[KE_HU_AN_HAO] + "," + line[JI_BEN_FA_LV_ZHUANG_TAI] + "," + line[ZI_SHU] + ",," + name + ",,").collect(Collectors.toList()));
            personData.addAll(table.stream().filter(line -> line[DI_ER_JIAO_DUI_REN].contains(name)).map(line -> line[GONG_SI_AN_HAO] + "," + line[KE_HU_AN_HAO] + "," + line[JI_BEN_FA_LV_ZHUANG_TAI] + "," + line[ZI_SHU] + ",,," + name + ",").collect(Collectors.toList()));
            personData.addAll(table.stream().filter(line -> line[JIAN_CHA].contains(name)).map(line -> line[GONG_SI_AN_HAO] + "," + line[KE_HU_AN_HAO] + "," + line[JI_BEN_FA_LV_ZHUANG_TAI] + "," + line[ZI_SHU] + ",,,," + name).collect(Collectors.toList()));

//            personData.forEach(System.out::println);

            int tran = table.stream().filter(line -> name.equals(line[FAN_YI_REN])).filter(line -> line[ZI_SHU].trim().length() > 0).map(line -> Integer.parseInt(line[ZI_SHU])).mapToInt(Integer::intValue).sum();
            int v1 = table.stream().filter(line -> name.equals(line[DI_YI_JIAO_DUI_REN])).filter(line -> line[ZI_SHU].trim().length() > 0).map(line -> Integer.parseInt(line[ZI_SHU])).mapToInt(Integer::intValue).sum();
            int v2 = table.stream().filter(line -> name.equals(line[DI_ER_JIAO_DUI_REN])).filter(line -> line[ZI_SHU].trim().length() > 0).map(line -> Integer.parseInt(line[ZI_SHU])).mapToInt(Integer::intValue).sum();
            int check = table.stream().filter(line -> name.equals(line[JIAN_CHA])).filter(line -> line[ZI_SHU].trim().length() > 0).map(line -> Integer.parseInt(line[ZI_SHU])).mapToInt(Integer::intValue).sum();

            String outPath = "/Users/robin/wangxp/6/" + name + ".csv";

            FileOutputStream fos = new FileOutputStream(outPath);
            wf(fos, header);
            for (String line : personData) {
                wf(fos, line);
            }

            String sTran = "翻译字数," + tran + newLine;
            String sRevision = "校对字数," + (v1 + v2) + newLine;
            String sCheck = "检查字数," + check + newLine;

            wf(fos, sTran);
            wf(fos, sRevision);
            wf(fos, sCheck);

            fos.close();
//            fw.close();

        }

        // stat
//        for(String name: people)


    }

    private void wf(FileOutputStream fos, String s) throws Exception {
        if (!s.endsWith(newLine)) s = s + newLine;
        fos.write(s.getBytes("UTF-8"));
    }

    private int sumList(List<String> list) {
        int ret = 0;
        for (String k : list) {
            ret += Integer.parseInt(k);
        }

        return ret;
    }
}
