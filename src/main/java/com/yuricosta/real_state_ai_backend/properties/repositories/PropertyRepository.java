package com.yuricosta.real_state_ai_backend.properties.repositories;

import com.yuricosta.real_state_ai_backend.properties.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface PropertyRepository extends JpaRepository<Property, UUID>,
        JpaSpecificationExecutor<Property> {

    @Override
    Page<Property> findAll(Pageable pageable);
}