package org.jim.elasticsearch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(indexName = "dev_employee", type = "employee")
public class Employee implements Serializable {
    /**
     * 主键
     */
    @Id
    private String id;

    /**
     * 用户昵称
     * 用于排序和聚合的字段：定义成Keyword类型
     */
    @Field(type = FieldType.Keyword)
    private String name;

    /**
     * 备注
     * 不用于查询的字段：index赋值false（不创建索引）
     */
    @Field(type = FieldType.Text, index = false)
    private String note;

    /**
     * 资产金额
     */
    @Field(type = FieldType.Keyword)
    private String assetAmount;

    /**
     * 创建时间
     */
    @Field(type = FieldType.Date)
    private long createTime;

}
