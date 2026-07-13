package com.hei.school.repository;

import com.hei.school.model.JUploadedFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JUploadedFileRepository extends JpaRepository<JUploadedFile, String> {}
