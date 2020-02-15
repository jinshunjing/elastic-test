package org.jim.elasticsearch.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;

/**
 * 投资收益
 *
 * @author Jim
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(indexName = "dev_invest_profit", type = "invest_profit")
public class InvestProfit {
    /**
     * 主键
     */
    @Id
    String id;

    /**
     * 类型
     */
    @Field
    int type;

    /**
     * 受益人
     */
    @Field(type = FieldType.Keyword)
    String owner;

    /**
     * 状态
     */
    @Field(type = FieldType.Integer)
    int state;

    /**
     * 创建时间
     */
    @Field
    long createTime;

    /**
     * 收益详情
     */
    @Field(type = FieldType.Keyword)
    String assetCode;

    /**
     * 资产金额
     */
    @Field(index = false)
    String assetAmount;

}
