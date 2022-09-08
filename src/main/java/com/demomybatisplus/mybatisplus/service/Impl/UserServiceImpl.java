package com.demomybatisplus.mybatisplus.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demomybatisplus.mybatisplus.exception.UserAlreadyExistException;
import com.demomybatisplus.mybatisplus.exception.UserNotExistException;
import com.demomybatisplus.mybatisplus.mapper.UserMapper;
import com.demomybatisplus.mybatisplus.model.User;
import com.demomybatisplus.mybatisplus.model.dto.UserQueryReq;
import com.demomybatisplus.mybatisplus.service.UserService;
import com.demomybatisplus.mybatisplus.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;


//    @Override
//    public List<User> queryUser(UserQueryReq req) {
//        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        queryWrapper.like("is_deleted", 0).or().isNull("email");
//        return userMapper.selectList(queryWrapper);
//    }

//    @Override
//    public IPage<User> queryUser(@RequestBody UserQueryReq req) {
//        IPage<User> userIPageQueryPOIPage = userService.queryAccounts(req);
//        return ResponseEntity.ok(userIPageQueryPOIPage);
//    }

    @Override
    public ResponseEntity<User> saveUser(User user) {
        if(UserUtils.validateUsernameIfExisting(userMapper, user)) {
            throw new UserAlreadyExistException(String.format("Username: %s already exist.", user.getName()));
        }
        userService.save(user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<User> updateUser(User user) {
        if(UserUtils.validateUsernameIfExisting(userMapper, user)) {
            throw new UserNotExistException(String.format("Username: %s is not existing.", user.getName()));
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("id", user.getId());
        userMapper.update(user, queryWrapper);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<User> deleteUser(Long id) {
        if(UserUtils.validateIDifExisting(userMapper, id)) {
            throw new UserNotExistException(String.format("User Id: %d is not existing.", id));
        }
        userMapper.deleteById(id);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @Override
    public Boolean restoreUser(Long id) {
        return userMapper.restoreUser(id);
    }


    @Override
    public IPage<User> queryList(UserQueryReq req) {
        IPage<User> userIPageQueryPOIPage = new Page<>();
        userIPageQueryPOIPage.setCurrent(req.getCurrent());
        userIPageQueryPOIPage.setSize(req.getSize());

        List<User> userQueryPOList = userMapper.queryList(userIPageQueryPOIPage,  req);
        List<User> userQueryPOListTotal = userMapper.queryList(null, req);
        userIPageQueryPOIPage.setTotal(userQueryPOListTotal.size());
        return userIPageQueryPOIPage.setRecords(userQueryPOList);
    }

    @Override
    public IPage<User> queryDeletedList(UserQueryReq req) {
        IPage<User> userIPageQueryPOIPage = new Page<>();
        userIPageQueryPOIPage.setCurrent(req.getCurrent());
        userIPageQueryPOIPage.setSize(req.getSize());

        List<User> userQueryPOList = userMapper.queryDeletedList(userIPageQueryPOIPage,  req);
        List<User> userQueryPOListTotal = userMapper.queryDeletedList(null, req);
        userIPageQueryPOIPage.setTotal(userQueryPOListTotal.size());
        return userIPageQueryPOIPage.setRecords(userQueryPOList);
    }

}