package com.ll.exam.app10.app.fileUpload.entity;

import com.ll.exam.app10.app.base.entity.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class GenFile extends BaseEntity {
    private String relTypeCode; // 어느것과 과련되었냐
    private long relId; // 어떤것과 관련 되어 있는데 그 Id는 어떤 것이냐
    private String typeCode; // 어디에 쓰일 것이냐 ( 용도 )
    private String type2Code; // 어디에 쓰일 것이냐 2 ( 용도 2 )
    private String fileExtTypeCode; // 파일 종류 단계 ( img )
    private String fileExtType2Code; // 파일 종류 단계 ( jpg )
    private int fileSize; // 파일 크기
    private int fileNo; // 파일 번호
    private String fileExt; // 파일 확장자
    private String fileDir; // 파일 경로
    private String originFileName; // 원본 파일 이름

    public String getFileName() {
        return getId() + "." + getFileExt();
    }
}
