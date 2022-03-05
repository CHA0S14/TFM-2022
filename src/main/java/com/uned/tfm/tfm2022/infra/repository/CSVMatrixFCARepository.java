package com.uned.tfm.tfm2022.infra.repository;

import java.io.IOException;
import java.util.List;

import com.uned.tfm.tfm2022.infra.repository.impl.CSVMatrixFCARepositoryIml;
import org.la4j.matrix.sparse.CRSMatrix;

public interface CSVMatrixFCARepository {
    String[] getAttributes(String path) throws IOException;

    String[] getObjects(String path) throws IOException;

    CRSMatrix getMatrix(String path, int objectLen, int attributesLen) throws IOException;

    CRSMatrix getMatrixByIdArray(String path, int objectLen, int attributesLen) throws IOException;
}
