package com.dteknoloji.domain;

import java.io.Serializable;
import javax.persistence.*;
import org.springframework.hateoas.ResourceSupport;

@Entity
@Table(name = "customer")
public class Customer extends ResourceSupport implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long identity;
    private String username;
    private String password;

    /*
        @JsonIgnore
        @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
        @JoinColumn(name = "account_id", nullable = true)
        private Account account;

        @JsonIgnore
        // Specifies that  one -Customer maps to many Applications-<
        // (mapping specification from this class to other, mapped by the specified
        // column name in the target class)
        @OneToMany(mappedBy="customer",fetch = FetchType.LAZY,cascade=CascadeType.ALL)
        private List<Application> applicationList;
    */

    public Long getIdentity() {
        return identity;
    }

    public void setIdentity(Long identity) {
        this.identity = identity;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
