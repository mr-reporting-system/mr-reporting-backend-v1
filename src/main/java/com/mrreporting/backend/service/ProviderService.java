package com.mrreporting.backend.service;

import com.mrreporting.backend.dto.ProviderDTO;
import com.mrreporting.backend.dto.ProviderTransferDTO;
import com.mrreporting.backend.entity.*;
import com.mrreporting.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProviderService {

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private DistrictRepository districtRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AreaRepository areaRepository;

    @Transactional
    public Provider saveProvider(ProviderDTO dto) {
        Provider provider = new Provider();

        // 1. Map Basic Fields ✍
        provider.setType(dto.getType());
        provider.setProviderCode(dto.getProviderCode());
        provider.setProviderName(dto.getProviderName());
        provider.setPhone(dto.getPhone());
        provider.setAadhaarNo(dto.getAadhaarNo());

        // 2. Map "Other Info" Fields
        provider.setOwnerName(dto.getOwnerName());
        provider.setOwnerDob(dto.getOwnerDob());
        provider.setOwnerDoa(dto.getOwnerDoa());
        provider.setShopDoa(dto.getShopDoa());
        provider.setAddress(dto.getAddress());
        provider.setCity(dto.getCity());
        provider.setEmail(dto.getEmail());
        provider.setPanCard(dto.getPanCard());
        provider.setGstNumber(dto.getGstNumber());
        provider.setLicenceNo(dto.getLicenceNo());
        provider.setCategory(dto.getCategory());

        // New providers are pending approval
        provider.setIsActive(false);
        provider.setRequestStatus("ADDITION");

        // 3. Set Relationships 🗺
        provider.setState(stateRepository.findById(dto.getStateId())
                .orElseThrow(() -> new RuntimeException("State not found")));

        provider.setDistrict(districtRepository.findById(dto.getDistrictId())
                .orElseThrow(() -> new RuntimeException("District not found")));

        provider.setEmployee(employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found")));

        provider.setArea(areaRepository.findById(dto.getAreaId())
                .orElseThrow(() -> new RuntimeException("Area not found")));

        return providerRepository.save(provider);
    }

    // returns "Approved" providers
    public List<Provider> getAllActiveProviders() {
        return providerRepository.findAll().stream()
                .filter(Provider::getIsActive)
                .toList();
    }

    // Filtered for active entries
    public List<Provider> getProvidersByAreaAndType(Long areaId, String type) {
        return providerRepository.findByAreaIdAndTypeAndIsActiveTrue(areaId, type);
    }

    // Flag it for the Approval Master instead of hard delete
    @Transactional
    public void requestProviderDeletion(Long id) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Provider not found with id: " + id));

        // Stays visible in standard lists but flags for the admin deletion table
        provider.setRequestStatus("DELETION");
        providerRepository.save(provider);
    }

    @Transactional
    public void transferProviders(ProviderTransferDTO dto) {
        Employee newEmployee = employeeRepository.findById(dto.getNewEmployeeId())
                .orElseThrow(() -> new RuntimeException("New Employee not found"));

        Area newArea = areaRepository.findById(dto.getNewAreaId())
                .orElseThrow(() -> new RuntimeException("New Area not found"));

        List<Provider> providersToTransfer = providerRepository.findAllById(dto.getProviderIds());

        for (Provider provider : providersToTransfer) {
            provider.setEmployee(newEmployee);
            provider.setArea(newArea);
        }

        providerRepository.saveAll(providersToTransfer);
    }
}