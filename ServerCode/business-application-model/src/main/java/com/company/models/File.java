package com.company.models;

import javax.persistence.*;

@Entity
@Table(name = "files")
public class File {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String fileName;

  private String alias;

  private String fileType;

  // private Long listingId;

  private String path;

  public File() {
  }

  public File(String fileName, String fileType, String path) {
    this.fileName = fileName;
    this.fileType = fileType;
    this.path = path;
  }

  public Long getId() {
    return this.id;
  }

  public String getAlias() {
    return this.alias;
  }

  public String getFileName() {
    return this.fileName;
  }

  public String getFileType() {
    return this.fileType;
  }

  public String getPath() {
    return this.path;
  }

  // public Long getListingId() {
  // return this.listingId;
  // }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public void setFileType(String fileType) {
    this.fileType = fileType;
  }

  public void setPath(String path) {
    this.path = path;
  }

  // public void setListingId(Long id) {
  // this.listingId = id;
  // }
}
