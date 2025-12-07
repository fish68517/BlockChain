package com.dlmgroup.collectorcoin.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dlmgroup.collectorcoin.models.File;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

}
