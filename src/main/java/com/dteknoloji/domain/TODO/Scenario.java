package com.dteknoloji.domain.TODO;

import java.io.Serializable;
import javax.persistence.*;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.web.bind.annotation.ResponseBody;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/*
 **************************
 * Copyright (c) 2014     *
 *                        *
 * This code belongs to:  *
 *                        *
 * Ahmet Emre Ünal        *
 * S001974                *
 *                        *
 * aemreunal@gmail.com    *
 * emre.unal@ozu.edu.tr   *
 *                        *
 * aemreunal.com          *
 **************************
 */

@Entity
@Table(name = "scenarios")
@ResponseBody
@JsonIgnoreProperties(value = {"links"})
public class Scenario extends ResourceSupport implements Serializable {
    @Id
    @Column(name = "scenario_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long scenarioId;
}
