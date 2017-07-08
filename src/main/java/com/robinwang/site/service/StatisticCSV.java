package com.robinwang.site.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

    public void readCSV() throws Exception {
        LOG.info("enter readCSV");

        Path path = new File("/Users/robin/wangxp/5.csv").toPath();
        List<String> content = Files.readAllLines(path);

        ArrayList<String[]> table = new ArrayList<>(100);
        for (String line : content) {
            String[] k = line.split(",", -1);
//            System.out.println(line);
            table.add(k);
//            System.out.println(k.length);
        }

        table.remove(0);

        Set<String> people = new HashSet<>();
        people.addAll(table.stream().map(line -> line[8]).flatMap(x -> Arrays.stream(x.split("、"))).collect(Collectors.toSet()));
        people.addAll(table.stream().map(line -> line[9]).flatMap(x -> Arrays.stream(x.split("、"))).collect(Collectors.toSet()));
        people.addAll(table.stream().map(line -> line[10]).flatMap(x -> Arrays.stream(x.split("、"))).collect(Collectors.toSet()));
        people.addAll(table.stream().map(line -> line[11]).flatMap(x -> Arrays.stream(x.split("、"))).collect(Collectors.toSet()));

        people = people.stream().filter(x -> x.trim().length() > 0).collect(Collectors.toSet());
        people.forEach(System.out::println);

        String header = "公司案号,客户案号,基本法律状态,字数,翻译人,第一校对人,第二校对人,检查";
        for (String name : people) {

            List<String> personData = table.stream().filter(line -> line[8].contains(name)).map(line -> line[0] + "," + line[2] + "," + line[3] + "," + line[7] + "," + name + ",,,").collect(Collectors.toList());
            personData.addAll(table.stream().filter(line -> line[9].contains(name)).map(line -> line[0] + "," + line[2] + "," + line[3] + "," + line[7] + ",," + name + ",,").collect(Collectors.toList()));
            personData.addAll(table.stream().filter(line -> line[10].contains(name)).map(line -> line[0] + "," + line[2] + "," + line[3] + "," + line[7] + ",,," + name + ",").collect(Collectors.toList()));
            personData.addAll(table.stream().filter(line -> line[11].contains(name)).map(line -> line[0] + "," + line[2] + "," + line[3] + "," + line[7] + ",,,," + name).collect(Collectors.toList()));

//            personData.forEach(System.out::println);

            int tran = table.stream().filter(line -> name.equals(line[8])).filter(line -> line[7].trim().length() > 0).map(line -> Integer.parseInt(line[7])).mapToInt(Integer::intValue).sum();
            int v1 = table.stream().filter(line -> name.equals(line[9])).filter(line -> line[7].trim().length() > 0).map(line -> Integer.parseInt(line[7])).mapToInt(Integer::intValue).sum();
            int v2 = table.stream().filter(line -> name.equals(line[10])).filter(line -> line[7].trim().length() > 0).map(line -> Integer.parseInt(line[7])).mapToInt(Integer::intValue).sum();
            int check = table.stream().filter(line -> name.equals(line[11])).filter(line -> line[7].trim().length() > 0).map(line -> Integer.parseInt(line[7])).mapToInt(Integer::intValue).sum();

            String outPath = "/Users/robin/wangxp/" + name + ".csv";

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
