package com.uned.tfm.tfm2022;

import com.jbraindead.core.formalEntities.Lattice;
import com.jbraindead.core.generator.ContextGenerator;
import com.uned.tfm.tfm2022.domain.service.FCAService;
import com.uned.tfm.tfm2022.infra.repository.CSVMatrixFCARepository;
import lombok.extern.log4j.Log4j2;
import org.la4j.matrix.sparse.CRSMatrix;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.neo4j.core.DatabaseSelection;
import org.springframework.data.neo4j.core.DatabaseSelectionProvider;

import java.util.Arrays;

@Log4j2
@SpringBootApplication
public class Tfm2022Application implements CommandLineRunner {
    private final FCAService service;
    private final CSVMatrixFCARepository csvFcaReader;

    @Value("${uid}")
    private String uid;


    public Tfm2022Application(CSVMatrixFCARepository csvFcaReader, FCAService service) {
        this.csvFcaReader = csvFcaReader;
        this.service = service;
    }

    public static void main(String[] args) {
        SpringApplication.run(Tfm2022Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        log.info("\nExecuting with params: " +
                "\n\t- UID: {}", uid);

        String attrPath = "./ProcessedData/metadata/" + uid + "/" + uid + "-attributes.csv";
        String objPath = "./ProcessedData/metadata/" + uid + "/" + uid + "-objects.csv";
        String relationsPath = "./ProcessedData/metadata/" + uid + "/" + uid + "-relations.csv";

        String[] attributes = csvFcaReader.getAttributes(attrPath);
        String[] objects = csvFcaReader.getObjects(objPath);
        CRSMatrix relations = csvFcaReader.getMatrixByIdArray(relationsPath, objects.length, attributes.length);

        ContextGenerator generator = new ContextGenerator(objects, attributes, relations);

        Lattice lattice = generator.getLattice();


        log.info("Saving lattice");

        service.saveLattice(lattice);

        log.info("Saved lattice");
    }
}
