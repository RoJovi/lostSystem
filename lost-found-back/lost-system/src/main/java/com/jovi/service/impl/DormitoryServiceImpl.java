package com.jovi.service.impl;

import com.jovi.mapper.DormitoryMapper;
import com.jovi.pojo.Location;
import com.jovi.service.DormitoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DormitoryServiceImpl implements DormitoryService {

    @Autowired
    private DormitoryMapper dormitoryMapper;

    @Override
    public List<Location> list() {
        return dormitoryMapper.findAll();
    }
}
