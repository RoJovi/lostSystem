package com.jovi.service;

import com.jovi.pojo.Post;
import com.jovi.pojo.UpdatePostRequest;

import java.util.List;
import java.util.Map;

public interface PostService {

    List<Post> getAll(Integer userId);

    boolean deletePost(String type, Integer id, Integer userId);

    boolean updatePost(String type, Integer id, UpdatePostRequest request, Integer userId);

    boolean setResolved(String type, Integer id, Integer userId);
}
