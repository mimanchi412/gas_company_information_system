package com.classwork.gas.service.impl;

import com.classwork.gas.entity.dto.customer.CustomerDtos;
import com.classwork.gas.entity.dto.meter.MeterDtos;
import com.classwork.gas.mapper.CustomerMapper;
import com.classwork.gas.mapper.MeterMapper;
import com.classwork.gas.entity.Customer;
import com.classwork.gas.entity.Meter;
import com.classwork.gas.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerMapper customerMapper;
    private final MeterMapper meterMapper;

    public CustomerServiceImpl(CustomerMapper customerMapper, MeterMapper meterMapper) {
        this.customerMapper = customerMapper;
        this.meterMapper = meterMapper;
    }

    @Override
    public Customer createCustomer(CustomerDtos.CreateOrUpdateCustomerRequest request) {
        Customer customer = Customer.builder()
                .name(request.getName())
                .type(request.getType())
                .contactPhone(request.getContactPhone())
                .email(request.getEmail())
                .address(request.getAddress())
                .createdAt(LocalDateTime.now())
                .build();
        customerMapper.insert(customer);
        return customer;
    }

    @Override
    public Customer updateCustomer(Long id, CustomerDtos.CreateOrUpdateCustomerRequest request) {
        Customer customer = customerMapper.selectById(id);
        if (customer == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "客户不存在");
        }
        customer.setName(request.getName());
        customer.setType(request.getType());
        customer.setContactPhone(request.getContactPhone());
        customer.setEmail(request.getEmail());
        customer.setAddress(request.getAddress());
        customerMapper.updateById(customer);
        return customer;
    }

    @Override
    public Customer getCustomer(Long id) {
        Customer customer = customerMapper.selectById(id);
        if (customer == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "客户不存在");
        }
        return customer;
    }

    @Override
    public List<Customer> search(String keyword) {
        String kw = (keyword == null || keyword.isBlank()) ? null : keyword;
        return customerMapper.search(kw);
    }

    @Override
    public Meter addMeter(Long customerId, MeterDtos.AddMeterRequest request) {
        Customer customer = getCustomer(customerId);
        Long exist = meterMapper.countByMeterNo(request.getMeterNo());
        if (exist != null && exist > 0L) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "表具编号已存在");
        }
        Meter meter = Meter.builder()
                .meterNo(request.getMeterNo())
                .customerId(customer.getId())
                .status(request.getStatus())
                .location(request.getLocation())
                .installedAt(request.getInstalledAt())
                .build();
        meterMapper.insert(meter);
        return meter;
    }

    @Override
    public List<Meter> listMeters(Long customerId) {
        return meterMapper.findByCustomerId(customerId);
    }
}
