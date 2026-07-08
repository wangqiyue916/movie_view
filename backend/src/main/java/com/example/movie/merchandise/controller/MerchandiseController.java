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

    @GetMapping("/by-movie/{movieId}")
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
        m1.setMovieId(1L);
        m1.setName("星际穿越主题海报");
        m1.setProductType("海报");
        m1.setPrice(new BigDecimal("39.90"));
        m1.setPlatform("淘宝");
        m1.setClickCount(0L);
        m1.setStatus("ONLINE");
        m1.setImageUrl("https://images.unsplash.com/photo-1446776811953-b23d57bd21aa?auto=format&fit=crop&w=300&q=85");
        list.add(m1);

        Merchandise m2 = new Merchandise();
        m2.setId(2L);
        m2.setMovieId(2L);
        m2.setName("盗梦空间陀螺模型");
        m2.setProductType("模型");
        m2.setPrice(new BigDecimal("89.00"));
        m2.setPlatform("京东");
        m2.setClickCount(0L);
        m2.setStatus("ONLINE");
        m2.setImageUrl("https://images.unsplash.com/photo-1485846234645-a62644f84728?auto=format&fit=crop&w=300&q=85");
        list.add(m2);

        Merchandise m3 = new Merchandise();
        m3.setId(3L);
        m3.setMovieId(1L);
        m3.setName("星际穿越TARS机器人模型");
        m3.setProductType("模型");
        m3.setPrice(new BigDecimal("299.00"));
        m3.setPlatform("淘宝");
        m3.setClickCount(0L);
        m3.setStatus("ONLINE");
        m3.setImageUrl("https://images.unsplash.com/photo-1536440136628-849c177e76a1?auto=format&fit=crop&w=300&q=85");
        list.add(m3);

        Merchandise m4 = new Merchandise();
        m4.setId(4L);
        m4.setMovieId(3L);
        m4.setName("流浪地球2金属书签套装");
        m4.setProductType("文创");
        m4.setPrice(new BigDecimal("29.90"));
        m4.setPlatform("拼多多");
        m4.setClickCount(0L);
        m4.setStatus("ONLINE");
        m4.setImageUrl("https://images.unsplash.com/photo-1509347528160-9a9e33742cdb?auto=format&fit=crop&w=300&q=85");
        list.add(m4);

        Merchandise m5 = new Merchandise();
        m5.setId(5L);
        m5.setMovieId(4L);
        m5.setName("暗夜骑士蝙蝠面具收藏版");
        m5.setProductType("模型");
        m5.setPrice(new BigDecimal("199.00"));
        m5.setPlatform("京东");
        m5.setClickCount(0L);
        m5.setStatus("ONLINE");
        m5.setImageUrl("https://images.unsplash.com/photo-1500462918059-b1a0cb512f1d?auto=format&fit=crop&w=300&q=85");
        list.add(m5);

        Merchandise m6 = new Merchandise();
        m6.setId(6L);
        m6.setMovieId(2L);
        m6.setName("盗梦空间主题笔记本");
        m6.setProductType("文创");
        m6.setPrice(new BigDecimal("49.90"));
        m6.setPlatform("淘宝");
        m6.setClickCount(0L);
        m6.setStatus("ONLINE");
        m6.setImageUrl("https://images.unsplash.com/photo-1524985069026-dd778a71c7b4?auto=format&fit=crop&w=300&q=85");
        list.add(m6);

        Merchandise m7 = new Merchandise();
        m7.setId(7L);
        m7.setMovieId(1L);
        m7.setName("星际穿越联名卫衣");
        m7.setProductType("服饰");
        m7.setPrice(new BigDecimal("159.00"));
        m7.setPlatform("淘宝");
        m7.setClickCount(0L);
        m7.setStatus("ONLINE");
        m7.setImageUrl("https://images.unsplash.com/photo-1516280440614-37939bbacd81?auto=format&fit=crop&w=300&q=85");
        list.add(m7);

        Merchandise m8 = new Merchandise();
        m8.setId(8L);
        m8.setMovieId(3L);
        m8.setName("电影主题明信片合集");
        m8.setProductType("文创");
        m8.setPrice(new BigDecimal("19.90"));
        m8.setPlatform("拼多多");
        m8.setClickCount(0L);
        m8.setStatus("ONLINE");
        m8.setImageUrl("https://images.unsplash.com/photo-1462331940025-496dfbfc7564?auto=format&fit=crop&w=300&q=85");
        list.add(m8);

        return list;
    }
}
