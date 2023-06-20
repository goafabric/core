package org.goafabric.core.data.logic;

import org.goafabric.core.data.controller.dto.Organization;
import org.goafabric.core.crossfunctional.DurationLog;
import org.goafabric.core.data.logic.mapper.OrganizationMapper;
import org.goafabric.core.data.persistence.OrganizationRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@DurationLog
@Transactional
public class OrganizationLogic {
    private final OrganizationMapper organizationMapper;

    private final OrganizationRepository organizationRepository;

    public OrganizationLogic(OrganizationMapper organizationMapper, OrganizationRepository organizationRepository) {
        this.organizationMapper = organizationMapper;
        this.organizationRepository = organizationRepository;
    }

    public Organization getById(String id) {
        return organizationMapper.map(
                organizationRepository.findById(id).get());
    }

    public List<Organization> findAll() {
        return organizationMapper.map(
                organizationRepository.findAll());
    }

    public List<Organization> findByName(String name) {
        return organizationMapper.map(
                organizationRepository.findByName(name));
    }



}