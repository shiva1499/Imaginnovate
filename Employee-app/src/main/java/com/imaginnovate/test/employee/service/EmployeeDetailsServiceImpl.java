package com.imaginnovate.test.employee.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.imaginnovate.test.employee.dto.EmployeeDto;
import com.imaginnovate.test.employee.exception.ResourceNotFoundExceptiont;
import com.imaginnovate.test.employee.model.Employee;
import com.imaginnovate.test.employee.model.EmployeeTaxDetails;
import com.imaginnovate.test.employee.repositopre.EmployeeDetailsRepository;

 

@Service
public class EmployeeDetailsServiceImpl implements EmployeeDetailsService {

 
	@Autowired
	private EmployeeDetailsRepository employeeRepository;

	@Autowired
	private ModelMapper modelMapper;

	@Override
	public EmployeeDto createEmployee(EmployeeDto employeedto) {

 		Employee employee = modelMapper.map(employeedto, Employee.class);
		EmployeeDto empDto = null;
		Employee emp = null;
		try {
			emp = employeeRepository.save(employee);

		} catch (Exception e) {
 			throw new ResourceNotFoundExceptiont("employee allready existed");
		}
		empDto = modelMapper.map(emp, EmployeeDto.class);
		return empDto;

	}

	@Override
	public List<EmployeeDto> getAllEmployee(int pageno, int psgesize, String sortby, String sortdrc) {
 		Sort sort = sortdrc.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortby).ascending()
				: Sort.by(sortby).descending();
		Pageable pag = PageRequest.of(pageno, psgesize, sort);
		Page<Employee> allemploylist = this.employeeRepository.findAll(pag);
		List<EmployeeDto> allemploydtolist = allemploylist.stream()
				.map((Employee) -> modelMapper.map(Employee, EmployeeDto.class)).collect(Collectors.toList());
		return allemploydtolist;
	}

	@Override
	public Double taxdeductionForCurrentYear(Long id) {

		Double tax = 0.0;
		Double cess = 0.0;
		EmployeeTaxDetails employeetaxdetails = new EmployeeTaxDetails();
		Employee employeebyid = employeeRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundExceptiont("EmployeeNotFoundException"));

		EmployeeDto employeeDto = modelMapper.map(employeebyid, EmployeeDto.class);
		LocalDate employeestartingdate = employeeDto.getDoj();
		LocalDate employeeendingdate = LocalDate.now();
		long totalemployeemonthsworking = ChronoUnit.MONTHS.between(employeestartingdate, employeeendingdate);
		Double totalsalaryofemployee = employeeDto.getSalary() * totalemployeemonthsworking;

		if (totalsalaryofemployee > 250000 && totalsalaryofemployee <= 500000) {

			tax = (totalsalaryofemployee - 250000) * 0.05;

		} else if (totalsalaryofemployee > 500000 && totalsalaryofemployee <= 1000000) {
			tax = 12500 + (totalsalaryofemployee - 500000) * 0.1;
		} else if (totalsalaryofemployee > 1000000) {

			tax = 112500 + (totalsalaryofemployee - 1000000) * 0.2;
		}

		if (totalsalaryofemployee > 2500000) {
			cess = (totalsalaryofemployee - 2500000) * 0.02;
		}
		double employeetotaltax = tax + cess;
		employeetaxdetails.setEmployeeID(employeeDto.getEmpId());
		employeetaxdetails.setFirstName(employeeDto.getFirstname());
		employeetaxdetails.setLastName(employeeDto.getLastname());
		employeetaxdetails.setEmail(employeeDto.getEmail());
		employeetaxdetails.setDOJ(employeeDto.getDoj());
		employeetaxdetails.setSalary(employeeDto.getSalary());
		employeetaxdetails.setTotalTexAmount(tax);
		employeetaxdetails.setTotalCessAmount(cess);

		return employeetotaltax;
	}

}
