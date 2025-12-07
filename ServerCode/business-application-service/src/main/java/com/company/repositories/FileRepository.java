package com.company.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.company.models.File;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

}
