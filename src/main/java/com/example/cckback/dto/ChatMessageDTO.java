package com.example.cckback.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter

public class ChatMessageDTO {
    private Long senderId;
    private Long receiverId;
    private String content;
    private String filePath; // Add file path
    private String fileType; // Add file type
    private String fileName; // Add file name
    private String timestamp;
    public ChatMessageDTO() {
    }
    // Constructor with all fields for explicit creation
    @JsonCreator
    public ChatMessageDTO(
            @JsonProperty("senderId") Long senderId,
            @JsonProperty("receiverId") Long receiverId,
            @JsonProperty("content") String content,@JsonProperty("filePath") String filePath,
            @JsonProperty("fileType") String fileType,
            @JsonProperty("fileName") String fileName,
            @JsonProperty("timestamp") String timestamp) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.filePath = filePath;
        this.fileType = fileType;
        this.fileName = fileName;
        this.timestamp = timestamp;
    }

    public Long getReceiverId() {
        return receiverId;
    }
    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    public Long getSenderId() {
        return senderId;
    }
    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }
    public String getFilePath() {
        return filePath;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    public String getFileType() {
        return fileType;
    }
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


}
