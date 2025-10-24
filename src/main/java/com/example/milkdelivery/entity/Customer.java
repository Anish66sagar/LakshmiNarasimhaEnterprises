
  // IntelliJ API Decompiler stub source generated from a class file
  // Implementation of methods is not available

package com.example.milkdelivery.entity;

@jakarta.persistence.Entity
@jakarta.persistence.Table(name = "customer")
public class Customer {
    @jakarta.persistence.Id
    @jakarta.persistence.GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private java.lang.Long id;
    @jakarta.validation.constraints.NotBlank
    private java.lang.@jakarta.validation.constraints.NotBlank String name;
    @jakarta.validation.constraints.NotBlank
    @jakarta.persistence.Column(unique = true)
    private java.lang.@jakarta.validation.constraints.NotBlank String phone;
    @jakarta.validation.constraints.NotBlank
    private java.lang.@jakarta.validation.constraints.NotBlank String address;
    @jakarta.validation.constraints.NotBlank
    private java.lang.@jakarta.validation.constraints.NotBlank String passwordHash;
    @jakarta.persistence.Column(length = 500)
    private java.lang.String refreshToken;
    @jakarta.persistence.OneToMany(mappedBy = "customer", cascade = {jakarta.persistence.CascadeType.ALL})
    private java.util.List<com.example.milkdelivery.entity.Order> orders;

    public Customer() {  }

    public java.lang.Long getId() { }

    public void setId(java.lang.Long id) { }

    public java.lang.String getName() {  }

    public void setName(java.lang.String name) {  }

    public java.lang.String getPhone() {  }

    public void setPhone(java.lang.String phone) {  }

    public java.lang.String getAddress() { }

    public void setAddress(java.lang.String address) {  }

    public java.lang.String getPasswordHash() {  }

    public void setPasswordHash(java.lang.String passwordHash) {  }

    public java.lang.String getRefreshToken() {  }

    public void setRefreshToken(java.lang.String refreshToken) {  }
}
