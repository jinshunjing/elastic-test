package org.jim.elasticsearch.jpa;

import org.jim.elasticsearch.model.InvestProfit;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;

/**
 * 投资收益的操作
 *
 * @author Jim
 */
@Component
public interface InvestProfitRepository extends ElasticsearchRepository<InvestProfit, String> {
}
