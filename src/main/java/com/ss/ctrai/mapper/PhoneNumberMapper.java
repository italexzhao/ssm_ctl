package com.ss.ctrai.mapper;

import com.ss.ctrai.entity.PhoneNumber;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PhoneNumberMapper {

    @Select("SELECT * FROM phone_numbers " +
            "WHERE status = true " +
            "AND last_used_time IS NULL " +  // 首先只选择未使用的号码
            "ORDER BY phone_number ASC " +    // 按号码升序排序
            "LIMIT 1 " +
            "FOR UPDATE")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "phoneNumber", column = "phone_number"),
            @Result(property = "type", column = "type"),
            @Result(property = "wechatNumber", column = "wechat_number"),
            @Result(property = "status", column = "status"),
            @Result(property = "lastUsedTime", column = "last_used_time"),
            @Result(property = "version", column = "version"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "updateTime", column = "update_time")
    })
    PhoneNumber findNextAvailableNumber();

    @Select("SELECT * FROM phone_numbers " +
            "WHERE status = true " +
            "AND last_used_time = (" +
            "    SELECT MIN(last_used_time) " +
            "    FROM phone_numbers " +
            "    WHERE status = true " +
            "    AND last_used_time IS NOT NULL" +
            ") " +
            "ORDER BY phone_number ASC " +
            "LIMIT 1 " +
            "FOR UPDATE")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "phoneNumber", column = "phone_number"),
            @Result(property = "type", column = "type"),
            @Result(property = "wechatNumber", column = "wechat_number"),
            @Result(property = "status", column = "status"),
            @Result(property = "lastUsedTime", column = "last_used_time"),
            @Result(property = "version", column = "version"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "updateTime", column = "update_time")
    })
    PhoneNumber findLeastUsedNumber();

    @Select("SELECT * FROM phone_numbers")
    List<PhoneNumber> findAll();

    @Select("SELECT * FROM phone_numbers WHERE id = #{id}")
    PhoneNumber findById(Long id);

    @Select("SELECT COUNT(*) FROM phone_numbers WHERE phone_number = #{phoneNumber}")
    int existsByPhoneNumber(String phoneNumber);

    @Insert("INSERT INTO phone_numbers (phone_number, type, wechat_number, status) " +
            "VALUES (#{phoneNumber}, #{type}, #{wechatNumber}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(PhoneNumber phoneNumber);

    @Update("UPDATE phone_numbers SET status = #{status}, " +
            "version = version + 1 " +
            "WHERE id = #{id} AND version = #{version}")
    int updateStatus(PhoneNumber phoneNumber);

    @Update("UPDATE phone_numbers " +
            "SET last_used_time = #{lastUsedTime}, " +
            "    version = version + 1, " +
            "    update_time = CURRENT_TIMESTAMP " +
            "WHERE id = #{id} " +
            "AND version = #{version} " +
            "AND status = true " +
            "AND (last_used_time IS NULL OR last_used_time < #{lastUsedTime})")
    int updateLastUsedTime(PhoneNumber phoneNumber);

    @Update("UPDATE phone_numbers SET wechat_number = #{wechatNumber}, " +
            "version = version + 1 " +
            "WHERE id = #{id} AND version = #{version}")
    int updateWechatNumber(PhoneNumber phoneNumber);

    @Delete("DELETE FROM phone_numbers WHERE id = #{id}")
    int deleteById(Long id);
}