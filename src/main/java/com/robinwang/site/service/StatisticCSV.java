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

    private static int GONG_SI_AN_HAO = 0; //公司案号
    private static int KE_HU_AN_HAO = 2; //客户案号
    private static int JI_BEN_FA_LV_ZHUANG_TAI = 3; //基本法律状态
    private static int ZI_SHU = 4; //字数
    private static int FAN_YI_REN = 7; //翻译人
    private static int DI_YI_JIAO_DUI_REN = 8; //第一校对人
    private static int DI_ER_JIAO_DUI_REN = 9; //第二校对人
    private static int JIAN_CHA = 10; //检查

    private static String DIRECTORY = "/Users/robin/wangxp/8";

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
     * <p>
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

        Path path = new File(DIRECTORY + "/8.csv").toPath();
        List<String> content = Files.readAllLines(path);

        ArrayList<String[]> table = new ArrayList<>(100);
        for (String line : content) {
            String[] k = line.split(",", -1);
            table.add(k);
        }

        String[] title = table.get(0);
        Map<String, Integer> mapName2Index = new HashMap<>();
        for (int i = 0; i < title.length; i++) {
            mapName2Index.put(title[i].trim().replace("\uFEFF", ""), i);
        }

        GONG_SI_AN_HAO = mapName2Index.get("公司案号");
        KE_HU_AN_HAO = mapName2Index.get("客户案号");
        JI_BEN_FA_LV_ZHUANG_TAI = mapName2Index.get("基本法律状态");
        ZI_SHU = mapName2Index.get("字数");
        FAN_YI_REN = mapName2Index.get("翻译人");
        DI_YI_JIAO_DUI_REN = mapName2Index.get("第一校对人");
        DI_ER_JIAO_DUI_REN = mapName2Index.get("第二校对人");
        JIAN_CHA = mapName2Index.get("检查");

        table.remove(0);

        Set<String> people = new HashSet<>();
        people.addAll(table.stream().map(line -> line[FAN_YI_REN].replaceAll("；", "、")).flatMap(x -> Arrays.stream(x.split("、"))).collect(Collectors.toSet()));

        people.addAll(table.stream().map(line -> line[DI_YI_JIAO_DUI_REN]).flatMap(x -> Arrays.stream(x.split("、"))).collect(Collectors.toSet()));

        people.addAll(table.stream().map(line -> line[DI_ER_JIAO_DUI_REN]).flatMap(x -> Arrays.stream(x.split("、"))).collect(Collectors.toSet()));

        people.addAll(table.stream().map(line -> line[JIAN_CHA]).flatMap(x -> Arrays.stream(x.split("、"))).collect(Collectors.toSet()));

        people = people.stream().map(String::trim).filter(x -> x.length() > 0).collect(Collectors.toSet());

        people.forEach(System.out::println);

        String header = "公司案号,客户案号,基本法律状态,字数,翻译人,第一校对人,第二校对人,检查";

        List<String> totalData = new ArrayList<>();
        int index = 1;
        for (String name : people) {

            System.out.println(name);

            List<String> personData = table.stream().filter(line -> line[FAN_YI_REN].contains(name)).map(line -> line[GONG_SI_AN_HAO] + "," + line[KE_HU_AN_HAO] + "," + line[JI_BEN_FA_LV_ZHUANG_TAI] + "," + line[ZI_SHU] + "," + name + ",,,").collect(Collectors.toList());
            personData.addAll(table.stream().filter(line -> line[DI_YI_JIAO_DUI_REN].contains(name)).map(line -> line[GONG_SI_AN_HAO] + "," + line[KE_HU_AN_HAO] + "," + line[JI_BEN_FA_LV_ZHUANG_TAI] + "," + line[ZI_SHU] + ",," + name + ",,").collect(Collectors.toList()));
            personData.addAll(table.stream().filter(line -> line[DI_ER_JIAO_DUI_REN].contains(name)).map(line -> line[GONG_SI_AN_HAO] + "," + line[KE_HU_AN_HAO] + "," + line[JI_BEN_FA_LV_ZHUANG_TAI] + "," + line[ZI_SHU] + ",,," + name + ",").collect(Collectors.toList()));
            personData.addAll(table.stream().filter(line -> line[JIAN_CHA].contains(name)).map(line -> line[GONG_SI_AN_HAO] + "," + line[KE_HU_AN_HAO] + "," + line[JI_BEN_FA_LV_ZHUANG_TAI] + "," + line[ZI_SHU] + ",,,," + name).collect(Collectors.toList()));

//            personData.forEach(System.out::println);

            int tran = table.stream().filter(line -> line[FAN_YI_REN].contains(name)).filter(line -> line[ZI_SHU].trim().length() > 0).map(line -> Integer.parseInt(line[ZI_SHU].trim())).mapToInt(Integer::intValue).sum();
            int v1 = table.stream().filter(line -> line[DI_YI_JIAO_DUI_REN].contains(name)).filter(line -> line[ZI_SHU].trim().length() > 0).map(line -> Integer.parseInt(line[ZI_SHU].trim())).mapToInt(Integer::intValue).sum();
            int v2 = table.stream().filter(line -> line[DI_ER_JIAO_DUI_REN].contains(name)).filter(line -> line[ZI_SHU].trim().length() > 0).map(line -> Integer.parseInt(line[ZI_SHU].trim())).mapToInt(Integer::intValue).sum();
            int check = table.stream().filter(line -> line[JIAN_CHA].contains(name)).filter(line -> line[ZI_SHU].trim().length() > 0).map(line -> Integer.parseInt(line[ZI_SHU].trim())).mapToInt(Integer::intValue).sum();

//            table.stream().filter(line -> line[JIAN_CHA].contains(name)).filter(line -> line[ZI_SHU].trim().length() > 0).map(line -> Integer.parseInt(line[ZI_SHU].trim())).forEach(System.out::println);

            String outPath = DIRECTORY + "/" + name + ".csv";

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


            // add to total list
            StringBuilder sb = new StringBuilder();
            sb.append(index).append(",").append(name).append(",").append(tran).append(",").append(v1 + v2).append(",").append(check).append(",").append(",").append(",").append(tran + v1 + v2);
            totalData.add(sb.toString());
            index += 1;
        }

        String header_total = "序号,姓名,翻译字数,校对字数,检查字数,OA,撰写,翻译/校对合计";
        String outPath = DIRECTORY + "/total.csv";

        FileOutputStream fos = new FileOutputStream(outPath);
        wf(fos, header_total);
        for (String line : totalData) {
            wf(fos, line);
        }
        fos.close();
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
