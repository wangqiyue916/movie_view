package com.example.movie.merchandise.controller;

import com.example.movie.common.response.ApiResponse;
import com.example.movie.common.response.PageResult;
import com.example.movie.merchandise.entity.Merchandise;
import com.example.movie.merchandise.service.MerchandiseService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/merchandise")
public class MerchandiseController {

    private final MerchandiseService merchandiseService;

    public MerchandiseController(MerchandiseService merchandiseService) {
        this.merchandiseService = merchandiseService;
    }

    @GetMapping
    public ApiResponse<PageResult<Merchandise>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long movieId,
            @RequestParam(required = false) String productType,
            @RequestParam(required = false) String keyword
    ) {
        IPage<Merchandise> result = merchandiseService.pageProducts(page, pageSize, movieId, productType, keyword);
        List<Merchandise> records = result.getRecords();

        if (records.isEmpty()) {
            records = buildMockMerchandiseList();
            if (movieId != null) {
                records = records.stream()
                        .filter(m -> movieId.equals(m.getMovieId()))
                        .toList();
            }
            if (productType != null && !productType.isBlank()) {
                records = records.stream()
                        .filter(m -> productType.equals(m.getProductType()))
                        .toList();
            }
            if (keyword != null && !keyword.isBlank()) {
                records = records.stream()
                        .filter(m -> m.getName().contains(keyword))
                        .toList();
            }
        }

        return ApiResponse.success(new PageResult<>(
                records,
                (int) result.getCurrent(),
                (int) result.getSize(),
                (long) records.size()
        ));
    }

    @GetMapping("/{id}")
    public ApiResponse<Merchandise> detail(@PathVariable Long id) {
        Merchandise m = merchandiseService.getProductById(id);
        if (m == null) {
            List<Merchandise> mockList = buildMockMerchandiseList();
            if (id >= 1 && id <= mockList.size()) {
                Merchandise mock = mockList.get((int) (id - 1));
                mock.setClickCount((mock.getClickCount() == null ? 0L : mock.getClickCount()) + 1);
                return ApiResponse.success(mock);
            }
            return ApiResponse.error(404, "商品不存在");
        }
        merchandiseService.recordClick(id);
        m.setClickCount((m.getClickCount() == null ? 0L : m.getClickCount()) + 1);
        return ApiResponse.success(m);
    }

    @GetMapping({"/by-movie/{movieId}", "/movie/{movieId}"})
    public ApiResponse<List<Merchandise>> listByMovie(@PathVariable Long movieId) {
        List<Merchandise> list = merchandiseService.listByMovie(movieId);
        if (list.isEmpty()) {
            list = buildMockMerchandiseList().stream()
                    .filter(m -> movieId.equals(m.getMovieId()))
                    .limit(6)
                    .toList();
        }
        return ApiResponse.success(list);
    }

    private List<Merchandise> buildMockMerchandiseList() {
        List<Merchandise> list = new ArrayList<>();

        Merchandise m1 = new Merchandise();
        m1.setId(1L);
        m1.setMovieId(3L);
        m1.setName("流浪地球2鼠标垫");
        m1.setProductType("数码");
        m1.setPrice(new BigDecimal("3.00"));
        m1.setPlatform("淘宝");
        m1.setExternalUrl("https://item.taobao.com/item.htm?ali_refid=a3_430673_1006%3A1613780005%3AH%3AOh18XfFayHNkRhT9xjRTkl9E3BEhcAfR%3Af59ff7ad021b802ccdc3d0e0aefb99b1&ali_trackid=318_f59ff7ad021b802ccdc3d0e0aefb99b1&id=698786584590&loginBonus=1&mi_id=0000wmLIcz6jfsga2V4D_N-iFT4ejU7AFJdF_-3W0a0o0f8&mm_sceneid=0_0_2430830031_0&priceTId=214780bf17834782223575470e0eaa&skuId=4947209101869&spm=a21n57.sem.item.5&utparam=%7B%22aplus_abtest%22%3A%22f8234a1e2672a0434575d29186ba1e08%22%7D&xxc=ad_ztc");
        m1.setClickCount(0L);
        m1.setStatus("ONLINE");
        m1.setImageUrl("/merch-shubiao.png");
        list.add(m1);

        Merchandise m2 = new Merchandise();
        m2.setId(2L);
        m2.setMovieId(2L);
        m2.setName("盗梦空间烟灰缸");
        m2.setProductType("其他");
        m2.setPrice(new BigDecimal("243.00"));
        m2.setPlatform("淘宝");
        m2.setExternalUrl("https://item.taobao.com/item.htm?ali_refid=a3_430673_1006%3A1150766372%3AH%3ANeZZNP4J71P6xl%2BTGv9mDA%3D%3D%3A1fcb4ea9281317db7971821d504e5978&ali_trackid=282_1fcb4ea9281317db7971821d504e5978&id=1028630747640&loginBonus=1&mi_id=0000u4M9bjAnqEVI0peAgVVfydeHqE4Z3aYg4ML0fAFbQe0&mm_sceneid=1_0_45000339_0&priceTId=2147842717834799515015417e1127&skuId=6042704393330&spm=a21n57.sem.item.3&utparam=%7B%22aplus_abtest%22%3A%22a730259ab86fa579e7483c5e3df92fbb%22%7D&xxc=ad_ztc");
        m2.setClickCount(0L);
        m2.setStatus("ONLINE");
        m2.setImageUrl("/merch-yanhuigang.png");
        list.add(m2);

        Merchandise m3 = new Merchandise();
        m3.setId(3L);
        m3.setMovieId(3L);
        m3.setName("流浪地球2笨笨积木玩具");
        m3.setProductType("模型");
        m3.setPrice(new BigDecimal("599.00"));
        m3.setPlatform("京东");
        m3.setExternalUrl("https://item.jd.com/10179164941794.html?pcdk=cLH2Z1pDlhGYQ8Ui_-J9dzFBsXgJ3CVsrFRmjhdrMMJF2lMusdWUoo17JOQx3Rm5.3z6a.aI3x&spmTag=YTAyMTkuYjAwMjM1Ni5jMDAwMDcyMTAua2V5d29yZF9lbnRlciU0MDE3ODM0ODA2MTY4MjYlMjMxNzc2ODYxNDIzOTg0Mjg5NDIwMTY1JTIzMjAwNzQ2OTYzOSUyQ2EwMjQwLmIwMDI0OTMuYzAwMDA0MDI3LjElMjNza3VfY2FyZCU0MDE3ODM0ODA2NTE5MDIlMjMxNzc2ODYxNDIzOTg0Mjg5NDIwMTY1JTIzMjcyMjYzMjQx");
        m3.setClickCount(0L);
        m3.setStatus("ONLINE");
        m3.setImageUrl("/merch-benben.png");
        list.add(m3);

        Merchandise m4 = new Merchandise();
        m4.setId(4L);
        m4.setMovieId(1L);
        m4.setName("星际穿越塔斯机器人");
        m4.setProductType("模型");
        m4.setPrice(new BigDecimal("174.40"));
        m4.setPlatform("京东");
        m4.setExternalUrl("https://item.jd.com/10219005177651.html?pcdk=oejlyESTkSXIawjktCcj4N-K-3BdQxL1GmTOlb-l_D4YfR4cP5Ez9Es5iNF-ggnBO3NNZtr7FPM&spmTag=YTAyMTkuYjAwMjM1Ni5jMDAwMDcyMTAua2V5d29yZF9lbnRlciU0MDE3ODM0ODA2MTY4MjYlMjMxNzc2ODYxNDIzOTg0Mjg5NDIwMTY1JTIzMjAwNzQ2OTYzOSUyQ2EwMjQwLmIwMDI0OTMuYzAwMDA0MDI3LjElMjNza3VfY2FyZCU0MDE3ODM0ODA2NTE5MDIlMjMxNzc2ODYxNDIzOTg0Mjg5NDIwMTY1JTIzMjcyMjYzMjQx");
        m4.setClickCount(0L);
        m4.setStatus("ONLINE");
        m4.setImageUrl("/merch-tasi.png");
        list.add(m4);

        Merchandise m5 = new Merchandise();
        m5.setId(5L);
        m5.setMovieId(4L);
        m5.setName("黑暗骑士合金战车");
        m5.setProductType("模型");
        m5.setPrice(new BigDecimal("148.00"));
        m5.setPlatform("京东");
        m5.setExternalUrl("https://item.jd.com/10224017016041.html?pcdk=Tf0-E8PHTH9YKCPbdh3iY34gVlTOdwfeKuA-UYqijOyxVQ99okRMvGSY_Y2FqmIy.3z6a.aI3x&spmTag=YTAyMTkuYjAwMjM1Ni5jMDAwMDcyMTAua2V5d29yZF9lbnRlciU0MDE3ODM0ODA2MTY4MjYlMjMxNzc2ODYxNDIzOTg0Mjg5NDIwMTY1JTIzMjAwNzQ2OTYzOSUyQ2EwMjQwLmIwMDI0OTMuYzAwMDA0MDI3LjclMjNza3VfY2FyZCU0MDE3ODM0ODE0MDY2NzYlMjMxNzc2ODYxNDIzOTg0Mjg5NDIwMTY1JTIzMTE0MzU3NTEwMg");
        m5.setClickCount(0L);
        m5.setStatus("ONLINE");
        m5.setImageUrl("/merch-zhanchen.png");
        list.add(m5);

        Merchandise m6 = new Merchandise();
        m6.setId(6L);
        m6.setMovieId(5L);
        m6.setName("蜘蛛侠2.0玩具手办");
        m6.setProductType("模型");
        m6.setPrice(new BigDecimal("183.33"));
        m6.setPlatform("京东");
        m6.setExternalUrl("https://item.jd.com/100136091846.html?pcdk=06uvUCKwGeRUq0BPdncp0uFfekCbSeAwiRLi0uMH9MfIQDM6ePKfpTOQKASzKNLi.3z6a.aI3x&spmTag=YTAyMTkuYjAwMjM1Ni5jMDAwMDcyMTAua2V5d29yZF9lbnRlciU0MDE3ODM0ODA2MTY4MjYlMjMxNzc2ODYxNDIzOTg0Mjg5NDIwMTY1JTIzMjAwNzQ2OTYzOSUyQ2EwMjQwLmIwMDI0OTMuYzAwMDA0MDI3LjIlMjNza3VfY2FyZCU0MDE3ODM0ODIzMDk0MTElMjMxNzc2ODYxNDIzOTg0Mjg5NDIwMTY1JTIzMTA2MzAwMzU2OA");
        m6.setClickCount(0L);
        m6.setStatus("ONLINE");
        m6.setImageUrl("/merch-zhizhu.png");
        list.add(m6);

        Merchandise m7 = new Merchandise();
        m7.setId(7L);
        m7.setMovieId(6L);
        m7.setName("钢铁侠联名蓝牙耳机");
        m7.setProductType("数码");
        m7.setPrice(new BigDecimal("147.00"));
        m7.setPlatform("淘宝");
        m7.setExternalUrl("https://item.taobao.com/item.htm?ali_refid=a3_430673_1006%3A1444890057%3AH%3ARUeSZmfxR6jaQyhcEGe7RQ%3D%3D%3Ae89749a5b6c6f913f13a21f425de152c&ali_trackid=318_e89749a5b6c6f913f13a21f425de152c&id=1058556026841&loginBonus=1&mi_id=00008vMkRZWnkLZwkZmZUZlI5hBkY500M4pKQfF_aNeh6oY&mm_sceneid=0_0_1751400032_0&priceTId=214784df17834824680374512e0f96&skuId=6102599564822&spm=a21n57.sem.item.92&utparam=%7B%22aplus_abtest%22%3A%22d340eab3bf6b65c2097e492f301abadd%22%7D&xxc=ad_ztc");
        m7.setClickCount(0L);
        m7.setStatus("ONLINE");
        m7.setImageUrl("/merch-erji.png");
        list.add(m7);

        Merchandise m8 = new Merchandise();
        m8.setId(8L);
        m8.setMovieId(6L);
        m8.setName("钢铁侠玩具男孩手办");
        m8.setProductType("模型");
        m8.setPrice(new BigDecimal("368.00"));
        m8.setPlatform("淘宝");
        m8.setExternalUrl("https://item.taobao.com/item.htm?ali_refid=a3_430673_1006%3A1680724198%3AH%3A0Wqyn9QRWucCOuNu2bodomLSa4aixxNm%3A44de117899f43051f32566e87f94ff0f&ali_trackid=282_44de117899f43051f32566e87f94ff0f&id=679221673041&loginBonus=1&mi_id=0000buXSmeA5pWfwe0sqDyJk2D5Ajk2YqFmA_2g479L3OMs&mm_sceneid=1_0_2982530010_0&priceTId=214784df17834824680374512e0f96&skuId=4924417320734&spm=a21n57.sem.item.88&utparam=%7B%22aplus_abtest%22%3A%229c03ab4acc99fecb21250f2d6b30ede2%22%7D&xxc=ad_ztc");
        m8.setClickCount(0L);
        m8.setStatus("ONLINE");
        m8.setImageUrl("/merch-gtxwj.png");
        list.add(m8);

        return list;
    }
}
