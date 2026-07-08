package com.example.movie.merchandise.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.movie.common.response.ApiResponse;
import com.example.movie.common.response.PageResult;
import com.example.movie.merchandise.entity.Merchandise;
import com.example.movie.merchandise.service.MerchandiseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/merchandise")
public class AdminMerchandiseController {

    private final MerchandiseService merchandiseService;

    public AdminMerchandiseController(MerchandiseService merchandiseService) {
        this.merchandiseService = merchandiseService;
    }

    @GetMapping
    public ApiResponse<PageResult<Merchandise>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword
    ) {
        IPage<Merchandise> result = merchandiseService.pageProducts(page, pageSize, null, null, keyword);
        List<Merchandise> records = result.getRecords();

        if (records.isEmpty()) {
            records = buildMockAdminMerchandiseList();
            if (keyword != null && !keyword.isBlank()) {
                records = records.stream()
                        .filter(m -> m.getName().contains(keyword))
                        .toList();
            }
            if (status != null && !status.isBlank()) {
                records = records.stream()
                        .filter(m -> status.equals(m.getStatus()))
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

    @PutMapping("/{id}/approve")
    public ApiResponse<Merchandise> approve(@PathVariable Long id) {
        Merchandise m = new Merchandise();
        m.setId(id);
        m.setStatus("APPROVED");
        return ApiResponse.success(merchandiseService.updateProduct(m));
    }

    @PutMapping("/{id}/reject")
    public ApiResponse<Merchandise> reject(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Merchandise m = new Merchandise();
        m.setId(id);
        m.setStatus("REJECTED");
        return ApiResponse.success(merchandiseService.updateProduct(m));
    }

    @PutMapping("/{id}/publish")
    public ApiResponse<Merchandise> publish(@PathVariable Long id) {
        Merchandise m = new Merchandise();
        m.setId(id);
        m.setStatus("ONLINE");
        return ApiResponse.success(merchandiseService.updateProduct(m));
    }

    @PutMapping("/{id}/offline")
    public ApiResponse<Merchandise> offline(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Merchandise m = new Merchandise();
        m.setId(id);
        m.setStatus("OFFLINE");
        return ApiResponse.success(merchandiseService.updateProduct(m));
    }

    private List<Merchandise> buildMockAdminMerchandiseList() {
        List<Merchandise> list = new ArrayList<>();

        Merchandise m1 = new Merchandise();
        m1.setId(1L);
        m1.setMovieId(1L);
        m1.setName("星际穿越主题海报");
        m1.setProductType("海报");
        m1.setPrice(new BigDecimal("39.90"));
        m1.setPlatform("淘宝");
        m1.setClickCount(150L);
        m1.setStatus("ONLINE");
        m1.setImageUrl("https://images.unsplash.com/photo-1446776811953-b23d57bd21aa?auto=format&fit=crop&w=300&q=85");
        m1.setCreatedAt(LocalDateTime.now().minusDays(30));
        list.add(m1);

        Merchandise m2 = new Merchandise();
        m2.setId(2L);
        m2.setMovieId(2L);
        m2.setName("盗梦空间陀螺模型");
        m2.setProductType("模型");
        m2.setPrice(new BigDecimal("89.00"));
        m2.setPlatform("京东");
        m2.setClickCount(320L);
        m2.setStatus("ONLINE");
        m2.setImageUrl("https://images.unsplash.com/photo-1485846234645-a62644f84728?auto=format&fit=crop&w=300&q=85");
        m2.setCreatedAt(LocalDateTime.now().minusDays(25));
        list.add(m2);

        Merchandise m3 = new Merchandise();
        m3.setId(3L);
        m3.setMovieId(1L);
        m3.setName("星际穿越TARS机器人模型");
        m3.setProductType("模型");
        m3.setPrice(new BigDecimal("299.00"));
        m3.setPlatform("淘宝");
        m3.setClickCount(89L);
        m3.setStatus("PENDING");
        m3.setImageUrl("https://images.unsplash.com/photo-1536440136628-849c177e76a1?auto=format&fit=crop&w=300&q=85");
        m3.setCreatedAt(LocalDateTime.now().minusDays(3));
        list.add(m3);

        Merchandise m4 = new Merchandise();
        m4.setId(4L);
        m4.setMovieId(3L);
        m4.setName("流浪地球2金属书签套装");
        m4.setProductType("文创");
        m4.setPrice(new BigDecimal("29.90"));
        m4.setPlatform("拼多多");
        m4.setClickCount(67L);
        m4.setStatus("REJECTED");
        m4.setImageUrl("https://images.unsplash.com/photo-1509347528160-9a9e33742cdb?auto=format&fit=crop&w=300&q=85");
        m4.setCreatedAt(LocalDateTime.now().minusDays(7));
        list.add(m4);

        return list;
    }
}
