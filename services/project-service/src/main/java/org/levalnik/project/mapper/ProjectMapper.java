package org.levalnik.project.mapper;

import org.levalnik.dto.projectDto.ProjectResponse;
import org.levalnik.dto.projectDto.ProjectRequest;
import org.levalnik.project.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    ProjectMapper INSTANCE = Mappers.getMapper(ProjectMapper.class);

    @Mapping(target = "id", source = "id")
    ProjectResponse toDTO(Project project);

    @Mapping(target = "id", ignore = true)
    Project toEntity(ProjectRequest projectDTO);
}