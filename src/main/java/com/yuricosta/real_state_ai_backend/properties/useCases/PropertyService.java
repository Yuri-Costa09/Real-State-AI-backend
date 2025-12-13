package com.yuricosta.real_state_ai_backend.properties.useCases;

import com.yuricosta.real_state_ai_backend.properties.Property;
import com.yuricosta.real_state_ai_backend.properties.repositories.PropertyRepository;
import com.yuricosta.real_state_ai_backend.shared.errors.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PropertyService {

    private final PropertyRepository propertyRepository;

    public PropertyService(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }


    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
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
