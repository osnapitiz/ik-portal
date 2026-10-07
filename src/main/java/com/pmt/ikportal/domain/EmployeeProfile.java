package com.pmt.ikportal.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "employee_profile", uniqueConstraints = @UniqueConstraint(name = "uk_profile_user", columnNames = "user_id"))
public class EmployeeProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    // --- Kişisel bilgiler ---
    @NotBlank(message = "Ad zorunludur")
    @Size(max = 80)
    @Column(name = "first_name", length = 80)
    private String firstName;
    @NotBlank(message = "Soyad zorunludur")
    @Size(max = 80)
    @Column(name = "last_name", length = 80)
    private String lastName;
    @Pattern(regexp = "^$|^[0-9]{11}$", message = "TC Kimlik No 11 haneli olmalıdır")
    @Column(name = "national_id", length = 11)
    private String nationalId;
    @NotNull(message = "Doğum tarihi zorunludur")
    @Past(message = "Doğum tarihi geçmiş bir tarih olmalıdır")
    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Size(max = 30)
    @Column(name = "phone", length = 30)
    private String phone;

    @Size(max = 300)
    @Column(name = "address", length = 300)
    private String address;

    @Size(max = 60)
    @Column(name = "city", length = 60)
    private String city;

    @Column(name = "marital_status", length = 20)
    private String maritalStatus;

    @Column(name = "blood_type", length = 10)
    private String bloodType;

    // --- İş bilgileri ---

    @NotBlank(message = "Çalıştığınız yer zorunludur")
    @Size(max = 150)
    @Column(name = "company_name", length = 150)
    private String companyName;

    @Size(max = 120)
    @Column(name = "department", length = 120)
    private String department;

    @Size(max = 120)
    @Column(name = "position", length = 120)
    private String position;

    @Size(max = 120)
    @Column(name = "manager_name", length = 120)
    private String managerName;

    @Size(max = 150)
    @Column(name = "work_location", length = 150)
    private String workLocation;

    @Column(name = "employment_type", length = 40)
    private String employmentType;

    @NotNull(message = "İşe başlama tarihi zorunludur")
    @Column(name = "start_date")
    private LocalDate startDate;

    // --- Maaş / banka ---

    @NotNull(message = "Maaş bilgisi zorunludur")
    @DecimalMin(value = "0.0", message = "Maaş negatif olamaz")
    @Digits(integer = 12, fraction = 2, message = "Geçerli bir maaş tutarı giriniz")
    @Column(name = "salary", precision = 14, scale = 2)
    private BigDecimal salary;

    @Column(name = "currency", length = 5)
    private String currency = "TRY";

    @Size(max = 120)
    @Column(name = "bank_name", length = 120)
    private String bankName;

    @Pattern(regexp = "^$|^TR[0-9]{24}$", message = "IBAN 'TR' ile başlamalı ve 26 karakter olmalıdır")
    @Column(name = "iban", length = 34)
    private String iban;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Transient
    public String getFullName() {
        String first = firstName == null ? "" : firstName;
        String last = lastName == null ? "" : lastName;
        return (first + " " + last).trim();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getNationalId() { return nationalId; }
    public void setNationalId(String nationalId) { this.nationalId = nationalId; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getMaritalStatus() { return maritalStatus; }
    public void setMaritalStatus(String maritalStatus) { this.maritalStatus = maritalStatus; }

    public String getBloodType() { return bloodType; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getManagerName() { return managerName; }
    public void setManagerName(String managerName) { this.managerName = managerName; }

    public String getWorkLocation() { return workLocation; }
    public void setWorkLocation(String workLocation) { this.workLocation = workLocation; }

    public String getEmploymentType() { return employmentType; }
    public void setEmploymentType(String employmentType) { this.employmentType = employmentType; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public BigDecimal getSalary() { return salary; }
    public void setSalary(BigDecimal salary) { this.salary = salary; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }

    public String getIban() { return iban; }
    public void setIban(String iban) { this.iban = iban; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
