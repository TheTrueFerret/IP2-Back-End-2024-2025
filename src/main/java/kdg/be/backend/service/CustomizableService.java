package kdg.be.backend.service;

import kdg.be.backend.controller.dto.customization.CustomizableDto;
import kdg.be.backend.repository.CustomizableRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomizableService {

    private final CustomizableRepository customizableRepository;

    public CustomizableService(CustomizableRepository customizableRepository) {
        this.customizableRepository = customizableRepository;
    }

    public List<CustomizableDto> getAllCustomizables() {
        return customizableRepository.findAll().stream().map(customizable -> new CustomizableDto(customizable.getName(), customizable.getDescription(), customizable.getColor(), customizable.getPoints())).toList();
    }
}
