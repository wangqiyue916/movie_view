package com.example.movie.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.movie.common.config.LoginUserContext;
import com.example.movie.common.config.RequireRole;
import com.example.movie.common.response.ApiResponse;
import com.example.movie.user.entity.RoleEntity;
import com.example.movie.user.entity.UserEntity;
import com.example.movie.user.entity.UserRoleEntity;
import com.example.movie.user.mapper.RoleMapper;
import com.example.movie.user.mapper.UserMapper;
import com.example.movie.user.mapper.UserRoleMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 超管专属：用户与角色管理
 */
@RestController
@RequestMapping("/api/admin/users")
@RequireRole("SUPER_ADMIN")
public class AdminUserController {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;

    public AdminUserController(UserMapper userMapper, RoleMapper roleMapper, UserRoleMapper userRoleMapper) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.userRoleMapper = userRoleMapper;
    }

    /**
     * GET /api/admin/users
     * 列出所有用户（含角色标签），支持按 username 搜索
     */
    @GetMapping
    public ApiResponse<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword) {

        LambdaQueryWrapper<UserEntity> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(UserEntity::getUsername, keyword.trim());
        }
        wrapper.orderByDesc(UserEntity::getCreatedAt);
        Page<UserEntity> result = userMapper.selectPage(new Page<>(page, pageSize), wrapper);

        // 一次性查出所有角色 ID->code 映射
        Map<Long, String> roleCodeMap = roleMapper.selectList(null)
            .stream().collect(Collectors.toMap(RoleEntity::getId, RoleEntity::getCode));

        // 查出当前页所有用户的角色
        List<Long> userIds = result.getRecords().stream().map(UserEntity::getId).toList();
        List<UserRoleEntity> allUserRoles = userIds.isEmpty()
            ? List.of()
            : userRoleMapper.selectList(new LambdaQueryWrapper<UserRoleEntity>().in(UserRoleEntity::getUserId, userIds));

        Map<Long, List<String>> userRolesMap = new HashMap<>();
        for (UserRoleEntity ur : allUserRoles) {
            String code = roleCodeMap.get(ur.getRoleId());
            if (code != null) {
                userRolesMap.computeIfAbsent(ur.getUserId(), k -> new ArrayList<>()).add(code);
            }
        }

        List<Map<String, Object>> items = new ArrayList<>();
        for (UserEntity user : result.getRecords()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", user.getId());
            item.put("username", user.getUsername());
            item.put("nickname", user.getNickname());
            item.put("email", user.getEmail());
            item.put("phone", user.getPhone());
            item.put("status", user.getStatus());
            item.put("roles", userRolesMap.getOrDefault(user.getId(), List.of()));
            item.put("lastLoginAt", user.getLastLoginAt());
            item.put("createdAt", user.getCreatedAt());
            items.add(item);
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("list", items);
        data.put("page", page);
        data.put("pageSize", pageSize);
        data.put("total", result.getTotal());
        return ApiResponse.success(data);
    }

    /**
     * PUT /api/admin/users/{id}/roles
     * 超管直接设置用户角色集合
     */
    @PutMapping("/{id}/roles")
    @Transactional
    public ApiResponse<List<String>> setRoles(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        UserEntity user = userMapper.selectById(id);
        if (user == null) return ApiResponse.error(404, "用户不存在");

        @SuppressWarnings("unchecked")
        List<String> roleCodes = (List<String>) body.get("roles");
        if (roleCodes == null || roleCodes.isEmpty()) {
            return ApiResponse.error(400, "角色列表不能为空");
        }

        // 删除旧角色
        userRoleMapper.delete(new LambdaQueryWrapper<UserRoleEntity>().eq(UserRoleEntity::getUserId, id));

        // 插入新角色
        for (String code : roleCodes) {
            RoleEntity role = roleMapper.selectOne(new LambdaQueryWrapper<RoleEntity>().eq(RoleEntity::getCode, code));
            if (role == null) continue;
            UserRoleEntity ur = new UserRoleEntity();
            ur.setUserId(id);
            ur.setRoleId(role.getId());
            ur.setCreatedAt(LocalDateTime.now());
            userRoleMapper.insert(ur);
        }

        // 返回当前角色
        List<String> current = userRoleMapper.selectList(
            new LambdaQueryWrapper<UserRoleEntity>().eq(UserRoleEntity::getUserId, id))
            .stream()
            .map(ur -> roleMapper.selectById(ur.getRoleId()).getCode())
            .toList();
        return ApiResponse.success(current);
    }

    /**
     * PUT /api/admin/users/{id}/ban
     */
    @PutMapping("/{id}/ban")
    @Transactional
    public ApiResponse<Void> ban(@PathVariable Long id) {
        if (LoginUserContext.getUserId().equals(id)) {
            return ApiResponse.error(400, "不能封禁自己的账号");
        }
        UserEntity user = userMapper.selectById(id);
        if (user == null) return ApiResponse.error(404, "用户不存在");
        if ("BANNED".equals(user.getStatus())) return ApiResponse.error(400, "用户已被封禁");
        user.setStatus("BANNED");
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
        return ApiResponse.success();
    }

    /**
     * PUT /api/admin/users/{id}/unban
     */
    @PutMapping("/{id}/unban")
    @Transactional
    public ApiResponse<Void> unban(@PathVariable Long id) {
        UserEntity user = userMapper.selectById(id);
        if (user == null) return ApiResponse.error(404, "用户不存在");
        if ("NORMAL".equals(user.getStatus())) return ApiResponse.error(400, "用户状态正常，无需解封");
        user.setStatus("NORMAL");
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
        return ApiResponse.success();
    }

    /**
     * POST /api/admin/users/{id}/reset-password
     */
    @PostMapping("/{id}/reset-password")
    @Transactional
    public ApiResponse<Void> resetPassword(@PathVariable Long id, @RequestBody Map<String, String> body) {
        UserEntity user = userMapper.selectById(id);
        if (user == null) return ApiResponse.error(404, "用户不存在");
        String newPassword = body.getOrDefault("password", "123456");
        user.setPasswordHash(newPassword);
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
        return ApiResponse.success();
    }
}