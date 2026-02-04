package kz.lab.fileuploaderservice.db.entity;

import jakarta.persistence.*;
import kz.lab.fileuploaderservice.db.enums.FileStatus;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "files",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"client_id", "idempotency_key"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_id", nullable = false)
    private String clientId;

    @Column(name = "idempotency_key", nullable = false)
    private String idempotencyKey;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FileStatus status;

    @Column(name = "storage_path")
    private String storagePath;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}

