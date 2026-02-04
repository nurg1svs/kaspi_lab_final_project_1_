package kz.lab.fileuploaderservice.db.repository;

import kz.lab.fileuploaderservice.db.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileRepository extends JpaRepository<File, Long> {

    Optional<File> findByClientIdAndIdempotencyKey(
            String clientId,
            String idempotencyKey
    );
}
