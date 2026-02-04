package kz.lab.fileuploaderservice.mapper;

import kz.lab.fileuploaderservice.db.entity.File;
import kz.lab.fileuploaderservice.dto.response.FileInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileMapper {
    public FileInfoResponse toDTO(File file){
        return new FileInfoResponse(
                file.getId(),
                file.getFileName(),
                file.getClientId(),
                file.getStatus(),
                file.getStoragePath(),
                file.getCreatedAt()
        );
    }
}
