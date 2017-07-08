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

    public void readCSV() throws Exception, IOException {
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

        Set<String> people1 = table.stream().map(line -> line[8]).flatMap(x -> Arrays.stream(x.split("、"))).collect(Collectors.toSet());
        Set<String> people2 = table.stream().map(line -> line[9]).collect(Collectors.toSet());
        Set<String> people3 = table.stream().map(line -> line[10]).collect(Collectors.toSet());
        Set<String> people4 = table.stream().map(line -> line[11]).collect(Collectors.toSet());


        Set<String> people = new HashSet<>();
        people.addAll(people1);
        people.addAll(people2);
        people.addAll(people3);

        people = people.stream().filter(x -> x.trim().length() > 0).collect(Collectors.toSet());
        String header = "客户案号,基本法律状态,字数,翻译人,第一校对人,第二校对人,检查" + newLine;
        for (String name : people) {

            List<String> personData = table.stream().filter(line -> name.equals(line[8])).map(line -> line[0] + "," + line[3] + "," + line[7] + "," + name + ",,,").collect(Collectors.toList());
            personData.addAll(table.stream().filter(line -> name.equals(line[9])).map(line -> line[0] + "," + line[3] + "," + line[7] + ",," + name + ",,").collect(Collectors.toList()));
            personData.addAll(table.stream().filter(line -> name.equals(line[10])).map(line -> line[0] + "," + line[3] + "," + line[7] + ",,," + name+",").collect(Collectors.toList()));
            personData.addAll(table.stream().filter(line -> name.equals(line[11])).map(line -> line[0] + "," + line[3] + "," + line[7] + ",,,," + name).collect(Collectors.toList()));

            personData.forEach(System.out::println);


            String outPath = "/Users/robin/wangxp/" + name + ".csv";

            FileOutputStream fos = new FileOutputStream(outPath);
            fos.write(header.getBytes("UTF-8"));
            for (String line : personData) {
                String s = line + newLine;
                fos.write(s.getBytes("UTF-8"));
//                fw.write(new String(line + newLine, "GBK"));
            }
            fos.close();
//            fw.close();

        }

        // stat
//        for(String name: people)


    }

}
