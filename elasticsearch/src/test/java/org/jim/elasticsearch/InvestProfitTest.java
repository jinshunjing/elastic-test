package org.jim.elasticsearch;

import org.jim.elasticsearch.jpa.InvestProfitRepo;
import org.jim.elasticsearch.jpa.InvestProfitRepository;
import org.jim.elasticsearch.model.InvestProfit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.ScrolledPage;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

/**
 * @author Jim
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class InvestProfitTest {
    @Autowired
    private InvestProfitRepo investProfitRepo;
    @Autowired
    private InvestProfitRepository investProfitRepository;

    @Before
    public void setUp() throws Exception {
        Thread.sleep(1000);
        System.out.println("==================");
        System.out.println();
    }

    @After
    public void tearDown() throws Exception {
        System.out.println("==================");
        System.out.println();
        Thread.sleep(1000);
    }

    @Test
    public void test1() {
        investProfitRepo.createIndex();
    }

    @Test
    public void test11() {
        investProfitRepository.deleteAll();
    }

    @Test
    public void test2() {
        for (int i = 0; i < 10; i++) {
            InvestProfit result = investProfitRepository.save(InvestProfit.builder()
                    .type(0)
                    .owner("0x66181cb1524240ff238a9c13d7f4a93955dc811c0e")
                    .state(0)
                    .createTime(System.currentTimeMillis())
                    .assetCode("000000000000000300000000")
                    .assetAmount("12346" + i)
                    .build());
            System.out.println(result.getId());
        }
    }

    @Test
    public void test3() {
        Page<InvestProfit> page = investProfitRepo.page(1, 3, null);
        System.out.println(page.getTotalElements()); //11
        System.out.println(page.getTotalPages()); // 1
        System.out.println(page.getNumber()); // 0
        System.out.println(page.getNumberOfElements()); // 3
        System.out.println(page.getSize()); // 0
        System.out.println(page.getContent().size()); // 3
        //System.out.println(page.getPageable().getPageNumber());
        //System.out.println(page.getPageable().getPageSize());
        System.out.println(page.getSort().isSorted());//false
        System.out.println(((ScrolledPage)page).getScrollId());
        // DnF1ZXJ5VGhlbkZldGNoBQAAAAAAAAANFnZIejAwbzhFUk15dGpqZllDbnhBUFEAAAAAAAAADBZ2SHowMG84RVJNeXRqamZZQ254QVBRAAAAAAAAAAkWZEZfb3pCa2JUQW1mbllhamJIRzFyUQAAAAAAAAAKFmRGX296QmtiVEFtZm5ZYWpiSEcxclEAAAAAAAAACBZkRl9vekJrYlRBbWZuWWFqYkhHMXJR
        // DnF1ZXJ5VGhlbkZldGNoBQAAAAAAAAAKFlYtcXN4THY2U282UUdUZTNCY0haNFEAAAAAAAAACxZkRl9vekJrYlRBbWZuWWFqYkhHMXJRAAAAAAAAAA4Wdkh6MDBvOEVSTXl0ampmWUNueEFQUQAAAAAAAAAJFlYtcXN4THY2U282UUdUZTNCY0haNFEAAAAAAAAACBZWLXFzeEx2NlNvNlFHVGUzQmNIWjRR
        System.out.println(page.getContent().get(0).getAssetAmount());
    }

    @Test
    public void test4() {
        // DnF1ZXJ5VGhlbkZldGNoBQAAAAAAAAAPFnZIejAwbzhFUk15dGpqZllDbnhBUFEAAAAAAAAAEBZ2SHowMG84RVJNeXRqamZZQ254QVBRAAAAAAAAAA4WZEZfb3pCa2JUQW1mbllhamJIRzFyUQAAAAAAAAANFmRGX296QmtiVEFtZm5ZYWpiSEcxclEAAAAAAAAADBZkRl9vekJrYlRBbWZuWWFqYkhHMXJR
        // DnF1ZXJ5VGhlbkZldGNoBQAAAAAAAAAPFnZIejAwbzhFUk15dGpqZllDbnhBUFEAAAAAAAAAEBZ2SHowMG84RVJNeXRqamZZQ254QVBRAAAAAAAAAA4WZEZfb3pCa2JUQW1mbllhamJIRzFyUQAAAAAAAAANFmRGX296QmtiVEFtZm5ZYWpiSEcxclEAAAAAAAAADBZkRl9vekJrYlRBbWZuWWFqYkhHMXJR
        String scrollId = "DnF1ZXJ5VGhlbkZldGNoBQAAAAAAAAAPFnZIejAwbzhFUk15dGpqZllDbnhBUFEAAAAAAAAAEBZ2SHowMG84RVJNeXRqamZZQ254QVBRAAAAAAAAAA4WZEZfb3pCa2JUQW1mbllhamJIRzFyUQAAAAAAAAANFmRGX296QmtiVEFtZm5ZYWpiSEcxclEAAAAAAAAADBZkRl9vekJrYlRBbWZuWWFqYkhHMXJR";
        Page<InvestProfit> page = investProfitRepo.page(3, 3, scrollId);
        System.out.println(page.getTotalElements()); //11
        System.out.println(page.getNumberOfElements());
        System.out.println(page.getContent().size()); // 3
        System.out.println(((ScrolledPage)page).getScrollId());
        System.out.println(page.getContent().get(0).getAssetAmount());
    }

    @Test
    public void test5() {
        investProfitRepo.sum("0x66181cb1524240ff238a9c13d7f4a93955dc811c0e", "000000000000000300000000");
    }
}
