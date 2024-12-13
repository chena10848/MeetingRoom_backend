package com.example.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.demo.Entity.MeetingRoomName;

import java.util.List;

@Repository
public interface MeetingRoomNameRepository extends JpaRepository<MeetingRoomName, Long> {

    @Query("SELECT mrn.meetingroomName FROM MeetingRoomName mrn WHERE mrn.id = :lesseeRoomId")
    String getMeetingRoomName(@Param("lesseeRoomId") Integer lesseeRoomId);

    @Query("SELECT mrn.id, mrn.meetingroomName FROM MeetingRoomName mrn")
    List<Object[]> getMeetingRoomId();
}
