package com.phoenix.feature;

import com.phoenix.feature.impl.FeatureImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class Application {

	private @Value("${features}") String features;
	private final Logger logger = LoggerFactory.getLogger(Application.class);

	private static final List<FeatureImpl> featureImplementations = new ArrayList<>();

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@PostConstruct public void postConstruct() {
		List.of(features.split(",")).forEach(featureClass -> {
			try {
				logger.info("/{}", featureClass);
				FeatureImpl feature = new FeatureImpl(Class.forName(featureClass).getDeclaredConstructor().newInstance());
				featureImplementations.add(feature);
			} catch (Exception e) {
				throw new RuntimeException("Failed to initialize " + featureClass, e);
			}
		});
	}

	public static List<FeatureImpl> getFeatures() {
		return featureImplementations;
	}

}
