package com.example.movie.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.movie.auth.dto.LoginRequest;
import com.example.movie.auth.dto.RegisterRequest;
import com.example.movie.auth.service.AuthService;
import com.example.movie.auth.vo.CurrentUserVO;
import com.example.movie.auth.vo.LoginResponse;
import com.example.movie.common.enums.RoleCodeEnum;
import com.example.movie.common.exception.BusinessException;
import com.example.movie.common.exception.ErrorCode;
import com.example.movie.common.util.JwtUtil;
import com.example.movie.user.entity.RoleEntity;
import com.example.movie.user.entity.UserEntity;
import com.example.movie.user.entity.UserRoleEntity;
import com.example.movie.user.mapper.RoleMapper;
import com.example.movie.user.mapper.UserMapper;
import com.example.movie.user.mapper.UserRoleMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;

    public AuthServiceImpl(UserMapper userMapper, UserRoleMapper userRoleMapper, RoleMapper roleMapper) {
        this.userMapper = userMapper;
        this.userRoleMapper = userRoleMapper;
        this.roleMapper = roleMapper;
    }

    @Override
    @Transactional
    public LoginResponse register(RegisterRequest request) {
        if (userMapper.selectOne(new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getUsername, request.username())) != null) {
            throw new BusinessException(ErrorCode.CONFLICT, "用户名已存在");
        }

        UserEntity user = new UserEntity();
        user.setUsername(request.username());
        user.setPasswordHash(request.password());
        user.setNickname(request.nickname() != null ? request.nickname() : request.username());
        user.setStatus("NORMAL");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.insert(user);

        RoleEntity role = roleMapper.selectOne(new LambdaQueryWrapper<RoleEntity>()
                .eq(RoleEntity::getCode, RoleCodeEnum.USER.name()));
        if (role == null) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "默认角色未初始化");
        }

        UserRoleEntity userRole = new UserRoleEntity();
        userRole.setUserId(user.getId());
        userRole.setRoleId(role.getId());
        userRole.setCreatedAt(LocalDateTime.now());
        userRoleMapper.insert(userRole);

        List<String> roles = List.of(RoleCodeEnum.USER.name());
        String token = JwtUtil.generateToken(user.getId(), user.getUsername(), roles);

        CurrentUserVO currentUser = new CurrentUserVO(user.getId(), user.getUsername(),
                user.getNickname(), user.getAvatarUrl(), roles);
        return new LoginResponse(token, currentUser);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        UserEntity user = userMapper.selectOne(new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getUsername, request.username()));
        if (user == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户名或密码错误");
        }

        if ("BANNED".equals(user.getStatus())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "账号已被封禁");
        }

        if (!request.password().equals(user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户名或密码错误");
        }

        user.setLastLoginAt(LocalDateTime.now());
        userMapper.updateById(user);

        List<String> roles = userRoleMapper.selectList(
                        new LambdaQueryWrapper<UserRoleEntity>()
                                .eq(UserRoleEntity::getUserId, user.getId()))
                .stream()
                .map(ur -> roleMapper.selectById(ur.getRoleId()).getCode())
                .toList();

        String token = JwtUtil.generateToken(user.getId(), user.getUsername(), roles);

        CurrentUserVO currentUser = new CurrentUserVO(user.getId(), user.getUsername(),
                user.getNickname(), user.getAvatarUrl(), roles);
        return new LoginResponse(token, currentUser);
    }
}