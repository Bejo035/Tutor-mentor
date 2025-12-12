package ge.batumi.tutormentor.utils;

import ge.batumi.tutormentor.model.db.UserDb;
import ge.batumi.tutormentor.model.request.UserRequest;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void copyProperties(UserRequest dto, @MappingTarget UserDb entity);
}
