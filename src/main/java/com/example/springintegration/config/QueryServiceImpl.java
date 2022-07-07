package com.example.springintegration.config;

import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author n1556638
 */
@Service
@Slf4j
public class QueryServiceImpl {

        public void processFile(S3ObjectInputStream sio) throws IOException {
        String s3FileName = StringUtils.EMPTY;
        FileTypeEnum fileTypeEnum = FileTypeEnum.INIT_VALUE;
        S3ObjectSummary s3objectSummary = new S3ObjectSummary();

        try {
            s3FileName = sio.getHttpRequest().getURI().getPath().substring(1);
            log.info("File name to be processed: " + s3FileName);
        } catch (Exception genEx) {
            log.error("Caught an exception in QueryServiceImpl.");
        }

        finally {
            closeIO(sio);
        }

        log.info("Exit processFile method");
    }

    private void closeIO(InputStream io) throws IOException{
        if (null != io) {
            io.close();
        }
    }
}
