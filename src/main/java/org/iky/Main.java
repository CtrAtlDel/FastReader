package org.iky;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class Main {

    static final String delimiter = ".";
    static final int index = 0;
    static final int batchSize = 1000; // зависит от размера файла

    public static void fastFileSort(String inputFileName, String outputfFilename) {
        File file = new File(System.getProperty("java.io.tmpdir") + File.separator + "fileSort");
        file.mkdir(); // создаем временную директорию
        List<File> batchList = new ArrayList<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(inputFileName));
            String header = bufferedReader.readLine();
            List<String> lines = new ArrayList<>();
            String line = "";
            int batchNumber = 0;
            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);

                if (lines.size() >= batchSize) {
                    List<String> sortedBatch = lines.stream()
                            .sorted(Comparator.comparing(x -> Integer.parseInt(x.split(delimiter)[index])))
                            .toList();

                    File batchFile = new File(file, "batch_" + batchNumber + ".csv");
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(batchFile))) {
                        writer.write(header);
                        writer.newLine();
                        for (var sortedLine : sortedBatch) {
                            writer.write(sortedLine);
                            writer.newLine();
                        }
                    }
                    batchList.add(batchFile);
                    lines.clear();
                    batchNumber++;
                }
            }

            if (!lines.isEmpty()) {
                List<String> sortedBatch = lines.stream()
                        .sorted(Comparator.comparing(x -> Integer.parseInt(x.split(delimiter)[index])))
                        .toList();
                File batchFile = new File(file, "batch_" + batchNumber + ".csv");
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(batchFile))) {
                    writer.write(header);
                    writer.newLine();
                    for (var sortedLine : sortedBatch) {
                        writer.write(sortedLine);
                        writer.newLine();
                    }
                }
                batchList.add(batchFile);

            }
            bufferedReader.close();


            PriorityQueue<BufferedReader> pq = new PriorityQueue<>(Comparator.comparingInt(br -> {
                try {
                    return Integer.parseInt(br.readLine().split(delimiter)[index]);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }));

            BufferedWriter writer = new BufferedWriter(new FileWriter(outputfFilename));
            writer.write(header);
            writer.newLine();
            for (File batchFile : batchList) {
                BufferedReader chunkReader = new BufferedReader(new FileReader(batchFile));
                pq.add(chunkReader);
            }
            while (!pq.isEmpty()) {
                BufferedReader br = pq.poll();
                String entry = br.readLine();
                if (entry != null) {
                    writer.write(entry);
                    writer.newLine();
                    pq.add(br);
                } else {
                    br.close();
                }
            }
            writer.close();

            deleteTempDirectory(batchList, file);

        } catch (FileNotFoundException e) {
            System.out.println("File not exist: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO reader exception: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static void deleteTempDirectory(List<File> batchList, File file) {
        for (File batchFile : batchList) {
            batchFile.delete();
        }
        file.delete();
    }

    public static void main(String[] args) {
        final String fileName = "generate_data.csv";
        final String outputFileName = "result.csv";
        fastFileSort(fileName, outputFileName);
    }
}