package kz.lab.fileuploaderservice.service;

import kz.lab.fileuploaderservice.dto.response.FileInfoResponse;
import kz.lab.fileuploaderservice.dto.response.FileUploadResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {

    FileUploadResponse handleUpload(String clientId,
                                           String idempotencyKey,
                                           MultipartFile file);

    FileInfoResponse getFile(Long fileId);

    List<FileInfoResponse> getAllFiles();

}
