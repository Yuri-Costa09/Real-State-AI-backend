package com.yuricosta.real_state_ai_backend.properties.useCases;

import com.yuricosta.real_state_ai_backend.properties.Property;
import com.yuricosta.real_state_ai_backend.properties.repositories.PropertyRepository;
import com.yuricosta.real_state_ai_backend.shared.errors.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PropertyService {

    private final PropertyRepository propertyRepository;

    public PropertyService(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }


    public Page<Property> getAllProperties(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return propertyRepository.findAll(pageable);
    }

    public Property getPropertyById(UUID id) {
        return propertyRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Property not found."));
    }

    @Transactional
    public void deleteProperty(UUID id) {
        propertyRepository.deleteById(id);
    }

}
