package com.uned.tfm.tfm2022.infra.repository.impl;

import com.uned.tfm.tfm2022.infra.repository.CSVMatrixFCARepository;
import lombok.extern.log4j.Log4j2;
import org.la4j.Vector;
import org.la4j.matrix.sparse.CRSMatrix;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Log4j2
@Component
public class CSVMatrixFCARepositoryIml implements CSVMatrixFCARepository {

    @Override
    public String[] getAttributes(String path) throws IOException {

        return getStringFromFile(path).split(",");
    }

    @Override
    public String[] getObjects(String path) throws IOException {

        return getStringFromFile(path).split(",");
    }

    @Override
    public CRSMatrix getMatrix(String path, int objectLen, int attributesLen) throws IOException {
        Path filePath = Paths.get(path);

        if (!Files.exists(filePath)) {
            log.error("The file {} doesn't exists", path);
            throw new IOException("The file " + path + " doesn't exists");
        }

        List<Vector> rows;

        log.info("Reading matrix file");

        try (Stream<String> stream = Files.lines(filePath, StandardCharsets.UTF_8)) {

            rows = stream.filter(line -> !line.trim().isEmpty())
                    .map(line -> {

                        line = line.trim();

                        List<String> lineElements = Arrays.asList(line.split(","));

                        double[] row = lineElements.stream().mapToDouble(Double::parseDouble).toArray();

                        return Vector.fromArray(row);
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw e;
        }

        log.info("Created matrix with {} rows and {} columns", objectLen, attributesLen);

        CRSMatrix relations = CRSMatrix.zero(objectLen, attributesLen);

        log.info("Filling up matrix");

        IntStream.range(0, rows.size()).forEach(iRow -> {

            Vector row = rows.get(iRow);


            relations.setRow(iRow, row);


            if (iRow % 1000 == 0)
                log.info("Filled {} lines", iRow);

        });

        log.info("The file {} read successfully", path);

        return relations;
    }


    private String getStringFromFile(String path) throws IOException {
        Path filePath = Paths.get(path);

        if (!Files.exists(filePath)) {
            log.error("The file {} doesn't exists", path);
            throw new IOException("The file " + path + " doesn't exists");
        }

        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(filePath, StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        } catch (IOException e) {
            throw e;
        }

        log.info("The file {} read successfully", path);

        return contentBuilder.toString().trim();
    }
}
