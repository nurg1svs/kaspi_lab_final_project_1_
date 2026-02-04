package kz.lab.fileuploaderservice.dto.response;

import kz.lab.fileuploaderservice.db.enums.FileStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class FileInfoResponse {

    private Long fileId;
    private String fileName;
    private String clientId;
    private FileStatus status;
    private String storagePath;
    private LocalDateTime createdAt;
}
