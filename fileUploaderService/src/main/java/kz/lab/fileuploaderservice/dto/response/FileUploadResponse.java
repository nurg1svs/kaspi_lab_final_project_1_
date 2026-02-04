package kz.lab.fileuploaderservice.dto.response;

import kz.lab.fileuploaderservice.db.enums.FileStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FileUploadResponse {
    private Long fileId;
    private FileStatus status;
}

