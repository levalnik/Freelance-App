package org.levalnik.mapper;

import org.levalnik.DTO.ProjectDTO;
import org.levalnik.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    ProjectMapper INSTANCE = Mappers.getMapper(ProjectMapper.class);

    @Mapping(target = "id", source = "id")
    ProjectDTO toDTO(Project project);

    @Mapping(target = "id", ignore = true)
    Project toEntity(ProjectDTO projectDTO);
}