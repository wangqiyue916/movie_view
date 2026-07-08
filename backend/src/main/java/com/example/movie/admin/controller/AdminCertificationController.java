package com.example.movie.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.movie.common.enums.RoleCodeEnum;
import com.example.movie.common.response.ApiResponse;
import com.example.movie.common.response.PageResult;
import com.example.movie.official.entity.OfficialProfileEntity;
import com.example.movie.official.mapper.OfficialProfileMapper;
import com.example.movie.user.entity.RoleEntity;
import com.example.movie.user.entity.UserRoleEntity;
import com.example.movie.user.mapper.RoleMapper;
import com.example.movie.user.mapper.UserRoleMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminCertificationController {

    private final OfficialProfileMapper officialProfileMapper;
    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;

    public AdminCertificationController(OfficialProfileMapper officialProfileMapper,
                                         UserRoleMapper userRoleMapper,
                                         RoleMapper roleMapper) {
        this.officialProfileMapper = officialProfileMapper;
        this.userRoleMapper = userRoleMapper;
        this.roleMapper = roleMapper;
    }

    /** 获取认证审核列表 */
    @GetMapping("/certifications")
    public ApiResponse<PageResult<OfficialProfileEntity>> listCertifications(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false, defaultValue = "") String status) {

        LambdaQueryWrapper<OfficialProfileEntity> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq(OfficialProfileEntity::getCertificationStatus, status);
        }
        wrapper.orderByDesc(OfficialProfileEntity::getCreatedAt);

        Page<OfficialProfileEntity> result = officialProfileMapper.selectPage(
                new Page<>(page, pageSize), wrapper);

        return ApiResponse.success(new PageResult<>(
                result.getRecords(),
                (int) result.getCurrent(),
                (int) result.getSize(),
                result.getTotal()));
    }

    /** 审核通过 */
    @PutMapping("/certifications/{id}/approve")
    @Transactional
    public ApiResponse<String> approveCertification(@PathVariable Long id) {
        OfficialProfileEntity profile = officialProfileMapper.selectById(id);
        if (profile == null) {
            return ApiResponse.error(404, "认证申请不存在");
        }
        if (!"PENDING".equals(profile.getCertificationStatus())) {
            return ApiResponse.error(400, "该申请当前状态不允许通过审核");
        }

        // 更新认证状态
        profile.setCertificationStatus("APPROVED");
        profile.setCertifiedAt(LocalDateTime.now());
        profile.setUpdatedAt(LocalDateTime.now());
        profile.setRejectReason(null);
        officialProfileMapper.updateById(profile);

        // 分配 OFFICIAL 角色
        RoleEntity officialRole = roleMapper.selectOne(
                new LambdaQueryWrapper<RoleEntity>()
                        .eq(RoleEntity::getCode, RoleCodeEnum.OFFICIAL.name()));
        if (officialRole != null) {
            boolean hasRole = userRoleMapper.selectCount(
                    new LambdaQueryWrapper<UserRoleEntity>()
                            .eq(UserRoleEntity::getUserId, profile.getUserId())
                            .eq(UserRoleEntity::getRoleId, officialRole.getId())) > 0;
            if (!hasRole) {
                UserRoleEntity userRole = new UserRoleEntity();
                userRole.setUserId(profile.getUserId());
                userRole.setRoleId(officialRole.getId());
                userRole.setCreatedAt(LocalDateTime.now());
                userRoleMapper.insert(userRole);
            }
        }

        return ApiResponse.success("认证审核已通过");
    }

    /** 审核驳回 */
    @PutMapping("/certifications/{id}/reject")
    public ApiResponse<String> rejectCertification(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        OfficialProfileEntity profile = officialProfileMapper.selectById(id);
        if (profile == null) {
            return ApiResponse.error(404, "认证申请不存在");
        }
        if (!"PENDING".equals(profile.getCertificationStatus())) {
            return ApiResponse.error(400, "该申请当前状态不允许驳回");
        }

        String reason = body.getOrDefault("rejectReason", "");
        if (reason.isEmpty()) {
            return ApiResponse.error(400, "驳回原因不能为空");
        }

        profile.setCertificationStatus("REJECTED");
        profile.setRejectReason(reason);
        profile.setUpdatedAt(LocalDateTime.now());
        officialProfileMapper.updateById(profile);

        return ApiResponse.success("认证已驳回");
    }
}