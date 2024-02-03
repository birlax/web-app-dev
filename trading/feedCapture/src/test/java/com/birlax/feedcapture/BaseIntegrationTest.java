package com.birlax.feedcapture;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.context.annotation.Profile;
import org.springframework.jmx.support.RegistrationPolicy;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@Rollback
@Transactional
@Profile("test")
@ExtendWith(SpringExtension.class)
@EnableMBeanExport(registration = RegistrationPolicy.REPLACE_EXISTING)
@ContextConfiguration(classes = FeedCaptureApplication.class)
public abstract class BaseIntegrationTest {}
