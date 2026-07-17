package com.ashram.repository;

import com.ashram.entity.Offering;
import com.ashram.entity.OfferingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OfferingRepository extends JpaRepository<Offering, Long> {
    Optional<Offering> findByRazorpayOrderId(String razorpayOrderId);
    List<Offering> findByStatusOrderByPaidAtDesc(OfferingStatus status);
}
