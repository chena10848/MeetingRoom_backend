package com.example.demo.Repository;

import com.example.demo.Entity.MeetingGroupUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface MeetingGroupUserRepository extends JpaRepository<MeetingGroupUser, Long> {

    @Transactional
    @Modifying
    @Query("DELETE FROM MeetingGroupUser mg " +
            "WHERE mg.meetingroomId = :meetingroomId")
    void deleteMeetingGroup(@Param("meetingroomId") Integer meetingroomId);

    @Transactional
    @Modifying
    @Query("DELETE FROM MeetingGroupUser mg " +
            "WHERE mg.meetingroomId = :meetingroomId " +
            "AND mg.userId = :userId")
    void deleteMeetingGroup(@Param("meetingroomId") Integer meetingroomId,
                            @Param("userId") Integer userId);

    @Query("SELECT u.id, u.userName " +
            "FROM MeetingGroupUser mg " +
            "INNER JOIN User u " +
            "ON mg.userId = u.id " +
            "WHERE mg.meetingroomId = :meetingroomId")
    List<Object[]> getMeetingGroupUserId(@Param("meetingroomId") Integer meetingroomId);

    @Transactional
    @Modifying
    @Query("DELETE FROM MeetingGroupUser mgu WHERE mgu.meetingroomId = :Id")
    void deleteMeetingGroupUsersByRoomId(@Param("Id") Integer Id);

}
