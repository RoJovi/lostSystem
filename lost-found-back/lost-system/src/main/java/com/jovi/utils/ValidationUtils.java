package com.jovi.utils;

import java.util.regex.Pattern;

public class ValidationUtils {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^1[3-9]\\d{9}$");

    // 密码长度限制
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MAX_PASSWORD_LENGTH = 20;

    // 管理员工号正则（4-10位纯数字）
    private static final Pattern ADMIN_NUM_PATTERN =
            Pattern.compile("^\\d{4,10}$");

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * 校验密码长度
     */
    public static boolean isValidPassword(String password) {
        return password != null &&
                password.length() >= MIN_PASSWORD_LENGTH &&
                password.length() <= MAX_PASSWORD_LENGTH;
    }

    /**
     * 获取密码长度要求提示
     */
    public static String getPasswordRequirement() {
        return "密码长度应为" + MIN_PASSWORD_LENGTH + "-" + MAX_PASSWORD_LENGTH + "位";
    }

    /**
     * 校验管理员工号格式
     */
    public static boolean isValidAdminNum(String adminNum) {
        return adminNum != null && ADMIN_NUM_PATTERN.matcher(adminNum).matches();
    }

    /**
     * 获取工号要求提示
     */
    public static String getAdminNumRequirement() {
        return "工号应为4-10位纯数字";
    }
}
