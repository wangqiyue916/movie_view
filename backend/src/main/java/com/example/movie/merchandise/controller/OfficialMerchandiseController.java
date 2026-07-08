package com.example.movie.merchandise.controller;

import com.example.movie.common.config.LoginUserContext;
import com.example.movie.common.response.ApiResponse;
import com.example.movie.merchandise.entity.Merchandise;
import com.example.movie.merchandise.service.MerchandiseService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/official/merchandise")
public class OfficialMerchandiseController {

    private final MerchandiseService merchandiseService;

    public OfficialMerchandiseController(MerchandiseService merchandiseService) {
        this.merchandiseService = merchandiseService;
    }

    @PostMapping
    public ApiResponse<Merchandise> submit(@RequestBody Map<String, Object> body) {
        Merchandise m = new Merchandise();
        m.setMovieId(body.get("movieId") != null ? ((Number) body.get("movieId")).longValue() : null);
        m.setName((String) body.get("name"));
        m.setImageUrl((String) body.get("imageUrl"));
        m.setProductType((String) body.get("productType"));
        if (body.get("price") != null) {
            m.setPrice(new BigDecimal(body.get("price").toString()));
        }
        m.setDescription((String) body.get("description"));
        m.setPlatform((String) body.get("platform"));
        m.setExternalUrl((String) body.get("externalUrl"));
        m.setStatus("PENDING");
        m.setClickCount(0L);

        Long userId = LoginUserContext.getUserId();
        if (userId != null) {
            m.setSubmitterId(userId);
        }

        return ApiResponse.success(merchandiseService.createProduct(m));
    }
}
