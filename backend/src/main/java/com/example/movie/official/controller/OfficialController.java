package com.example.movie.official.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.movie.common.config.LoginUserContext;
import com.example.movie.common.response.ApiResponse;
import com.example.movie.official.entity.OfficialProfileEntity;
import com.example.movie.official.mapper.OfficialProfileMapper;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/official")
public class OfficialController {

    private final OfficialProfileMapper officialProfileMapper;

    public OfficialController(OfficialProfileMapper officialProfileMapper) {
        this.officialProfileMapper = officialProfileMapper;
    }

    /** 获取当前用户的认证状态 */
    @GetMapping("/certification")
    public ApiResponse<OfficialProfileEntity> getCertification() {
        Long userId = LoginUserContext.getUserId();
        if (userId == null) return ApiResponse.error(401, "请先登录");

        OfficialProfileEntity profile = officialProfileMapper.selectOne(
                new LambdaQueryWrapper<OfficialProfileEntity>()
                        .eq(OfficialProfileEntity::getUserId, userId));
        return ApiResponse.success(profile);
    }

    /** 提交认证申请 */
    @PostMapping("/certification")
    public ApiResponse<OfficialProfileEntity> submitCertification(@RequestBody Map<String, String> body) {
        Long userId = LoginUserContext.getUserId();
        if (userId == null) return ApiResponse.error(401, "请先登录");

        // 检查是否已有申请
        OfficialProfileEntity existing = officialProfileMapper.selectOne(
                new LambdaQueryWrapper<OfficialProfileEntity>()
                        .eq(OfficialProfileEntity::getUserId, userId));
        if (existing != null) {
            // 如果已驳回，允许重新提交
            if ("REJECTED".equals(existing.getCertificationStatus())) {
                existing.setCompanyName(body.getOrDefault("companyName", existing.getCompanyName()));
                existing.setContactName(body.getOrDefault("contactName", existing.getContactName()));
                existing.setContactPhone(body.getOrDefault("contactPhone", existing.getContactPhone()));
                existing.setLicenseUrl(body.getOrDefault("licenseUrl", existing.getLicenseUrl()));
                existing.setCertificationStatus("PENDING");
                existing.setRejectReason(null);
                existing.setUpdatedAt(LocalDateTime.now());
                officialProfileMapper.updateById(existing);
                return ApiResponse.success(existing);
            }
            return ApiResponse.error(409, "您已提交过认证申请，请等待审核");
        }

        OfficialProfileEntity profile = new OfficialProfileEntity();
        profile.setUserId(userId);
        profile.setCompanyName(body.getOrDefault("companyName", ""));
        profile.setContactName(body.getOrDefault("contactName", ""));
        profile.setContactPhone(body.getOrDefault("contactPhone", ""));
        profile.setLicenseUrl(body.getOrDefault("licenseUrl", ""));
        profile.setCertificationStatus("PENDING");
        profile.setCreatedAt(LocalDateTime.now());
        profile.setUpdatedAt(LocalDateTime.now());
        officialProfileMapper.insert(profile);

        return ApiResponse.success(profile);
    }

    /** 获取认证申请详情 */
    @GetMapping("/certification/{id}")
    public ApiResponse<OfficialProfileEntity> getCertificationDetail(@PathVariable Long id) {
        OfficialProfileEntity profile = officialProfileMapper.selectById(id);
        if (profile == null) return ApiResponse.error(404, "认证申请不存在");
        return ApiResponse.success(profile);
    }
}