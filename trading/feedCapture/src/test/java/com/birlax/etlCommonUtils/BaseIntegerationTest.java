package com.birlax.etlCommonUtils;

import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.context.annotation.Profile;
import org.springframework.jmx.support.RegistrationPolicy;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Profile("test")
@EnableMBeanExport(registration = RegistrationPolicy.REPLACE_EXISTING)
public abstract class BaseIntegerationTest {}
